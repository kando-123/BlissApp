package pl.polsl.blissapp.ui.views.alchemy;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.Primitive;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.ui.repository.AlchemyRepository;
import pl.polsl.blissapp.ui.repository.SymbolRepository;
import pl.polsl.blissapp.ui.repository.TranslationRepository;
import pl.polsl.blissapp.ui.views.alchemy.AlchemyAdapter.MatchStatus;

@HiltViewModel
public class AlchemyViewModel extends ViewModel {
    private static final String TAG = "AlchemyVM";
    public static final int DAILY_GOAL = 10;

    private final AlchemyRepository mAlchemyRepository;
    private final SymbolRepository mSymbolRepository;
    private final TranslationRepository mTranslationRepository;

    private final MutableLiveData<CraftingTable> mCraftingTable = new MutableLiveData<>(new CraftingTable());
    private final MutableLiveData<Symbol> mResultingSymbol = new MutableLiveData<>();
    private final MutableLiveData<Map<String, List<Symbol>>> mSymbolFolders = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<Boolean> mShowCheering = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> mCheerIcon = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> mIsTargetMatched = new MutableLiveData<>(false);

    private final MutableLiveData<Symbol> mTargetSymbol = new MutableLiveData<>();
    private final MutableLiveData<String> mTargetLabel = new MutableLiveData<>("");
    private final MutableLiveData<Integer> mDailyProgress = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> mDailyGoalReached = new MutableLiveData<>(false);

    private final MutableLiveData<List<AlchemyAdapter.CraftingItem>> mCraftingItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<AlchemyAdapter.CraftingItem>> mHintItems = new MutableLiveData<>(new ArrayList<>());

    private final Set<Symbol> mDiscoveredSymbols = new HashSet<>();
    private List<Map<Primitive, Integer>> mTargetVariants = new ArrayList<>();
    private boolean mDiscoveryInProgress = false;

    @Inject
    public AlchemyViewModel(AlchemyRepository alchemyRepository,
                            SymbolRepository symbolRepository,
                            TranslationRepository translationRepository) {
        this.mAlchemyRepository = alchemyRepository;
        this.mSymbolRepository = symbolRepository;
        this.mTranslationRepository = translationRepository;
        loadInitialState();
    }

    private void loadInitialState() {
        mAlchemyRepository.getGameState(new Callback<Set<Symbol>, Exception>() {
            @Override
            public void onSuccess(Set<Symbol> result) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    mDiscoveredSymbols.addAll(result);
                    updateFolders();
                    pickNewTargetSymbol();
                });
            }

