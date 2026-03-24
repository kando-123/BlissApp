package pl.polsl.blissapp.data.repository;

import static java.lang.Math.min;

import android.util.Log;
import android.util.LruCache;

import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import pl.polsl.blissapp.BlissApplication;
import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.common.exception.NoResultsException;
import pl.polsl.blissapp.data.model.Translation;
import pl.polsl.blissapp.data.model.Primitive;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.data.room.BlissDatabase;
import pl.polsl.blissapp.data.room.dto.SymbolDto;
import pl.polsl.blissapp.data.room.dto.TranslationDto;
import pl.polsl.blissapp.data.room.dto.VariantDto;
import pl.polsl.blissapp.data.room.entity.SymbolImageEntity;
import pl.polsl.blissapp.ui.repository.SymbolRepository;

public class SymbolRepositoryImpl implements SymbolRepository
{
    private final BlissDatabase database = BlissApplication.getDatabase();

    /**
     * Retrieves the list of components (component indices) for the given symbol.
     * Internally, the list is either cached or fetched from the database.
     */
    private final LruCache<Integer, List<Integer>> mSymbolComponentsCache = new LruCache<>(500)
    {
        @Override
        protected List<Integer> create(Integer symbolIdx)
        {
            return database.symbolDao().getComponents(symbolIdx);
        }
    };

    /**
     * Retrieves the list of variants for the given component.
     * Internally, the list is either cached or fetched from the database.
     */
    private final LruCache<Integer, List<Map<Primitive, Integer>>> mComponentVariantsCache = new LruCache<>(500)
    {
        @Override
        protected List<Map<Primitive, Integer>> create(Integer componentIdx)
        {
            List<VariantDto> variantDtos = database.symbolDao().getVariants(componentIdx);
            TreeMap<Integer, Map<Primitive, Integer>> variants = new TreeMap<>();
            for (VariantDto dto : variantDtos)
            {
                variants.computeIfAbsent(dto.variant, v -> new HashMap<>())
                        .put(Primitive.valueOf(dto.primitiveCode), dto.primitiveCount);
            }
            List<Map<Primitive, Integer>> list = new ArrayList<>(variants.size());

            for (Map.Entry<Integer, Map<Primitive, Integer>> entry;
                 !variants.isEmpty();
                 variants.remove(entry.getKey()))
            {
                entry = variants.pollFirstEntry();
                list.add(entry.getValue());
            }

            return list;
        }
    };

