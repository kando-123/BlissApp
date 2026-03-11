package pl.polsl.blissapp.ui.mapping;

import androidx.annotation.DrawableRes;

import java.util.EnumMap;

import pl.polsl.blissapp.R;
import pl.polsl.blissapp.data.model.Primitive;
import static pl.polsl.blissapp.data.model.Primitive.*;

public class DrawableMapper {

    private static final EnumMap<Primitive, Integer> radicalMap = new EnumMap<>(Primitive.class);

    static {
        // Radicals
        radicalMap.put(WAVY_LINE, R.drawable.radical_wavy_line_horizontal);
        radicalMap.put(WAVY_LINE_HORIZONTAL, R.drawable.radical_wavy_line_horizontal);
        radicalMap.put(WAVY_LINE_VERTICAL, R.drawable.radical_wavy_line_vertical);

        radicalMap.put(HEART, R.drawable.radical_heart);

        radicalMap.put(CROSSHATCH, R.drawable.radical_crosshatch_straight);
        radicalMap.put(CROSSHATCH_STRAIGHT, R.drawable.radical_crosshatch_straight);
        radicalMap.put(CROSSHATCH_PITCHED, R.drawable.radical_crosshatch_pitched);

        radicalMap.put(BUILDING, R.drawable.radical_building);

        radicalMap.put(EAR, R.drawable.radical_ear);

        radicalMap.put(ARROW, R.drawable.radical_arrow_east);
        radicalMap.put(ARROW_NORTH, R.drawable.radical_arrow_north);
        radicalMap.put(ARROW_SOUTH, R.drawable.radical_arrow_south);
        radicalMap.put(ARROW_EAST, R.drawable.radical_arrow_east);
        radicalMap.put(ARROW_WEST, R.drawable.radical_arrow_west);

        radicalMap.put(DOT, R.drawable.radical_dot);

        radicalMap.put(WHEEL, R.drawable.radical_wheel);

        radicalMap.put(CIRCLE, R.drawable.radical_circle_large);
        radicalMap.put(CIRCLE_LARGE, R.drawable.radical_circle_large);
        radicalMap.put(CIRCLE_SMALL, R.drawable.radical_circle_small);

        radicalMap.put(SEMICIRCLE, R.drawable.radical_semicircle_north);
        radicalMap.put(SEMICIRCLE_NORTH, R.drawable.radical_semicircle_north);
        radicalMap.put(SEMICIRCLE_EAST, R.drawable.radical_semicircle_east);
        radicalMap.put(SEMICIRCLE_SOUTH, R.drawable.radical_semicircle_south);

        radicalMap.put(ARC, R.drawable.radical_arc_north);
        radicalMap.put(ARC_NORTH, R.drawable.radical_arc_north);
        radicalMap.put(ARC_NORTHEAST, R.drawable.radical_arc_northeast);
        radicalMap.put(ARC_EAST, R.drawable.radical_arc_east);
        radicalMap.put(ARC_SOUTHEAST, R.drawable.radical_arc_southeast);
        radicalMap.put(ARC_SOUTH, R.drawable.radical_arc_south);
        radicalMap.put(ARC_SOUTHWEST, R.drawable.radical_arc_southwest);
        radicalMap.put(ARC_WEST, R.drawable.radical_arc_west);
        radicalMap.put(ARC_NORTHWEST, R.drawable.radical_arc_northwest);

        radicalMap.put(PARENTHESIS_RIGHT, R.drawable.radical_parenthesis_right);
        radicalMap.put(PARENTHESIS_LEFT, R.drawable.radical_parenthesis_left);

        radicalMap.put(SQUARE, R.drawable.radical_square_large);
        radicalMap.put(SQUARE_LARGE, R.drawable.radical_square_large);
        radicalMap.put(SQUARE_SMALL, R.drawable.radical_square_small);

        radicalMap.put(OPEN_SQUARE, R.drawable.radical_open_square_large_north);
        radicalMap.put(OPEN_SQUARE_LARGE_NORTH, R.drawable.radical_open_square_large_north);
        radicalMap.put(OPEN_SQUARE_LARGE_EAST, R.drawable.radical_open_square_large_east);
        radicalMap.put(OPEN_SQUARE_LARGE_SOUTH, R.drawable.radical_open_square_large_south);
        radicalMap.put(OPEN_SQUARE_SMALL_NORTH, R.drawable.radical_open_square_small_north);
        radicalMap.put(OPEN_SQUARE_SMALL_SOUTH, R.drawable.radical_open_square_small_south);
        radicalMap.put(OPEN_SQUARE_SMALL_SOUTHWEST, R.drawable.radical_open_square_small_southwest);

        radicalMap.put(RECTANGLE, R.drawable.radical_rectangle_large);
        radicalMap.put(RECTANGLE_LARGE, R.drawable.radical_rectangle_large);
        radicalMap.put(RECTANGLE_SMALL, R.drawable.radical_rectangle_small);

        radicalMap.put(OPEN_RECTANGLE, R.drawable.radical_open_rectangle_north);
        radicalMap.put(OPEN_RECTANGLE_NORTH, R.drawable.radical_open_rectangle_north);
        radicalMap.put(OPEN_RECTANGLE_EAST, R.drawable.radical_open_rectangle_east);
        radicalMap.put(OPEN_RECTANGLE_SOUTH_VERTICAL, R.drawable.radical_open_rectangle_south_vertical);
        radicalMap.put(OPEN_RECTANGLE_SOUTH_HORIZONTAL, R.drawable.radical_open_rectangle_south_horizontal);

        radicalMap.put(RIGHT_ANGLE, R.drawable.radical_right_angle_large_north);
        radicalMap.put(RIGHT_ANGLE_LARGE_NORTH, R.drawable.radical_right_angle_large_north);
        radicalMap.put(RIGHT_ANGLE_LARGE_EAST, R.drawable.radical_right_angle_large_east);
        radicalMap.put(RIGHT_ANGLE_LARGE_SOUTH, R.drawable.radical_right_angle_large_south);
        radicalMap.put(RIGHT_ANGLE_LARGE_WEST, R.drawable.radical_right_angle_large_west);
        radicalMap.put(RIGHT_ANGLE_SMALL_NORTH, R.drawable.radical_right_angle_small_north);
        radicalMap.put(RIGHT_ANGLE_SMALL_EAST, R.drawable.radical_right_angle_small_east);
        radicalMap.put(RIGHT_ANGLE_SMALL_WEST, R.drawable.radical_right_angle_small_west);
        radicalMap.put(RIGHT_ANGLE_SMALL_SOUTHWEST, R.drawable.radical_right_angle_small_southwest);

        radicalMap.put(RIGHT_TRIANGLE, R.drawable.radical_right_triangle_large_north);
        radicalMap.put(RIGHT_TRIANGLE_LARGE_NORTH, R.drawable.radical_right_triangle_large_north);
        radicalMap.put(RIGHT_TRIANGLE_LARGE_EAST, R.drawable.radical_right_triangle_large_east);
        radicalMap.put(RIGHT_TRIANGLE_LARGE_SOUTHEAST, R.drawable.radical_right_triangle_large_southeast);
        radicalMap.put(RIGHT_TRIANGLE_SMALL_EAST, R.drawable.radical_right_triangle_small_east);
        radicalMap.put(RIGHT_TRIANGLE_SMALL_SOUTHEAST, R.drawable.radical_right_triangle_small_southeast);

        radicalMap.put(PIN, R.drawable.radical_pin_large_north);
        radicalMap.put(PIN_LARGE_NORTH, R.drawable.radical_pin_large_north);
        radicalMap.put(PIN_LARGE_NORTHEAST, R.drawable.radical_pin_large_northeast);
        radicalMap.put(PIN_LARGE_EAST, R.drawable.radical_pin_large_east);
        radicalMap.put(PIN_LARGE_SOUTH, R.drawable.radical_pin_large_south);
        radicalMap.put(PIN_LARGE_SOUTHWEST, R.drawable.radical_pin_large_southwest);
        radicalMap.put(PIN_SMALL_EAST, R.drawable.radical_pin_small_east);
        radicalMap.put(PIN_SMALL_SOUTH, R.drawable.radical_pin_small_south);
        radicalMap.put(PIN_SMALL_WEST, R.drawable.radical_pin_small_west);

        radicalMap.put(CROSS, R.drawable.radical_cross_large_orthogonal);
        radicalMap.put(CROSS_LARGE_ORTHOGONAL, R.drawable.radical_cross_large_orthogonal);
        radicalMap.put(CROSS_LARGE_DIAGONAL, R.drawable.radical_cross_large_diagonal);
        radicalMap.put(CROSS_SMALL_ORTHOGONAL, R.drawable.radical_cross_small_orthogonal);
        radicalMap.put(CROSS_SMALL_DIAGONAL, R.drawable.radical_cross_small_diagonal);

        radicalMap.put(ACUTE_ANGLE, R.drawable.radical_acute_angle_large_north);
        radicalMap.put(ACUTE_ANGLE_LARGE_NORTH, R.drawable.radical_acute_angle_large_north);
        radicalMap.put(ACUTE_ANGLE_LARGE_SOUTH, R.drawable.radical_acute_angle_large_south);
        radicalMap.put(ACUTE_ANGLE_SMALL_NORTH, R.drawable.radical_acute_angle_small_north);
        radicalMap.put(ACUTE_ANGLE_SMALL_SOUTH, R.drawable.radical_acute_angle_small_south);

        radicalMap.put(WEIRD_ANGLE, R.drawable.radical_acute_angle_weird);

        radicalMap.put(ACUTE_TRIANGLE, R.drawable.radical_acute_triangle_large_north);
        radicalMap.put(ACUTE_TRIANGLE_LARGE_NORTH, R.drawable.radical_acute_triangle_large_north);
        radicalMap.put(ACUTE_TRIANGLE_SMALL_NORTH, R.drawable.radical_acute_triangle_small_north);
        radicalMap.put(ACUTE_TRIANGLE_SMALL_SOUTH, R.drawable.radical_acute_triangle_small_south);

        radicalMap.put(HORIZONTAL_LINE, R.drawable.radical_horizontal_line_large);
        radicalMap.put(HORIZONTAL_LINE_LARGE, R.drawable.radical_horizontal_line_large);
        radicalMap.put(HORIZONTAL_LINE_SMALL, R.drawable.radical_horizontal_line_small);

        radicalMap.put(VERTICAL_LINE, R.drawable.radical_vertical_line_large);
        radicalMap.put(VERTICAL_LINE_LARGE, R.drawable.radical_vertical_line_large);
        radicalMap.put(VERTICAL_LINE_SMALL, R.drawable.radical_vertical_line_small);

        radicalMap.put(DIAGONAL_LINE, R.drawable.radical_diagonal_line_large_northeast);
        radicalMap.put(DIAGONAL_LINE_LARGE_UPWARD, R.drawable.radical_diagonal_line_large_northeast);
        radicalMap.put(DIAGONAL_LINE_LARGE_DOWNWARD, R.drawable.radical_diagonal_line_large_southeast);
        radicalMap.put(DIAGONAL_LINE_SMALL_UPWARD, R.drawable.radical_diagonal_line_small_northeast);
        radicalMap.put(DIAGONAL_LINE_SMALL_DOWNWARD, R.drawable.radical_diagonal_line_small_southeast);

        radicalMap.put(POINTER, R.drawable.radical_pointer_south);
        radicalMap.put(POINTER_NORTH, R.drawable.radical_pointer_north);
        radicalMap.put(POINTER_NORTHEAST, R.drawable.radical_pointer_northeast);
        radicalMap.put(POINTER_EAST, R.drawable.radical_pointer_east);
        radicalMap.put(POINTER_SOUTHEAST, R.drawable.radical_pointer_southeast);
        radicalMap.put(POINTER_SOUTH, R.drawable.radical_pointer_south);
        radicalMap.put(POINTER_SOUTHWEST, R.drawable.radical_pointer_southwest);
        radicalMap.put(POINTER_WEST, R.drawable.radical_pointer_west);
        radicalMap.put(POINTER_NORTHWEST, R.drawable.radical_pointer_northwest);

        radicalMap.put(COMMA_MARK, R.drawable.radical_other_punctuation_comma);
        radicalMap.put(EXCLAMATION_MARK, R.drawable.radical_other_punctuation_exclamation_mark);
        radicalMap.put(QUESTION_MARK, R.drawable.radical_other_punctuation_question_mark);

        radicalMap.put(DIGIT_ZERO, R.drawable.radical_digit_0);
        radicalMap.put(DIGIT_ONE, R.drawable.radical_digit_1);
        radicalMap.put(DIGIT_TWO, R.drawable.radical_digit_2);
        radicalMap.put(DIGIT_THREE, R.drawable.radical_digit_3);
        radicalMap.put(DIGIT_FOUR, R.drawable.radical_digit_4);
        radicalMap.put(DIGIT_FIVE, R.drawable.radical_digit_5);
        radicalMap.put(DIGIT_SIX, R.drawable.radical_digit_6);
        radicalMap.put(DIGIT_SEVEN, R.drawable.radical_digit_7);
        radicalMap.put(DIGIT_EIGHT, R.drawable.radical_digit_8);
        radicalMap.put(DIGIT_NINE, R.drawable.radical_digit_9);

        // Indicators
        radicalMap.put(INDICATOR_ACTION, R.drawable.indicator_action);
        radicalMap.put(INDICATOR_ACTIVE, R.drawable.indicator_active);
        radicalMap.put(INDICATOR_CONDITIONAL, R.drawable.indicator_conditional);
        radicalMap.put(INDICATOR_DEFINITE, R.drawable.indicator_definite_form);
        radicalMap.put(INDICATOR_DESCRIPTION, R.drawable.indicator_description);
        radicalMap.put(INDICATOR_DOT, R.drawable.indicator_dot);
        radicalMap.put(INDICATOR_FUTURE_ACTION, R.drawable.indicator_future_action);
        radicalMap.put(INDICATOR_IMPERATIVE, R.drawable.indicator_imperative_form);
        radicalMap.put(INDICATOR_PASSIVE, R.drawable.indicator_passive);
        radicalMap.put(INDICATOR_PAST_ACTION, R.drawable.indicator_past_action);
        radicalMap.put(INDICATOR_PLURAL, R.drawable.indicator_plural);
        radicalMap.put(INDICATOR_THING, R.drawable.indicator_thing);
    }

    @DrawableRes
    public static int getDrawableRes(Primitive primitive) {
        Integer res = radicalMap.get(primitive);
        return res != null ? res : R.drawable.indicator_placeholder;
    }
}
