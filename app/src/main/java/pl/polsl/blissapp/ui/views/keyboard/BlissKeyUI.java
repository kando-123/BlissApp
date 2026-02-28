package pl.polsl.blissapp.ui.views.keyboard;

import androidx.annotation.Nullable;

import java.util.List;

import pl.polsl.blissapp.data.model.Indicator;
import pl.polsl.blissapp.data.model.Radical;

public class BlissKeyUI extends KeyUI {
    public final Radical baseRadical;
    @Nullable
    public final Indicator indicator; // Fixed type to Indicator
    public final List<Radical> variants;

    // Smart constructor: Automatically fetches variants from the Radical enum!
    public BlissKeyUI(Radical baseRadical, @Nullable Indicator indicator, float weight) {
        super(weight);
        this.baseRadical = baseRadical;
        this.indicator = indicator;
        this.variants = Radical.getChildren(baseRadical);
    }

    public boolean hasVariants() {
        return variants != null && !variants.isEmpty();
    }

    public boolean hasIndicator() {
        return indicator != null;
    }
}