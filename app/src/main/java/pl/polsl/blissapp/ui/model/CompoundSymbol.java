package pl.polsl.blissapp.ui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CompoundSymbol extends Symbol
{
    private final List<SimpleSymbol> units;

    public CompoundSymbol(int index, String uri, List<SimpleSymbol> units)
    {
        super(index, uri);
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
}
