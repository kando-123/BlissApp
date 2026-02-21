package pl.polsl.blissapp.data.model;

import androidx.annotation.DrawableRes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import pl.polsl.blissapp.R;

public enum Radical
{
    WAVY_LINE(R.drawable.radical_wavy_line_horizontal),
    WAVY_LINE_HORIZONTAL(WAVY_LINE, R.drawable.radical_wavy_line_horizontal),
    WAVY_LINE_VERTICAL(WAVY_LINE, R.drawable.radical_wavy_line_vertical),

    HEART(R.drawable.radical_heart),

    CROSSHATCH(R.drawable.radical_crosshatch_straight),
    CROSSHATCH_STRAIGHT(CROSSHATCH, R.drawable.radical_crosshatch_straight),
    CROSSHATCH_PITCHED(CROSSHATCH, R.drawable.radical_crosshatch_pitched),

    BUILDING(R.drawable.radical_building),

    EAR(R.drawable.radical_ear),

    ARROW(R.drawable.radical_arrow_east),
    ARROW_NORTH(ARROW, R.drawable.radical_arrow_north),
    ARROW_SOUTH(ARROW, R.drawable.radical_arrow_south),
    ARROW_EAST(ARROW, R.drawable.radical_arrow_east),
    ARROW_WEST(ARROW, R.drawable.radical_arrow_west),

    DOT(R.drawable.radical_dot),

    WHEEL(R.drawable.radical_wheel),

    CIRCLE(R.drawable.radical_circle_large),
    CIRCLE_LARGE(CIRCLE, R.drawable.radical_circle_large),
    CIRCLE_SMALL(CIRCLE, R.drawable.radical_circle_small),

    SEMICIRCLE(R.drawable.radical_semicircle_north),
    SEMICIRCLE_NORTH(SEMICIRCLE, R.drawable.radical_semicircle_north),
    SEMICIRCLE_EAST(SEMICIRCLE, R.drawable.radical_semicircle_east),
    SEMICIRCLE_SOUTH(SEMICIRCLE, R.drawable.radical_semicircle_south),

    ARC(R.drawable.radical_arc_north),
    ARC_NORTH(ARC, R.drawable.radical_arc_north),
    ARC_NORTHEAST(ARC, R.drawable.radical_arc_northeast),
    ARC_EAST(ARC, R.drawable.radical_arc_east),
    ARC_SOUTHEAST(ARC, R.drawable.radical_arc_southeast),
    ARC_SOUTH(ARC, R.drawable.radical_arc_south),
    ARC_SOUTHWEST(ARC, R.drawable.radical_arc_southwest),
    ARC_WEST(ARC, R.drawable.radical_arc_west),
    ARC_NORTHWEST(ARC, R.drawable.radical_arc_northwest),
    PARENTHESIS_RIGHT(ARC, R.drawable.radical_parenthesis_right),
    PARENTHESIS_LEFT(ARC, R.drawable.radical_parenthesis_left),

    SQUARE(R.drawable.radical_square_large),
    SQUARE_LARGE(SQUARE, R.drawable.radical_square_large),
    SQUARE_SMALL(SQUARE, R.drawable.radical_square_small),

    OPEN_SQUARE(R.drawable.radical_open_square_large_north),
    OPEN_SQUARE_LARGE_NORTH(OPEN_SQUARE, R.drawable.radical_open_square_large_north),
    OPEN_SQUARE_LARGE_EAST(OPEN_SQUARE, R.drawable.radical_open_square_large_east),
    OPEN_SQUARE_LARGE_SOUTH(OPEN_SQUARE, R.drawable.radical_open_square_large_south),
    OPEN_SQUARE_SMALL_NORTH(OPEN_SQUARE, R.drawable.radical_open_square_small_north),
    OPEN_SQUARE_SMALL_SOUTH(OPEN_SQUARE, R.drawable.radical_open_square_small_south),
    OPEN_SQUARE_SMALL_SOUTHWEST(OPEN_SQUARE, R.drawable.radical_open_square_small_southwest),

    RECTANGLE(R.drawable.radical_rectangle_small),
    RECTANGLE_LARGE(RECTANGLE, R.drawable.radical_rectangle_large),
    RECTANGLE_SMALL(RECTANGLE, R.drawable.radical_rectangle_small),

