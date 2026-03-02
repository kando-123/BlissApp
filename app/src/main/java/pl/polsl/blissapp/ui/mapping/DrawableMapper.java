package pl.polsl.blissapp.ui.mapping;

import androidx.annotation.DrawableRes;

import java.util.EnumMap;

import pl.polsl.blissapp.R;
import pl.polsl.blissapp.data.model.Primitive;

public class DrawableMapper {

    private static final EnumMap<Primitive, Integer> radicalMap = new EnumMap<>(Primitive.class);

    static {
        // Radicals
        radicalMap.put(Primitive.WAVY_LINE, R.drawable.radical_wavy_line_horizontal);
        radicalMap.put(Primitive.WAVY_LINE_HORIZONTAL, R.drawable.radical_wavy_line_horizontal);
        radicalMap.put(Primitive.WAVY_LINE_VERTICAL, R.drawable.radical_wavy_line_vertical);

        radicalMap.put(Primitive.HEART, R.drawable.radical_heart);

        radicalMap.put(Primitive.CROSSHATCH, R.drawable.radical_crosshatch_straight);
        radicalMap.put(Primitive.CROSSHATCH_STRAIGHT, R.drawable.radical_crosshatch_straight);
        radicalMap.put(Primitive.CROSSHATCH_PITCHED, R.drawable.radical_crosshatch_pitched);

        radicalMap.put(Primitive.BUILDING, R.drawable.radical_building);

        radicalMap.put(Primitive.EAR, R.drawable.radical_ear);

        radicalMap.put(Primitive.ARROW, R.drawable.radical_arrow_east);
        radicalMap.put(Primitive.ARROW_NORTH, R.drawable.radical_arrow_north);
        radicalMap.put(Primitive.ARROW_SOUTH, R.drawable.radical_arrow_south);
        radicalMap.put(Primitive.ARROW_EAST, R.drawable.radical_arrow_east);
        radicalMap.put(Primitive.ARROW_WEST, R.drawable.radical_arrow_west);

        radicalMap.put(Primitive.DOT, R.drawable.radical_dot);

        radicalMap.put(Primitive.WHEEL, R.drawable.radical_wheel);

        radicalMap.put(Primitive.CIRCLE, R.drawable.radical_circle_large);
        radicalMap.put(Primitive.CIRCLE_LARGE, R.drawable.radical_circle_large);
        radicalMap.put(Primitive.CIRCLE_SMALL, R.drawable.radical_circle_small);

        radicalMap.put(Primitive.SEMICIRCLE, R.drawable.radical_semicircle_north);
        radicalMap.put(Primitive.SEMICIRCLE_NORTH, R.drawable.radical_semicircle_north);
        radicalMap.put(Primitive.SEMICIRCLE_EAST, R.drawable.radical_semicircle_east);
        radicalMap.put(Primitive.SEMICIRCLE_SOUTH, R.drawable.radical_semicircle_south);

        radicalMap.put(Primitive.ARC, R.drawable.radical_arc_north);
        radicalMap.put(Primitive.ARC_NORTH, R.drawable.radical_arc_north);
        radicalMap.put(Primitive.ARC_NORTHEAST, R.drawable.radical_arc_northeast);
        radicalMap.put(Primitive.ARC_EAST, R.drawable.radical_arc_east);
        radicalMap.put(Primitive.ARC_SOUTHEAST, R.drawable.radical_arc_southeast);
        radicalMap.put(Primitive.ARC_SOUTH, R.drawable.radical_arc_south);
        radicalMap.put(Primitive.ARC_SOUTHWEST, R.drawable.radical_arc_southwest);
        radicalMap.put(Primitive.ARC_WEST, R.drawable.radical_arc_west);
        radicalMap.put(Primitive.ARC_NORTHWEST, R.drawable.radical_arc_northwest);

        radicalMap.put(Primitive.PARENTHESIS_RIGHT, R.drawable.radical_parenthesis_right);
        radicalMap.put(Primitive.PARENTHESIS_LEFT, R.drawable.radical_parenthesis_left);

        radicalMap.put(Primitive.SQUARE, R.drawable.radical_square_large);
        radicalMap.put(Primitive.SQUARE_LARGE, R.drawable.radical_square_large);
        radicalMap.put(Primitive.SQUARE_SMALL, R.drawable.radical_square_small);

        radicalMap.put(Primitive.OPEN_SQUARE, R.drawable.radical_open_square_large_north);
        radicalMap.put(Primitive.OPEN_SQUARE_LARGE_NORTH, R.drawable.radical_open_square_large_north);
        radicalMap.put(Primitive.OPEN_SQUARE_LARGE_EAST, R.drawable.radical_open_square_large_east);
        radicalMap.put(Primitive.OPEN_SQUARE_LARGE_SOUTH, R.drawable.radical_open_square_large_south);
        radicalMap.put(Primitive.OPEN_SQUARE_SMALL_NORTH, R.drawable.radical_open_square_small_north);
        radicalMap.put(Primitive.OPEN_SQUARE_SMALL_SOUTH, R.drawable.radical_open_square_small_south);
        radicalMap.put(Primitive.OPEN_SQUARE_SMALL_SOUTHWEST, R.drawable.radical_open_square_small_southwest);

        radicalMap.put(Primitive.RECTANGLE, R.drawable.radical_rectangle_large);
        radicalMap.put(Primitive.RECTANGLE_LARGE, R.drawable.radical_rectangle_large);
        radicalMap.put(Primitive.RECTANGLE_SMALL, R.drawable.radical_rectangle_small);

        radicalMap.put(Primitive.OPEN_RECTANGLE, R.drawable.radical_open_rectangle_north);
        radicalMap.put(Primitive.OPEN_RECTANGLE_NORTH, R.drawable.radical_open_rectangle_north);
        radicalMap.put(Primitive.OPEN_RECTANGLE_EAST, R.drawable.radical_open_rectangle_east);
        radicalMap.put(Primitive.OPEN_RECTANGLE_SOUTH_VERTICAL, R.drawable.radical_open_rectangle_south_vertical);
        radicalMap.put(Primitive.OPEN_RECTANGLE_SOUTH_HORIZONTAL, R.drawable.radical_open_rectangle_south_horizontal);

        radicalMap.put(Primitive.RIGHT_ANGLE, R.drawable.radical_right_angle_large_north);
        radicalMap.put(Primitive.RIGHT_ANGLE_LARGE_NORTH, R.drawable.radical_right_angle_large_north);
        radicalMap.put(Primitive.RIGHT_ANGLE_LARGE_EAST, R.drawable.radical_right_angle_large_east);
        radicalMap.put(Primitive.RIGHT_ANGLE_LARGE_SOUTH, R.drawable.radical_right_angle_large_south);
        radicalMap.put(Primitive.RIGHT_ANGLE_LARGE_WEST, R.drawable.radical_right_angle_large_west);
        radicalMap.put(Primitive.RIGHT_ANGLE_SMALL_NORTH, R.drawable.radical_right_angle_small_north);
        radicalMap.put(Primitive.RIGHT_ANGLE_SMALL_EAST, R.drawable.radical_right_angle_small_east);
        radicalMap.put(Primitive.RIGHT_ANGLE_SMALL_WEST, R.drawable.radical_right_angle_small_west);
        radicalMap.put(Primitive.RIGHT_ANGLE_SMALL_SOUTHWEST, R.drawable.radical_right_angle_small_southwest);

        radicalMap.put(Primitive.RIGHT_TRIANGLE, R.drawable.radical_right_triangle_large_southeast);
        radicalMap.put(Primitive.RIGHT_TRIANGLE_LARGE_NORTH, R.drawable.radical_right_triangle_large_north);
        radicalMap.put(Primitive.RIGHT_TRIANGLE_LARGE_EAST, R.drawable.radical_right_triangle_large_east);
        radicalMap.put(Primitive.RIGHT_TRIANGLE_LARGE_SOUTHEAST, R.drawable.radical_right_triangle_large_southeast);
        radicalMap.put(Primitive.RIGHT_TRIANGLE_SMALL_EAST, R.drawable.radical_right_triangle_small_east);
        radicalMap.put(Primitive.RIGHT_TRIANGLE_SMALL_SOUTHEAST, R.drawable.radical_right_triangle_small_southeast);

        radicalMap.put(Primitive.PIN, R.drawable.radical_pin_large_north);
        radicalMap.put(Primitive.PIN_LARGE_NORTH, R.drawable.radical_pin_large_north);
        radicalMap.put(Primitive.PIN_LARGE_NORTHEAST, R.drawable.radical_pin_large_northeast);
        radicalMap.put(Primitive.PIN_LARGE_EAST, R.drawable.radical_pin_large_east);
        radicalMap.put(Primitive.PIN_LARGE_SOUTH, R.drawable.radical_pin_large_south);
        radicalMap.put(Primitive.PIN_LARGE_SOUTHWEST, R.drawable.radical_pin_large_southwest);
        radicalMap.put(Primitive.PIN_SMALL_EAST, R.drawable.radical_pin_small_east);
        radicalMap.put(Primitive.PIN_SMALL_SOUTH, R.drawable.radical_pin_small_south);
        radicalMap.put(Primitive.PIN_SMALL_WEST, R.drawable.radical_pin_small_west);

        radicalMap.put(Primitive.CROSS, R.drawable.radical_cross_large_orthogonal);
        radicalMap.put(Primitive.CROSS_LARGE_ORTHOGONAL, R.drawable.radical_cross_large_orthogonal);
        radicalMap.put(Primitive.CROSS_LARGE_DIAGONAL, R.drawable.radical_cross_large_diagonal);
        radicalMap.put(Primitive.CROSS_SMALL_ORTHOGONAL, R.drawable.radical_cross_small_orthogonal);
        radicalMap.put(Primitive.CROSS_SMALL_DIAGONAL, R.drawable.radical_cross_small_diagonal);

        radicalMap.put(Primitive.ACUTE_ANGLE, R.drawable.radical_acute_angle_large_north);
        radicalMap.put(Primitive.ACUTE_ANGLE_LARGE_NORTH, R.drawable.radical_acute_angle_large_north);
        radicalMap.put(Primitive.ACUTE_ANGLE_LARGE_SOUTH, R.drawable.radical_acute_angle_large_south);
        radicalMap.put(Primitive.ACUTE_ANGLE_SMALL_NORTH, R.drawable.radical_acute_angle_small_north);
        radicalMap.put(Primitive.ACUTE_ANGLE_SMALL_SOUTH, R.drawable.radical_acute_angle_small_south);

        radicalMap.put(Primitive.ACUTE_ANGLE_WEIRD, R.drawable.radical_acute_angle_weird);
        // ACUTE_ANGLE_WEIRD_NORTH,
        // ACUTE_ANGLE_WEIRD_EAST
        // ACUTE_ANGLE_WEIRD_SOUTHEAST
        // ACUTE_ANGLE_WEIRD_SOUTH
        // ACUTE_ANGLE_WEIRD_SOUTHWEST
        // ACUTE_ANGLE_WEIRD_WEST

        radicalMap.put(Primitive.ACUTE_TRIANGLE, R.drawable.radical_acute_triangle_large_north);
        radicalMap.put(Primitive.ACUTE_TRIANGLE_LARGE_NORTH, R.drawable.radical_acute_triangle_large_north);
        radicalMap.put(Primitive.ACUTE_TRIANGLE_SMALL_NORTH, R.drawable.radical_acute_triangle_small_north);
        radicalMap.put(Primitive.ACUTE_TRIANGLE_SMALL_SOUTH, R.drawable.radical_acute_triangle_small_south);

        radicalMap.put(Primitive.HORIZONTAL_LINE, R.drawable.radical_horizontal_line_large);
        radicalMap.put(Primitive.HORIZONTAL_LINE_LARGE, R.drawable.radical_horizontal_line_large);
        radicalMap.put(Primitive.HORIZONTAL_LINE_SMALL, R.drawable.radical_horizontal_line_small);

        radicalMap.put(Primitive.VERTICAL_LINE, R.drawable.radical_vertical_line_large);
        radicalMap.put(Primitive.VERTICAL_LINE_LARGE, R.drawable.radical_vertical_line_large);
        radicalMap.put(Primitive.VERTICAL_LINE_SMALL, R.drawable.radical_vertical_line_small);

        radicalMap.put(Primitive.DIAGONAL_LINE, R.drawable.radical_diagonal_line_large_northeast);
        radicalMap.put(Primitive.DIAGONAL_LINE_LARGE_NORTHEAST, R.drawable.radical_diagonal_line_large_northeast);
        radicalMap.put(Primitive.DIAGONAL_LINE_LARGE_SOUTHEAST, R.drawable.radical_diagonal_line_large_southeast);
        radicalMap.put(Primitive.DIAGONAL_LINE_SMALL_NORTHEAST, R.drawable.radical_diagonal_line_small_northeast);
        radicalMap.put(Primitive.DIAGONAL_LINE_SMALL_SOUTHEAST, R.drawable.radical_diagonal_line_small_southeast);

        radicalMap.put(Primitive.POINTER, R.drawable.radical_pointer_south);
        radicalMap.put(Primitive.POINTER_NORTH, R.drawable.radical_pointer_north);
        radicalMap.put(Primitive.POINTER_NORTHEAST, R.drawable.radical_pointer_northeast);
        radicalMap.put(Primitive.POINTER_EAST, R.drawable.radical_pointer_east);
        radicalMap.put(Primitive.POINTER_SOUTHEAST, R.drawable.radical_pointer_southeast);
        radicalMap.put(Primitive.POINTER_SOUTH, R.drawable.radical_pointer_south);
        radicalMap.put(Primitive.POINTER_SOUTHWEST, R.drawable.radical_pointer_southwest);
        radicalMap.put(Primitive.POINTER_WEST, R.drawable.radical_pointer_west);
        radicalMap.put(Primitive.POINTER_NORTHWEST, R.drawable.radical_pointer_northwest);

        radicalMap.put(Primitive.PUNCTUATION, R.drawable.radical_other_punctuation_question_mark);
        radicalMap.put(Primitive.DIGIT, R.drawable.radical_digit_1);
        radicalMap.put(Primitive.LETTER, R.drawable.radical_digit_0);

        // Indicators
        radicalMap.put(Primitive.INDICATOR_ACTION, R.drawable.indicator_action);
        radicalMap.put(Primitive.INDICATOR_ACTIVE, R.drawable.indicator_active);
        radicalMap.put(Primitive.INDICATOR_CONDITIONAL, R.drawable.indicator_conditional);
        radicalMap.put(Primitive.INDICATOR_DEFINITE, R.drawable.indicator_definite_form);
        radicalMap.put(Primitive.INDICATOR_DESCRIPTION, R.drawable.indicator_description);
        radicalMap.put(Primitive.INDICATOR_DOT, R.drawable.indicator_dot);
        radicalMap.put(Primitive.INDICATOR_FUTURE_ACTION, R.drawable.indicator_action);
        radicalMap.put(Primitive.INDICATOR_IMPERATIVE, R.drawable.indicator_imperative_form);
        radicalMap.put(Primitive.INDICATOR_PASSIVE, R.drawable.indicator_passive);
        radicalMap.put(Primitive.INDICATOR_PAST_ACTION, R.drawable.indicator_past_action);
        radicalMap.put(Primitive.INDICATOR_PLURAL, R.drawable.indicator_plural);
        radicalMap.put(Primitive.INDICATOR_THING, R.drawable.indicator_thing);
    }

    @DrawableRes
    public static int getDrawableRes(Primitive primitive) {
        return radicalMap.get(primitive);
    }
}
