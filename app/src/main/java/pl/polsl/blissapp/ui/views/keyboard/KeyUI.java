package pl.polsl.blissapp.ui.views.keyboard;

import androidx.annotation.Nullable;
import java.util.List;
import pl.polsl.blissapp.data.model.Indicator;
import pl.polsl.blissapp.data.model.Radical;

public abstract class KeyUI {
    public final float weight;

    public KeyUI(float weight) {
        this.weight = weight;
    }
}