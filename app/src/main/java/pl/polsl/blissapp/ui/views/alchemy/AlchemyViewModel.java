package pl.polsl.blissapp.ui.views.alchemy;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import pl.polsl.blissapp.ui.repository.SymbolRepository;

@HiltViewModel
public class AlchemyViewModel extends ViewModel
{
    private SymbolRepository symbolRepository;

    @Inject
    public AlchemyViewModel(SymbolRepository symbolRepository)
    {
        this.symbolRepository = symbolRepository;
    }
}
