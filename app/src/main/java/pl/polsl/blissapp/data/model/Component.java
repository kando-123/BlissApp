package pl.polsl.blissapp.data.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public record Component(int index, List<Map<Primitive, Integer>> variants)
{
    public Component(int index, List<Map<Primitive, Integer>> variants)
    {
        this.index = index;
        if (variants.isEmpty())
        {
            throw new IllegalArgumentException("Variants cannot be empty.");
        }
        this.variants = new ArrayList<>(variants.size());
        for (Map<Primitive, Integer> variant : variants)
        {
            if (variant.isEmpty())
            {
                throw new IllegalArgumentException("Variant cannot be empty.");
            }
            this.variants.add(Map.copyOf(variant));
        }
        this.variants.sort(Comparator.comparingInt(Map::size));
    }

    public Map<Primitive, Integer> getVariant(int i)
    {
        return variants.get(i);
    }

    public int getVariantCount()
    {
        return variants.size();
    }
}
