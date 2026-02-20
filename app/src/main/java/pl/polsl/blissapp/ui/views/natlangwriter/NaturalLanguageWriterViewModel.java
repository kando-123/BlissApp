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
    private final SymbolRepository symbolRepository;
    private final MutableLiveData<List<MeaningfulSymbol>> translations = new MutableLiveData<>();
    private final MutableLiveData<Exception> failure = new MutableLiveData<>();

    @Inject
    public NaturalLanguageWriterViewModel(SymbolRepository symbolRepository)
    {
        this.symbolRepository = symbolRepository;
    }

    LiveData<List<MeaningfulSymbol>> getTranslations()
    {
        return translations;
    }

    LiveData<Exception> getFailure()
    {
        return failure;
    }

    void translate(String input)
    {
        var callback = new Callback<List<MeaningfulSymbol>, Exception>()
        {
            @Override
            public void onSuccess(List<MeaningfulSymbol> data)
            {
                translations.setValue(data);
            }

            @Override
            public void onFailure(Exception data)
            {
                failure.setValue(data);
            }
        };
        symbolRepository.getTranslations(input, callback);
    }

    void clearTranslations()
    {
        translations.setValue(Collections.emptyList());
    }
}