    OPEN_RECTANGLE(R.drawable.radical_open_rectangle_north),
    OPEN_RECTANGLE_NORTH(OPEN_RECTANGLE, R.drawable.radical_open_rectangle_north),
    OPEN_RECTANGLE_EAST(OPEN_RECTANGLE, R.drawable.radical_open_rectangle_east),
    OPEN_RECTANGLE_SOUTH_VERTICAL(OPEN_RECTANGLE, R.drawable.radical_open_rectangle_south_vertical),
    OPEN_RECTANGLE_SOUTH_HORIZONTAL(OPEN_RECTANGLE, R.drawable.radical_open_rectangle_south_horizontal),

    RIGHT_ANGLE(R.drawable.radical_right_angle_large_north),
    RIGHT_ANGLE_LARGE_NORTH(RIGHT_ANGLE, R.drawable.radical_right_angle_large_north),
    RIGHT_ANGLE_LARGE_EAST(RIGHT_ANGLE, R.drawable.radical_right_angle_large_east),
    RIGHT_ANGLE_LARGE_SOUTH(RIGHT_ANGLE, R.drawable.radical_right_angle_large_south),
    RIGHT_ANGLE_LARGE_WEST(RIGHT_ANGLE, R.drawable.radical_right_angle_large_west),
    RIGHT_ANGLE_SMALL_NORTH(RIGHT_ANGLE, R.drawable.radical_right_angle_small_north),
    RIGHT_ANGLE_SMALL_EAST(RIGHT_ANGLE, R.drawable.radical_right_angle_small_east),
    RIGHT_ANGLE_SMALL_WEST(RIGHT_ANGLE, R.drawable.radical_right_angle_small_west),
    RIGHT_ANGLE_SMALL_SOUTHWEST(RIGHT_ANGLE, R.drawable.radical_right_angle_small_southwest),

    RIGHT_TRIANGLE(R.drawable.radical_right_triangle_large_southeast),
    RIGHT_TRIANGLE_LARGE_NORTH(RIGHT_TRIANGLE, R.drawable.radical_right_triangle_large_north),
    RIGHT_TRIANGLE_LARGE_EAST(RIGHT_TRIANGLE, R.drawable.radical_right_triangle_large_east),
    RIGHT_TRIANGLE_LARGE_SOUTHEAST(RIGHT_TRIANGLE, R.drawable.radical_right_triangle_large_southeast),
    RIGHT_TRIANGLE_SMALL_EAST(RIGHT_TRIANGLE, R.drawable.radical_right_triangle_small_east),
    RIGHT_TRIANGLE_SMALL_SOUTHEAST(RIGHT_TRIANGLE, R.drawable.radical_right_triangle_small_southeast),

    PIN(R.drawable.radical_pin_large_north),
    PIN_LARGE_NORTH(PIN, R.drawable.radical_pin_large_north),
    PIN_LARGE_NORTHEAST(PIN, R.drawable.radical_pin_large_northeast),
    PIN_LARGE_EAST(PIN, R.drawable.radical_pin_large_east),
    PIN_LARGE_SOUTH(PIN, R.drawable.radical_pin_large_south),
    PIN_LARGE_SOUTHWEST(PIN, R.drawable.radical_pin_large_southwest),
    PIN_SMALL_EAST(PIN, R.drawable.radical_pin_small_east),
    PIN_SMALL_SOUTH(PIN, R.drawable.radical_pin_small_south),
    PIN_SMALL_WEST(PIN, R.drawable.radical_pin_small_west),

    CROSS(R.drawable.radical_cross_large_orthogonal),
    CROSS_LARGE_ORTHOGONAL(CROSS, R.drawable.radical_cross_large_orthogonal),
    CROSS_LARGE_DIAGONAL(CROSS, R.drawable.radical_cross_large_diagonal),
    CROSS_SMALL_ORTHOGONAL(CROSS, R.drawable.radical_cross_small_orthogonal),
    CROSS_SMALL_DIAGONAL(CROSS, R.drawable.radical_cross_small_diagonal),

    ACUTE_ANGLE(R.drawable.radical_acute_angle_large_north),
    ACUTE_ANGLE_LARGE_NORTH(ACUTE_ANGLE, R.drawable.radical_acute_angle_large_north),
    ACUTE_ANGLE_LARGE_SOUTH(ACUTE_ANGLE, R.drawable.radical_acute_angle_large_south),
    ACUTE_ANGLE_SMALL_NORTH(ACUTE_ANGLE, R.drawable.radical_acute_angle_small_north),
    ACUTE_ANGLE_SMALL_SOUTH(ACUTE_ANGLE, R.drawable.radical_acute_angle_small_south),
    ACUTE_ANGLE_WEIRD(ACUTE_ANGLE, R.drawable.radical_acute_angle_weird),

