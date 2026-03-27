package pl.polsl.blissapp.ui.views.alchemy;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.android.material.card.MaterialCardView;

import dagger.hilt.android.AndroidEntryPoint;
import pl.polsl.blissapp.R;
import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.ui.common.TextToSpeechManager;
import pl.polsl.blissapp.ui.repository.SymbolRepository;
import pl.polsl.blissapp.ui.views.keyboard.BlissKeyboardViewModel;

import java.util.Objects;
import javax.inject.Inject;

@AndroidEntryPoint
public class AlchemyFragment extends Fragment {
    private AlchemyViewModel viewModel;
    private BlissKeyboardViewModel keyboardViewModel;

    private RecyclerView rvCraftingTable;
    private RecyclerView rvHints;
    private FrameLayout compositionArea;
    private ImageView ivCheer;

    private MaterialCardView targetSymbolCard;
    private ImageView ivTargetSymbol;
    private TextView tvTargetLabel;
    private TextView tvDailyGoalLabel;
    private ProgressBar pbDailyGoal;
    private ImageView ivGoalStar;

    private AlchemyAdapter craftingAdapter;
    private AlchemyAdapter hintAdapter;

    @Inject
    SymbolRepository symbolRepository;

    @Inject
    TextToSpeechManager mTextToSpeechManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alchemy, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCraftingTable = view.findViewById(R.id.rv_crafting_table);
        rvHints = view.findViewById(R.id.rv_hints);
        compositionArea = view.findViewById(R.id.composition_area);
        ivCheer = view.findViewById(R.id.iv_cheer);

        targetSymbolCard = view.findViewById(R.id.target_symbol_card);
        ivTargetSymbol = view.findViewById(R.id.iv_target_symbol);
        tvTargetLabel = view.findViewById(R.id.tv_target_label);
        tvDailyGoalLabel = view.findViewById(R.id.tv_daily_goal_label);
        pbDailyGoal = view.findViewById(R.id.pb_daily_goal);
        ivGoalStar = view.findViewById(R.id.iv_goal_star);

        viewModel = new ViewModelProvider(this).get(AlchemyViewModel.class);
        keyboardViewModel = new ViewModelProvider(this).get(BlissKeyboardViewModel.class);

        viewModel.refreshLanguageIfNeeded();

