package pl.polsl.blissapp.ui.views.alchemy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.polsl.blissapp.data.model.Indicator;
import pl.polsl.blissapp.data.model.Radical;
import pl.polsl.blissapp.data.model.Symbol;

class CraftingTable
{
    final List<Symbol> symbols;
    final List<Radical> radicals;
    final List<Indicator> indicators;

    CraftingTable()
    {
        symbols = new ArrayList<>();
        radicals = new ArrayList<>();
        indicators = new ArrayList<>();
    }

    CraftingTable(CraftingTable other)
    {
        symbols = new ArrayList<>(other.symbols);
        radicals = new ArrayList<>(other.radicals);
        indicators = new ArrayList<>(other.indicators);
    }
}
