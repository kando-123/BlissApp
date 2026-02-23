package pl.polsl.blissapp.data.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static java.lang.Math.abs;
import static java.lang.Math.min;

public final class SimpleSymbol extends Symbol
{
    private final List<Radical> radicals;
    private final List<Indicator> indicators;

    public SimpleSymbol(int index, String uri, List<Radical> radicals)
    {
        this(index, uri, radicals, Collections.emptyList());
    }

    public SimpleSymbol(int index, String uri, List<Radical> radicals, List<Indicator> indicators)
    {
        super(index, uri);
        this.radicals = List.copyOf(radicals);
        this.indicators = List.copyOf(indicators);
    }

    @Override
    public boolean isSimple() { return true; }

    @Override
    public SimpleSymbol asSimple() { return this; }

    @Override
    public int getUnitCount() { return 1; }

    @Override
    public int getRadicalCount() { return radicals.size(); }

    public List<Radical> getRadicals() { return radicals; }

    public List<Indicator> getIndicators() { return indicators; }

    public int matches(List<Radical> requirements)
    {
        return match(radicals, requirements);
    }
}
