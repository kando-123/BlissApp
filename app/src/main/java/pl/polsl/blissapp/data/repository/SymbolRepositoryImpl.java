package pl.polsl.blissapp.data.repository;

import android.util.Log;
import android.util.LruCache;

import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import pl.polsl.blissapp.BlissApplication;
import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.Component;
import pl.polsl.blissapp.data.model.MeaningfulSymbol;
import pl.polsl.blissapp.data.model.Primitive;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.data.room.BlissDatabase;
import pl.polsl.blissapp.data.room.dto.SymbolDto;
import pl.polsl.blissapp.ui.repository.SymbolRepository;

public class SymbolRepositoryImpl implements SymbolRepository
{
    private final BlissDatabase database = BlissApplication.getDatabase();

    /**
     * Retrieves the symbols that match given filter.
     * <p>
     * If {@code symbol} is {@code null}, the result contains {@code SimpleSymbol}s that contain
     * all given {@code primitives} (and possibly some else).
     * <p>
     * If {@code symbol} is not {@code null}, the result contains {@code CompoundSymbol}s that
     * begin with the given {@code symbol}, and whose remaining part contains all given {@code
     * primitives} (and possibly some else).
     * <p>
     * The returned list contains at most {@code maxCount} elements. They are sorted according to
     * how exact the match is, i.e. increasingly with respect to the value of {@code matches} method.
     *
     * @param prefixSymbol the symbol that the results are expected to begin with, or null
     * @param suffixPrimitives the primitives that the set of primitives of the simple symbol,
     *                   or the set of remaining primitives of the compound symbol,
     *                   are required to contain
     * @param maxCount the maximum number of symbols to return
     * @param callback the callback that will be called with the results, or failure
     */
    @Override
    public void getMatchingSymbols(Symbol prefixSymbol,
                                   Map<Primitive, Integer> suffixPrimitives,
                                   int maxCount,
                                   Callback<List<Symbol>, Exception> callback)
    {
        if (maxCount <= 0)
        {
            callback.onFailure(new IllegalArgumentException("maxCount must be positive"));
            return;
        }

        List<Component> prefixComponents = prefixSymbol != null
                ? prefixSymbol.components()
                : Collections.emptyList();
        SimpleSQLiteQuery query = getQuery(prefixComponents, suffixPrimitives);

        // final String tag = "SymbolRepositoryImpl";
        // Log.d(tag, "Getting the matching symbols");
        Thread worker = new Thread(() ->
        {
            // Log.d(tag, "Starting the worker thread");
            try
            {
                // Log.d(tag, "Querying database");
                List<SymbolDto> dtos = database.symbolDao().getSymbols(query);
                // Log.d(tag, "Done querying database");

                // if (dtos.isEmpty())
                //     Log.d(tag, "No symbols found");
                // else for (SymbolDto dto : dtos)
                //     Log.d(tag, "Found symbol #" + dto.index + " URI='" + dto.resourceUri + "'");

                List<Symbol> symbols = dtos.stream()
                        .map(dto -> new Symbol(dto.index, dto.resourceUri, Collections.emptyList()))
                        .collect(Collectors.toList());
                callback.onSuccess(symbols);
            }
            catch (Exception exc)
            {
                // Log.d(tag, "Error querying database", exc);
                callback.onFailure(exc);
            }
            // Log.d(tag, "Finished the worker thread");
        });
        worker.start();
    }

