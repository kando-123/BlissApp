package pl.polsl.blissapp.ui.model;

import java.util.Objects;

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
}
