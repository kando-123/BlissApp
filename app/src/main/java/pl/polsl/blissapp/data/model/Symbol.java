package pl.polsl.blissapp.data.model;

import static java.lang.Math.min;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record Symbol(int index, String uri, List<Component> components)
{
    public Symbol(int index, String uri, List<Component> components)
    {
        this.index = index;
        this.uri = uri;
        this.components = List.copyOf(components);
    }

    public boolean isEmpty()
    {
        return components.isEmpty();
    }

    public boolean isSimple()
    {
        return components.size() == 1;
    }

    public boolean isCompound()
    {
        return components.size() > 1;
    }

    public static final int MATCH_FAILURE = -1;
    public static final int EXACT_MATCH = 0;

    public int matches(Symbol subSymbol, Map<Primitive, Integer> requiredPrimitives)
    {
        List<Component> requiredComponents = subSymbol == null
                ? Collections.emptyList()
                : subSymbol.components;

        if (!checkBeginning(components, requiredComponents))
        {
            return MATCH_FAILURE;
        }

        List<Component> remainingComponents = components.subList(requiredComponents.size(), components.size());

        return matchComponents(remainingComponents, requiredPrimitives);
    }

    private static boolean checkBeginning(List<Component> provided, List<Component> required)
    {
        if (required.size() > provided.size())
        {
            return false;
        }

        for (int i = 0; i < required.size(); ++i)
        {
            if (!provided.get(i).equals(required.get(i)))
            {
                return false;
            }
        }
        return true;
    }

    private static int matchComponents(List<Component> providedComponents,
                                       Map<Primitive, Integer> requiredPrimitives)
    {
        int[] variants = new int[providedComponents.size()];

        int minMatch = MATCH_FAILURE;
        boolean hasNext;
        do
        {
            Map<Primitive, Integer> providedPrimitives = new EnumMap<>(Primitive.class);
            for (int i = 0; i < providedComponents.size(); ++i)
            {
                Component component = providedComponents.get(i);
                for (var entry : component.getVariant(variants[i]).entrySet())
                {
                    providedPrimitives.merge(entry.getKey(), entry.getValue(), Integer::sum);
                }
            }

            int result = matchPrimitives(providedPrimitives, requiredPrimitives);
            if (minMatch == MATCH_FAILURE || result < minMatch)
            {
                minMatch = result;
            }

            hasNext = false;
            for (int i = 0; i < providedComponents.size(); ++i)
            {
                if (++variants[i] < providedComponents.get(i).getVariantCount())
                {
                    hasNext = true;
                    break;
                }
                variants[i] = 0;
            }
        }
        while (hasNext);

        return minMatch;
    }

    /**
     * The method evaluates how the provided radicals match the required ones.
     *
     * <p>If for every required radical, there is a matching radical provided, the match is fully
     * successful and 0 is returned. A provided radical matches a required one if they are equal,
     * or if the provided radical is a child of the required radical (a specific radical satisfies
     * a general requirement).</p>
     *
     * <p>A general requirement may be covered by equal or child radicals provided. A specific
     * requirement may be covered by equal or sibling radicals provided. If a requirement is not
     * covered, the match is failed and -1 is returned.</p>
     *
     * <p>The returned result is equal to the number of sibling matches plus the number of radicals
     * excessively provided in the symbol, which translates to a non-negative number, with smaller
     * numbers indicating a better match.</p>
     *
     * @param provided
     * @param required
     *
     * @return
     */
    private static int matchPrimitives(Map<Primitive, Integer> provided, // What this symbol provides
                                       Map<Primitive, Integer> required) // What the user requires
    {
        // If more is required than is provided, the match is failed.
        int requiredCount = required.values().stream().mapToInt(Integer::intValue).sum();
        int providedCount = provided.values().stream().mapToInt(Integer::intValue).sum();
        if (requiredCount > providedCount)
        {
            return MATCH_FAILURE;
        }

        // Count the radicals provided as positive,
        // count the radicals required as negative.
        int[] counter = new int[Primitive.values().length];

        // Step 1. Cancel out the identical radicals.
        for (var entry : provided.entrySet())
        {
            counter[entry.getKey().ordinal()] += entry.getValue();
        }
        for (var entry : required.entrySet())
        {
            counter[entry.getKey().ordinal()] -= entry.getValue();
        }

        // Step 2. Try to match the provided specific radicals (the "children") to the required
        // general radicals (the "parents").
        for (Primitive child : Primitive.getChildPrimitives())
        {
            Primitive parent = child.getParent();
            assert parent != null;

            if (counter[child.ordinal()] > 0)
            {
                counter[parent.ordinal()] += counter[child.ordinal()];
                counter[child.ordinal()] = 0;
            }
        }

        // If any "parent" has a negative score now, it means that the more general requirement
        // (the parent) was not covered by the provided specific radicals (the children).
        if (Primitive.getParentPrimitives().stream().anyMatch(r -> counter[r.ordinal()] < 0))
        {
            return MATCH_FAILURE;
        }

        // Step 3. If a radical is provided in one form but required in another form (a "sibling"),
        // the symbol still may count as a match, but with a lower preference (result > 0).
        // The points will be aggregated in the parent's counters.
        int result = 0;
        for (Primitive child : Primitive.getChildPrimitives())
        {
            Primitive parent = child.getParent();
            assert parent != null;

            // Count the reductions to the result.
            if (counter[child.ordinal()] < 0 && counter[parent.ordinal()] > 0)
            {
                result += min(-counter[child.ordinal()], +counter[parent.ordinal()]);
            }

            // Pass the score to the parent.
            counter[parent.ordinal()] += counter[child.ordinal()];
            counter[child.ordinal()] = 0;
        }

        // If we still have an unsatisfied requirement (a negative counter), the match is failed.
        // If there are excessively provided symbols, the result is worse (> 0).
        for (int count : counter)
        {
            if (count < 0)
            {
                return MATCH_FAILURE;
            }
            result += count;
        }
        return result;
    }
}
