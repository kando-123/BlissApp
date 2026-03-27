package pl.polsl.blissapp.ui.views.alchemy;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
public class AlchemyViewModel extends AndroidViewModel {
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
    private final MutableLiveData<String> mSelectedLanguage = new MutableLiveData<>();
    private final MutableLiveData<List<String>> mSpeakRequest = new MutableLiveData<>();

    private final MutableLiveData<List<AlchemyAdapter.CraftingItem>> mCraftingItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<AlchemyAdapter.CraftingItem>> mHintItems = new MutableLiveData<>(new ArrayList<>());

    private final Set<Symbol> mDiscoveredSymbols = new HashSet<>();
    private List<Integer> mTargetComponentSequence = new ArrayList<>();
    private List<Map<Primitive, Integer>> mTargetVariants = new ArrayList<>();
    private Set<Integer> mTargetComponentIndices = new HashSet<>();
    private final Map<Integer, Map<Primitive, Integer>> mSymbolPrimitivesMap = new HashMap<>();
    private boolean mDiscoveryInProgress = false;

    @Inject
    public AlchemyViewModel(@NonNull Application application,
                            AlchemyRepository alchemyRepository,
                            SymbolRepository symbolRepository,
                            TranslationRepository translationRepository) {
        super(application);
        this.mAlchemyRepository = alchemyRepository;
        this.mSymbolRepository = symbolRepository;
        this.mTranslationRepository = translationRepository;

        mSelectedLanguage.setValue(getCurrentAppLanguage());

        loadInitialState();
    }

