package pl.polsl.blissapp.data.model;

import androidx.annotation.DrawableRes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Radical
{
    WAVY_LINE,
    WAVY_LINE_HORIZONTAL(WAVY_LINE),
    WAVY_LINE_VERTICAL(WAVY_LINE),

    HEART,

    CROSSHATCH,
    CROSSHATCH_STRAIGHT(CROSSHATCH),
    CROSSHATCH_PITCHED(CROSSHATCH),

    BUILDING,

    EAR,

    ARROW,
    ARROW_NORTH(ARROW),
    ARROW_SOUTH(ARROW),
    ARROW_EAST(ARROW),
    ARROW_WEST(ARROW),

    DOT,

    WHEEL,

    CIRCLE,
    CIRCLE_LARGE(CIRCLE),
    CIRCLE_SMALL(CIRCLE),

    SEMICIRCLE,
    SEMICIRCLE_NORTH(SEMICIRCLE),
    SEMICIRCLE_EAST(SEMICIRCLE),
    SEMICIRCLE_SOUTH(SEMICIRCLE),

    ARC,
    ARC_NORTH(ARC),
    ARC_NORTHEAST(ARC),
    ARC_EAST(ARC),
    ARC_SOUTHEAST(ARC),
    ARC_SOUTH(ARC),
    ARC_SOUTHWEST(ARC),
    ARC_WEST(ARC),
    ARC_NORTHWEST(ARC),
    PARENTHESIS_RIGHT(ARC),
    PARENTHESIS_LEFT(ARC),

    SQUARE,
    SQUARE_LARGE(SQUARE),
    SQUARE_SMALL(SQUARE),

    OPEN_SQUARE,
    OPEN_SQUARE_LARGE_NORTH(OPEN_SQUARE),
    OPEN_SQUARE_LARGE_EAST(OPEN_SQUARE),
    OPEN_SQUARE_LARGE_SOUTH(OPEN_SQUARE),
    OPEN_SQUARE_SMALL_NORTH(OPEN_SQUARE),
    OPEN_SQUARE_SMALL_SOUTH(OPEN_SQUARE),
    OPEN_SQUARE_SMALL_SOUTHWEST(OPEN_SQUARE),

    RECTANGLE,
    RECTANGLE_LARGE(RECTANGLE),
    RECTANGLE_SMALL(RECTANGLE),

    OPEN_RECTANGLE,
    OPEN_RECTANGLE_NORTH(OPEN_RECTANGLE),
    OPEN_RECTANGLE_EAST(OPEN_RECTANGLE),
    OPEN_RECTANGLE_SOUTH_VERTICAL(OPEN_RECTANGLE),
    OPEN_RECTANGLE_SOUTH_HORIZONTAL(OPEN_RECTANGLE),

    RIGHT_ANGLE,
    RIGHT_ANGLE_LARGE_NORTH(RIGHT_ANGLE),
    RIGHT_ANGLE_LARGE_EAST(RIGHT_ANGLE),
    RIGHT_ANGLE_LARGE_SOUTH(RIGHT_ANGLE),
    RIGHT_ANGLE_LARGE_WEST(RIGHT_ANGLE),
    RIGHT_ANGLE_SMALL_NORTH(RIGHT_ANGLE),
    RIGHT_ANGLE_SMALL_EAST(RIGHT_ANGLE),
    RIGHT_ANGLE_SMALL_WEST(RIGHT_ANGLE),
    RIGHT_ANGLE_SMALL_SOUTHWEST(RIGHT_ANGLE),

    RIGHT_TRIANGLE,
    RIGHT_TRIANGLE_LARGE_NORTH(RIGHT_TRIANGLE),
    RIGHT_TRIANGLE_LARGE_EAST(RIGHT_TRIANGLE),
    RIGHT_TRIANGLE_LARGE_SOUTHEAST(RIGHT_TRIANGLE),
    RIGHT_TRIANGLE_SMALL_EAST(RIGHT_TRIANGLE),
    RIGHT_TRIANGLE_SMALL_SOUTHEAST(RIGHT_TRIANGLE),

    PIN,
    PIN_LARGE_NORTH(PIN),
    PIN_LARGE_NORTHEAST(PIN),
    PIN_LARGE_EAST(PIN),
    PIN_LARGE_SOUTH(PIN),
    PIN_LARGE_SOUTHWEST(PIN),
    PIN_SMALL_EAST(PIN),
    PIN_SMALL_SOUTH(PIN),
    PIN_SMALL_WEST(PIN),

    CROSS,
    CROSS_LARGE_ORTHOGONAL(CROSS),
    CROSS_LARGE_DIAGONAL(CROSS),
    CROSS_SMALL_ORTHOGONAL(CROSS),
    CROSS_SMALL_DIAGONAL(CROSS),

    ACUTE_ANGLE,
    ACUTE_ANGLE_LARGE_NORTH(ACUTE_ANGLE),
    ACUTE_ANGLE_LARGE_SOUTH(ACUTE_ANGLE),
    ACUTE_ANGLE_SMALL_NORTH(ACUTE_ANGLE),
    ACUTE_ANGLE_SMALL_SOUTH(ACUTE_ANGLE),

    ACUTE_ANGLE_WEIRD(ACUTE_ANGLE),
    // ACUTE_ANGLE_WEIRD_NORTH,
    // ACUTE_ANGLE_WEIRD_EAST
    // ACUTE_ANGLE_WEIRD_SOUTHEAST
    // ACUTE_ANGLE_WEIRD_SOUTH
    // ACUTE_ANGLE_WEIRD_SOUTHWEST
    // ACUTE_ANGLE_WEIRD_WEST

    ACUTE_TRIANGLE,
    ACUTE_TRIANGLE_LARGE_NORTH(ACUTE_TRIANGLE),
    ACUTE_TRIANGLE_SMALL_NORTH(ACUTE_TRIANGLE),
    ACUTE_TRIANGLE_SMALL_SOUTH(ACUTE_TRIANGLE),

    HORIZONTAL_LINE,
    HORIZONTAL_LINE_LARGE(HORIZONTAL_LINE),
    HORIZONTAL_LINE_SMALL(HORIZONTAL_LINE),

    VERTICAL_LINE,
    VERTICAL_LINE_LARGE(VERTICAL_LINE),
    VERTICAL_LINE_SMALL(VERTICAL_LINE),

    DIAGONAL_LINE,
    DIAGONAL_LINE_LARGE_NORTHEAST(DIAGONAL_LINE),
    DIAGONAL_LINE_LARGE_SOUTHEAST(DIAGONAL_LINE),
    DIAGONAL_LINE_SMALL_NORTHEAST(DIAGONAL_LINE),
    DIAGONAL_LINE_SMALL_SOUTHEAST(DIAGONAL_LINE),

    POINTER,
    POINTER_NORTH(POINTER),
    POINTER_NORTHEAST(POINTER),
    POINTER_EAST(POINTER),
    POINTER_SOUTHEAST(POINTER),
    POINTER_SOUTH(POINTER),
    POINTER_SOUTHWEST(POINTER),
    POINTER_WEST(POINTER),
    POINTER_NORTHWEST(POINTER),

    OTHER,
    PUNCTUATION(OTHER),
    DIGIT(OTHER),
    LETTER(OTHER); // TEMPORARY!

    private final Radical parent;

    Radical() {
        this.parent = null;
    }

    Radical(Radical parent) {
        assert parent == null || parent.parent == null : "Multi-layer hierarchy detected!";

        this.parent = parent;
    }

    public Radical getParent() {
        return parent;
    }

    private static final Map<Radical, List<Radical>> parenthood = new EnumMap<>(Radical.class);

    private static final List<Radical> PARENT_RADICALS;

    private static final List<Radical> CHILD_RADICALS;

    static {
        for (Radical radical : Radical.values()) {
            if (radical.parent == null) {
                parenthood.put(radical, new ArrayList<>());
            } else {
                List<Radical> children = parenthood.get(radical.parent);
                assert children != null: String.format("Child radical %s defined before its parent!",
                        radical.name());
                children.add(radical);
            }
        }

        PARENT_RADICALS = Arrays.stream(Radical.values())
                .filter(r -> r.parent == null)
                .collect(Collectors.toList());

        CHILD_RADICALS = Arrays.stream(Radical.values())
                .filter(r -> r.parent != null)
                .collect(Collectors.toList());
    }

    public static List<Radical> getChildren(Radical parent) {
        List<Radical> children = parenthood.get(parent);
        return children == null ? Collections.emptyList() : List.copyOf(children);
    }

    public static List<Radical> getParentRadicals() {
        return Collections.unmodifiableList(PARENT_RADICALS);
    }

    public static List<Radical> getChildRadicals() {
        return Collections.unmodifiableList(CHILD_RADICALS);
    }
}
