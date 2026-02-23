package pl.polsl.blissapp.data.model;

import java.util.Collections;
import java.util.List;

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
    public int getRadicalCount() { return radicals.size(); }

    @Override
    public List<SimpleSymbol> getComponents()
    {
        return Collections.singletonList(this);
    }

    public List<Radical> getRadicals() { return radicals; }

    public List<Indicator> getIndicators() { return indicators; }

    public int matches(List<Radical> requiredRadicals, List<Indicator> requiredIndicators)
    {
        int radicalMatch = matchRadicals(radicals, requiredRadicals);
        int indicatorMatch = matchIndicators(indicators, requiredIndicators);
        return radicalMatch < 0 || indicatorMatch < 0 ? -1 : radicalMatch + indicatorMatch;
    }
}
