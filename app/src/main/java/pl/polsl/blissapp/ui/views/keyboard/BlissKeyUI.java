package pl.polsl.blissapp.ui.views.keyboard;

import androidx.annotation.Nullable;

import java.util.List;

import pl.polsl.blissapp.data.model.Primitive;

public class BlissKeyUI extends KeyUI
{
    public final Primitive basePrimitive;
    @Nullable
    public final Primitive indicator;
    public final List<Primitive> variants;

    public BlissKeyUI(Primitive basePrimitive, @Nullable Primitive indicator, float weight)
    {
        super(weight);
        this.basePrimitive = basePrimitive;
        this.indicator = indicator;
        variants = Primitive.getChildren(basePrimitive);
    }
}