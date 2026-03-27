package pl.polsl.blissapp.data.repository;

import java.util.List;
import java.util.stream.Collectors;

import pl.polsl.blissapp.BlissApplication;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.data.room.BlissDatabase;

public class AlchemyRepositoryImpl
{
    private final BlissDatabase database = BlissApplication.getDatabase();

    public boolean isDiscovered(int symbolIndex)
    {
        Integer isDiscovered = database.alchemyDao().isDiscovered(symbolIndex);
        return isDiscovered != null && isDiscovered == 1;
    }

    public List<Symbol> getUndiscovered()
    {
        return database.alchemyDao().getUndiscovered().stream()
                .map(Symbol::new)
                .collect(Collectors.toList());
    }

    public void discover(int symbolIndex)
    {
        database.alchemyDao().setDiscovered(symbolIndex);
    }

    public void undoscover(int symbolIndex)
    {
        database.alchemyDao().setUndiscovered(symbolIndex);
    }
}
