package pl.polsl.blissapp.ui.repository;

import java.util.Set;

import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.Symbol;

public interface AlchemyRepository
{
    void getGameState(Callback<Set<Symbol>, Exception> callback);
    void setGameState(Set<Symbol> newGameState);


}
