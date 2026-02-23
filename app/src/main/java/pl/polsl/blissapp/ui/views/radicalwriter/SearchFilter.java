package pl.polsl.blissapp.ui.views.radicalwriter;

import java.util.ArrayList;
import java.util.List;

import pl.polsl.blissapp.data.model.Indicator;
import pl.polsl.blissapp.data.model.Radical;

class SearchFilter
{
    private final List<Radical> radicals;
    private final List<Indicator> indicators;

    SearchFilter()
    {
        radicals = new ArrayList<>();
        indicators = new ArrayList<>();
    }

    void addRadical(Radical radical)
    {
        radicals.add(radical);
    }

    void addIndicator(Indicator indicator)
    {
        indicators.add(indicator);
    }

    void removeRadical(Radical radical)
    {
        radicals.remove(radical);
    }

    void removeIndicator(Indicator indicator)
    {
        indicators.remove(indicator);
    }

    List<Radical> getRadicals()
    {
        return List.copyOf(radicals);
    }

    List<Indicator> getIndicators()
    {
        return List.copyOf(indicators);
    }
}
