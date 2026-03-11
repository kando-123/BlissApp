package pl.polsl.blissapp.ui.views.keyboard;

import androidx.annotation.Nullable;
import pl.polsl.blissapp.data.model.Primitive;

public class AlphanumericKeyUI extends KeyUI {
    public final Primitive letter;
    @Nullable
    public final Primitive alternativeDigit;

    public AlphanumericKeyUI(Primitive letter, @Nullable Primitive alternativeDigit, float weight) {
        super(weight);
        this.letter = letter;
        this.alternativeDigit = alternativeDigit;
    }
}
