package pl.polsl.blissapp.ui.views.natlangwriter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import pl.polsl.blissapp.data.model.Translation;
import pl.polsl.blissapp.ui.repository.SymbolRepository;
import pl.polsl.blissapp.ui.repository.TranslationRepository;

@AndroidEntryPoint
public class NaturalLanguageWriterFragment extends Fragment
{
    @Inject
    TranslationRepository mTranslationRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        var viewModel = new ViewModelProvider(this).get(NaturalLanguageWriterViewModel.class);

        viewModel.getTranslations().observe(getViewLifecycleOwner(), translations ->
        {
            for (Translation translation : translations)
            {
                Log.d("Translation", translation.toString());
            }
        });
    }
}