            @Override
            public void onFailure(Exception reason) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    updateFolders();
                    pickNewTargetSymbol();
                });
            }
        });
    }

    private void pickNewTargetSymbol() {
        mSymbolRepository.getRandomSymbol(new Callback<Symbol, Exception>() {
            @Override
            public void onSuccess(Symbol result) {
                mTargetSymbol.postValue(result);
                fetchTargetLabel(result);
                fetchTargetVariants(result);
            }

            @Override
            public void onFailure(Exception reason) {}
        });
    }

    private void fetchTargetLabel(Symbol symbol) {
        mTranslationRepository.getMeanings(symbol, "en", new Callback<List<String>, Exception>() {
            @Override
            public void onSuccess(List<String> result) {
                mTargetLabel.postValue(!result.isEmpty() ? result.get(0) : "???");
            }
            @Override
            public void onFailure(Exception reason) { mTargetLabel.postValue("???"); }
        });
    }

    private void fetchTargetVariants(Symbol symbol) {
        mSymbolRepository.getPrimitiveVariants(symbol, new Callback<List<Map<Primitive, Integer>>, Exception>() {
            @Override
            public void onSuccess(List<Map<Primitive, Integer>> result) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    mTargetVariants = result;
                    updateCraftingItems();
                    updateHints();
                });
            }
            @Override
            public void onFailure(Exception reason) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    mTargetVariants = new ArrayList<>();
                    updateCraftingItems();
                    updateHints();
                });
            }
        });
    }

    private void updateFolders() {
        Map<String, List<Symbol>> folders = new LinkedHashMap<>();
        folders.put("Discovery Journal", new ArrayList<>(mDiscoveredSymbols));
        mSymbolFolders.postValue(folders);
    }

    private void updateCraftingItems() {
        CraftingTable table = mCraftingTable.getValue();
        if (table == null || mTargetVariants.isEmpty()) return;

        List<Object> items = table.getAllItems();
        List<AlchemyAdapter.CraftingItem> combined = new ArrayList<>();

        // Find the "best" variant to show hints for (the one with the most matches)
        Map<Primitive, Integer> currentPrims = new HashMap<>();
        for (Object obj : items) {
            if (obj instanceof Primitive p) currentPrims.merge(p, 1, Integer::sum);
        }

        Map<Primitive, Integer> bestVariant = mTargetVariants.get(0);
        int maxExactMatches = -1;

        for (Map<Primitive, Integer> variant : mTargetVariants) {
            int matches = 0;
            for (Map.Entry<Primitive, Integer> entry : currentPrims.entrySet()) {
                if (variant.containsKey(entry.getKey())) {
                    matches += Math.min(entry.getValue(), variant.get(entry.getKey()));
                }
            }
            if (matches > maxExactMatches) {
                maxExactMatches = matches;
                bestVariant = variant;
            }
        }

        Map<Primitive, Integer> targetCounts = new HashMap<>(bestVariant);
        Set<Primitive> targetRoots = new HashSet<>();
        for (Primitive p : bestVariant.keySet()) {
            targetRoots.add(p.getRoot());
        }

        for (Object obj : items) {
            if (!(obj instanceof Primitive p)) {
                combined.add(new AlchemyAdapter.CraftingItem(obj, MatchStatus.NONE));
                continue;
            }

            MatchStatus status;
            if (bestVariant.containsKey(p)) {
                if (targetCounts.get(p) > 0) {
                    status = MatchStatus.EXACT;
                    targetCounts.put(p, targetCounts.get(p) - 1);
                } else {
                    status = MatchStatus.INCORRECT;
                }
            } else if (targetRoots.contains(p.getRoot())) {
                status = MatchStatus.PARTIAL;
            } else {
                status = MatchStatus.INCORRECT;
            }
            combined.add(new AlchemyAdapter.CraftingItem(p, status, null));
        }

        mCraftingItems.setValue(combined);
    }

    private void updateHints() {
        List<AlchemyAdapter.CraftingItem> currentItems = mCraftingItems.getValue();
        if (currentItems == null || currentItems.isEmpty()) {
            mHintItems.postValue(new ArrayList<>());
            return;
        }

        Map<Primitive, Integer> filterMap = new HashMap<>();
        for (AlchemyAdapter.CraftingItem item : currentItems) {
            if (item.object instanceof Primitive p) {
                filterMap.merge(p, 1, Integer::sum);
            }
        }

        if (filterMap.isEmpty()) {
            mHintItems.postValue(new ArrayList<>());
            return;
        }

        mSymbolRepository.getMatchingSymbols(null, filterMap, 6, new Callback<List<Symbol>, Exception>() {
            @Override
            public void onSuccess(List<Symbol> result) {
                if (result.isEmpty()) {
                    mHintItems.postValue(new ArrayList<>());
                    return;
                }

                AlchemyAdapter.CraftingItem[] resultsArray = new AlchemyAdapter.CraftingItem[result.size()];
                AtomicInteger pending = new AtomicInteger(result.size());
                Symbol target = mTargetSymbol.getValue();

                for (int i = 0; i < result.size(); i++) {
                    final int index = i;
                    final Symbol s = result.get(i);
                    final boolean isTarget = (target != null && s.index() == target.index());
                    
                    if (isTarget) {
                        mTranslationRepository.getMeanings(s, "en", new Callback<List<String>, Exception>() {
                            @Override
                            public void onSuccess(List<String> meanings) {
                                String label = meanings.isEmpty() ? "" : meanings.get(0);
                                resultsArray[index] = new AlchemyAdapter.CraftingItem(s, MatchStatus.EXACT, label);
                                if (pending.decrementAndGet() == 0) {
                                    mHintItems.postValue(Arrays.asList(resultsArray));
                                }
                            }
                            @Override
                            public void onFailure(Exception reason) {
                                resultsArray[index] = new AlchemyAdapter.CraftingItem(s, MatchStatus.EXACT, "");
                                if (pending.decrementAndGet() == 0) {
                                    mHintItems.postValue(Arrays.asList(resultsArray));
                                }
                            }
                        });
                    } else {
                        resultsArray[index] = new AlchemyAdapter.CraftingItem(s, MatchStatus.NONE, null);
                        if (pending.decrementAndGet() == 0) {
                            mHintItems.postValue(Arrays.asList(resultsArray));
                        }
                    }
                }
            }
            @Override
            public void onFailure(Exception reason) {
                mHintItems.postValue(new ArrayList<>());
            }
        });
    }

    public void addRadical(@Nullable Primitive primitive) {
        if (primitive == null || mDiscoveryInProgress) return;

        CraftingTable table = mCraftingTable.getValue();
        if (table != null) {
            mCraftingTable.setValue(table.addItem(primitive));
            updateCraftingItems();
            updateHints();
        }
    }

    public void onHintPressed(Object item) {
        if (mDiscoveryInProgress || !(item instanceof Symbol symbol)) return;
        Symbol target = mTargetSymbol.getValue();
        if (target != null && symbol.index() == target.index()) {
            startDiscovery(symbol);
        }
    }

    private void startDiscovery(Symbol symbol) {
        if (symbol == null || mDiscoveryInProgress) return;
        mDiscoveryInProgress = true;

        Symbol target = mTargetSymbol.getValue();
        boolean isTarget = target != null && symbol.index() == target.index();

        mCheerIcon.postValue(isTarget ? pl.polsl.blissapp.R.drawable.ic_splash_logo : pl.polsl.blissapp.R.drawable.ic_bliss);
        mIsTargetMatched.postValue(isTarget);
        mShowCheering.postValue(true);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            mResultingSymbol.setValue(symbol);
            if (mDiscoveredSymbols.add(symbol)) {
                updateFolders();
                mAlchemyRepository.setGameState(mDiscoveredSymbols);
            }
            if (isTarget) {
                incrementProgress();
                pickNewTargetSymbol();
            }

            mCraftingTable.setValue(new CraftingTable());
            updateCraftingItems();
            updateHints();
            mIsTargetMatched.setValue(false);
            mDiscoveryInProgress = false;
        }, 800);
    }

    private void incrementProgress() {
        int current = (mDailyProgress.getValue() == null) ? 0 : mDailyProgress.getValue();
        int next = current + 1;

        // Internal count keeps going up
        mDailyProgress.setValue(next);

        // Goal reached logic
        if (next >= DAILY_GOAL) {
            // Trigger only once when exactly reaching the goal
            if (!Boolean.TRUE.equals(mDailyGoalReached.getValue())) {
                mDailyGoalReached.setValue(true);
            }
        }
    }

    public void onPopPressed() {
        if (mDiscoveryInProgress) return;
        CraftingTable table = mCraftingTable.getValue();
        if (table != null) {
            mCraftingTable.setValue(table.removeLast());
            updateCraftingItems();
            updateHints();
        }
    }

    public void removeItem(Object item) {
        if (mDiscoveryInProgress) return;
        CraftingTable table = mCraftingTable.getValue();
        if (table != null) {
            mCraftingTable.setValue(table.removeItem(item));
            updateCraftingItems();
            updateHints();
        }
    }

    public void dismissCheering() {
        mShowCheering.setValue(false);
        mResultingSymbol.setValue(null);
    }

    public void onEnterPressed() {}

    public LiveData<List<AlchemyAdapter.CraftingItem>> getCraftingItems() { return mCraftingItems; }
    public LiveData<List<AlchemyAdapter.CraftingItem>> getHintItems() { return mHintItems; }
    public LiveData<Symbol> getResultingSymbol() { return mResultingSymbol; }
    public LiveData<Map<String, List<Symbol>>> getSymbolFolders() { return mSymbolFolders; }
    public LiveData<Boolean> getShowCheering() { return mShowCheering; }
    public LiveData<Integer> getCheerIcon() { return mCheerIcon; }
    public LiveData<Boolean> getIsTargetMatched() { return mIsTargetMatched; }
    public LiveData<Symbol> getTargetSymbol() { return mTargetSymbol; }
    public LiveData<String> getTargetLabel() { return mTargetLabel; }
    public LiveData<Integer> getDailyProgress() { return mDailyProgress; }
    public LiveData<Boolean> getDailyGoalReached() { return mDailyGoalReached; }
}