package pl.polsl.blissapp.data.model;

import java.util.List;

public record Translation(Symbol symbol, List<String> meanings)
{
    public Translation(Symbol symbol, List<String> meanings)
    {
        this.symbol = symbol;
        this.meanings = List.copyOf(meanings);
    }
}
