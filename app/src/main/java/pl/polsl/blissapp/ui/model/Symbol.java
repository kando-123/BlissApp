package pl.polsl.blissapp.ui.model;

import static java.lang.Math.abs;
import static java.lang.Math.min;

import java.util.List;
import java.util.Objects;

import pl.polsl.blissapp.common.Radical;

public abstract sealed class Symbol permits CompoundSymbol, SimpleSymbol
{
    private final int index;
    private final String uri;

    protected Symbol(int index, String uri)
    {
        this.index = index;
        this.uri = uri;
    }

    public final int getIndex() { return index; }
    public final String getUri() { return uri; }

    public boolean isSimple() { return false; }
    public boolean isCompound() { return false; }

    public SimpleSymbol asSimple() { return null; }
    public CompoundSymbol asCompound() { return null; }

    public abstract int getUnitCount();
    public abstract int getRadicalCount();

    @Override
    public boolean equals(Object other)
    {
        return other instanceof Symbol that && this.index == that.index;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(index);
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
     * @return
     */
    protected static int match(List<Radical> provided, List<Radical> required)
    {
        // If more is required than is provided, the match is failed.
        if (required.size() > provided.size())
        {
            return -1;
        }

        // Count the radicals provided as positive,
        // count the radicals required as negative.
        int[] counter = new int[Radical.values().length];

        // Step 1. Cancel out the identical radicals.
        for (Radical radical : provided)
        {
            ++counter[radical.ordinal()];
        }
        for (Radical radical : required)
        {
            --counter[radical.ordinal()];
        }

        // Step 2. Try to match the provided specific radicals (the "children") to the required
        // general radicals (the "parents").
        for (Radical child : Radical.CHILD_RADICALS)
        {
            Radical parent = child.getParent();
            assert parent != null;
            if (counter[parent.ordinal()] < 0 &&  // The "parent" (more general radical) is required
                counter[child.ordinal()] > 0)     // The "child" (more specific radical) is provided
            {
                counter[parent.ordinal()] += counter[child.ordinal()];
                counter[child.ordinal()] = 0;
            }
        }

        // If any "parent" has a negative score now, it means that the more general requirement
        // (the parent) was not covered by the provided specific radicals (the children).
        if (Radical.PARENT_RADICALS.stream()
                .anyMatch(parent -> counter[parent.ordinal()] < 0))
        {
            return -1;
        }

        // Step 3. If a radical is provided in one form but required in another form (a "sibling"),
        // the symbol still may count as a match, but with a lower preference (result > 0).
        // A specific requirement that is still not satisfied by the equal radicals provided,
        // it might be compensated by radicals from the same "family". The points will be aggregated
        // in the parent's counters.
        int result = 0;
        for (Radical child : Radical.CHILD_RADICALS)
        {
            Radical parent = child.getParent();
            assert parent != null;

            // If this child's points are reducing with its siblings',
            // reflect that fact in the final result.
            if (counter[child.ordinal()] > 0 && counter[parent.ordinal()] < 0 &&
                counter[child.ordinal()] < 0 && counter[parent.ordinal()] > 0)
            {
                result += min(abs(counter[child.ordinal()]), abs(counter[parent.ordinal()]));
            }

            // Pass the score to the parent, be it positive or negative.
            counter[parent.ordinal()] += counter[child.ordinal()];
            counter[child.ordinal()] = 0;
        }

        // If we still have an unsatisfied requirement (a negative counter), the match is failed.
        // If there are excessively provided symbols, the result is worse (> 0).
        for (int count : counter)
        {
            if (count < 0)
            {
                return -1;
            }
            result += count;
        }
        return result;
    }
}
