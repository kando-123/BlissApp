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
        
        // Restore the current selection without triggering the listener
        String savedThemeKey = ThemeManager.getSavedThemeKey(requireContext());
        if (ThemeManager.THEME_OCEAN.equals(savedThemeKey)) {
            radioGroup.check(R.id.radio_ocean);
        } else if (ThemeManager.THEME_FOREST.equals(savedThemeKey)) {
            radioGroup.check(R.id.radio_forest);
        } else if (ThemeManager.THEME_SUNSET.equals(savedThemeKey)) {
            radioGroup.check(R.id.radio_sunset);
        } else {
            radioGroup.check(R.id.radio_lavender);
        }

        // Set the listener AFTER restoring the state to avoid immediate recreation loop
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String themeKey = ThemeManager.THEME_LAVENDER;
            if (checkedId == R.id.radio_ocean) {
                themeKey = ThemeManager.THEME_OCEAN;
            } else if (checkedId == R.id.radio_forest) {
                themeKey = ThemeManager.THEME_FOREST;
            } else if (checkedId == R.id.radio_sunset) {
                themeKey = ThemeManager.THEME_SUNSET;
            } else if (checkedId == R.id.radio_lavender) {
                themeKey = ThemeManager.THEME_LAVENDER;
            }
            
            // Only change if the theme is actually different
            if (!themeKey.equals(ThemeManager.getSavedThemeKey(requireContext()))) {
                ThemeManager.changeTheme(requireActivity(), themeKey);
            }
        });
    }
}
