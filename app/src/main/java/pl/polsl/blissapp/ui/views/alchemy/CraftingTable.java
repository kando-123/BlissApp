package pl.polsl.blissapp.ui.views.alchemy;

import java.util.ArrayList;
import java.util.List;

import pl.polsl.blissapp.data.model.Primitive;
import pl.polsl.blissapp.data.model.Symbol;

class CraftingTable
{
    private final List<Symbol> mSymbols;
    private final List<Primitive> mPrimitives;
    // To maintain order of insertion for "pop"
    private final List<Object> mHistory;

    CraftingTable()
    {
        mSymbols = new ArrayList<>();
        mPrimitives = new ArrayList<>();
        mHistory = new ArrayList<>();
    }

    private CraftingTable(CraftingTable base)
    {
        mSymbols = new ArrayList<>(base.mSymbols);
        mPrimitives = new ArrayList<>(base.mPrimitives);
        mHistory = new ArrayList<>(base.mHistory);
    }

    CraftingTable addSymbol(Symbol symbol)
    {
        CraftingTable copy = new CraftingTable(this);
        copy.mSymbols.add(symbol);
        copy.mHistory.add(symbol);
        return copy;
    }

    CraftingTable addRadical(Primitive primitive)
    {
        CraftingTable copy = new CraftingTable(this);
        copy.mPrimitives.add(primitive);
        copy.mHistory.add(primitive);
        return copy;
    }

    CraftingTable removeSymbol(Symbol symbol)
    {
        CraftingTable copy = new CraftingTable(this);
        copy.mSymbols.remove(symbol);
        copy.mHistory.remove(symbol);
        return copy;
    }

    CraftingTable removeRadical(Primitive primitive)
    {
        CraftingTable copy = new CraftingTable(this);
        copy.mPrimitives.remove(primitive);
        copy.mHistory.remove(primitive);
        return copy;
    }

    CraftingTable removeLast() {
        if (mHistory.isEmpty()) return this;
        CraftingTable copy = new CraftingTable(this);
        Object last = copy.mHistory.remove(copy.mHistory.size() - 1);
        if (last instanceof Symbol) {
            copy.mSymbols.remove(last);
        } else if (last instanceof Primitive) {
            copy.mPrimitives.remove(last);
        }
        return copy;
    }

    List<Symbol> getSymbols() { return mSymbols; }
    List<Primitive> getPrimitives() { return mPrimitives; }
    List<Object> getAllItems() { return mHistory; }
}
