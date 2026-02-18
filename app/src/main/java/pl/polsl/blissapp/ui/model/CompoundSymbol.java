package pl.polsl.blissapp.ui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.polsl.blissapp.common.Radical;

public final class CompoundSymbol extends Symbol
{
    private final List<SimpleSymbol> units;
    private final int radicalCount;

    public CompoundSymbol(int index, String uri, List<SimpleSymbol> units)
    {
        super(index, uri);
        if (units.isEmpty())
        {
            throw new IllegalArgumentException("List of units shall be nonempty!");
        }
        this.units = new ArrayList<>(units);

        this.radicalCount = this.units.stream()
                .mapToInt(SimpleSymbol::getRadicalCount)
                .sum();
    }

    @Override
    public boolean isCompound() { return true; }

    @Override
    public CompoundSymbol asCompound() { return this; }

    public List<SimpleSymbol> getUnits()
    {
        return Collections.unmodifiableList(units);
    }

    @Override
    public int getUnitCount()
    {
        return units.size();
    }

    @Override
    public int getRadicalCount()
    {
        return radicalCount;
    }

    public int matches(Symbol subSymbol, List<Radical> requirements)
    {
        final int failure = -1;

        List<SimpleSymbol> initials = Collections.emptyList();
        if (subSymbol instanceof SimpleSymbol simple)
        {
            initials = Collections.singletonList(simple);
        }
        else if (subSymbol instanceof CompoundSymbol compound)
        {
            initials = compound.getUnits();
        }

        if (initials.size() > units.size())
        {
            return failure;
        }

        for (int i = 0; i < initials.size(); ++i)
        {
            SimpleSymbol provided = units.get(i);
            SimpleSymbol required = initials.get(i);
            if (!provided.equals(required))
            {
                return failure;
            }
        }

        List<SimpleSymbol> remainder = units.subList(initials.size(), units.size());
        List<Radical> radicals = remainder.stream()
                .flatMap(s -> s.getRadicals().stream())
                .toList();
        return match(radicals, requirements);
    }
}
