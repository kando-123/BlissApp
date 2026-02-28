package pl.polsl.blissapp.ui.views.keyboard;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import pl.polsl.blissapp.R;
import pl.polsl.blissapp.data.model.Indicator;
import pl.polsl.blissapp.data.model.Radical;
import pl.polsl.blissapp.ui.mapping.DrawableMapper;

@AndroidEntryPoint
public class BlissKeyboardFragment extends Fragment {

    private BlissKeyboardViewModel viewModel;
    private final List<View> radicalButtons = new ArrayList<>();
    private PopupWindow currentPopupWindow;

    private boolean isIndicatorMode = false;

    // Optimizations: Cache these to prevent repetitive lookups
    private ImageButton indicatorModeButton;

    private static final int VARIANTS_PER_ROW = 6;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Fragment parent = getParentFragment();
        if (parent != null) {
            viewModel = new ViewModelProvider(parent).get(BlissKeyboardViewModel.class);
        } else {
            viewModel = new ViewModelProvider(requireActivity()).get(BlissKeyboardViewModel.class);
        }
        return createKeyboardView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (currentPopupWindow != null && currentPopupWindow.isShowing()) {
            currentPopupWindow.dismiss();
        }
        currentPopupWindow = null;
    }

    // --- KEYBOARD UI GENERATION ---

    private View createKeyboardView() {
        LinearLayout keyboardLayout = new LinearLayout(getContext());
        keyboardLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        keyboardLayout.setOrientation(LinearLayout.VERTICAL);
        keyboardLayout.setGravity(Gravity.BOTTOM);
        keyboardLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.keyboard_background));
        int padding = getResources().getDimensionPixelSize(R.dimen.keyboard_padding);
        keyboardLayout.setPadding(padding, padding, padding, padding);

        for (List<KeyUI> rowConfig : getKeyboardRows()) {
            keyboardLayout.addView(createRowView(rowConfig));
        }

        return keyboardLayout;
    }

    private View createRowView(List<KeyUI> rowConfig) {
        LinearLayout rowLayout = new LinearLayout(getContext());
        rowLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        rowLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        for (KeyUI keyConfig : rowConfig) {
            View keyView = createKeyView(keyConfig);
            setupKeyLogic(keyView, keyConfig);
            rowLayout.addView(keyView);
        }
        return rowLayout;
    }

    private View createKeyView(KeyUI keyConfig) {
        int keyHeight = getResources().getDimensionPixelSize(R.dimen.keyboard_key_height);
        int keyMargin = getResources().getDimensionPixelSize(R.dimen.keyboard_key_margin);
        int keyPadding = getResources().getDimensionPixelSize(R.dimen.keyboard_key_padding);

        if (keyConfig instanceof BlissKeyUI) {
            ImageButton imageButton = new ImageButton(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, keyHeight, keyConfig.weight);
            params.setMargins(keyMargin, keyMargin, keyMargin, keyMargin);
            imageButton.setLayoutParams(params);

            imageButton.setImageResource(DrawableMapper.getDrawableRes(((BlissKeyUI) keyConfig).baseRadical));
            imageButton.setContentDescription(((BlissKeyUI) keyConfig).baseRadical.name());
            imageButton.setBackgroundResource(R.drawable.key_background);
            imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageButton.setPadding(keyPadding, keyPadding, keyPadding, keyPadding);

            radicalButtons.add(imageButton);
            return imageButton;

        } else if (keyConfig instanceof ControlKeyUI) {
            ControlKey action = ((ControlKeyUI) keyConfig).action;
            ImageButton imageButton = new ImageButton(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, keyHeight, keyConfig.weight);
            params.setMargins(keyMargin, keyMargin, keyMargin, keyMargin);
            imageButton.setLayoutParams(params);
            imageButton.setBackgroundResource(R.drawable.key_background);
            imageButton.setContentDescription(action.name());
            imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageButton.setPadding(keyPadding, keyPadding, keyPadding, keyPadding);

            switch (action) {
                case POP_SYMBOL:
                    imageButton.setImageResource(android.R.drawable.ic_input_delete);
                    imageButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.black), PorterDuff.Mode.SRC_ATOP);
                    break;
                case INDICATOR_MODE:
                    imageButton.setImageResource(R.drawable.indicator_placeholder);
                    imageButton.setBackgroundResource(R.drawable.indicator_key_background);
                    indicatorModeButton = imageButton;
                    break;
                case PUSH_SYMBOL:
                    imageButton.setImageResource(android.R.drawable.ic_menu_add);
                    break;
                case ENTER:
                    imageButton.setImageResource(android.R.drawable.ic_menu_send);
                    break;
            }
            return imageButton;
        }
        throw new IllegalArgumentException("Unknown KeyUI type");
    }

    // --- KEYBOARD CONFIGURATION DSL ---

    private BlissKeyUI key(Radical r) { return new BlissKeyUI(r, null, 1f); }
    private BlissKeyUI key(Radical r, Indicator i) { return new BlissKeyUI(r, i, 1f); }
    private ControlKeyUI ctrl(ControlKey c, float weight) { return new ControlKeyUI(c, weight); }

    private List<List<KeyUI>> getKeyboardRows() {
        return Arrays.asList(
                // ROW 1
                Arrays.asList(
                        key(Radical.SEMICIRCLE),
                        key(Radical.ARC),
                        key(Radical.ARROW),
                        key(Radical.BUILDING),
                        key(Radical.CROSSHATCH),
                        key(Radical.CROSS),
                        key(Radical.POINTER),
                        ctrl(ControlKey.POP_SYMBOL, 1.5f)
                ),
                // ROW 2
                Arrays.asList(
                        key(Radical.EAR),
                        key(Radical.HEART),
                        key(Radical.OPEN_RECTANGLE),
                        key(Radical.RECTANGLE, Indicator.PLURAL),
                        key(Radical.OPEN_SQUARE),
                        key(Radical.SQUARE, Indicator.THING),
                        key(Radical.DOT, Indicator.DOT),
                        key(Radical.CIRCLE),
                        key(Radical.WHEEL)
                ),
                // ROW 3
                Arrays.asList(
                        key(Radical.ACUTE_ANGLE),
                        key(Radical.ACUTE_TRIANGLE),
                        key(Radical.RIGHT_ANGLE),
                        key(Radical.RIGHT_TRIANGLE),
                        key(Radical.PIN),
                        key(Radical.WAVY_LINE),
                        key(Radical.HORIZONTAL_LINE, Indicator.PAST_ACTION),
                        key(Radical.VERTICAL_LINE, Indicator.FUTURE_ACTION),
                        key(Radical.DIAGONAL_LINE)
                ),
                // ROW 4 (Controls)
                Arrays.asList(
                        ctrl(ControlKey.INDICATOR_MODE, 1.5f),
                        ctrl(ControlKey.PUSH_SYMBOL, 4f),
                        ctrl(ControlKey.ENTER, 1.5f)
                )
        );
    }

    // --- EVENT HANDLING LOGIC ---

    private void setupKeyLogic(View buttonView, KeyUI blueprint) {
        buttonView.setTag(blueprint);

        if (blueprint instanceof BlissKeyUI blissKey) {
            final List<Radical> variants = Radical.getChildren(blissKey.baseRadical);

            // Standard Click
            buttonView.setOnClickListener(v -> {
                if (isIndicatorMode) {
                    if (blissKey.indicator != null) { // Direct null check clears IDE warning
                        viewModel.onIndicatorKeyTapped(blissKey.indicator);

                        // Turn indicator mode off automatically using the cached button
                        if (indicatorModeButton != null && isIndicatorMode) {
                            toggleIndicatorMode(indicatorModeButton);
                        }
                    }
                } else {
                    viewModel.onRadicalKeyTapped(blissKey.baseRadical);
                }
            });

            // Long Click Variants (Disabled if Indicator mode is ON)
            if (!variants.isEmpty()) {
                buttonView.setOnLongClickListener(v -> {
                    if (!isIndicatorMode) {
                        showVariantsPopup(buttonView, variants);
                        return true;
                    }
                    return false;
                });
            } else {
                buttonView.setLongClickable(false);
            }
        } else if (blueprint instanceof ControlKeyUI controlKey) {
            buttonView.setOnClickListener(v -> {
                if (controlKey.action == ControlKey.INDICATOR_MODE) {
                    toggleIndicatorMode((ImageButton) buttonView);
                } else {
                    viewModel.onControlKeyTapped(controlKey.action);
                }
            });
            buttonView.setLongClickable(false);
        }
    }

    private void toggleIndicatorMode(ImageButton shiftButton) {
        isIndicatorMode = !isIndicatorMode;

        // Visual toggle for the SHIFT button
        shiftButton.setActivated(isIndicatorMode);

        // Loop through all Radical buttons and update their appearance
        for (View btn : radicalButtons) {
            BlissKeyUI blueprint = (BlissKeyUI) btn.getTag();
            ImageButton imgBtn = (ImageButton) btn;

            if (isIndicatorMode) {
                if (blueprint.indicator != null) { // Direct null check clears IDE warning
                    imgBtn.setImageResource(DrawableMapper.getDrawableRes(blueprint.indicator));
                    imgBtn.setAlpha(1.0f);
                    imgBtn.setEnabled(true);
                } else {
                    // Disable keys that have a NULL indicator
                    imgBtn.setAlpha(0.25f);
                    imgBtn.setEnabled(false);
                }
            } else {
                imgBtn.setImageResource(DrawableMapper.getDrawableRes(blueprint.baseRadical));
                imgBtn.setAlpha(1.0f);
                imgBtn.setEnabled(true);
            }
        }
    }

    private void showVariantsPopup(View anchorView, List<Radical> variants) {
        Context context = getContext();
        if (context == null) return;

        int popupOffset = getResources().getDimensionPixelSize(R.dimen.keyboard_popup_offset);

        ViewGroup root = (ViewGroup) anchorView.getRootView();
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_bliss_variants, root, false);
        LinearLayout container = popupView.findViewById(R.id.popup_container);

        currentPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        currentPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LinearLayout currentRow = null;
        for (int i = 0; i < variants.size(); i++) {
            if (i % VARIANTS_PER_ROW == 0) {
                currentRow = new LinearLayout(context);
                currentRow.setOrientation(LinearLayout.HORIZONTAL);
                container.addView(currentRow);
            }
            ImageButton btn = getVariantButton(context, variants.get(i), currentPopupWindow);
            currentRow.addView(btn);
        }

        // Measure the popup and display it
        popupView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int popupHeight = popupView.getMeasuredHeight();
        int popupWidth = popupView.getMeasuredWidth();

        int xOffset = (anchorView.getWidth() - popupWidth) / 2;
        int yOffset = -anchorView.getHeight() - popupHeight - popupOffset;

        currentPopupWindow.showAsDropDown(anchorView, xOffset, yOffset);
    }

    @NonNull
    private ImageButton getVariantButton(@NonNull Context context, Radical variant, PopupWindow popupWindow) {
        int keyHeight = getResources().getDimensionPixelSize(R.dimen.keyboard_key_height);
        int keyMargin = getResources().getDimensionPixelSize(R.dimen.keyboard_key_margin);
        int keyPadding = getResources().getDimensionPixelSize(R.dimen.keyboard_key_padding);

        ImageButton btn = new ImageButton(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(keyHeight, keyHeight);
        params.setMargins(keyMargin, 0, keyMargin, 0);
        btn.setLayoutParams(params);

        btn.setImageResource(DrawableMapper.getDrawableRes(variant));
        btn.setContentDescription(variant.name());
        btn.setBackgroundResource(R.drawable.key_background);
        btn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        btn.setPadding(keyPadding, keyPadding, keyPadding, keyPadding);

        btn.setOnClickListener(v -> {
            viewModel.onRadicalKeyTapped(variant);
            popupWindow.dismiss();
        });
        return btn;
    }
}
