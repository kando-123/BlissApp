package pl.polsl.blissapp.ui.views.keyboard;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.polsl.blissapp.R;
import pl.polsl.blissapp.common.Radical;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BlissKeyboardFragment extends Fragment
{
    private BlissKeyboardViewModel viewModel;
    private final List<View> radicalButtons = new ArrayList<>();
    private boolean isIndicatorMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bliss_keyboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(BlissKeyboardViewModel.class);

        for (KeyUI keyBlueprint : getKeyboardConfiguration()) {
            View buttonView = view.findViewById(keyBlueprint.viewId);
            if (buttonView != null) {
                setupKeyLogic(buttonView, keyBlueprint);
            }
        }
    }

    private List<KeyUI> getKeyboardConfiguration() {
        return Arrays.asList(
                // --- ROW 1 ---
                new BlissKeyUI(R.id.key_arc, Radical.ARC, List.of(Radical.ARC_NORTH, Radical.ARC_SOUTH, Radical.ARC_EAST, Radical.ARC_WEST)),
                new BlissKeyUI(R.id.key_arrow, Radical.ARROW, List.of(Radical.ARROW_NORTH, Radical.ARROW_SOUTH)),
                new BlissKeyUI(R.id.key_building, Radical.BUILDING, null),
                new BlissKeyUI(R.id.key_cross_hatches, Radical.CROSSHATCH, List.of(Radical.CROSSHATCH_STRAIGHT, Radical.CROSSHATCH_PITCHED)),
                new BlissKeyUI(R.id.key_pointer, Radical.POINTER, List.of(Radical.POINTER_NORTH, Radical.POINTER_SOUTH)),
                new BlissKeyUI(R.id.key_punctuation, Radical.PUNCTUATION, null),
                new BlissKeyUI(R.id.key_digit, Radical.DIGIT, null),
                new ControlKeyUI(R.id.key_backspace, ControlKey.POP_SYMBOL),

                // --- ROW 2 ---
                new BlissKeyUI(R.id.key_ear, Radical.EAR, null),
                new BlissKeyUI(R.id.key_heart, Radical.HEART, null),
                new BlissKeyUI(R.id.key_open_rectangle, Radical.OPEN_RECTANGLE, List.of(Radical.OPEN_RECTANGLE_NORTH, Radical.OPEN_RECTANGLE_SOUTH_VERTICAL)),
                new BlissKeyUI(R.id.key_rectangle, Radical.RECTANGLE, Radical.RECTANGLE_LARGE, List.of(Radical.RECTANGLE_SMALL)), // Example Indicator!
                new BlissKeyUI(R.id.key_open_square, Radical.OPEN_SQUARE, List.of(Radical.OPEN_SQUARE_LARGE_NORTH)),
                new BlissKeyUI(R.id.key_square, Radical.SQUARE, Radical.SQUARE_LARGE, List.of(Radical.SQUARE_SMALL)), // Example Indicator!
                new BlissKeyUI(R.id.key_dot, Radical.DOT, null),
                new BlissKeyUI(R.id.key_small_circle, Radical.CIRCLE_SMALL, null),
                new BlissKeyUI(R.id.key_wheel, Radical.WHEEL, null),

                // --- ROW 3 ---
                new BlissKeyUI(R.id.key_acute_angle, Radical.ACUTE_ANGLE, List.of(Radical.ACUTE_ANGLE_LARGE_NORTH)),
                new BlissKeyUI(R.id.key_acute_triangle, Radical.ACUTE_TRIANGLE, List.of(Radical.ACUTE_TRIANGLE_LARGE_NORTH)),
                new BlissKeyUI(R.id.key_right_angle, Radical.RIGHT_ANGLE, List.of(Radical.RIGHT_ANGLE_LARGE_NORTH)),
                new BlissKeyUI(R.id.key_right_triangle, Radical.RIGHT_TRIANGLE, List.of(Radical.RIGHT_TRIANGLE_LARGE_NORTH)),
                new BlissKeyUI(R.id.key_wave, Radical.WAVY_LINE, List.of(Radical.WAVY_LINE_HORIZONTAL, Radical.WAVY_LINE_VERTICAL)),
                new BlissKeyUI(R.id.key_horizontal_line, Radical.HORIZONTAL_LINE, Radical.HORIZONTAL_LINE_LARGE, List.of(Radical.HORIZONTAL_LINE_SMALL)), // Indicator
                new BlissKeyUI(R.id.key_vertical_line, Radical.VERTICAL_LINE, Radical.VERTICAL_LINE_LARGE, List.of(Radical.VERTICAL_LINE_SMALL)), // Indicator
                new BlissKeyUI(R.id.key_diagonal_line, Radical.DIAGONAL_LINE, List.of(Radical.DIAGONAL_LINE_LARGE_NORTHEAST, Radical.SLASH)),

                // --- ROW 4 (Controls) ---
                new ControlKeyUI(R.id.key_shift, ControlKey.INDICATOR_MODE),
                new ControlKeyUI(R.id.key_space, ControlKey.PUSH_SYMBOL),
                new ControlKeyUI(R.id.key_enter, ControlKey.ENTER)
        );
    }

    private void setupKeyLogic(View buttonView, KeyUI blueprint) {
        buttonView.setTag(blueprint);

        if (blueprint instanceof BlissKeyUI blissKey) {
            radicalButtons.add(buttonView);

            // Standard Click
            buttonView.setOnClickListener(v -> {
                Radical radicalToSend = (isIndicatorMode && blissKey.isIndicator())
                        ? blissKey.indicatorRadical
                        : blissKey.baseRadical;
                viewModel.onRadicalKeyTapped(radicalToSend);

                if(isIndicatorMode){
                    toggleIndicatorMode((Button) buttonView);
                }
            });

            // Long Click Variants (Disabled if Shift/Indicator mode is ON)
            if (blissKey.hasVariants()) {
                buttonView.setOnLongClickListener(v -> {
                    if (!isIndicatorMode) {
                        showVariantsPopup(buttonView, blissKey.variants);
                        return true;
                    }
                    return false;
                });
            }
        } else if (blueprint instanceof ControlKeyUI controlKey) {

            buttonView.setOnClickListener(v -> {
                if (controlKey.action == ControlKey.INDICATOR_MODE) {
                    toggleIndicatorMode((Button) buttonView);
                } else {
                    viewModel.onControlKeyTapped(controlKey.action);
                }
            });
        }
    }

    private void toggleIndicatorMode(Button shiftButton) {
        isIndicatorMode = !isIndicatorMode;

        // Visual toggle for the SHIFT button
        shiftButton.setBackgroundColor(isIndicatorMode ? Color.LTGRAY : Color.TRANSPARENT);
        shiftButton.setText(isIndicatorMode ? "SHIFT (ON)" : "SHIFT");

        // Loop through all Radical buttons and update their appearance
        for (View btn : radicalButtons) {
            BlissKeyUI blueprint = (BlissKeyUI) btn.getTag();
            ImageButton imgBtn = (ImageButton) btn;

            if (isIndicatorMode) {
                if (blueprint.isIndicator()) {
                    // It is an indicator key: Swap the image to the indicator version
                    imgBtn.setImageResource(blueprint.indicatorRadical.getDrawableRes());
                    imgBtn.setAlpha(1.0f);
                    imgBtn.setEnabled(true);
                } else {
                    // Not an indicator key: Dim and disable it
                    imgBtn.setAlpha(0.3f);
                    imgBtn.setEnabled(false);
                }
            } else {
                // Shift is OFF: Revert to normal base images
                imgBtn.setImageResource(blueprint.baseRadical.getDrawableRes());
                imgBtn.setAlpha(1.0f);
                imgBtn.setEnabled(true);
            }
        }
    }

    private void showVariantsPopup(View anchorView, List<Radical> variants) {
        Context context = getContext();
        if (context == null) {
            return;
        }
        LinearLayout container = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.popup_bliss_variants, null);

        PopupWindow popupWindow = new PopupWindow(container, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Add the variant buttons dynamically
        for (Radical variant : variants) {
            ImageButton btn = getImageButton(context, variant, popupWindow);
            container.addView(btn);
        }

        // Show the popup anchored above the button that was long-pressed
        popupWindow.showAsDropDown(anchorView, 0, -150, Gravity.CENTER_HORIZONTAL);
    }

    @NonNull
    private ImageButton getImageButton(@NonNull Context context, Radical variant, PopupWindow popupWindow) {
        ImageButton btn = new ImageButton(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120, 120);
        params.setMargins(8, 0, 8, 0);
        btn.setLayoutParams(params);

        btn.setImageResource(variant.getDrawableRes());
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        btn.setBackgroundResource(outValue.resourceId);
        btn.setScaleType(ImageButton.ScaleType.FIT_CENTER);
        btn.setPadding(16, 16, 16, 16);

        btn.setOnClickListener(v -> {
            viewModel.onRadicalKeyTapped(variant);
            popupWindow.dismiss();
        });
        return btn;
    }
}