    private String getCurrentAppLanguage() {
        LocaleListCompat currentLocales = AppCompatDelegate.getApplicationLocales();
        Locale locale = currentLocales.isEmpty() ? Locale.getDefault() : currentLocales.get(0);

        if (locale == null) return "English";
        String lang = locale.getLanguage();
        return (lang != null && lang.startsWith("pl")) ? "Polish" : "English";
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

    public void pickNewTargetSymbol() {
        mSymbolRepository.getRandomSymbol(new Callback<Symbol, Exception>() {
            @Override
            public void onSuccess(Symbol result) {
                mTargetSymbol.postValue(result);
                fetchTargetLabel(result);
                fetchTargetVariants(result);
                fetchTargetComponents(result);
            }

            @Override
            public void onFailure(Exception reason) {}
        });
    }

    private void fetchTargetLabel(Symbol symbol) {
        mTranslationRepository.getMeanings(symbol, getSelectedLanguage().getValue(), new Callback<List<String>, Exception>() {
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

    private void fetchTargetComponents(Symbol symbol) {
        mSymbolRepository.getComponents(symbol, new Callback<List<Symbol>, Exception>() {
            @Override
            public void onSuccess(List<Symbol> result) {
                mTargetComponentIndices = new HashSet<>();
                mTargetComponentSequence = new ArrayList<>();
                for (Symbol s : result) {
                    mTargetComponentIndices.add(s.index());
                    mTargetComponentSequence.add(s.index()); // Track the sequence
                }
            }
            @Override
            public void onFailure(Exception reason) {
                mTargetComponentIndices = new HashSet<>();
                mTargetComponentSequence = new ArrayList<>();
            }
        });
    }

    private int getNextExpectedComponentIndex() {
        CraftingTable table = mCraftingTable.getValue();
        Set<Integer> symbolsOnBoard = new HashSet<>();

        if (table != null) {
            for (Object obj : table.getAllItems()) {
                if (obj instanceof Symbol) {
                    symbolsOnBoard.add(((Symbol) obj).index());
                }
            }
        }

        // Return the first component from the string that isn't on the board yet
        for (Integer compIndex : mTargetComponentSequence) {
            if (!symbolsOnBoard.contains(compIndex)) {
                return compIndex;
            }
        }
        return -1; // All components found
    }

    private void updateFolders() {
        Map<String, List<Symbol>> folders = new LinkedHashMap<>();
        folders.put("Discovery Journal", new ArrayList<>(mDiscoveredSymbols));
        mSymbolFolders.postValue(folders);
    }

    private void updateCraftingItems() {
        CraftingTable table = mCraftingTable.getValue();
        if (table == null || mTargetVariants.isEmpty()) return;

        List<Object> rawItems = table.getAllItems();
        
        // 1. Group board items for display
        Map<Primitive, Integer> primDisplayCounts = new LinkedHashMap<>();
        Map<Symbol, Integer> symbolDisplayCounts = new LinkedHashMap<>();
        for (Object obj : rawItems) {
            if (obj instanceof Primitive p) primDisplayCounts.merge(p, 1, Integer::sum);
            else if (obj instanceof Symbol s) symbolDisplayCounts.merge(s, 1, Integer::sum);
        }

        // 2. Calculate TOTAL primitive counts for matching (including expanded symbols)
        Map<Primitive, Integer> totalPrims = new HashMap<>();
        for (Map.Entry<Primitive, Integer> entry : primDisplayCounts.entrySet()) {
            totalPrims.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
        for (Map.Entry<Symbol, Integer> entry : symbolDisplayCounts.entrySet()) {
            Map<Primitive, Integer> sPrims = mSymbolPrimitivesMap.get(entry.getKey().index());
            if (sPrims != null) {
                for (Map.Entry<Primitive, Integer> sp : sPrims.entrySet()) {
                    totalPrims.merge(sp.getKey(), sp.getValue() * entry.getValue(), Integer::sum);
                }
            }
        }

        // 3. Find the "best" variant of the target
        Map<Primitive, Integer> bestVariant = mTargetVariants.get(0);
        int maxExactMatches = -1;
        for (Map<Primitive, Integer> variant : mTargetVariants) {
            int matches = 0;
            for (Map.Entry<Primitive, Integer> entry : totalPrims.entrySet()) {
                if (variant.containsKey(entry.getKey())) {
                    matches += Math.min(entry.getValue(), variant.get(entry.getKey()));
                }
            }
            if (matches > maxExactMatches) {
                maxExactMatches = matches;
                bestVariant = variant;
            }
        }

        Set<Primitive> targetRoots = new HashSet<>();
        for (Primitive p : bestVariant.keySet()) {
            targetRoots.add(p.getRoot());
        }

        // 4. Build results
        List<AlchemyAdapter.CraftingItem> combined = new ArrayList<>();
        
        for (Map.Entry<Symbol, Integer> entry : symbolDisplayCounts.entrySet()) {
            Symbol s = entry.getKey();
            MatchStatus status = mTargetComponentIndices.contains(s.index()) ? MatchStatus.PARTIAL : MatchStatus.INCORRECT;
            combined.add(new AlchemyAdapter.CraftingItem(s, status, null, entry.getValue()));
        }

        for (Map.Entry<Primitive, Integer> entry : primDisplayCounts.entrySet()) {
            Primitive p = entry.getKey();
            int count = entry.getValue();

            MatchStatus status;
            if (bestVariant.containsKey(p)) {
                status = MatchStatus.EXACT;
            } else if (targetRoots.contains(p.getRoot())) {
                status = MatchStatus.PARTIAL;
            } else {
                status = MatchStatus.INCORRECT;
            }
            
            combined.add(new AlchemyAdapter.CraftingItem(p, status, null, count));
        }

        mCraftingItems.setValue(combined);
    }

    private void updateHints() {
        Map<Primitive, Integer> filterMap = getTotalPrimitiveCounts();
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

                List<AlchemyAdapter.CraftingItem> newHints = new ArrayList<>();
                Symbol target = mTargetSymbol.getValue();
                int nextExpectedIndex = getNextExpectedComponentIndex();

                for (Symbol s : result) {
                    boolean isTarget = (target != null && s.index() == target.index());
                    boolean isNextComponent = (s.index() == nextExpectedIndex);

                    MatchStatus status;
                    if (isTarget) {
                        status = MatchStatus.EXACT;
                    } else if (isNextComponent) {
                        status = MatchStatus.PARTIAL;
                    } else {
                        status = MatchStatus.NONE;
                    }

                    // Pass null for the label so the adapter hides the TextView entirely
                    newHints.add(new AlchemyAdapter.CraftingItem(s, status, null));
                }

                mHintItems.postValue(newHints);
            }

            @Override
            public void onFailure(Exception reason) {
                mHintItems.postValue(new ArrayList<>());
            }
        });
    }

    private Map<Primitive, Integer> getTotalPrimitiveCounts() {
        Map<Primitive, Integer> total = new HashMap<>();
        CraftingTable table = mCraftingTable.getValue();
        if (table == null) return total;
        
        for (Object obj : table.getAllItems()) {
            if (obj instanceof Primitive p) {
                total.merge(p, 1, Integer::sum);
            } else if (obj instanceof Symbol s) {
                Map<Primitive, Integer> prims = mSymbolPrimitivesMap.get(s.index());
                if (prims != null) {
                    for (Map.Entry<Primitive, Integer> entry : prims.entrySet()) {
                        total.merge(entry.getKey(), entry.getValue(), Integer::sum);
                    }
                }
            }
        }
        return total;
    }

    public LiveData<String> getSelectedLanguage() {
        return mSelectedLanguage;
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
// ... previous code
        if (target != null && symbol.index() == target.index()) {
            startDiscovery(symbol);
        } else if (symbol.index() == getNextExpectedComponentIndex()) { // <-- UPDATED
            mSymbolRepository.getPrimitiveVariants(symbol, new Callback<List<Map<Primitive, Integer>>, Exception>() {
                @Override
                public void onSuccess(List<Map<Primitive, Integer>> variants) {
                    if (variants.isEmpty()) return;

                    new Handler(Looper.getMainLooper()).post(() -> {
                        // Keep track of the primitives for this symbol
                        mSymbolPrimitivesMap.put(symbol.index(), variants.get(0));

                        // 1. CLEAR the filter by creating a completely fresh crafting table
                        CraftingTable table = new CraftingTable();

                        // 2. Add ONLY the newly matched yellow symbol to the board
                        table = table.addItem(symbol);

                        mCraftingTable.setValue(table);
                        updateCraftingItems();
                        updateHints();
                    });
                }
                @Override
                public void onFailure(Exception data) {}
            });
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

    public void onEnterPressed() {
        String label = mTargetLabel.getValue();
        if (label != null && !label.isEmpty() && !"???".equals(label)) {
            mSpeakRequest.postValue(Collections.singletonList(label));
        }
    }

    public LiveData<List<String>> getSpeakRequest() {
        return mSpeakRequest;
    }

    public void clearSpeakRequest() {
        mSpeakRequest.setValue(null);
    }

    public void refreshLanguageIfNeeded() {
        String currentAppLang = getCurrentAppLanguage();
        // If the system language changed (e.g., from Settings), update our LiveData
        if (!currentAppLang.equals(mSelectedLanguage.getValue())) {
            mSelectedLanguage.setValue(currentAppLang);
        }
    }

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
