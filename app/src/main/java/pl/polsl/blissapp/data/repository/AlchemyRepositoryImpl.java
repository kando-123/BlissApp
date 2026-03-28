package pl.polsl.blissapp.data.repository;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.polsl.blissapp.BlissApplication;
import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.data.room.BlissDatabase;
import pl.polsl.blissapp.data.room.entity.AlchemyProgressEntity;
import pl.polsl.blissapp.ui.repository.AlchemyRepository;

@Singleton
public class AlchemyRepositoryImpl implements AlchemyRepository {
    private final BlissDatabase database = BlissApplication.getDatabase();

    @Inject
    public AlchemyRepositoryImpl() {}

    @Override
    public void getDiscovered(Callback<List<Symbol>, Exception> callback) {
        new Thread(() -> {
            try {
                List<Symbol> symbols = database.alchemyDao().getDiscovered().stream()
                        .map(Symbol::new)
                        .collect(Collectors.toList());
                if (callback != null) callback.onSuccess(symbols);
            } catch (Exception e) {
                if (callback != null) callback.onFailure(e);
            }
        }).start();
    }

    @Override
    public void getUndiscovered(Callback<List<Symbol>, Exception> callback) {
        new Thread(() -> {
            try {
                List<Symbol> symbols = database.alchemyDao().getUndiscovered().stream()
                        .map(Symbol::new)
                        .collect(Collectors.toList());
                if (callback != null) callback.onSuccess(symbols);
            } catch (Exception e) {
                if (callback != null) callback.onFailure(e);
            }
        }).start();
    }

    @Override
    public void getRandomUndiscovered(Callback<Symbol, Exception> callback) {
        new Thread(() -> {
            try {
                Integer index = database.alchemyDao().getRandomUndiscovered();
                if (callback != null) {
                    if (index != null) {
                        callback.onSuccess(new Symbol(index));
                    } else {
                        callback.onSuccess(null);
                    }
                }
            } catch (Exception e) {
                if (callback != null) callback.onFailure(e);
            }
        }).start();
    }

    @Override
    public void setDiscovered(Symbol symbol, Callback<Void, Exception> callback) {
        new Thread(() -> {
            try {
                AlchemyProgressEntity entity = new AlchemyProgressEntity();
                entity.symbolIndex = symbol.index();
                entity.isDiscovered = 1;
                database.alchemyDao().upsertProgress(entity);
                if (callback != null) callback.onSuccess(null);
            } catch (Exception e) {
                if (callback != null) callback.onFailure(e);
            }
        }).start();
    }
}
