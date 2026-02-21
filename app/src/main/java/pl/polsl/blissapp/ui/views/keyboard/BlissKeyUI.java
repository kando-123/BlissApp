package pl.polsl.blissapp.ui.views.keyboard;

import java.util.List;
import pl.polsl.blissapp.data.model.Radical;

/**
 * Represents a key that inputs a Bliss Symbol (Radical).
 */
public class BlissKeyUI extends KeyUI {
    public final Radical baseRadical;
    public final Radical indicatorRadical; // The symbol to output when Shift/Indicator mode is ON
    public final List<Radical> variants;   // Long-press options (disabled in Indicator mode)

    public BlissKeyUI(int viewId, Radical baseRadical, Radical indicatorRadical, List<Radical> variants) {
        super(viewId);
        this.baseRadical = baseRadical;
        this.indicatorRadical = indicatorRadical;
        this.variants = variants;
    }

    // Convenience constructor for standard keys (no indicator behavior)
    public BlissKeyUI(int viewId, Radical baseRadical, List<Radical> variants) {
        this(viewId, baseRadical, null, variants);
    }

    public boolean hasVariants() {
        return variants != null && !variants.isEmpty();
    }

    public  boolean isIndicator() {
        return (indicatorRadical != null);
    }
}