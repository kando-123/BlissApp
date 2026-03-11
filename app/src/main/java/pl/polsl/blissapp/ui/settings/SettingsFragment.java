package pl.polsl.blissapp.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import pl.polsl.blissapp.R;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        setupLanguageSpinner(root);
        setupThemeSelection(root);

        return root;
    }

    private void setupLanguageSpinner(View root) {
        Spinner spinner = root.findViewById(R.id.spinner_language);
        List<String> languages = new ArrayList<>();
        languages.add("English");
        languages.add("Polish");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupThemeSelection(View root) {
        RadioGroup radioGroup = root.findViewById(R.id.radio_group_color);
        
        // Restore the current selection
        int savedThemeId = ThemeManager.getSavedTheme(requireContext());
        if (savedThemeId == R.style.Theme_BlissApp_Ocean) {
            radioGroup.check(R.id.radio_ocean);
        } else if (savedThemeId == R.style.Theme_BlissApp_Forest) {
            radioGroup.check(R.id.radio_forest);
        } else if (savedThemeId == R.style.Theme_BlissApp_Sunset) {
            radioGroup.check(R.id.radio_sunset);
        } else {
            radioGroup.check(R.id.radio_lavender);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int themeId = R.style.Theme_BlissApp; // Default/Lavender
            if (checkedId == R.id.radio_ocean) {
                themeId = R.style.Theme_BlissApp_Ocean;
            } else if (checkedId == R.id.radio_forest) {
                themeId = R.style.Theme_BlissApp_Forest;
            } else if (checkedId == R.id.radio_sunset) {
                themeId = R.style.Theme_BlissApp_Sunset;
            } else if (checkedId == R.id.radio_lavender) {
                themeId = R.style.Theme_BlissApp_Lavender;
            }
            
            if (themeId != ThemeManager.getSavedTheme(requireContext())) {
                ThemeManager.changeTheme(requireActivity(), themeId);
            }
        });
    }
}
