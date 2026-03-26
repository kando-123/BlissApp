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
    private static final String PREFS_NAME = "NatLangWriterPrefs";
    private static final String PREF_LANGUAGE_KEY = "last_language";
    private static final String DEFAULT_LANGUAGE = "English";

    private final TranslationRepository mTranslationRepository;
    private final SharedPreferences mPrefs;
    
    private final MutableLiveData<List<Translation>> mTranslations = new MutableLiveData<>();
    private final MutableLiveData<Exception> mFailure = new MutableLiveData<>();
    private final MutableLiveData<String> mSelectedLanguage = new MutableLiveData<>();
    
    // Track the latest search term to avoid race conditions
    private String mCurrentQuery = "";

    @Inject
    public NaturalLanguageWriterViewModel(@NonNull Application application, TranslationRepository translationRepository)
    {
        super(application);
        this.mTranslationRepository = translationRepository;
        this.mPrefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        String lastLanguage = mPrefs.getString(PREF_LANGUAGE_KEY, DEFAULT_LANGUAGE);
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

    LiveData<String> getSelectedLanguage()
    {
        return mSelectedLanguage;
    }

    void setSelectedLanguage(String language)
    {
        mSelectedLanguage.setValue(language);
        mPrefs.edit().putString(PREF_LANGUAGE_KEY, language).apply();
    }

    void translate(String input)
    {
        final String query = (input == null) ? "" : input.trim();
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
}
