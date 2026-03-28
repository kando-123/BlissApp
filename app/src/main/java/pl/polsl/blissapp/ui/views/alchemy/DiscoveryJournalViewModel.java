package pl.polsl.blissapp.ui.views.alchemy;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.ui.repository.AlchemyRepository;
import pl.polsl.blissapp.ui.repository.TranslationRepository;

@HiltViewModel
public class DiscoveryJournalViewModel extends AndroidViewModel {

    private final AlchemyRepository alchemyRepository;
    private final TranslationRepository translationRepository;

    private final MutableLiveData<List<JournalItem>> journalItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);

    public static class JournalItem {
        public final Symbol symbol;
        public final String label;

        public JournalItem(Symbol symbol, String label) {
            this.symbol = symbol;
            this.label = label;
        }
    }

    @Inject
    public DiscoveryJournalViewModel(@NonNull Application application,
                                     AlchemyRepository alchemyRepository,
                                     TranslationRepository translationRepository) {
        super(application);
        this.alchemyRepository = alchemyRepository;
        this.translationRepository = translationRepository;
        loadDiscoveredSymbols();
    }

    private String getCurrentAppLanguage() {
        LocaleListCompat currentLocales = AppCompatDelegate.getApplicationLocales();
        Locale locale = currentLocales.isEmpty() ? Locale.getDefault() : currentLocales.get(0);
        if (locale == null) return "English";
        String lang = locale.getLanguage();
        return (lang != null && lang.startsWith("pl")) ? "Polish" : "English";
    }

    private void loadDiscoveredSymbols() {
        isLoading.setValue(true);
        alchemyRepository.getDiscovered(new Callback<List<Symbol>, Exception>() {
            @Override
            public void onSuccess(List<Symbol> symbols) {
                Log.d("Journal", "getDiscovered success, symbols size: " + (symbols != null ? symbols.size() : "null"));
                if (symbols == null || symbols.isEmpty()) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        journalItems.setValue(new ArrayList<>());
                        isLoading.setValue(false);
                    });
                    return;
                }

                String currentLang = getCurrentAppLanguage();

                // Use a Thread-safe list and an Atomic counter
                List<JournalItem> items = java.util.Collections.synchronizedList(new ArrayList<>());
                java.util.concurrent.atomic.AtomicInteger counter = new java.util.concurrent.atomic.AtomicInteger(0);
                final int total = symbols.size();

                for (Symbol symbol : symbols) {
                    translationRepository.getMeanings(symbol, currentLang, new Callback<List<String>, Exception>() {
                        @Override
                        public void onSuccess(List<String> meanings) {
                            String label = !meanings.isEmpty() ? meanings.get(0) : "???";
                            items.add(new JournalItem(symbol, label));

                            if (counter.incrementAndGet() == total) {
                                finalizeList(items);
                            }
                        }

                        @Override
                        public void onFailure(Exception reason) {
                            items.add(new JournalItem(symbol, "???"));

                            if (counter.incrementAndGet() == total) {
                                finalizeList(items);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception reason) {
                Log.e("Journal", "getDiscovered failed", reason);
                new Handler(Looper.getMainLooper()).post(() -> {
                    journalItems.setValue(new ArrayList<>());
                    isLoading.setValue(false);
                });
            }
        });
    }

    private void finalizeList(List<JournalItem> items) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Log.d("Journal", "Discovered symbols count: " + items.size());
            journalItems.setValue(new ArrayList<>(items));
            isLoading.setValue(false);
        });
    }

    public LiveData<List<JournalItem>> getJournalItems() { return journalItems; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
}