        setupUI();
        setupRecyclerViews();
        observeViewModel();
        setupKeyboardInteractions();
    }

    private void setupUI() {
        tvDailyGoalLabel.setText(getString(R.string.alchemy_daily_goal, AlchemyViewModel.DAILY_GOAL));
    }

    private void setupRecyclerViews() {
        craftingAdapter = new AlchemyAdapter(
                symbolRepository,
                R.layout.item_alchemy_crafting,
                item -> viewModel.removeItem(item)
        );
        rvCraftingTable.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCraftingTable.setAdapter(craftingAdapter);

        hintAdapter = new AlchemyAdapter(
                symbolRepository,
                R.layout.item_alchemy_crafting,
                item -> viewModel.onHintPressed(item)
        );
        rvHints.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });
        rvHints.setAdapter(hintAdapter);

        rvHints.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rvHints.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int availableWidth = rvHints.getWidth() - rvHints.getPaddingStart() - rvHints.getPaddingEnd();
                int containerHeight = rvHints.getHeight(); // Assume maxSize of a hint is based on height (square items)
                
                if (availableWidth > 0 && containerHeight > 0) {
                    // Formula: take the max allowed width divide it by the maxSize of a hint -> then you ceil the result 
                    // and you shrink the hintboxes so they fit.
                    double count = (double) availableWidth / containerHeight;
                    int numItems = (int) Math.ceil(count);
                    if (numItems > 0) {
                        int itemWidth = availableWidth / numItems;
                        hintAdapter.setItemWidth(itemWidth);
                    }
                }
            }
        });
    }

    private void observeViewModel() {
        viewModel.getCraftingItems().observe(getViewLifecycleOwner(), craftingItems -> {
            craftingAdapter.setItems(craftingItems);
            if (!craftingItems.isEmpty()) {
                rvCraftingTable.smoothScrollToPosition(craftingItems.size() - 1);
            }
        });

        viewModel.getHintItems().observe(getViewLifecycleOwner(), hintItems -> {
            hintAdapter.setItems(hintItems);
        });

        viewModel.getTargetSymbol().observe(getViewLifecycleOwner(), symbol -> {
            if (symbol != null) {
                loadTargetSymbol(symbol);
            }
        });

        viewModel.getTargetLabel().observe(getViewLifecycleOwner(), label -> {
            tvTargetLabel.setText(label);
        });

        viewModel.getDailyProgress().observe(getViewLifecycleOwner(), progress -> {
            int visualProgress = Math.min(progress, 10);
            pbDailyGoal.setProgress(visualProgress);
            updateStarPosition(visualProgress);
        });

        viewModel.getDailyGoalReached().observe(getViewLifecycleOwner(), reached -> {
            if (reached) {
                // Big celebratory pop for the star
                ivGoalStar.setImageResource(R.drawable.ic_star_shine); // Fill the star
                ivGoalStar.animate()
                        .scaleX(2.0f)
                        .scaleY(2.0f)
                        .setDuration(500)
                        .setInterpolator(new OvershootInterpolator(2.0f))
                        .withEndAction(() -> ivGoalStar.animate().scaleX(1.5f).scaleY(1.5f).setDuration(300).start())
                        .start();

                ivGoalStar.setColorFilter(getResources().getColor(R.color.sunset_primary, null));

                // Trigger the success animation on the card too for extra "oomph"
                targetSymbolCard.animate()
                        .scaleX(1.1f)
                        .scaleY(1.1f)
                        .setDuration(200)
                        .withEndAction(() -> targetSymbolCard.animate().scaleX(1.0f).scaleY(1.0f).start())
                        .start();
            }
        });

        viewModel.getResultingSymbol().observe(getViewLifecycleOwner(), symbol -> {
            if (symbol != null) {
                playSuccessAnimation();
            }
        });

        viewModel.getShowCheering().observe(getViewLifecycleOwner(), show -> {
            if (show) {
                ivCheer.setVisibility(View.VISIBLE);
                // Start tiny and invisible
                ivCheer.setAlpha(0.0f);
                ivCheer.setScaleX(0.3f);
                ivCheer.setScaleY(0.3f);

                // Fast springy pop up
                ivCheer.animate()
                        .scaleX(1.3f)
                        .scaleY(1.3f)
                        .alpha(1.0f)
                        .setDuration(200) // Much faster pop
                        .setInterpolator(new OvershootInterpolator(1.5f))
                        .withEndAction(null)
                        .start();

                // Dismiss much sooner
                ivCheer.postDelayed(() -> viewModel.dismissCheering(), 800);
            } else {
                // Quick exit
                ivCheer.animate()
                        .scaleX(0.5f)
                        .scaleY(0.5f)
                        .alpha(0.0f)
                        .setDuration(150) // Faster disappear
                        .setInterpolator(new AnticipateInterpolator())
                        .withEndAction(() -> ivCheer.setVisibility(View.GONE))
                        .start();
            }
        });

        viewModel.getCheerIcon().observe(getViewLifecycleOwner(), iconRes -> {
            if (iconRes != 0) {
                ivCheer.setImageResource(iconRes);
            }
        });

        viewModel.getIsTargetMatched().observe(getViewLifecycleOwner(), this::updateCompositionAreaMatch);

        viewModel.getSelectedLanguage().observe(getViewLifecycleOwner(), language -> {
            if (language != null) {
                // We no longer set the language on the TTS Manager here!
                viewModel.pickNewTargetSymbol(); // Just refresh the label
            }
        });

        viewModel.getSpeakRequest().observe(getViewLifecycleOwner(), texts -> {
            if (texts != null && !texts.isEmpty()) {
                // Grab the language directly from the ViewModel and pass it to speak()
                String currentLang = viewModel.getSelectedLanguage().getValue();
                mTextToSpeechManager.speak(texts, currentLang);
                viewModel.clearSpeakRequest();
            }
        });
    }

    private void updateCompositionAreaMatch(boolean isMatch) {
        int strokeColor = isMatch ? Color.parseColor("#4CAF50") : getResources().getColor(R.color.settings_divider, null);
        float density = getResources().getDisplayMetrics().density;
        int strokeWidth = (int) ((isMatch ? 3 : 1) * density);

        Drawable background = compositionArea.getBackground();
        if (background instanceof GradientDrawable shape) {
            shape.setStroke(strokeWidth, strokeColor);
        }

        if (targetSymbolCard != null) {
            targetSymbolCard.setStrokeColor(strokeColor);
            targetSymbolCard.setStrokeWidth(strokeWidth);
        }
    }

    private void loadTargetSymbol(Symbol symbol) {
        symbolRepository.getSvg(symbol, new Callback<String, Exception>() {
            @Override
            public void onSuccess(String svgString) {
                try {
                    SVG svg = SVG.getFromString(svgString);
                    PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
                    ivTargetSymbol.post(() -> {
                        ivTargetSymbol.setImageDrawable(drawable);
                        int tintColor = getThemeColor(ivTargetSymbol.getContext(), android.R.attr.textColorPrimary);
                        ivTargetSymbol.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP);
                    });
                } catch (SVGParseException ignored) {}
            }
            @Override
            public void onFailure(Exception data) {}
        });
    }

    private int getThemeColor(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(attr, typedValue, true)) {
            return typedValue.data;
        }
        return Color.BLACK;
    }

    private void updateStarPosition(int progress) {
        pbDailyGoal.post(() -> {
            float width = pbDailyGoal.getWidth();
            float max = pbDailyGoal.getMax();

            // Calculate position along the bar
            float translationX = (progress / max) * width;

            // Shift the star so its CENTER at the progress point
            ivGoalStar.setTranslationX(translationX - (ivGoalStar.getWidth() / 2f));

            // Optional: Add a slight "bob" animation when it moves
            ivGoalStar.animate()
                    .scaleX(1.2f).scaleY(1.2f)
                    .setDuration(100)
                    .withEndAction(() -> ivGoalStar.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start())
                    .start();
        });
    }

    private void setupKeyboardInteractions() {
        keyboardViewModel.getPrimitiveInput().observe(getViewLifecycleOwner(), primitive -> {
            if (primitive != null) {
                viewModel.addRadical(primitive);
                keyboardViewModel.clearInputs();
            }
        });

        keyboardViewModel.getControlInput().observe(getViewLifecycleOwner(), controlKey -> {
            if (controlKey != null) {
                switch (controlKey) {
                    case TEXT_TO_SPEECH:
                        viewModel.onEnterPressed();
                        break;
                    case POP_SYMBOL:
                        viewModel.onPopPressed();
                        break;
                }
                keyboardViewModel.clearInputs();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTextToSpeechManager.stop();
    }

    private void playSuccessAnimation() {
        rvCraftingTable.animate()
                .scaleX(0.2f)
                .scaleY(0.2f)
                .alpha(0f)
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    rvCraftingTable.setScaleX(1f);
                    rvCraftingTable.setScaleY(1f);
                    rvCraftingTable.setAlpha(1f);
                })
                .start();
    }
}
