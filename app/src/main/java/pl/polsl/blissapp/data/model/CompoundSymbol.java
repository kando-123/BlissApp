package pl.polsl.blissapp.data.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        this.units = List.copyOf(units);

        this.radicalCount = this.units.stream()
                .mapToInt(SimpleSymbol::getRadicalCount)
                .sum();
    }

    @Override
    public boolean isCompound() { return true; }

    @Override
    public CompoundSymbol asCompound() { return this; }

    @Override
    public int getRadicalCount() { return radicalCount; }

    @Override
    public List<SimpleSymbol> getComponents()
    {
        return units;
    }

    /**
     * If {@code provided} begins with {@code required}, returns the list of the remaining provided
     * {@code SimpleSymbols}. Otherwise, {@code null} is returned to signalize failure.
     *
     * @param provided
     * @param required
     * @return
     */
    private static List<SimpleSymbol> subtractRequiredSymbols(List<SimpleSymbol> provided,
                                                              List<SimpleSymbol> required)
    {
        if (required.size() > provided.size())
        {
            return null;
        }

        for (int i = 0; i < required.size(); ++i)
        {
            if (!provided.get(i).equals(required.get(i)))
            {
                return null;
            }
        }
        return provided.subList(required.size(), provided.size());
    }

    private static List<SimpleSymbol> subtractRequiredSymbolsIgnoreOrder(List<SimpleSymbol> provided,
                                                                         List<SimpleSymbol> required)
    {
        if (required.size() > provided.size())
        {
            return null;
        }

        List<SimpleSymbol> providedCopy = new ArrayList<>(provided);
        providedCopy.sort(Comparator.comparingInt(Symbol::getIndex));
        for (SimpleSymbol component : required)
        {
            int index = Collections.binarySearch(providedCopy, component,
                    Comparator.comparingInt(Symbol::getIndex));
            if (index < 0)
            {
                return null;
            }
            providedCopy.remove(index);
        }
        return providedCopy;
    }

    public int matches(Symbol subSymbol, List<Primitive> requiredPrimitives)
    {
        List<SimpleSymbol> requiredSymbols = subSymbol == null
                ? Collections.emptyList()
                : subSymbol.getComponents();

        List<SimpleSymbol> remainder = subtractRequiredSymbols(units, requiredSymbols);

        if (remainder == null)
        {
            return MATCH_FAILURE;
        }

        List<Primitive> primitives = remainder.stream()
                .flatMap(s -> s.getPrimitives().stream())
                .collect(Collectors.toList());

        return match(primitives, requiredPrimitives);
    }
}
