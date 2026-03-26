package pl.polsl.blissapp.ui.views.natlangwriter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.Translation;
import pl.polsl.blissapp.ui.repository.TranslationRepository;

@HiltViewModel
public class NaturalLanguageWriterViewModel extends ViewModel
{
    private final TranslationRepository mTranslationRepository;
    private final MutableLiveData<List<Translation>> mTranslations = new MutableLiveData<>();
    private final MutableLiveData<Exception> mFailure = new MutableLiveData<>();
    private final MutableLiveData<String> mSelectedLanguage = new MutableLiveData<>("English");
    
    // Track the latest search term to avoid race conditions
    private String mCurrentQuery = "";

    @Inject
    public NaturalLanguageWriterViewModel(TranslationRepository translationRepository)
    {
        this.mTranslationRepository = translationRepository;
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