    /**
     * Retrieves the symbols that begin with given prefix, if any, and have a suffix that contains
     * given primitives, with parent and sibling matches allowed. At most {@code maxCount} symbols
     * will be returned. They will be ordered so that the best matches come first, followed by the
     * worse ones.
     * <p>
     * <em>Important</em>: The callback will be invoked from another thread.
     *
     * @param prefixSymbol the symbol that the results are expected to begin with, or null
     * @param suffixPrimitives the primitives that the suffix components are expected to contain
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

        Thread worker = new Thread(() ->
        {
            try
            {
                List<Integer> prefixComponents = prefixSymbol != null
                        ? mSymbolComponentsCache.get(prefixSymbol.index())
                        : Collections.emptyList();

                SimpleSQLiteQuery query = getQuery(prefixComponents, suffixPrimitives);

                Log.d("Hint", "Querying database...");
                List<SymbolDto> dtos = database.symbolDao().getSymbols(query);
                Log.d("Hint", "...done querying database; " + dtos.size() + " records found.");

                List<Symbol> hints = new ArrayList<>(maxCount);

                record Candidate(SymbolDto dto, int match) implements Comparable<Candidate>
                {
                    @Override
                    public int compareTo(Candidate that) { return Integer.compare(this.match, that.match); }
                }
                Queue<Candidate> candidates = new PriorityQueue<>();

                final int nPrimitives = suffixPrimitives.values().stream().mapToInt(Integer::intValue).sum();
                for (SymbolDto dto : dtos)
                {
                    // The best possible match for this dto's symbol is achieved when all the required
                    // primitives find a good (i.e. exact or parent) match - no penalty points.
                    // Then, as many penalty points are counted as the difference between the minimal
                    // size and the number of required primitives.
                    final int bestPossibleMatch = Math.max(dto.minSize - nPrimitives, 0);

                    // Add all the symbols that are better than the best match achievable for this very dto.
                    while (hints.size() < maxCount && !candidates.isEmpty() && candidates.element().match < bestPossibleMatch)
                    {
                        SymbolDto symbol = candidates.remove().dto;
                        hints.add(new Symbol(symbol.index));
                    }

                    // Match this dto
                    if (hints.size() < maxCount)
                    {
                        // Get suffix components
                        List<Integer> components = mSymbolComponentsCache.get(dto.index);
                        List<Integer> suffixComponents = components.subList(prefixComponents.size(), components.size());

                        // Do match
                        int match = matchSuffix(suffixComponents, suffixPrimitives);

                        if (match == 0) // Add immediately in case of exact match
                        {
                            hints.add(new Symbol(dto.index));
                        }
                        else if (match > 0) // Enqueue if it is an inexact match
                        {
                            candidates.add(new Candidate(dto, match));
                        }
                    }
                    else
                    {
                        break;
                    }
                }

                // Flush the candidates
                while (hints.size() < maxCount && !candidates.isEmpty())
                {
                    SymbolDto symbol = candidates.remove().dto;
                    hints.add(new Symbol(symbol.index));
                }

                callback.onSuccess(hints);
            }
            catch (Exception exc)
            {
                Log.d("Hint", "Failure", exc);
                callback.onFailure(exc);
            }
        });
        worker.start();
    }

    private SimpleSQLiteQuery getQuery(List<Integer> prefixComponents, Map<Primitive, Integer> suffixPrimitives)
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
                SELECT S."symbol_index", QS."min_size"
                FROM "Symbol" S
                JOIN "QualifiedSymbol" QS ON S."symbol_index" = QS."symbol_index"
                ORDER BY QS."min_size", QS."max_size";
                """;
        Log.v("Query", query);
        return new SimpleSQLiteQuery(query);
    }

    private String prefixFilterClause(List<Integer> prefix)
    {
        if (prefix.isEmpty())
        {
            return "";
        }

        StringBuilder clause = new StringBuilder("WHERE ");
        for (int i = 0; i < prefix.size(); ++i)
        {
            int index = prefix.get(i);
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

    public static final int MATCH_FAILURE = -1;

    private int matchSuffix(List<Integer> suffixComponents,
                            Map<Primitive, Integer> requiredPrimitives)
    {
        final int suffixSize = suffixComponents.size();

        // Counter of the variants used in incumbent variance:
        //     if position [i] contains value of j,
        //     then the i-th component is used in its j-th variant.
        // *The "i-th" in "the i-th component" is the position in the list, not the index in the DB!
        int[] varianceCounter = new int[suffixSize];

        // Fetch the variants of the components
        List<List<Map<Primitive, Integer>>> allComponentsVariants = new ArrayList<>(suffixComponents.size());
        for (var componentIndex : suffixComponents) // componentIndex is the in-DB index
        {
            allComponentsVariants.add(mComponentVariantsCache.get(componentIndex));
        }
        // From here, the component's in-DB index is irrelevant
        // and the component is referred to only by its in-list position.

        int minMatch = MATCH_FAILURE;
        boolean hasNext;
        do
        {
            // Count the primitives for the current variance
            Map<Primitive, Integer> variancePrimitives = new EnumMap<>(Primitive.class);

            // For every component in the suffix:
            for (int i = 0; i < suffixSize; ++i)
            {
                // All variants of the component
                List<Map<Primitive, Integer>> theComponentVariants = allComponentsVariants.get(i);

                // The variant of the component that is considered in this variance
                Map<Primitive, Integer> theVariant = theComponentVariants.get(varianceCounter[i]);

                // Add to the variance primitive multiset
                for (var entry : theVariant.entrySet())
                {
                    Primitive primitive = entry.getKey();
                    int count = entry.getValue();
                    variancePrimitives.merge(primitive, count, Integer::sum);
                }
            }

            int result = matchPrimitives(variancePrimitives, requiredPrimitives);
            if (minMatch == MATCH_FAILURE || result < minMatch)
            {
                minMatch = result;
            }

            // Advance the counter
            hasNext = false;
            for (int i = 0; i < suffixSize; ++i)
            {
                if (++varianceCounter[i] < allComponentsVariants.get(i).size())
                {
                    hasNext = true;
                    break;
                }
                varianceCounter[i] = 0;
            }
        }
        while (hasNext);

        return minMatch;
    }

    /**
     * The method evaluates how the provided radicals match the required ones.
     *
     * <p>If for every required radical, there is a matching radical provided, the match is fully
     * successful and 0 is returned. A provided radical matches a required one if they are equal,
     * or if the provided radical is a child of the required radical (a specific radical satisfies
     * a general requirement).</p>
     *
     * <p>A general requirement may be covered by equal or child radicals provided. A specific
     * requirement may be covered by equal or sibling radicals provided. If a requirement is not
     * covered, the match is failed and -1 is returned.</p>
     *
     * <p>The returned result is equal to the number of sibling matches plus the number of radicals
     * excessively provided in the symbol, which translates to a non-negative number, with smaller
     * numbers indicating a better match.</p>
     *
     * @param provided What this symbol provides
     * @param required What the user requires
     *
     * @return
     */
    private static int matchPrimitives(Map<Primitive, Integer> provided,
                                       Map<Primitive, Integer> required)
    {
        // If more is required than is provided, the match is failed.
        int requiredCount = required.values().stream().mapToInt(Integer::intValue).sum();
        int providedCount = provided.values().stream().mapToInt(Integer::intValue).sum();
        if (requiredCount > providedCount)
        {
            return MATCH_FAILURE;
        }

        // Count the radicals provided as positive,
        // count the radicals required as negative.
        int[] counter = new int[Primitive.values().length];

        // Step 1. Cancel out the identical radicals.
        for (var entry : provided.entrySet())
        {
            counter[entry.getKey().ordinal()] += entry.getValue();
        }
        for (var entry : required.entrySet())
        {
            counter[entry.getKey().ordinal()] -= entry.getValue();
        }

        // Step 2. Try to match the provided specific radicals (the "children") to the required
        // general radicals (the "parents").
        for (Primitive child : Primitive.getChildPrimitives())
        {
            Primitive parent = child.getParent();
            assert parent != null;

            if (counter[child.ordinal()] > 0)
            {
                counter[parent.ordinal()] += counter[child.ordinal()];
                counter[child.ordinal()] = 0;
            }
        }

        // If any "parent" has a negative score now, it means that the more general requirement
        // (the parent) was not covered by the provided specific radicals (the children).
        if (Primitive.getParentPrimitives().stream().anyMatch(r -> counter[r.ordinal()] < 0))
        {
            return MATCH_FAILURE;
        }

        // Step 3. If a radical is provided in one form but required in another form (a "sibling"),
        // the symbol still may count as a match, but with a lower preference (result > 0).
        // The points will be aggregated in the parent's counters.
        int result = 0;
        for (Primitive child : Primitive.getChildPrimitives())
        {
            Primitive parent = child.getParent();
            assert parent != null;

            // Count the reductions to the result.
            if (counter[child.ordinal()] < 0 && counter[parent.ordinal()] > 0)
            {
                result += min(-counter[child.ordinal()], +counter[parent.ordinal()]);
            }

            // Pass the score to the parent.
            counter[parent.ordinal()] += counter[child.ordinal()];
            counter[child.ordinal()] = 0;
        }

        // If we still have an unsatisfied requirement (a negative counter), the match is failed.
        // If there are excessively provided symbols, the result is worse (> 0).
        for (int count : counter)
        {
            if (count < 0)
            {
                return MATCH_FAILURE;
            }
            result += count;
        }
        return result;
    }

    @Override
    public void getSvg(Symbol symbol, Callback<String, Exception> callback)
    {
        Thread worker = new Thread(() ->
        {
            try
            {
                SymbolImageEntity imageEntity = database.symbolDao().getSymbolImage(symbol.index());
                if (imageEntity == null)
                {
                    callback.onFailure(new Exception("Symbol image not found in database"));
                    return;
                }

                String svgTemplate = """
                    <svg xmlns="http://www.w3.org/2000/svg" fill-rule="evenodd" \
                    preserveAspectRatio="none" stroke-linecap="round" width="%sin" \
                    height="4.5in" viewBox="0 0 %s 324">
                        <style type="text/css">
                            .p { stroke: rgb(0,0,0); stroke-width: 7; stroke-linejoin: round; }
                            .f { font-size: 78px; font-family: Arial; }
                        </style>
                        %s
                    </svg>
                    """;

                String svg = String.format(svgTemplate, imageEntity.width, imageEntity.view_box_width, imageEntity.content);

                callback.onSuccess(svg);
            }
            catch (Exception e)
            {
                callback.onFailure(e);
            }
        });
        worker.start();
    }
}
