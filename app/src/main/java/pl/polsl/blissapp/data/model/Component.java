package pl.polsl.blissapp.data.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

class Component
{
    private final List<Map<Primitive, Integer>> mVariants;

    public Component(List<Map<Primitive, Integer>> variants)
    {
        if (variants.isEmpty())
        {
            throw new IllegalArgumentException("Variants cannot be empty.");
        }
        mVariants = new ArrayList<>(variants.size());
        for (Map<Primitive, Integer> variant : variants)
        {
            if (variant.isEmpty())
            {
                throw new IllegalArgumentException("Variant cannot be empty.");
            }
            mVariants.add(Map.copyOf(variant));
        }
        mVariants.sort(Comparator.comparingInt(Map::size));
    }

    public Map<Primitive, Integer> getVariant(int i)
    {
        return mVariants.get(i);
    }

    public int getVariantCount()
    {
        return mVariants.size();
    }
}
