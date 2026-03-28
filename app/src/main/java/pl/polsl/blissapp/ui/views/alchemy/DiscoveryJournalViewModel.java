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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

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
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> totalDiscovered = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> currentPage = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> totalPages = new MutableLiveData<>(1);

    private static final int PAGE_SIZE = 12;

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
        loadTotalCount();
    }

    private String getCurrentAppLanguage() {
        LocaleListCompat currentLocales = AppCompatDelegate.getApplicationLocales();
        Locale locale = currentLocales.isEmpty() ? Locale.getDefault() : currentLocales.get(0);
        if (locale == null) return "English";
        String lang = locale.getLanguage();
        return (lang != null && lang.startsWith("pl")) ? "Polish" : "English";
    }

    private void loadTotalCount() {
        alchemyRepository.getDiscoveredCount(new Callback<Integer, Exception>() {
            @Override
            public void onSuccess(Integer count) {
                totalDiscovered.postValue(count);
                int pages = (int) Math.ceil((double) count / PAGE_SIZE);
                totalPages.postValue(pages > 0 ? pages : 1);
                loadPage(0);
            }

            @Override
            public void onFailure(Exception data) {
                totalDiscovered.postValue(0);
                totalPages.postValue(1);
                loadPage(0);
            }
        });
    }

    public void loadPage(int page) {
        if (page < 0) return;
        if (Boolean.TRUE.equals(isLoading.getValue())) return;

        int total = totalDiscovered.getValue() != null ? totalDiscovered.getValue() : 0;
        int pages = totalPages.getValue() != null ? totalPages.getValue() : 1;
        if (page >= pages) return;

        isLoading.postValue(true);
        loadPageInternal(page);
    }

    private void loadPageInternal(int page) {
        int offset = page * PAGE_SIZE;
        alchemyRepository.getDiscoveredPaginated(PAGE_SIZE, offset, new Callback<List<Symbol>, Exception>() {
            @Override
            public void onSuccess(List<Symbol> symbols) {
                if (symbols == null) symbols = Collections.emptyList();

                String currentLang = getCurrentAppLanguage();
                List<JournalItem> newItems = Collections.synchronizedList(new ArrayList<>());
                AtomicInteger counter = new AtomicInteger(0);
                int totalCount = symbols.size();

                if (totalCount == 0) {
                    journalItems.postValue(new ArrayList<>());
                    isLoading.postValue(false);
                    return;
                }

                for (Symbol symbol : symbols) {
                    translationRepository.getMeanings(symbol, currentLang, new Callback<List<String>, Exception>() {
                        @Override
                        public void onSuccess(List<String> meanings) {
                            String label = !meanings.isEmpty() ? meanings.get(0) : "???";
                            addItem(symbol, label, newItems, counter, totalCount);
                        }

                        @Override
                        public void onFailure(Exception reason) {
                            addItem(symbol, "???", newItems, counter, totalCount);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception reason) {
                Log.e("Journal", "Failed to load page", reason);
                isLoading.postValue(false);
            }
        });
    }

    private void addItem(Symbol symbol, String label, List<JournalItem> newItems,
                         AtomicInteger counter, int total) {
        newItems.add(new JournalItem(symbol, label));
        if (counter.incrementAndGet() == total) {
            new Handler(Looper.getMainLooper()).post(() -> {
                journalItems.setValue(new ArrayList<>(newItems));
                isLoading.setValue(false);
            });
        }
    }

    public void nextPage() {
        Integer current = currentPage.getValue();
        Integer total = totalPages.getValue();
        if (current != null && total != null && current + 1 < total) {
            // Set loading synchronously to avoid flicker
            isLoading.setValue(true);
            currentPage.setValue(current + 1);
            loadPageInternal(current + 1);
        }
    }

    public void previousPage() {
        Integer current = currentPage.getValue();
        if (current != null && current > 0) {
            // Set loading synchronously to avoid flicker
            isLoading.setValue(true);
            currentPage.setValue(current - 1);
            loadPageInternal(current - 1);
        }
    }

    public LiveData<List<JournalItem>> getJournalItems() { return journalItems; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Integer> getTotalDiscovered() { return totalDiscovered; }
    public LiveData<Integer> getCurrentPage() { return currentPage; }
    public LiveData<Integer> getTotalPages() { return totalPages; }
}
