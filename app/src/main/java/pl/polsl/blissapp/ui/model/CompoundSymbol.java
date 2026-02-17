package pl.polsl.blissapp.ui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.polsl.blissapp.common.Radical;

public final class CompoundSymbol extends Symbol
{
    private final List<SimpleSymbol> units;

    public CompoundSymbol(int index, String uri, List<SimpleSymbol> units)
    {
        super(index, uri);
        if (units.isEmpty())
        {
            throw new IllegalArgumentException("List of units shall be nonempty!");
        }
        this.units = new ArrayList<>(units);
    }

    @Override
    public boolean isCompound() { return true; }

    @Override
    public CompoundSymbol asCompound() { return this; }

    public List<SimpleSymbol> getUnits()
    {
        return Collections.unmodifiableList(units);
    }

    /**
     * Checks whether this compound symbol contains all the simple symbols of the given compound symbol,
     * and whether the remaining simple symbols of this compound symbol contain all the given radicals.
     *
     * @param subSymbol the subSymbol compound symbol whose simple symbols need to be all contained in this compound symbol
     * @param radicals the radicals that need to be contained in the remaining simple symbols of this compound symbol
     * @return result of checking whether this compound symbol contains all the simple symbols of the given compound symbol,
     * and whether the remaining simple symbols of this compound symbol contain all the given radicals
     */
    public boolean matches(Symbol subSymbol, List<Radical> radicals)
    {
        int subUnitsSize;

        // Check if this symbol contains the subSymbol.
        if (subSymbol == null)
        {
            subUnitsSize = 0;
        }
        else if (subSymbol.isSimple())
        {
            SimpleSymbol simple = subSymbol.asSimple();

            // Check if this compound symbol begins with that simple symbol.
            if (!units.getFirst().equals(simple))
            {
                return false;
            }

            subUnitsSize = 1;
        }
        else
        {
            CompoundSymbol compound = subSymbol.asCompound();

            // If the subSymbol is compound and it contains more simple symbols than this one,
            // it cannot be a match.
            if (compound.units.size() > units.size())
            {
                return false;
            }

            // If this compound symbol does not begin with all the simple symbols of the subSymbol,
            // it cannot be a match.
            for (int i = 0; i < compound.units.size(); ++i)
            {
                SimpleSymbol symbol1 = units.get(i);
                SimpleSymbol symbol2 = compound.units.get(i);
                if (!symbol1.equals(symbol2))
                {
                    return false;
                }
            }

            subUnitsSize = compound.units.size();
        }

        // Check if the given radicals are a subset of the remaining simple symbols of this symbol.
        // Respect the count of each symbol on both sides.

        List<SimpleSymbol> remainder = units.subList(subUnitsSize, units.size());

        int[] counter = new int[Radical.values().length];
        for (SimpleSymbol simpleSymbol : remainder)
        {
            for (Radical radical : simpleSymbol.getRadicals())
            {
                ++counter[radical.ordinal()];
            }
        }
        for (Radical radical : radicals)
        {
            if (--counter[radical.ordinal()] < 0)
            {
                // If it has just hit -1, it may only get worse.
                return false;
            }
        }
        return true;
    }
}
