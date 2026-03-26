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
import pl.polsl.blissapp.ui.repository.SymbolRepository;
import pl.polsl.blissapp.ui.repository.TranslationRepository;

@HiltViewModel
public class NaturalLanguageWriterViewModel extends ViewModel
{
    @Inject
    TranslationRepository mTranslationRepository;
    private final MutableLiveData<List<Translation>> mTranslations = new MutableLiveData<>();
    private final MutableLiveData<Exception> mFailure = new MutableLiveData<>();

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

    void translate(String input)
    {
        var callback = new Callback<List<Translation>, Exception>()
        {
            @Override
            public void onSuccess(List<Translation> data)
            {
                mTranslations.setValue(data);
            }

            @Override
            public void onFailure(Exception data)
            {
                mFailure.setValue(data);
            }
        };
        mTranslationRepository.getTranslations(input, callback);
    }

    void clearTranslations()
    {
        mTranslations.setValue(Collections.emptyList());
    }
}
