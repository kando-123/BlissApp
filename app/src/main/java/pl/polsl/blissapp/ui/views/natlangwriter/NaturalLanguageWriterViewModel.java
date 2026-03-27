package pl.polsl.blissapp.ui.views.natlangwriter;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.Translation;
import pl.polsl.blissapp.ui.repository.TranslationRepository;

@HiltViewModel
public class NaturalLanguageWriterViewModel extends AndroidViewModel
{
    private static final String PREFS_NAME = "BlissAppContentPrefs";
    private static final String PREF_CONTENT_LANGUAGE_KEY = "content_language";
    private static final String DEFAULT_LANGUAGE = "English";

    private final SharedPreferences mPrefs;
    private final SharedPreferences.OnSharedPreferenceChangeListener mPrefListener;
    private final MutableLiveData<String> mSelectedLanguage = new MutableLiveData<>();

    private final TranslationRepository mTranslationRepository;
    
    private final MutableLiveData<List<Translation>> mTranslations = new MutableLiveData<>();
    private final MutableLiveData<Exception> mFailure = new MutableLiveData<>();
    
    // Track the latest search term to avoid race conditions
    private String mCurrentQuery = "";

    @Inject
    public NaturalLanguageWriterViewModel(@NonNull Application application, TranslationRepository translationRepository)
    {
        super(application);
        this.mTranslationRepository = translationRepository;
        this.mPrefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Listen for changes made by the Writer (or other tabs)
        mPrefListener = (prefs, key) -> {
            if (PREF_CONTENT_LANGUAGE_KEY.equals(key)) {
                String lang = prefs.getString(PREF_CONTENT_LANGUAGE_KEY, DEFAULT_LANGUAGE);
                if (!lang.equals(mSelectedLanguage.getValue())) {
                    mSelectedLanguage.postValue(lang);
                    // Trigger your dictionary search again here if needed so the results update instantly!
                }
            }
        };
        mPrefs.registerOnSharedPreferenceChangeListener(mPrefListener);

        // Load the initial language
        String lastLanguage = mPrefs.getString(PREF_CONTENT_LANGUAGE_KEY, DEFAULT_LANGUAGE);
        mSelectedLanguage.setValue(lastLanguage);
    }

    LiveData<List<Translation>> getTranslations()
    {
        return mTranslations;
    }

    LiveData<Exception> getFailure()
    {
        return mFailure;
    }

    void translate(String input)
    {
        final String query = (input == null) ? "" : input;
        mCurrentQuery = query;

        if (query.isEmpty()) {
            mTranslations.setValue(Collections.emptyList());
            return;
        }

        String language = mSelectedLanguage.getValue();
        var callback = new Callback<List<Translation>, Exception>()
        {
            @Override
            public void onSuccess(List<Translation> data)
            {
                // Only post results if this query is still the current one
                if (query.equals(mCurrentQuery)) {
                    mTranslations.postValue(data);
                }
            }

            @Override
            public void onFailure(Exception data)
            {
                if (query.equals(mCurrentQuery)) {
                    mFailure.postValue(data);
                }
            }
        };
        mTranslationRepository.getTranslations(query, language, callback);
    }

    void clearTranslations()
    {
        mCurrentQuery = "";
        mTranslations.setValue(Collections.emptyList());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mPrefs.unregisterOnSharedPreferenceChangeListener(mPrefListener);
    }

    public LiveData<String> getSelectedLanguage() {
        return mSelectedLanguage;
    }

    public void setSelectedLanguage(String language) {
        if (language.equals(mSelectedLanguage.getValue())) return;

        // 1. Instantly update the LiveData so translate() immediately sees the new value
        mSelectedLanguage.setValue(language);

        // 2. Notify the other tabs by saving it to SharedPreferences
        mPrefs.edit().putString(PREF_CONTENT_LANGUAGE_KEY, language).apply();
    }
}
