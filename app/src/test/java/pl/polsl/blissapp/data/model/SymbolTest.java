package pl.polsl.blissapp.data.model;

import junit.framework.TestCase;

import java.util.List;

import pl.polsl.blissapp.common.Radical;
import static pl.polsl.blissapp.common.Radical.*;

public class SymbolTest extends TestCase
{
    public void testMatch1()
    {
        // Exact match

        List<Radical> provided = List.of(CIRCLE_LARGE);
        List<Radical> required = List.of(CIRCLE_LARGE);

        assertEquals(0, Symbol.match(provided, required));
    }

    public void testMatch2()
    {
        // Parent match

        List<Radical> provided = List.of(CIRCLE_LARGE);
        List<Radical> required = List.of(CIRCLE);

        assertEquals(0, Symbol.match(provided, required));
    }

    public void testMatch3()
    {
        // Sibling match

        List<Radical> provided = List.of(CIRCLE_LARGE);
        List<Radical> required = List.of(CIRCLE_SMALL);

        assertEquals(1, Symbol.match(provided, required));
    }

    public void testMatch4()
    {
        // No match

        List<Radical> provided = List.of(CIRCLE_LARGE);
        List<Radical> required = List.of(SQUARE);

        assertEquals(-1, Symbol.match(provided, required));
    }

    public void testMatch5()
    {
        // Excessive match

        List<Radical> provided = List.of(CIRCLE_LARGE, CIRCLE_SMALL);
        List<Radical> required = List.of(CIRCLE_LARGE);

        assertEquals(1, Symbol.match(provided, required));
    }

    public void testMatch6()
    {
        // Excessive match

        List<Radical> provided = List.of(CIRCLE_SMALL, CIRCLE_SMALL);
        List<Radical> required = List.of(CIRCLE_LARGE);

        assertEquals(2, Symbol.match(provided, required));
    }

    public void testMatch7()
    {
        // Dolphin match

        List<Radical> provided = List.of(SEMICIRCLE_NORTH, WAVY_LINE_HORIZONTAL, ARC_EAST);

        List<Radical> required1 = List.of(SEMICIRCLE, WAVY_LINE, ARC);
        assertEquals(0, Symbol.match(provided, required1));

        List<Radical> required2 = List.of(SEMICIRCLE, WAVY_LINE);
        assertEquals(1, Symbol.match(provided, required2));

        List<Radical> required3 = List.of(WAVY_LINE);
        assertEquals(2, Symbol.match(provided, required3));

        List<Radical> required4 = List.of(SEMICIRCLE_NORTH, WAVY_LINE);
        assertEquals(1, Symbol.match(provided, required4));

        List<Radical> required5 = List.of(SEMICIRCLE_SOUTH, WAVY_LINE_HORIZONTAL);
        assertEquals(2, Symbol.match(provided, required5));

        List<Radical> required6 = List.of(SEMICIRCLE, WAVY_LINE, WAVY_LINE);
        assertEquals(-1, Symbol.match(provided, required6));
    }

    public void testMatch8()
    {
        // Priestly match

        List<Radical> priest = List.of(PIN_LARGE_NORTH, // person
                                         SEMICIRCLE_SOUTH, ARROW_NORTH, // giving
                                         SEMICIRCLE_NORTH, OPEN_RECTANGLE_SOUTH_HORIZONTAL, // knowledge
                                         ACUTE_ANGLE_LARGE_NORTH, ACUTE_ANGLE_LARGE_NORTH); // [about] God

        List<Radical> wheel = List.of(WHEEL);
        assertEquals(-1, Symbol.match(priest, wheel));

        List<Radical> person1 = List.of(PIN);
        assertEquals(6, Symbol.match(priest, person1));

        List<Radical> person2 = List.of(PIN_LARGE_NORTH);
        assertEquals(6, Symbol.match(priest, person2));

        List<Radical> person3 = List.of(PIN_SMALL_SOUTH);
        assertEquals(7, Symbol.match(priest, person3));

        List<Radical> wisdom = List.of(SEMICIRCLE_NORTH, OPEN_RECTANGLE_SOUTH_HORIZONTAL);
        assertEquals(5, Symbol.match(priest, wisdom));

        List<Radical> wisdomUpsideDown = List.of(SEMICIRCLE_SOUTH, OPEN_RECTANGLE_NORTH);
        assertEquals(6, Symbol.match(priest, wisdomUpsideDown));
    }
}