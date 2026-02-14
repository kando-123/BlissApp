package pl.polsl.blissapp.ui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.polsl.blissapp.common.Radical;

public final class SimpleSymbol extends Symbol
{
    private final List<Radical> radicals;

    public SimpleSymbol(int index, String uri, List<Radical> radicals)
    {
        super(index, uri);
        this.radicals = new ArrayList<>(radicals);
    }

    @Override
    public boolean isSimple() { return true; }

    @Override
    public SimpleSymbol asSimple() { return this; }

    public List<Radical> getRadicals()
    {
        return Collections.unmodifiableList(radicals);
    }
}
