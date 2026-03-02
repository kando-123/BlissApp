package pl.polsl.blissapp.ui.views.alchemy;

import java.util.ArrayList;
import java.util.List;

import pl.polsl.blissapp.data.model.Primitive;
import pl.polsl.blissapp.data.model.Symbol;

class CraftingTable
{
    private final List<Symbol> mSymbols;
    private final List<Primitive> mPrimitives;

    CraftingTable()
    {
        mSymbols = new ArrayList<>();
        mPrimitives = new ArrayList<>();
    }

    private CraftingTable(CraftingTable base)
    {
        mSymbols = new ArrayList<>(base.mSymbols);
        mPrimitives = new ArrayList<>(base.mPrimitives);
    }

    CraftingTable addSymbol(Symbol symbol)
    {
        CraftingTable copy = new CraftingTable(this);
        copy.mSymbols.add(symbol);
        return copy;
    }

    CraftingTable addRadical(Primitive primitive)
    {
        CraftingTable copy = new CraftingTable(this);
        copy.mPrimitives.add(primitive);
        return copy;
    }

    CraftingTable removeSymbol(Symbol symbol)
    {
        CraftingTable copy = new CraftingTable(this);
        copy.mSymbols.remove(symbol);
        return copy;
    }

    CraftingTable removeRadical(Primitive primitive)
    {
        CraftingTable copy = new CraftingTable(this);
        copy.mPrimitives.remove(primitive);
        return copy;
    }
}
