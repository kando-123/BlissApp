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
    SMALL_CIRCLE("crlS"),

    ETC("etc")
    ;

    Radical(String code)
    {
        this.code = code;
    }

    private final String code;

    public String code()
    {
        return code;
    }
}
