package pl.polsl.blissapp.ui.views.keyboard;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

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
import pl.polsl.blissapp.data.model.Primitive;
import pl.polsl.blissapp.ui.mapping.DrawableMapper;

@AndroidEntryPoint
public class BlissKeyboardFragment extends Fragment
{
    private static final int VARIANTS_PER_ROW = 6;

    private BlissKeyboardViewModel mViewModel;
    private final List<View> mRadicalButtons = new ArrayList<>();
    private boolean mIsIndicatorMode = false;
    private ImageButton mIndicatorModeButton;
    private PopupWindow mCurrentPopupWindow;

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
        return createKeyboardView();
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

    // --- KEYBOARD UI GENERATION ---

    private View createKeyboardView()
    {
        LinearLayout keyboardLayout = new LinearLayout(getContext());
        keyboardLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        keyboardLayout.setOrientation(LinearLayout.VERTICAL);
        keyboardLayout.setGravity(Gravity.BOTTOM);
        keyboardLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.keyboard_background));
        int padding = getResources().getDimensionPixelSize(R.dimen.keyboard_padding);
        keyboardLayout.setPadding(padding, padding, padding, padding);

        for (List<KeyUI> rowConfig : getKeyboardRows())
        {
            keyboardLayout.addView(createRowView(rowConfig));
        }

        return keyboardLayout;
    }

    private View createRowView(List<KeyUI> rowConfig)
    {
        LinearLayout rowLayout = new LinearLayout(getContext());
        rowLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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

        if (keyConfig instanceof BlissKeyUI)
        {
            ImageButton imageButton = new ImageButton(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, keyHeight, keyConfig.weight);
            params.setMargins(keyMargin, keyMargin, keyMargin, keyMargin);
            imageButton.setLayoutParams(params);

            imageButton.setImageResource(DrawableMapper.getDrawableRes(((BlissKeyUI) keyConfig).basePrimitive));
            imageButton.setContentDescription(((BlissKeyUI) keyConfig).basePrimitive.name());
            imageButton.setBackgroundResource(R.drawable.key_background);
            imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageButton.setPadding(keyPadding, keyPadding, keyPadding, keyPadding);

            mRadicalButtons.add(imageButton);
            return imageButton;
        }
        else if (keyConfig instanceof ControlKeyUI)
        {
            ControlKey action = ((ControlKeyUI) keyConfig).action;
            ImageButton imageButton = new ImageButton(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, keyHeight, keyConfig.weight);
            params.setMargins(keyMargin, keyMargin, keyMargin, keyMargin);
            imageButton.setLayoutParams(params);
            imageButton.setBackgroundResource(R.drawable.key_background);
            imageButton.setContentDescription(action.name());
            imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageButton.setPadding(keyPadding, keyPadding, keyPadding, keyPadding);

            switch (action)
            {
                case POP_SYMBOL:
                    imageButton.setImageResource(android.R.drawable.ic_input_delete);
                    imageButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.black), PorterDuff.Mode.SRC_ATOP);
                    break;
                case INDICATOR_MODE:
                    imageButton.setImageResource(R.drawable.indicator_placeholder);
                    imageButton.setBackgroundResource(R.drawable.indicator_key_background);
                    mIndicatorModeButton = imageButton;
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

    private BlissKeyUI key(Primitive r)
    {
        return new BlissKeyUI(r, null, 1f);
    }

    private BlissKeyUI key(Primitive r, Primitive i)
    {
        return new BlissKeyUI(r, i, 1f);
    }

    private ControlKeyUI ctrl(ControlKey c, float weight)
    {
        return new ControlKeyUI(c, weight);
    }

    private List<List<KeyUI>> getKeyboardRows()
    {
        return Arrays.asList(
                // ROW 1
                Arrays.asList(
                        key(Primitive.SEMICIRCLE),
                        key(Primitive.ARC),
                        key(Primitive.ARROW),
                        key(Primitive.BUILDING),
                        key(Primitive.CROSSHATCH),
                        key(Primitive.CROSS),
                        key(Primitive.POINTER),
                        ctrl(ControlKey.POP_SYMBOL, 1.5f)
                ),
                // ROW 2
                Arrays.asList(
                        key(Primitive.EAR),
                        key(Primitive.HEART),
                        key(Primitive.OPEN_RECTANGLE),
                        key(Primitive.RECTANGLE, Primitive.INDICATOR_PLURAL),
                        key(Primitive.OPEN_SQUARE),
                        key(Primitive.SQUARE, Primitive.INDICATOR_THING),
                        key(Primitive.DOT, Primitive.DOT),
                        key(Primitive.CIRCLE),
                        key(Primitive.WHEEL)
                ),
                // ROW 3
                Arrays.asList(
                        key(Primitive.ACUTE_ANGLE),
                        key(Primitive.ACUTE_TRIANGLE),
                        key(Primitive.RIGHT_ANGLE),
                        key(Primitive.RIGHT_TRIANGLE),
                        key(Primitive.PIN),
                        key(Primitive.WAVY_LINE),
                        key(Primitive.HORIZONTAL_LINE, Primitive.INDICATOR_PAST_ACTION),
                        key(Primitive.VERTICAL_LINE, Primitive.INDICATOR_FUTURE_ACTION),
                        key(Primitive.DIAGONAL_LINE)
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

    private void setupKeyLogic(View buttonView, KeyUI blueprint)
    {
        buttonView.setTag(blueprint);

        if (blueprint instanceof BlissKeyUI blissKey)
        {
            final List<Primitive> variants = Primitive.getChildren(blissKey.basePrimitive);

            // Standard Click
            buttonView.setOnClickListener(v ->
            {
                if (mIsIndicatorMode)
                {
                    if (blissKey.indicator != null)
                    {
                        mViewModel.onBlissKeyTapped(blissKey.indicator);

                        // Turn indicator mode off automatically using the cached button
                        if (mIndicatorModeButton != null && mIsIndicatorMode)
                        {
                            toggleIndicatorMode(mIndicatorModeButton);
                        }
                    }
                }
                else
                {
                    mViewModel.onBlissKeyTapped(blissKey.basePrimitive);
                }
            });

            // Long Click Variants (Disabled if Indicator mode is ON)
            if (!variants.isEmpty())
            {
                buttonView.setOnLongClickListener(v ->
                {
                    if (!mIsIndicatorMode)
                    {
                        showVariantsPopup(buttonView, variants);
                        return true;
                    }
                    return false;
                });
            }
            else
            {
                buttonView.setLongClickable(false);
            }
        }
        else if (blueprint instanceof ControlKeyUI controlKey)
        {
            buttonView.setOnClickListener(v ->
            {
                if (controlKey.action == ControlKey.INDICATOR_MODE)
                {
                    toggleIndicatorMode((ImageButton) buttonView);
                }
                else
                {
                    mViewModel.onControlKeyTapped(controlKey.action);
                }
            });
            buttonView.setLongClickable(false);
        }
    }

    private void toggleIndicatorMode(ImageButton shiftButton)
    {
        mIsIndicatorMode = !mIsIndicatorMode;

        // Visual toggle for the SHIFT button
        shiftButton.setActivated(mIsIndicatorMode);

        // Loop through all Radical buttons and update their appearance
        for (View btn : mRadicalButtons)
        {
            BlissKeyUI blueprint = (BlissKeyUI) btn.getTag();
            ImageButton imgBtn = (ImageButton) btn;

            if (mIsIndicatorMode)
            {
                if (blueprint.indicator != null)
                { // Direct null check clears IDE warning
                    imgBtn.setImageResource(DrawableMapper.getDrawableRes(blueprint.indicator));
                    imgBtn.setAlpha(1.0f);
                    imgBtn.setEnabled(true);
                }
                else
                {
                    // Disable keys that have a NULL indicator
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
    }

    private void showVariantsPopup(View anchorView, List<Primitive> variants)
    {
        Context context = getContext();
        if (context == null)
        {
            return;
        }

        int popupOffset = getResources().getDimensionPixelSize(R.dimen.keyboard_popup_offset);

        ViewGroup root = (ViewGroup) anchorView.getRootView();
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_bliss_variants, root, false);
        LinearLayout container = popupView.findViewById(R.id.popup_container);

        mCurrentPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
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

        // Measure the popup and display it
        popupView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
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
        params.setMargins(keyMargin, 0, keyMargin, 0);
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
