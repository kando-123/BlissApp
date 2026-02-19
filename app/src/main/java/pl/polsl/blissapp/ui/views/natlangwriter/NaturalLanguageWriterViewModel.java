package pl.polsl.blissapp.ui.views.natlangwriter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.ui.repository.SymbolRepository;

public class NaturalLanguageWriterViewModel extends ViewModel
{
    private final SymbolRepository symbolRepository;
    private final MutableLiveData<List<Symbol>> translations = new MutableLiveData<>();
    private final MutableLiveData<Exception> failure = new MutableLiveData<>();

    public NaturalLanguageWriterViewModel(SymbolRepository symbolRepository)
    {
        this.symbolRepository = symbolRepository;
    }

    LiveData<List<Symbol>> getTranslations()
    {
        return translations;
    }

    LiveData<Exception> getFailure()
    {
        return failure;
    }

    void translate(String input)
    {
        var callback = new Callback<List<Symbol>, Exception>()
        {
            @Override
            public void onSuccess(List<Symbol> data)
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
}