    ACUTE_TRIANGLE(R.drawable.radical_acute_triangle_large_north),
    ACUTE_TRIANGLE_LARGE_NORTH(ACUTE_TRIANGLE, R.drawable.radical_acute_triangle_large_north),
    ACUTE_TRIANGLE_SMALL_NORTH(ACUTE_TRIANGLE, R.drawable.radical_acute_triangle_small_north),
    ACUTE_TRIANGLE_SMALL_SOUTH(ACUTE_TRIANGLE, R.drawable.radical_acute_triangle_small_south),

    HORIZONTAL_LINE(R.drawable.radical_horizontal_line_large),
    HORIZONTAL_LINE_LARGE(HORIZONTAL_LINE, R.drawable.radical_horizontal_line_large),
    HORIZONTAL_LINE_SMALL(HORIZONTAL_LINE, R.drawable.radical_horizontal_line_small),

    VERTICAL_LINE(R.drawable.radical_vertical_line_large),
    VERTICAL_LINE_LARGE(VERTICAL_LINE, R.drawable.radical_vertical_line_large),
    VERTICAL_LINE_SMALL(VERTICAL_LINE, R.drawable.radical_vertical_line_small),

    DIAGONAL_LINE(R.drawable.radical_diagonal_line_large_northeast),
    DIAGONAL_LINE_LARGE_NORTHEAST(DIAGONAL_LINE, R.drawable.radical_diagonal_line_large_northeast),
    DIAGONAL_LINE_LARGE_SOUTHEAST(DIAGONAL_LINE, R.drawable.radical_diagonal_line_large_southeast),
    DIAGONAL_LINE_SMALL_NORTHEAST(DIAGONAL_LINE, R.drawable.radical_diagonal_line_small_northeast),
    DIAGONAL_LINE_SMALL_SOUTHEAST(DIAGONAL_LINE, R.drawable.radical_diagonal_line_small_southeast),

    POINTER(R.drawable.radical_pointer_south),
    POINTER_NORTH(POINTER, R.drawable.radical_pointer_north),
    POINTER_NORTHEAST(POINTER, R.drawable.radical_pointer_northeast),
    POINTER_EAST(POINTER, R.drawable.radical_pointer_east),
    POINTER_SOUTHEAST(POINTER, R.drawable.radical_pointer_southeast),
    POINTER_SOUTH(POINTER, R.drawable.radical_pointer_south),
    POINTER_SOUTHWEST(POINTER, R.drawable.radical_pointer_southwest),
    POINTER_WEST(POINTER, R.drawable.radical_pointer_west),
    POINTER_NORTHWEST(POINTER, R.drawable.radical_pointer_northwest),

    OTHER(R.drawable.radical_other_punctuation_question_mark),
    PUNCTUATION(OTHER, R.drawable.radical_other_punctuation_question_mark),
    DIGIT(OTHER, R.drawable.radical_digit_1),
    LETTER(OTHER, R.drawable.radical_digit_0); // TEMPORARY

    private final Radical parent;

    @DrawableRes
    private final int drawableRes;

    Radical(@DrawableRes int drawableRes)
    {
        this.parent = null;
        this.drawableRes = drawableRes;
    }

    Radical(Radical parent, @DrawableRes int drawableRes)
    {
        assert parent == null || parent.parent == null : "Multi-layer hierarchy detected!";

        this.parent = parent;
        this.drawableRes = drawableRes;
    }

    public Radical getParent() {
        return parent;
    }

    private static final Map<Radical, List<Radical>> parenthood = new TreeMap<>();

    private static final List<Radical> PARENT_RADICALS = Arrays.stream(Radical.values())
            .filter(r -> r.parent == null)
            .toList();

    private static final List<Radical> CHILD_RADICALS = Arrays.stream(Radical.values())
            .filter(r -> r.parent != null)
            .toList();

    static
    {
        for (Radical radical : Radical.values())
        {
            if (radical.parent == null)
            {
                parenthood.put(radical, new ArrayList<>());
            }
            else
            {
                List<Radical> children = parenthood.get(radical.parent);
                assert children != null: String.format("Child radical %s defined before its parent!",
                        radical.name());
                children.add(radical);
            }
        }
    }

    public static List<Radical> getChildren(Radical parent)
    {
        List<Radical> children = parenthood.get(parent);
        return children == null ? Collections.emptyList() : List.copyOf(children);
    }

    public static List<Radical> getParentRadicals()
    {
        return PARENT_RADICALS;
    }

    public static List<Radical> getChildRadicals()
    {
        return CHILD_RADICALS;
    }

    @DrawableRes
    public int getDrawableRes()
    {
        return drawableRes;
    }
}
