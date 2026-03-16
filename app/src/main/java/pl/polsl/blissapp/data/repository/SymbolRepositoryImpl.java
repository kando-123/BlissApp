package pl.polsl.blissapp.data.repository;

import android.util.LruCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import pl.polsl.blissapp.BlissApplication;
import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.Component;
import pl.polsl.blissapp.data.model.MeaningfulSymbol;
import pl.polsl.blissapp.data.model.Primitive;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.data.room.BlissDatabase;
import pl.polsl.blissapp.ui.repository.SymbolRepository;

public class SymbolRepositoryImpl implements SymbolRepository
{
    private final BlissDatabase database = BlissApplication.getDatabase();

    private final LruCache<Integer, Component> componentCache = new LruCache<>(500);
    private final LruCache<Integer, Symbol> symbolCache = new LruCache<>(1000);

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
     * @param symbol the symbol that the results are expected to begin with, or null
     * @param primitives the primitives that the set of primitives of the simple symbol,
     *                   or the set of remaining primitives of the compound symbol,
     *                   are required to contain
     * @param maxCount the maximum number of symbols to return
     * @param callback the callback that will be called with the results, or failure
     */
    @Override
    public void getMatchingSymbols(Symbol symbol,
                                   List<Primitive> primitives,
                                   int maxCount,
                                   Callback<List<Symbol>, Exception> callback)
    {
        if (maxCount <= 0)
        {
            callback.onFailure(new IllegalArgumentException("maxCount must be positive"));
            return;
        }

        List<Symbol> symbols = new ArrayList<>(maxCount);
        List<Component> components = symbol.components();
        Set<Primitive> roots = primitives.stream()
                .map(Primitive::getRoot)
                .collect(Collectors.toSet());

        String query = getQuery(components, roots, primitives.size());



    }

    private String getQuery(List<Component> components, Set<Primitive> roots, int nPrimitives)
    {
        StringBuilder query = new StringBuilder("""
                WITH "QualifiedSymbols" AS
                (
                    SELECT
                        "symbol_index"
                    FROM
                        "Composition"
                """)
                .append(createInitialComponentsFilter(components))
                .append("""
                ),
                "ExcessComponents" AS
                (
                    SELECT
                        "symbol_index", "component_index", "component_position"
                    FROM
                        "Composition"
                    JOIN
                        "QualifiedSymbols" ON "Composition"."symbol_index" = "QualifiedSymbols"."symbol_index"
                    WHERE
                        "Composition"."component_position" >=\s""").append(components.size())
                .append("""
                ),
                "MatchingRoots" AS
                (
                    SELECT
                        "ExcessComponents"."symbol_index"
                    FROM
                        "ExcessComponents"
                    JOIN
                        "Definition" ON "ExcessComponents"."component_index" = "Definition"."component_index"
                    JOIN
                        "Primitive" ON "Definition"."primitive_code" = "Primitive"."primitive_code"
                    WHERE
                        IFNULL("Primitive"."parent_code", "Primitive"."primitive_code") IN\s""").append(getRootList(roots))
                .append("""
                    GROUP BY
                        "ExcessComponents"."symbol_index"
                    HAVING
                        COUNT(DISTINCT IFNULL("Primitive"."parent_code", "Primitive"."primitive_code")) =\s""").append(roots.size())
                .append("""
                ),
                "RemainingCounts" AS
                (
                    SELECT
                        "ExcessComponents"."symbol_index",
                        MIN("VariantSum"."variant_sum") AS "min_primitive_count"
                    FROM
                        "ExcessComponents"
                    JOIN
                        "MatchingRoots" ON "ExcessComponents"."symbol_index" = "MatchingRoots"."symbol_index"
                    JOIN
                        (
                            SELECT
                                "component_index",
                                "variant",
                                SUM("primitive_count") AS "variant_sum"
                            FROM
                                "Definition"
                            GROUP BY
                                "component_index",
                                "variant"
                        )
                        AS "VariantSum" ON "ExcessComponents"."component_index" = "VariantSum"."component_index"
                    GROUP BY
                        "ExcessComponents"."symbol_index",
                        "ExcessComponents"."component_position"
                ),
                "Indices" AS
                (
                    SELECT
                        "QualifiedSymbols"."symbol_index",
                        IFNULL(SUM("RemainingCounts"."min_primitive_count"), 0) AS "total_min_size"
                    FROM
                        "QualifiedSymbols"
                    JOIN
                        "RemainingCounts" ON "QualifiedSymbols"."symbol_index" = "RemainingCounts"."symbol_index"
                    WHERE
                        "QualifiedSymbols"."symbol_index" IN "MatchingRoots"
                    GROUP BY
                        "QualifiedSymbols"."symbol_index"
                    HAVING
                        "total_min_size" >=\s""").append(nPrimitives)
                .append("""
                )
                SELECT
                    "Symbol"."symbol_index",
                    "Symbol"."resource_uri",
                    "Composition"."component_position",
                    "Composition"."component_index",
                    "Definition"."variant",
                    "Definition"."primitive_code",
                    "Definition"."primitive_count",
                    "Indices"."total_min_weight"
                FROM
                    "Symbol"
                JOIN
                    "Composition" ON "Symbol"."symbol_index" = "Composition"."symbol_index"
                JOIN
                    "Definition" ON "Composition"."component_index" = "Definition"."component_index"
                JOIN
                    "Indices" ON "Symbol"."symbol_index" IN "Indices"."symbol_index"
                ORDER BY
                    "Indices"."total_min_weight" ASC,
                    "Symbol"."symbol_index",
                    "Composition"."component_position",
                    "Definition"."variant"
                """);
        return query.toString();
    }

    private String createInitialComponentsFilter(List<Component> components)
    {
        StringBuilder clause = new StringBuilder();
        if (!components.isEmpty())
        {
            clause.append("WHERE ");
            for (int i = 0; i < components.size(); ++i)
            {
                int index = components.get(i).index();
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
                    .append(components.size());
        }
        return clause.toString();
    }

    private String getRootList(Set<Primitive> roots)
    {
        StringBuilder vector = new StringBuilder("(");
        int i = 0;
        for (Primitive root : roots)
        {
            vector.append("'").append(root.name()).append("'").append(++i < roots.size() ? ", " : ")");
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
