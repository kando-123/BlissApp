package pl.polsl.blissapp.ui.views.natlangwriter;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import pl.polsl.blissapp.R;
import pl.polsl.blissapp.ui.repository.SymbolRepository;

@AndroidEntryPoint
public class NaturalLanguageWriterFragment extends Fragment
{
    private NaturalLanguageWriterViewModel mViewModel;
    private TranslationAdapter mAdapter;
    private TextInputEditText mEtSearch;

    @Inject
    SymbolRepository mSymbolRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_natural_language_writer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(NaturalLanguageWriterViewModel.class);

        mEtSearch = view.findViewById(R.id.et_search);
        RecyclerView rvResults = view.findViewById(R.id.rv_results);

        mAdapter = new TranslationAdapter(mSymbolRepository);
        rvResults.setAdapter(mAdapter);
        rvResults.setLayoutManager(new LinearLayoutManager(requireContext()));

        setupSearchLogic();
        setupMenu();

        mViewModel.getTranslations().observe(getViewLifecycleOwner(), translations ->
                mAdapter.submitList(translations));

        mViewModel.getFailure().observe(getViewLifecycleOwner(), exception -> {
            if (exception != null && isAdded()) {
                String message = exception.getMessage();
                if (message != null && !message.isEmpty()) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Request focus and show keyboard
        showKeyboard();
    }

    private void showKeyboard() {
        if (mEtSearch != null) {
            mEtSearch.requestFocus();
            mEtSearch.post(() -> {
                if (isAdded()) {
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(mEtSearch, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            });
        }
    }

    private void setupSearchLogic()
    {
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewModel.translate(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Ensure keyboard shows up when clicking the field manually
        mEtSearch.setOnClickListener(v -> showKeyboard());
    }

    private void setupMenu()
    {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_natural_language_writer, menu);
                MenuItem item = menu.findItem(R.id.action_language_selector);
                if (item != null) {
                    View view = item.getActionView();
                    if (view instanceof Spinner spinner) {
                        setupLanguageSpinner(spinner);
                    }
                }
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void setupLanguageSpinner(Spinner spinner)
    {
        String[] languages = {getString(R.string.language_english), getString(R.string.language_polish)};
        String[] values = {"English", "Polish"};
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                R.layout.item_language_selected, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        mViewModel.getSelectedLanguage().observe(getViewLifecycleOwner(), language -> {
            for (int i = 0; i < values.length; i++) {
                if (values[i].equals(language)) {
                    spinner.setSelection(i);
                    break;
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue = values[position];
                if (!selectedValue.equals(mViewModel.getSelectedLanguage().getValue())) {
                    mViewModel.setSelectedLanguage(selectedValue);
                    if (mEtSearch != null) {
                        mViewModel.translate(mEtSearch.getText().toString());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}
