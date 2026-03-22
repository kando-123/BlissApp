package pl.polsl.blissapp.ui.views.alchemy;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.Primitive;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.ui.repository.AlchemyRepository;
import pl.polsl.blissapp.ui.repository.SymbolRepository;

@HiltViewModel
public class AlchemyViewModel extends ViewModel
{
    private static final String TAG = "AlchemyVM";
    
    private final AlchemyRepository mAlchemyRepository;
    private final SymbolRepository mSymbolRepository;
    
    private final MutableLiveData<CraftingTable> mCraftingTable = new MutableLiveData<>(new CraftingTable());
    private final MutableLiveData<Symbol> mResultingSymbol = new MutableLiveData<>();
    private final MutableLiveData<Map<String, List<Symbol>>> mSymbolFolders = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<Boolean> mShowCheering = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mIsMatchDiscovered = new MutableLiveData<>(false);

    private final Set<Symbol> mDiscoveredSymbols = new HashSet<>();
    private final List<Primitive> mCurrentFilter = new ArrayList<>();
    private boolean mDiscoveryInProgress = false;

    @Inject
    public AlchemyViewModel(AlchemyRepository alchemyRepository, SymbolRepository symbolRepository)
    {
        this.mAlchemyRepository = alchemyRepository;
        this.mSymbolRepository = symbolRepository;
        loadInitialState();
    }

    private void loadInitialState() {
        mAlchemyRepository.getGameState(new Callback<Set<Symbol>, Exception>() {
            @Override
            public void onSuccess(Set<Symbol> result) {
                mDiscoveredSymbols.addAll(result);
                updateFolders();
            }

            @Override
            public void onFailure(Exception reason) {
                Log.e(TAG, "Load state failed", reason);
                updateFolders();
            }
        });
    }

    private void updateFolders() {
        Map<String, List<Symbol>> folders = new LinkedHashMap<>();
        folders.put("Discovery Journal", new ArrayList<>(mDiscoveredSymbols));
        mSymbolFolders.postValue(folders);
    }

    public LiveData<CraftingTable> getCraftingTable() { return mCraftingTable; }
    public LiveData<Symbol> getResultingSymbol() { return mResultingSymbol; }
    public LiveData<Map<String, List<Symbol>>> getSymbolFolders() { return mSymbolFolders; }
    public LiveData<Boolean> getShowCheering() { return mShowCheering; }
    public LiveData<Boolean> getIsMatchDiscovered() { return mIsMatchDiscovered; }

    public void addRadical(@Nullable Primitive primitive)
    {
        if (primitive == null || mDiscoveryInProgress) return;
        Log.d(TAG, "Adding radical: " + primitive);
        
        mCurrentFilter.add(primitive);
        CraftingTable table = mCraftingTable.getValue();
        if (table != null) {
            mCraftingTable.setValue(table.addRadical(primitive));
        }
        
        checkDiscovery();
    }

    private void checkDiscovery() {
        if (mCurrentFilter.isEmpty()) return;

        Map<Primitive, Integer> filterMap = new HashMap<>();
        for (Primitive p : mCurrentFilter) {
            filterMap.merge(p, 1, Integer::sum);
        }

        mSymbolRepository.getMatchingSymbols(null, filterMap, 1, new Callback<List<Symbol>, Exception>() {
            @Override
            public void onSuccess(List<Symbol> data) {
                if (!data.isEmpty()) {
                    Symbol match = data.get(0);
                    if (!mDiscoveredSymbols.contains(match)) {
                        Log.d(TAG, "Undiscovered match found: " + match.index());
                        new Handler(Looper.getMainLooper()).post(() -> startDiscovery(match));
                    } else {
                        Log.d(TAG, "Match already discovered: " + match.index());
                    }
                }
            }

            @Override
            public void onFailure(Exception reason) {
                Log.e(TAG, "Hint search failed", reason);
            }
        });
    }

    private void startDiscovery(Symbol symbol) {
        mDiscoveryInProgress = true;
        mIsMatchDiscovered.setValue(true);
        
        // Wait for animation phase in UI
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            mResultingSymbol.setValue(symbol);
            mShowCheering.setValue(true);
            
            if (mDiscoveredSymbols.add(symbol)) {
                updateFolders();
                mAlchemyRepository.setGameState(mDiscoveredSymbols);
            }
            
            mCurrentFilter.clear();
            mCraftingTable.setValue(new CraftingTable());
            mIsMatchDiscovered.setValue(false);
            mDiscoveryInProgress = false;
        }, 1500); 
    }

    public void onEnterPressed() {
        if (mCurrentFilter.isEmpty() || mDiscoveryInProgress) return;
        
        Map<Primitive, Integer> filterMap = new HashMap<>();
        for (Primitive p : mCurrentFilter) {
            filterMap.merge(p, 1, Integer::sum);
        }

        mSymbolRepository.getMatchingSymbols(null, filterMap, 1, new Callback<List<Symbol>, Exception>() {
            @Override
            public void onSuccess(List<Symbol> data) {
                if (!data.isEmpty()) {
                    new Handler(Looper.getMainLooper()).post(() -> startDiscovery(data.get(0)));
                }
            }
            @Override
            public void onFailure(Exception reason) {
                Log.e(TAG, "Manual craft failed", reason);
            }
        });
    }

    public void onPopPressed() {
        if (mDiscoveryInProgress) return;
        if (!mCurrentFilter.isEmpty()) {
            mCurrentFilter.remove(mCurrentFilter.size() - 1);
        }
        CraftingTable table = mCraftingTable.getValue();
        if (table != null) {
            mCraftingTable.setValue(table.removeLast());
        }
    }

    public void dismissCheering() {
        mShowCheering.setValue(false);
        mResultingSymbol.setValue(null);
    }
}
