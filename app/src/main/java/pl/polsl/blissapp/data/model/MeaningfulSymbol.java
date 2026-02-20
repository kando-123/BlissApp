package pl.polsl.blissapp.data.model;

import java.util.ArrayList;
import java.util.List;

public record MeaningfulSymbol(Symbol symbol, List<String> meanings)
{
    public MeaningfulSymbol(Symbol symbol, List<String> meanings)
    {
        this.symbol = symbol;
        this.meanings = List.copyOf(meanings);
    }
}
