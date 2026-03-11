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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import pl.polsl.blissapp.R;
import pl.polsl.blissapp.data.model.Primitive;
import pl.polsl.blissapp.ui.mapping.DrawableMapper;

@AndroidEntryPoint
public class BlissKeyboardFragment extends Fragment
{
    private static final int VARIANTS_PER_ROW = 6;

    private BlissKeyboardViewModel mViewModel;
    private final List<View> mRadicalButtons = new ArrayList<>();
    private final List<View> mAlphanumericButtons = new ArrayList<>();
    private final List<View> mShiftButtons = new ArrayList<>();  // for both keyboards (Issue #9)
    private boolean mIsShiftMode = false;
    private PopupWindow mCurrentPopupWindow;
    private LinearLayout mKeyboardContainer;
    private View mBlissKeyboard;   // pre‑built bliss layout
    private View mAlphaKeyboard;   // pre‑built alphanumeric layout

    public BlissKeyboardViewModel getViewModel()
    {
        return mViewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        Fragment parent = getParentFragment();
        mViewModel = new ViewModelProvider(parent != null ? parent : this)
                .get(BlissKeyboardViewModel.class);

        mKeyboardContainer = new LinearLayout(getContext());
        mKeyboardContainer.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mKeyboardContainer.setOrientation(LinearLayout.VERTICAL);
        mKeyboardContainer.setGravity(Gravity.BOTTOM);
        mKeyboardContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.keyboard_background));
        int padding = getResources().getDimensionPixelSize(R.dimen.keyboard_padding);
        mKeyboardContainer.setPadding(padding, padding, padding, padding);

        // Pre‑build both keyboards (Issue #9)
        mBlissKeyboard = createBlissKeyboard();
        mAlphaKeyboard = createAlphanumericKeyboard();
        mKeyboardContainer.addView(mBlissKeyboard);
        mKeyboardContainer.addView(mAlphaKeyboard);

        // Observe mode and toggle visibility
        mViewModel.getKeyboardMode().observe(getViewLifecycleOwner(), mode -> {
            // Reset shift mode when switching keyboards
            if (mIsShiftMode) {
                toggleShiftMode();
            }
            if (mode == BlissKeyboardViewModel.KeyboardMode.BLISS) {
                mBlissKeyboard.setVisibility(View.VISIBLE);
                mAlphaKeyboard.setVisibility(View.GONE);
            } else {
                mBlissKeyboard.setVisibility(View.GONE);
                mAlphaKeyboard.setVisibility(View.VISIBLE);
            }
        });

        return mKeyboardContainer;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if (mCurrentPopupWindow != null && mCurrentPopupWindow.isShowing())
        {
            mCurrentPopupWindow.dismiss();
        }
        mCurrentPopupWindow = null;
    }

    // ------------------- Keyboard Creation Methods -------------------

    private View createBlissKeyboard() {
        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        for (List<KeyUI> rowConfig : getBlissKeyboardRows()) {
            root.addView(createRowView(rowConfig));
        }
        return root;
    }

    private View createAlphanumericKeyboard() {
        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        for (List<KeyUI> rowConfig : getAlphanumericKeyboardRows()) {
            root.addView(createRowView(rowConfig));
        }
        return root;
    }

    private View createRowView(List<KeyUI> rowConfig)
    {
        LinearLayout rowLayout = new LinearLayout(getContext());
        rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        rowLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        for (KeyUI keyConfig : rowConfig)
        {
            View keyView = createKeyView(keyConfig);
            setupKeyLogic(keyView, keyConfig);
            rowLayout.addView(keyView);
        }
        return rowLayout;
    }

    private View createKeyView(KeyUI keyConfig)
    {
        int keyHeight = getResources().getDimensionPixelSize(R.dimen.keyboard_key_height);
        int keyMargin = getResources().getDimensionPixelSize(R.dimen.keyboard_key_margin);
        int keyPadding = getResources().getDimensionPixelSize(R.dimen.keyboard_key_padding);

        if (keyConfig instanceof BlissKeyUI blissKey)
        {
            ImageButton imageButton = new ImageButton(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, keyHeight, keyConfig.weight);
            params.setMargins(keyMargin, keyMargin, keyMargin, keyMargin);
            imageButton.setLayoutParams(params);

            imageButton.setImageResource(DrawableMapper.getDrawableRes(blissKey.basePrimitive));
            imageButton.setContentDescription(blissKey.basePrimitive.name());
            imageButton.setBackgroundResource(R.drawable.key_background);
            imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageButton.setPadding(keyPadding, keyPadding, keyPadding, keyPadding);

            mRadicalButtons.add(imageButton);
            return imageButton;
        }
        else if (keyConfig instanceof AlphanumericKeyUI alphaKey)
        {
            FrameLayout frameLayout = new FrameLayout(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, keyHeight, keyConfig.weight);
            params.setMargins(keyMargin, keyMargin, keyMargin, keyMargin);
            frameLayout.setLayoutParams(params);
            frameLayout.setBackgroundResource(R.drawable.key_background);

            TextView mainText = new TextView(getContext());
            mainText.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mainText.setGravity(Gravity.CENTER);
            mainText.setText(alphaKey.letter.getLetterLabel());
            mainText.setTextColor(Color.BLACK);
            mainText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            frameLayout.addView(mainText);

            if (alphaKey.alternativeDigit != null) {
                ImageView altImage = new ImageView(getContext());
                int altSize = (int)(keyHeight/1.5f);
                FrameLayout.LayoutParams altParams = new FrameLayout.LayoutParams(altSize, altSize);
                altParams.gravity = Gravity.TOP | Gravity.END;
                // Use dimension resources instead of hardcoded pixels (Issue #6)
                int offsetX = getResources().getDimensionPixelOffset(R.dimen.keyboard_alt_image_offset_x);
                int offsetY = getResources().getDimensionPixelOffset(R.dimen.keyboard_alt_image_offset_y);
                altParams.setMargins(0, offsetY, offsetX, 0);
                altImage.setLayoutParams(altParams);
                altImage.setImageResource(DrawableMapper.getDrawableRes(alphaKey.alternativeDigit));
                altImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                altImage.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                frameLayout.addView(altImage);
            }

            mAlphanumericButtons.add(frameLayout);
            return frameLayout;
        }
        else if (keyConfig instanceof ControlKeyUI controlKey)
        {
            ControlKey action = controlKey.action;
            View keyView;

            if (action == ControlKey.SWITCH_TO_ALPHANUMERIC || action == ControlKey.SWITCH_TO_BLISS) {
                FrameLayout frameLayout = new FrameLayout(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        0, keyHeight, keyConfig.weight);
                params.setMargins(keyMargin, keyMargin, keyMargin, keyMargin);
                frameLayout.setLayoutParams(params);
                frameLayout.setBackgroundResource(R.drawable.control_key_background);

                TextView textView = new TextView(getContext());
                textView.setLayoutParams(new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                textView.setGravity(Gravity.CENTER);
                switch (action)
                {
                    case SWITCH_TO_ALPHANUMERIC:
                        textView.setText("ABC");
                        break;
                    case SWITCH_TO_BLISS:
                        textView.setText("BLISS");
                        break;
                }

                textView.setTextColor(Color.BLACK);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                frameLayout.addView(textView);

                keyView = frameLayout;
            } else {
                ImageButton imageButton = new ImageButton(getContext());
                imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageButton.setPadding(keyPadding, keyPadding, keyPadding, keyPadding);

                switch (action)
                {
                    case POP_SYMBOL:
                        imageButton.setImageResource(android.R.drawable.ic_input_delete);
                        imageButton.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                        break;
                    case SHIFT:
                        imageButton.setImageResource(R.drawable.indicator_placeholder);
                        mShiftButtons.add(imageButton);   // track shift buttons (Issue #9)
                        break;
                    case PUSH_SYMBOL:
                        imageButton.setImageResource(android.R.drawable.ic_menu_add);
                        break;
                    case ENTER:
                        imageButton.setImageResource(android.R.drawable.ic_menu_send);
                        break;
                }
                keyView = imageButton;
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, keyHeight, keyConfig.weight);
            params.setMargins(keyMargin, keyMargin, keyMargin, keyMargin);
            keyView.setLayoutParams(params);
            keyView.setBackgroundResource(R.drawable.control_key_background);

            return keyView;
        }
        throw new IllegalArgumentException("Unknown KeyUI type");
    }

    // ----- KEYBOARD CONFIGURATION -----

    private BlissKeyUI bliss(Primitive r) { return new BlissKeyUI(r, null, 1f); }
    private BlissKeyUI bliss(Primitive r, Primitive i) { return new BlissKeyUI(r, i, 1f); }
    private AlphanumericKeyUI alpha(Primitive l) { return new AlphanumericKeyUI(l, null, 1f); }
    private AlphanumericKeyUI alpha(Primitive l, Primitive d) { return new AlphanumericKeyUI(l, d, 1f); }
    private ControlKeyUI ctrl(ControlKey c, float weight) { return new ControlKeyUI(c, weight); }

    private List<List<KeyUI>> getBlissKeyboardRows()
    {
        return Arrays.asList(
                // Row 1: Radicals with Indicators
                Arrays.asList(
                        bliss(Primitive.SEMICIRCLE, Primitive.DIGIT_ONE),
                        bliss(Primitive.ARC, Primitive.DIGIT_TWO),
                        bliss(Primitive.ARROW, Primitive.DIGIT_THREE),
                        bliss(Primitive.BUILDING, Primitive.DIGIT_FOUR),
                        bliss(Primitive.CROSSHATCH, Primitive.DIGIT_FIVE),
                        bliss(Primitive.CROSS, Primitive.DIGIT_SIX),
                        bliss(Primitive.POINTER, Primitive.DIGIT_SEVEN),
                        bliss(Primitive.EAR, Primitive.DIGIT_EIGHT),
                        bliss(Primitive.HEART, Primitive.DIGIT_NINE),
                        bliss(Primitive.DOT, Primitive.DIGIT_ZERO)
                ),
                // Row 2: More Radicals
                Arrays.asList(
                        bliss(Primitive.OPEN_RECTANGLE, Primitive.INDICATOR_PAST_ACTION),
                        bliss(Primitive.RECTANGLE, Primitive.INDICATOR_FUTURE_ACTION),
                        bliss(Primitive.OPEN_SQUARE, Primitive.INDICATOR_IMPERATIVE),
                        bliss(Primitive.SQUARE, Primitive.INDICATOR_THING),
                        bliss(Primitive.CIRCLE, Primitive.INDICATOR_ACTION),
                        bliss(Primitive.WHEEL, Primitive.INDICATOR_CONDITIONAL),
                        bliss(Primitive.RIGHT_TRIANGLE, Primitive.INDICATOR_DEFINITE),
                        bliss(Primitive.RIGHT_ANGLE, Primitive.INDICATOR_PLURAL),
                        bliss(Primitive.ACUTE_TRIANGLE, Primitive.INDICATOR_ACTIVE),
                        bliss(Primitive.ACUTE_ANGLE, Primitive.INDICATOR_PASSIVE)
                ),
                // Row 3: Punctuations and Shift
                Arrays.asList(
                        ctrl(ControlKey.SHIFT, 1.5f),
                        bliss(Primitive.PIN, Primitive.INDICATOR_DESCRIPTION),
                        bliss(Primitive.WAVY_LINE, Primitive.INDICATOR_DOT),
                        bliss(Primitive.HORIZONTAL_LINE, Primitive.COMMA_MARK),
                        bliss(Primitive.VERTICAL_LINE, Primitive.EXCLAMATION_MARK),
                        bliss(Primitive.DIAGONAL_LINE, Primitive.QUESTION_MARK),
                        ctrl(ControlKey.POP_SYMBOL, 1.5f)
                ),
                // Row 4: Controls
                Arrays.asList(
                        ctrl(ControlKey.SWITCH_TO_ALPHANUMERIC, 1.5f),
                        ctrl(ControlKey.PUSH_SYMBOL, 4f),
                        ctrl(ControlKey.ENTER, 1.5f)
                )
        );
    }

    private List<List<KeyUI>> getAlphanumericKeyboardRows()
    {
        return Arrays.asList(
                Arrays.asList(
                        alpha(Primitive.LETTER_LOWER_Q, Primitive.DIGIT_ONE),
                        alpha(Primitive.LETTER_LOWER_W, Primitive.DIGIT_TWO),
                        alpha(Primitive.LETTER_LOWER_E, Primitive.DIGIT_THREE),
                        alpha(Primitive.LETTER_LOWER_R, Primitive.DIGIT_FOUR),
                        alpha(Primitive.LETTER_LOWER_T, Primitive.DIGIT_FIVE),
                        alpha(Primitive.LETTER_LOWER_Y, Primitive.DIGIT_SIX),
                        alpha(Primitive.LETTER_LOWER_U, Primitive.DIGIT_SEVEN),
                        alpha(Primitive.LETTER_LOWER_I, Primitive.DIGIT_EIGHT),
                        alpha(Primitive.LETTER_LOWER_O, Primitive.DIGIT_NINE),
                        alpha(Primitive.LETTER_LOWER_P, Primitive.DIGIT_ZERO)
                ),
                Arrays.asList(
                        alpha(Primitive.LETTER_LOWER_A), alpha(Primitive.LETTER_LOWER_S),
                        alpha(Primitive.LETTER_LOWER_D), alpha(Primitive.LETTER_LOWER_F),
                        alpha(Primitive.LETTER_LOWER_G), alpha(Primitive.LETTER_LOWER_H),
                        alpha(Primitive.LETTER_LOWER_J), alpha(Primitive.LETTER_LOWER_K),
                        alpha(Primitive.LETTER_LOWER_L)
                ),
                Arrays.asList(
                        ctrl(ControlKey.SHIFT, 1.5f),
                        alpha(Primitive.LETTER_LOWER_Z), alpha(Primitive.LETTER_LOWER_X),
                        alpha(Primitive.LETTER_LOWER_C), alpha(Primitive.LETTER_LOWER_V),
                        alpha(Primitive.LETTER_LOWER_B), alpha(Primitive.LETTER_LOWER_N),
                        alpha(Primitive.LETTER_LOWER_M), ctrl(ControlKey.POP_SYMBOL, 1.5f)
                ),
                Arrays.asList(
                        ctrl(ControlKey.SWITCH_TO_BLISS, 1.5f),
                        ctrl(ControlKey.PUSH_SYMBOL, 4f),
                        ctrl(ControlKey.ENTER, 1.5f)
                )
        );
    }

    // ----- EVENT HANDLING LOGIC -----

    private void setupKeyLogic(View buttonView, KeyUI blueprint)
    {
        buttonView.setTag(blueprint);

        if (blueprint instanceof BlissKeyUI blissKey)
        {
            buttonView.setOnClickListener(v ->
            {
                if (mIsShiftMode && blissKey.indicator != null)
                {
                    mViewModel.onBlissKeyTapped(blissKey.indicator);
                    toggleShiftMode();
                }
                else
                {
                    mViewModel.onBlissKeyTapped(blissKey.basePrimitive);
                }
            });

            if (!blissKey.variants.isEmpty())
            {
                buttonView.setOnLongClickListener(v ->
                {
                    if (!mIsShiftMode)
                    {
                        showVariantsPopup(buttonView, blissKey.variants);
                        return true;
                    }
                    return false;
                });
            }
        }
        else if (blueprint instanceof AlphanumericKeyUI alphaKey)
        {
            buttonView.setOnClickListener(v ->
            {
                mViewModel.onAlphanumericKeyTapped(alphaKey.letter, mIsShiftMode);
                if (mIsShiftMode) toggleShiftMode();
            });

            buttonView.setOnLongClickListener(v -> {
                if (alphaKey.alternativeDigit != null && !mIsShiftMode) {
                    showVariantsPopup(buttonView, Collections.singletonList(alphaKey.alternativeDigit));
                    return true;
                }
                return false;
            });
        }
        else if (blueprint instanceof ControlKeyUI controlKey)
        {
            buttonView.setOnClickListener(v ->
            {
                switch (controlKey.action) {
                    case SHIFT:
                        toggleShiftMode();
                        break;
                    case SWITCH_TO_ALPHANUMERIC:
                        mViewModel.setKeyboardMode(BlissKeyboardViewModel.KeyboardMode.ALPHANUMERIC);
                        break;
                    case SWITCH_TO_BLISS:
                        mViewModel.setKeyboardMode(BlissKeyboardViewModel.KeyboardMode.BLISS);
                        break;
                    default:
                        mViewModel.onControlKeyTapped(controlKey.action);
                        break;
                }
            });
        }
    }

    private void toggleShiftMode()
    {
        mIsShiftMode = !mIsShiftMode;
        // Update all shift buttons (both keyboards)
        for (View shiftBtn : mShiftButtons) {
            shiftBtn.setActivated(mIsShiftMode);
        }

        // Update radical buttons (bliss)
        for (View btn : mRadicalButtons)
        {
            BlissKeyUI blueprint = (BlissKeyUI) btn.getTag();
            ImageButton imgBtn = (ImageButton) btn;
            if (mIsShiftMode)
            {
                if (blueprint.indicator != null)
                {
                    imgBtn.setImageResource(DrawableMapper.getDrawableRes(blueprint.indicator));
                    imgBtn.setAlpha(1.0f);
                    imgBtn.setEnabled(true);
                }
                else
                {
                    imgBtn.setAlpha(0.25f);
                    imgBtn.setEnabled(false);
                }
            }
            else
            {
                imgBtn.setImageResource(DrawableMapper.getDrawableRes(blueprint.basePrimitive));
                imgBtn.setAlpha(1.0f);
                imgBtn.setEnabled(true);
            }
        }

        // Update alphanumeric buttons
        for (View btn : mAlphanumericButtons)
        {
            AlphanumericKeyUI blueprint = (AlphanumericKeyUI) btn.getTag();
            TextView mainText = (TextView) ((ViewGroup)btn).getChildAt(0);
            Primitive target = mIsShiftMode ? blueprint.letter.getUpperVariant() : blueprint.letter.getLowerVariant();
            mainText.setText(target.getLetterLabel());
        }
    }

    private void showVariantsPopup(View anchorView, List<Primitive> variants)
    {
        Context context = getContext();
        if (context == null) return;

        int popupOffset = getResources().getDimensionPixelSize(R.dimen.keyboard_popup_offset);
        ViewGroup root = (ViewGroup) anchorView.getRootView();
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_bliss_variants, root, false);
        LinearLayout container = popupView.findViewById(R.id.popup_container);

        mCurrentPopupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mCurrentPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LinearLayout currentRow = null;
        for (int i = 0; i < variants.size(); i++)
        {
            if (i % VARIANTS_PER_ROW == 0)
            {
                currentRow = new LinearLayout(context);
                currentRow.setOrientation(LinearLayout.HORIZONTAL);
                container.addView(currentRow);
            }
            ImageButton btn = getVariantButton(context, variants.get(i), mCurrentPopupWindow);
            currentRow.addView(btn);
        }

        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupHeight = popupView.getMeasuredHeight();
        int popupWidth = popupView.getMeasuredWidth();

        int xOffset = (anchorView.getWidth() - popupWidth) / 2;
        int yOffset = -anchorView.getHeight() - popupHeight - popupOffset;

        mCurrentPopupWindow.showAsDropDown(anchorView, xOffset, yOffset);
    }

    @NonNull
    private ImageButton getVariantButton(@NonNull Context context, Primitive variant, PopupWindow popupWindow)
    {
        int keyHeight = getResources().getDimensionPixelSize(R.dimen.keyboard_key_height);
        int keyMargin = getResources().getDimensionPixelSize(R.dimen.keyboard_key_margin);
        int keyPadding = getResources().getDimensionPixelSize(R.dimen.keyboard_key_padding);

        ImageButton btn = new ImageButton(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(keyHeight, keyHeight);
        params.setMargins(keyMargin, keyMargin, keyMargin, keyPadding);
        btn.setLayoutParams(params);

        btn.setImageResource(DrawableMapper.getDrawableRes(variant));
        btn.setContentDescription(variant.name());
        btn.setBackgroundResource(R.drawable.key_background);
        btn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        btn.setPadding(keyPadding, keyPadding, keyPadding, keyPadding);

        btn.setOnClickListener(v ->
        {
            mViewModel.onBlissKeyTapped(variant);
            popupWindow.dismiss();
        });
        return btn;
    }
}
