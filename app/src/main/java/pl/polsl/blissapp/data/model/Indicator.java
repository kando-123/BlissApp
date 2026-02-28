package pl.polsl.blissapp.data.model;

import androidx.annotation.DrawableRes;

public enum Indicator
{
    ACTION,
    ACTIVE,
    CONDITIONAL,
    DEFINITE,
    DESCRIPTION,
    DOT,
    FUTURE_ACTION,
    IMPERATIVE,
    PASSIVE,
    PAST_ACTION,
    PLURAL,
    THING;

    Indicator() { }

    @DrawableRes
    public int getDrawableResource()
    {
        return 0; // Will be replaced by a mapping in the UI layer
    }
}
