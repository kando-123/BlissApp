package pl.polsl.blissapp.data.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Primitive
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

    WEIRD_ANGLE,
    WEIRD_ANGLE_NORTH(WEIRD_ANGLE),
    WEIRD_ANGLE_EAST(WEIRD_ANGLE),
    WEIRD_ANGLE_SOUTHEAST(WEIRD_ANGLE),
    WEIRD_ANGLE_SOUTH(WEIRD_ANGLE),
    WEIRD_ANGLE_SOUTHWEST(WEIRD_ANGLE),
    WEIRD_ANGLE_WEST(WEIRD_ANGLE),

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
    DIAGONAL_LINE_LARGE_UPWARD(DIAGONAL_LINE),
    DIAGONAL_LINE_LARGE_DOWNWARD(DIAGONAL_LINE),
    DIAGONAL_LINE_SMALL_UPWARD(DIAGONAL_LINE),
    DIAGONAL_LINE_SMALL_DOWNWARD(DIAGONAL_LINE),

    POINTER,
    POINTER_NORTH(POINTER),
    POINTER_NORTHEAST(POINTER),
    POINTER_EAST(POINTER),
    POINTER_SOUTHEAST(POINTER),
    POINTER_SOUTH(POINTER),
    POINTER_SOUTHWEST(POINTER),
    POINTER_WEST(POINTER),
    POINTER_NORTHWEST(POINTER),

    PUNCTUATION,
    QUESTION_MARK(PUNCTUATION),
    EXCLAMATION_MARK(PUNCTUATION),
	PERCENT_MARK(PUNCTUATION),

    DIGIT,
    DIGIT_ZERO(DIGIT),
    DIGIT_ONE(DIGIT),
    DIGIT_TWO(DIGIT),
    DIGIT_THREE(DIGIT),
    DIGIT_FOUR(DIGIT),
    DIGIT_FIVE(DIGIT),
    DIGIT_SIX(DIGIT),
    DIGIT_SEVEN(DIGIT),
    DIGIT_EIGHT(DIGIT),
    DIGIT_NINE(DIGIT),

    LETTER,
    LETTER_A(LETTER),
    LETTER_B(LETTER),
    LETTER_C(LETTER),
    LETTER_D(LETTER),
    LETTER_E(LETTER),
    LETTER_F(LETTER),
    LETTER_G(LETTER),
    LETTER_H(LETTER),
    LETTER_I(LETTER),
    LETTER_J(LETTER),
    LETTER_K(LETTER),
    LETTER_L(LETTER),
    LETTER_M(LETTER),
    LETTER_N(LETTER),
    LETTER_O(LETTER),
    LETTER_P(LETTER),
    LETTER_Q(LETTER),
    LETTER_R(LETTER),
    LETTER_S(LETTER),
    LETTER_T(LETTER),
    LETTER_U(LETTER),
    LETTER_V(LETTER),
    LETTER_W(LETTER),
    LETTER_X(LETTER),
    LETTER_Y(LETTER),
    LETTER_Z(LETTER),

    INDICATOR,
    INDICATOR_ACTION(INDICATOR),
    INDICATOR_ACTIVE(INDICATOR),
    INDICATOR_CONDITIONAL(INDICATOR),
    INDICATOR_DEFINITE(INDICATOR),
    INDICATOR_DESCRIPTION(INDICATOR),
    INDICATOR_DOT(INDICATOR),
    INDICATOR_FUTURE_ACTION(INDICATOR),
    INDICATOR_IMPERATIVE(INDICATOR),
    INDICATOR_PASSIVE(INDICATOR),
    INDICATOR_PAST_ACTION(INDICATOR),
    INDICATOR_PLURAL(INDICATOR),
    INDICATOR_THING(INDICATOR);

    private final Primitive parent;

    Primitive() {
        this.parent = null;
    }

    Primitive(Primitive parent) {
        assert parent == null || parent.parent == null : "Multi-layer hierarchy detected!";

        this.parent = parent;
    }

    public Primitive getParent() {
        return parent;
    }

    private static final Map<Primitive, List<Primitive>> parenthood = new EnumMap<>(Primitive.class);

    private static final List<Primitive> PARENT_PRIMITIVES;

    private static final List<Primitive> CHILD_PRIMITIVES;

    static
    {
        for (Primitive primitive : Primitive.values())
        {
            if (primitive.parent == null)
            {
                parenthood.put(primitive, new ArrayList<>());
            }
            else
            {
                List<Primitive> children = parenthood.get(primitive.parent);
                assert children != null: String.format("Child radical %s defined before its parent!",
                        primitive.name());
                children.add(primitive);
            }
        }

        PARENT_PRIMITIVES = Arrays.stream(Primitive.values())
                .filter(r -> r.parent == null)
                .collect(Collectors.toList());

        CHILD_PRIMITIVES = Arrays.stream(Primitive.values())
                .filter(r -> r.parent != null)
                .collect(Collectors.toList());
    }

    public static List<Primitive> getChildren(Primitive parent) {
        List<Primitive> children = parenthood.get(parent);
        return children == null ? Collections.emptyList() : List.copyOf(children);
    }

    public static List<Primitive> getParentPrimitives() {
        return Collections.unmodifiableList(PARENT_PRIMITIVES);
    }

    public static List<Primitive> getChildPrimitives() {
        return Collections.unmodifiableList(CHILD_PRIMITIVES);
    }
}
