package pl.polsl.blissapp.data.model;

import pl.polsl.blissapp.R;

public enum Indicator
{
    ACTION(R.drawable.indicator_action),
    ACTIVE(R.drawable.indicator_active),
    CONDITIONAL(R.drawable.indicator_conditional),
    DEFINITE(R.drawable.indicator_definite_form),
    DESCRIPTION(R.drawable.indicator_description),
    DOT(R.drawable.indicator_dot),
    FUTURE_ACTION(R.drawable.indicator_action),
    IMPERATIVE(R.drawable.indicator_imperative_form),
    PASSIVE(R.drawable.indicator_passive),
    PAST_ACTION(R.drawable.indicator_past_action),
    PLURAL(R.drawable.indicator_plural),
    THING(R.drawable.indicator_thing);

    Indicator(int res) { drawableResource = res; }

    private final int drawableResource;

    public int getDrawableResource()
    {
        return drawableResource;
    }
}
