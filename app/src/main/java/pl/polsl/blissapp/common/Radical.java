package pl.polsl.blissapp.common;

import java.util.Collections;
import java.util.List;

public enum Radical
{
    HORIZONTAL_WAVE("wav"),
    VERTICAL_WAVE("wav3"),

    HEART("hrt"),

    EAR("ear"),

    BIG_CIRCLE("crlB"),
    SMALL_CIRCLE("crlS");

    Radical(String code)
    {
        this.code = code;
    }

    private final String code;

    public String code()
    {
        return code;
    }

    private static final List<List<Radical>> GROUPS = List.of(
            List.of(HORIZONTAL_WAVE, VERTICAL_WAVE),
            List.of(HEART),
            List.of(EAR),
            List.of(BIG_CIRCLE, SMALL_CIRCLE)
    );

    public static List<List<Radical>> groups()
    {
        return GROUPS;
    }
}
