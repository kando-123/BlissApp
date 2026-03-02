package pl.polsl.blissapp.ui.views.natlangwriter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.MeaningfulSymbol;
import pl.polsl.blissapp.ui.repository.SymbolRepository;

@HiltViewModel
public class NaturalLanguageWriterViewModel extends ViewModel
{
    private final SymbolRepository mSymbolRepository;
    private final MutableLiveData<List<MeaningfulSymbol>> mTranslations = new MutableLiveData<>();
    private final MutableLiveData<Exception> mFailure = new MutableLiveData<>();

    @Inject
    public NaturalLanguageWriterViewModel(SymbolRepository symbolRepository)
    {
        this.mSymbolRepository = symbolRepository;
    }

    LiveData<List<MeaningfulSymbol>> getTranslations()
    {
        return mTranslations;
    }

    LiveData<Exception> getFailure()
    {
        return mFailure;
    }

    void translate(String input)
    {
        var callback = new Callback<List<MeaningfulSymbol>, Exception>()
        {
            @Override
            public void onSuccess(List<MeaningfulSymbol> data)
            {
                mTranslations.setValue(data);
            }

            @Override
            public void onFailure(Exception data)
            {
                mFailure.setValue(data);
            }
        };
        mSymbolRepository.getTranslations(input, callback);
    }

    void clearTranslations()
    {
        mTranslations.setValue(Collections.emptyList());
    }
}
