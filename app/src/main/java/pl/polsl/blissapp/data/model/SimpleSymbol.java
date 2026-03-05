package pl.polsl.blissapp.data.model;

import java.util.Collections;
import java.util.List;

public final class SimpleSymbol extends Symbol
{
    private final List<Primitive> mPrimitives;

    public SimpleSymbol(int index, String uri, List<Primitive> primitives)
    {
        super(index, uri);
        mPrimitives = List.copyOf(primitives);
    }

    @Override
    public boolean isSimple() { return true; }

    @Override
    public SimpleSymbol asSimple() { return this; }

    @Override
    public int getRadicalCount() { return mPrimitives.size(); }

    @Override
    public List<SimpleSymbol> getComponents()
    {
        return Collections.singletonList(this);
    }

    public List<Primitive> getPrimitives() { return mPrimitives; }

    public int matches(List<Primitive> requiredPrimitives)
    {
        return match(mPrimitives, requiredPrimitives);
    }

    @Override
    public int matches(Symbol subSymbol, List<Primitive> requiredPrimitives)
    {
        if (subSymbol == null)
        {
            return matches(requiredPrimitives);
        }
        else
        {
            return equals(subSymbol) && requiredPrimitives.isEmpty()
                    ? EXACT_MATCH
                    : MATCH_FAILURE;
        }
    }
}
