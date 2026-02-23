package pl.polsl.blissapp.data.model;

import junit.framework.TestCase;

import java.util.List;

import static pl.polsl.blissapp.data.model.Radical.*;

public class SymbolTest extends TestCase
{
    public void testMatchRadicals1()
    {
        // Exact match

        List<Radical> provided = List.of(CIRCLE_LARGE);
        List<Radical> required = List.of(CIRCLE_LARGE);

        assertEquals(0, Symbol.matchRadicals(provided, required));
    }

    public void testMatchRadicals2()
    {
        // Parent match

        List<Radical> provided = List.of(CIRCLE_LARGE);
        List<Radical> required = List.of(CIRCLE);

        assertEquals(0, Symbol.matchRadicals(provided, required));
    }

    public void testMatchRadicals3()
    {
        // Sibling match

        List<Radical> provided = List.of(CIRCLE_LARGE);
        List<Radical> required = List.of(CIRCLE_SMALL);

        assertEquals(1, Symbol.matchRadicals(provided, required));
    }

    public void testMatchRadicals4()
    {
        // No match

        List<Radical> provided = List.of(CIRCLE_LARGE);
        List<Radical> required = List.of(SQUARE);

        assertEquals(-1, Symbol.matchRadicals(provided, required));
    }

    public void testMatchRadicals5()
    {
        // Excessive match

        List<Radical> provided = List.of(CIRCLE_LARGE, CIRCLE_SMALL);
        List<Radical> required = List.of(CIRCLE_LARGE);

        assertEquals(1, Symbol.matchRadicals(provided, required));
    }

    public void testMatchRadicals6()
    {
        // Excessive match

        List<Radical> provided = List.of(CIRCLE_SMALL, CIRCLE_SMALL);
        List<Radical> required = List.of(CIRCLE_LARGE);

        assertEquals(2, Symbol.matchRadicals(provided, required));
    }

    public void testMatchRadicals7()
    {
        // Dolphin match

        List<Radical> provided = List.of(SEMICIRCLE_NORTH, WAVY_LINE_HORIZONTAL, ARC_EAST);

        List<Radical> required1 = List.of(SEMICIRCLE, WAVY_LINE, ARC);
        assertEquals(0, Symbol.matchRadicals(provided, required1));

        List<Radical> required2 = List.of(SEMICIRCLE, WAVY_LINE);
        assertEquals(1, Symbol.matchRadicals(provided, required2));

        List<Radical> required3 = List.of(WAVY_LINE);
        assertEquals(2, Symbol.matchRadicals(provided, required3));

        List<Radical> required4 = List.of(SEMICIRCLE_NORTH, WAVY_LINE);
        assertEquals(1, Symbol.matchRadicals(provided, required4));

        List<Radical> required5 = List.of(SEMICIRCLE_SOUTH, WAVY_LINE_HORIZONTAL);
        assertEquals(2, Symbol.matchRadicals(provided, required5));

        List<Radical> required6 = List.of(SEMICIRCLE, WAVY_LINE, WAVY_LINE);
        assertEquals(-1, Symbol.matchRadicals(provided, required6));
    }

    public void testMatchRadicals8()
    {
        // Priestly match

        List<Radical> priest = List.of(PIN_LARGE_NORTH, // person
                                         SEMICIRCLE_SOUTH, ARROW_NORTH, // giving
                                         SEMICIRCLE_NORTH, OPEN_RECTANGLE_SOUTH_HORIZONTAL, // knowledge
                                         ACUTE_ANGLE_LARGE_NORTH, ACUTE_ANGLE_LARGE_NORTH); // [about] God

        List<Radical> wheel = List.of(WHEEL);
        assertEquals(-1, Symbol.matchRadicals(priest, wheel));

        List<Radical> person1 = List.of(PIN);
        assertEquals(6, Symbol.matchRadicals(priest, person1));

        List<Radical> person2 = List.of(PIN_LARGE_NORTH);
        assertEquals(6, Symbol.matchRadicals(priest, person2));

        List<Radical> person3 = List.of(PIN_SMALL_SOUTH);
        assertEquals(7, Symbol.matchRadicals(priest, person3));

        List<Radical> wisdom = List.of(SEMICIRCLE_NORTH, OPEN_RECTANGLE_SOUTH_HORIZONTAL);
        assertEquals(5, Symbol.matchRadicals(priest, wisdom));

        List<Radical> wisdomUpsideDown = List.of(SEMICIRCLE_SOUTH, OPEN_RECTANGLE_NORTH);
        assertEquals(6, Symbol.matchRadicals(priest, wisdomUpsideDown));
    }
}