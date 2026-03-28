package pl.polsl.blissapp.ui.views.alchemy;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
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
import java.util.stream.Collectors;

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

    // LiveData for the crafting table (immutable state)
    private final MutableLiveData<CraftingTable> mCraftingTable = new MutableLiveData<>(new CraftingTable());

    // Other LiveData
    private final MutableLiveData<Symbol> mResultingSymbol = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mShowCheering = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> mCheerIcon = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> mIsTargetMatched = new MutableLiveData<>(false);

    private final MutableLiveData<Symbol> mTargetSymbol = new MutableLiveData<>();
    private final MutableLiveData<String> mTargetLabel = new MutableLiveData<>("");
    private final MutableLiveData<Integer> mDailyProgress = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> mDailyGoalReached = new MutableLiveData<>(false);
    private final MutableLiveData<String> mSelectedLanguage = new MutableLiveData<>();
    private final MutableLiveData<List<String>> mSpeakRequest = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mNavigateToJournal = new MutableLiveData<>(false);

    // Reactive crafting items: recomputed when mCraftingTable or mTargetVariants changes
    private final MediatorLiveData<List<AlchemyAdapter.CraftingItem>> mCraftingItems = new MediatorLiveData<>();

    // Hints are recomputed when mCraftingTable changes
    private final MediatorLiveData<List<AlchemyAdapter.CraftingItem>> mHintItems = new MediatorLiveData<>();

    // Internal data that affects crafting items
    private final MutableLiveData<List<Map<Primitive, Integer>>> mTargetVariants = new MutableLiveData<>(new ArrayList<>());

    // Other internal fields
    private List<Integer> mTargetComponentSequence = new ArrayList<>();
    private Set<Integer> mTargetComponentIndices = new HashSet<>();
    private final Map<Integer, Map<Primitive, Integer>> mSymbolPrimitivesMap = new HashMap<>();
    private boolean mDiscoveryInProgress = false;
    private final Map<Integer, Symbol> mDiscoveredSymbols = new LinkedHashMap<>();

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

        // Set up MediatorLiveData
        mCraftingItems.addSource(mCraftingTable, table -> recomputeCraftingItems());
        mCraftingItems.addSource(mTargetVariants, variants -> recomputeCraftingItems());

        mHintItems.addSource(mCraftingTable, table -> recomputeHints());

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
        mAlchemyRepository.getDiscovered(new Callback<List<Symbol>, Exception>() {
            @Override
            public void onSuccess(List<Symbol> result) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    mDiscoveredSymbols.clear();
                    if (result != null) {
                        for (Symbol s : result) {
                            mDiscoveredSymbols.put(s.index(), s);
                        }
                    }
                });
            }
            @Override
            public void onFailure(Exception reason) {}
        });

        pickNewTargetSymbol();
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

            if (!mDiscoveredSymbols.containsKey(symbol.index())) {
                mDiscoveredSymbols.put(symbol.index(), symbol);
                Log.d("AlchemyVM", "Saving discovered symbol: " + symbol.index());
                mAlchemyRepository.setDiscovered(symbol, new Callback<Void, Exception>() {
                    @Override
                    public void onSuccess(Void result) {
                        Log.d("AlchemyVM", "Successfully saved discovered symbol");
                    }
                    @Override
                    public void onFailure(Exception data) {
                        Log.e("AlchemyVM", "Failed to save discovered symbol", data);
                    }
                });
            }

            if (isTarget) {
                incrementProgress();
                pickNewTargetSymbol();
            }

            mCraftingTable.setValue(new CraftingTable());
            mIsTargetMatched.setValue(false);
            mDiscoveryInProgress = false;
        }, 800);
    }

    public void pickNewTargetSymbol() {
        mAlchemyRepository.getRandomUndiscovered(new Callback<Symbol, Exception>() {
            @Override
            public void onSuccess(Symbol result) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (result != null) {
                        mTargetSymbol.setValue(result);
                        fetchTargetLabel(result);
                        fetchTargetVariants(result);
                        fetchTargetComponents(result);
                    } else {
                        mTargetLabel.setValue("All Symbols Discovered!");
                        mTargetSymbol.setValue(null);
                    }
                });
            }

            @Override
            public void onFailure(Exception reason) {
                // Handle error: maybe no undiscovered symbols
                new Handler(Looper.getMainLooper()).post(() -> {
                    mTargetLabel.setValue("All Symbols Discovered!");
                    mTargetSymbol.setValue(null);
                });
            }
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
                    mTargetVariants.setValue(result != null ? result : new ArrayList<>());
                });
            }
            @Override
            public void onFailure(Exception reason) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    mTargetVariants.setValue(new ArrayList<>());
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
                    mTargetComponentSequence.add(s.index());
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

        for (Integer compIndex : mTargetComponentSequence) {
            if (!symbolsOnBoard.contains(compIndex)) {
                return compIndex;
            }
        }
        return -1;
    }

    // Core method to recompute crafting items based on current table and target variants
    private void recomputeCraftingItems() {
        CraftingTable table = mCraftingTable.getValue();
        if (table == null) {
            mCraftingItems.setValue(new ArrayList<>());
            return;
        }

        List<Object> rawItems = table.getAllItems();
        Map<Primitive, Integer> primDisplayCounts = new LinkedHashMap<>();
        Map<Symbol, Integer> symbolDisplayCounts = new LinkedHashMap<>();
        for (Object obj : rawItems) {
            if (obj instanceof Primitive p) primDisplayCounts.merge(p, 1, Integer::sum);
            else if (obj instanceof Symbol s) symbolDisplayCounts.merge(s, 1, Integer::sum);
        }

        List<AlchemyAdapter.CraftingItem> combined = new ArrayList<>();

        // Inside recomputeCraftingItems(), after computing totalPrims:
        List<Map<Primitive, Integer>> variants = mTargetVariants.getValue();
        Set<Primitive> allVariantPrimitives = new HashSet<>();
        Set<Primitive> allVariantRoots = new HashSet<>();

        if (variants != null && !variants.isEmpty()) {
            for (Map<Primitive, Integer> variant : variants) {
                allVariantPrimitives.addAll(variant.keySet());
                // Collect roots of all primitives in the variant
                for (Primitive p : variant.keySet()) {
                    allVariantRoots.add(p.getRoot());
                }
            }
        }

        // Then when processing primitives:
        for (Map.Entry<Primitive, Integer> entry : primDisplayCounts.entrySet()) {
            Primitive p = entry.getKey();
            int count = entry.getValue();
            MatchStatus status = MatchStatus.NONE;
            if (!allVariantPrimitives.isEmpty()) {
                if (allVariantPrimitives.contains(p)) {
                    status = MatchStatus.EXACT;
                } else if (allVariantRoots.contains(p.getRoot())) {
                    status = MatchStatus.PARTIAL;
                } else {
                    status = MatchStatus.INCORRECT;
                }
            }
            combined.add(new AlchemyAdapter.CraftingItem(p, status, null, count));
        }

        mCraftingItems.setValue(combined);
    }
    private Map<Primitive, Integer> computeTotalPrimitives(Map<Primitive, Integer> prims,
                                                           Map<Symbol, Integer> symbols) {
        Map<Primitive, Integer> total = new HashMap<>(prims);
        for (Map.Entry<Symbol, Integer> entry : symbols.entrySet()) {
            Symbol s = entry.getKey();
            Map<Primitive, Integer> sPrims = mSymbolPrimitivesMap.get(s.index());
            if (sPrims != null) {
                int multiplier = entry.getValue();
                for (Map.Entry<Primitive, Integer> sp : sPrims.entrySet()) {
                    total.merge(sp.getKey(), sp.getValue() * multiplier, Integer::sum);
                }
            }
        }
        return total;
    }

    private Map<Primitive, Integer> selectBestVariant(Map<Primitive, Integer> totalPrims,
                                                      List<Map<Primitive, Integer>> variants) {
        Map<Primitive, Integer> best = variants.get(0);
        int maxMatches = -1;
        for (Map<Primitive, Integer> variant : variants) {
            int matches = 0;
            for (Map.Entry<Primitive, Integer> entry : totalPrims.entrySet()) {
                Integer variantCount = variant.get(entry.getKey());
                if (variantCount != null) {
                    matches += Math.min(entry.getValue(), variantCount);
                }
            }
            if (matches > maxMatches) {
                maxMatches = matches;
                best = variant;
            }
        }
        return best;
    }

    private void recomputeHints() {
        Map<Primitive, Integer> filterMap = getTotalPrimitiveCounts();
        if (filterMap.isEmpty()) {
            mHintItems.setValue(new ArrayList<>());
            return;
        }

        mSymbolRepository.getMatchingSymbols(null, filterMap, 4, new Callback<List<Symbol>, Exception>() {
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

    // Public LiveData getters
    public LiveData<String> getSelectedLanguage() {
        return mSelectedLanguage;
    }

    public void addRadical(@Nullable Primitive primitive) {
        if (primitive == null || mDiscoveryInProgress) return;

        CraftingTable table = mCraftingTable.getValue();
        if (table != null) {
            mCraftingTable.setValue(table.addItem(primitive));
        }
    }

    public void onHintPressed(Object item) {
        if (mDiscoveryInProgress || !(item instanceof Symbol symbol)) return;

        Symbol target = mTargetSymbol.getValue();
        if (target != null && symbol.index() == target.index()) {
            startDiscovery(symbol);
        } else if (symbol.index() == getNextExpectedComponentIndex()) {
            mSymbolRepository.getPrimitiveVariants(symbol, new Callback<List<Map<Primitive, Integer>>, Exception>() {
                @Override
                public void onSuccess(List<Map<Primitive, Integer>> variants) {
                    if (variants.isEmpty()) return;

                    new Handler(Looper.getMainLooper()).post(() -> {
                        // Store primitive decomposition for this symbol
                        mSymbolPrimitivesMap.put(symbol.index(), variants.get(0));

                        // Add to existing table, not replace
                        CraftingTable table = new CraftingTable();
                        table = table.addItem(symbol);
                        mCraftingTable.setValue(table);
                    });
                }
                @Override
                public void onFailure(Exception data) {
                    Log.e(TAG, "Failed to get primitive variants for hint", data);
                }
            });
        }
    }

    private void incrementProgress() {
        int current = (mDailyProgress.getValue() == null) ? 0 : mDailyProgress.getValue();
        int next = current + 1;

        mDailyProgress.setValue(next);

        if (next >= DAILY_GOAL) {
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
        }
    }

    public void removeItem(Object item) {
        if (mDiscoveryInProgress) return;
        CraftingTable table = mCraftingTable.getValue();
        if (table != null) {
            mCraftingTable.setValue(table.removeItem(item));
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

    public void requestJournalNavigation() {
        mNavigateToJournal.setValue(true);
    }

    public void clearNavigation() {
        mNavigateToJournal.setValue(false);
    }

    public void clearSpeakRequest() {
        mSpeakRequest.setValue(null);
    }

    public void refreshLanguageIfNeeded() {
        String currentAppLang = getCurrentAppLanguage();
        if (!currentAppLang.equals(mSelectedLanguage.getValue())) {
            mSelectedLanguage.setValue(currentAppLang);

            Symbol currentTarget = mTargetSymbol.getValue();
            if (currentTarget != null) {
                fetchTargetLabel(currentTarget);
            }
        }
    }

    // LiveData getters
    public LiveData<List<String>> getSpeakRequest() { return mSpeakRequest; }
    public LiveData<List<AlchemyAdapter.CraftingItem>> getCraftingItems() { return mCraftingItems; }
    public LiveData<List<AlchemyAdapter.CraftingItem>> getHintItems() { return mHintItems; }
    public LiveData<Symbol> getResultingSymbol() { return mResultingSymbol; }
    public LiveData<Boolean> getShowCheering() { return mShowCheering; }
    public LiveData<Integer> getCheerIcon() { return mCheerIcon; }
    public LiveData<Boolean> getIsTargetMatched() { return mIsTargetMatched; }
    public LiveData<Symbol> getTargetSymbol() { return mTargetSymbol; }
    public LiveData<String> getTargetLabel() { return mTargetLabel; }
    public LiveData<Integer> getDailyProgress() { return mDailyProgress; }
    public LiveData<Boolean> getDailyGoalReached() { return mDailyGoalReached; }
    public LiveData<Boolean> getNavigateToJournal() { return mNavigateToJournal; }
}