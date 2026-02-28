package pl.polsl.blissapp.ui.views.alchemy;

import java.util.ArrayList;
import java.util.List;

import pl.polsl.blissapp.data.model.Indicator;
import pl.polsl.blissapp.data.model.Radical;
import pl.polsl.blissapp.data.model.Symbol;

class CraftingTable
{
    private final List<Symbol> symbols;
    private final List<Radical> radicals;
    private final List<Indicator> indicators;

    CraftingTable()
    {
        symbols = new ArrayList<>();
        radicals = new ArrayList<>();
        indicators = new ArrayList<>();
    }

    private CraftingTable(CraftingTable base)
    {
        symbols = new ArrayList<>(base.symbols);
        radicals = new ArrayList<>(base.radicals);
        indicators = new ArrayList<>(base.indicators);
    }

    CraftingTable addSymbol(Symbol symbol)
    {
        CraftingTable copy = new CraftingTable(this);
        copy.symbols.add(symbol);
        return copy;
    }

    CraftingTable addRadical(Radical radical)
    {
        CraftingTable copy = new CraftingTable(this);
        copy.radicals.add(radical);
        return copy;
    }

    CraftingTable addIndicator(Indicator indicator)
    {
        CraftingTable copy = new CraftingTable(this);
        copy.indicators.add(indicator);
        return copy;
    }

    CraftingTable removeSymbol(Symbol symbol)
    {
        CraftingTable copy = new CraftingTable(this);
        copy.symbols.remove(symbol);
        return copy;
    }

    CraftingTable removeRadical(Radical radical)
    {
        CraftingTable copy = new CraftingTable(this);
        copy.radicals.remove(radical);
        return copy;
    }

    CraftingTable removeIndicator(Indicator indicator)
    {
        CraftingTable copy = new CraftingTable(this);
        copy.indicators.remove(indicator);
        return copy;
    }
}