    private SimpleSQLiteQuery getQuery(List<Component> prefixComponents, Map<Primitive, Integer> suffixPrimitives)
    {
        Set<Primitive> rootSet = suffixPrimitives.keySet().stream()
                .map(Primitive::getRoot)
                .collect(Collectors.toSet());
        int nPrimitives = suffixPrimitives.values().stream().mapToInt(Integer::intValue).sum();
        String query = """
                WITH "PrefixedSymbol" AS
                (
                    SELECT DISTINCT "symbol_index" FROM "Composition"
                """
                + prefixFilterClause(prefixComponents)
                + """
                ),
                "SuffixComponents" AS
                (
                    SELECT C."symbol_index", C."component_index", C."component_position"
                    FROM "Composition" C
                    JOIN "PrefixedSymbol" PS ON C."symbol_index" = PS."symbol_index"
                    WHERE C."component_position" >=\s""" + prefixComponents.size()
                + """
                ),
                "RemainingComponent" AS
                (
                    SELECT DISTINCT "component_index"
                    FROM "SuffixComponents"
                ),
                "VariantSize" AS
                (
                    SELECT D."component_index", D."variant", SUM(D."primitive_count") AS "variant_size"
                    FROM "Definition" D
                    JOIN "RemainingComponent" RC ON RC."component_index" = D."component_index"
                    GROUP BY D."component_index", D."variant"
                ),
                "ComponentSize" AS
                (
                    SELECT "component_index", MIN("variant_size") AS "min_size", MAX("variant_size") AS "max_size"
                    FROM "VariantSize"
                    GROUP BY "component_index"
                ),
                "PromisingSymbol" AS
                (
                    SELECT SC."symbol_index"
                    FROM "SuffixComponents" SC
                    JOIN "Definition" D ON SC."component_index" = D."component_index"
                    JOIN "Primitive" P ON D."primitive_code" = P."primitive_code"
                """ + primitiveRootFilter(rootSet)
                + """
                    GROUP BY SC."symbol_index"
                    HAVING COUNT(DISTINCT IFNULL(P."parent_code", P."primitive_code")) =\s""" + rootSet.size()
                + """
                ),
                "QualifiedSymbol" AS
                (
                	SELECT PS."symbol_index", SUM(CS."min_size") AS "min_size", SUM(CS."max_size") AS "max_size"
                	FROM "PromisingSymbol" PS
                	JOIN "Composition" C ON C."symbol_index" = PS."symbol_index"
                	JOIN "ComponentSize" CS ON CS."component_index" = C."component_index"
                	GROUP BY PS."symbol_index"
                	HAVING SUM(CS."max_size") >=\s""" + nPrimitives
                + """
                )
                SELECT S."symbol_index", S."resource_uri"
                FROM "Symbol" S
                JOIN "QualifiedSymbol" QS ON S."symbol_index" = QS."symbol_index"
                ORDER BY QS."min_size", QS."max_size";
                """;
        Log.v("Query", query);
        return new SimpleSQLiteQuery(query);
    }

    private String prefixFilterClause(List<Component> prefix)
    {
        if (prefix.isEmpty())
        {
            return "";
        }

        StringBuilder clause = new StringBuilder("WHERE ");
        for (int i = 0; i < prefix.size(); ++i)
        {
            int index = prefix.get(i).index();
            if (i > 0)
            {
                clause.append("OR ");
            }
            clause.append("(\"component_position\" = ")
                    .append(i)
                    .append(" AND \"component_index\" = ")
                    .append(index)
                    .append(") ");
        }
        clause.append("GROUP BY \"symbol_index\" HAVING COUNT(*) = ")
                .append(prefix.size());

        return clause.toString();
    }

    private String primitiveRootFilter(Set<Primitive> roots)
    {
        if (roots.isEmpty())
        {
            return "";
        }

        StringBuilder vector = new StringBuilder("WHERE IFNULL(P.\"parent_code\", P.\"primitive_code\") IN (");
        int i = 0;
        for (Primitive root : roots)
        {
            vector.append("'").append(root.name()).append("'").append(++i < roots.size() ? "," : ")");
        }
        return vector.toString();
    }



    @Override
    public void getMeanings(Symbol symbol,
                            Callback<List<String>, Exception> callback)
    {

    }

    @Override
    public void getTranslations(String input,
                                Callback<List<MeaningfulSymbol>, Exception> callback)
    {

    }
}
