package pl.polsl.blissapp.ui.mapping;

import androidx.annotation.DrawableRes;

import java.util.EnumMap;

import pl.polsl.blissapp.R;
import pl.polsl.blissapp.data.model.Indicator;
import pl.polsl.blissapp.data.model.Radical;

public class DrawableMapper {

    private static final EnumMap<Radical, Integer> radicalMap = new EnumMap<>(Radical.class);
    private static final EnumMap<Indicator, Integer> indicatorMap = new EnumMap<>(Indicator.class);

    static {
        // Radicals
        radicalMap.put(Radical.WAVY_LINE, R.drawable.radical_wavy_line_horizontal);
        radicalMap.put(Radical.WAVY_LINE_HORIZONTAL, R.drawable.radical_wavy_line_horizontal);
        radicalMap.put(Radical.WAVY_LINE_VERTICAL, R.drawable.radical_wavy_line_vertical);

        radicalMap.put(Radical.HEART, R.drawable.radical_heart);

        radicalMap.put(Radical.CROSSHATCH, R.drawable.radical_crosshatch_straight);
        radicalMap.put(Radical.CROSSHATCH_STRAIGHT, R.drawable.radical_crosshatch_straight);
        radicalMap.put(Radical.CROSSHATCH_PITCHED, R.drawable.radical_crosshatch_pitched);

        radicalMap.put(Radical.BUILDING, R.drawable.radical_building);

        radicalMap.put(Radical.EAR, R.drawable.radical_ear);

        radicalMap.put(Radical.ARROW, R.drawable.radical_arrow_east);
        radicalMap.put(Radical.ARROW_NORTH, R.drawable.radical_arrow_north);
        radicalMap.put(Radical.ARROW_SOUTH, R.drawable.radical_arrow_south);
        radicalMap.put(Radical.ARROW_EAST, R.drawable.radical_arrow_east);
        radicalMap.put(Radical.ARROW_WEST, R.drawable.radical_arrow_west);

        radicalMap.put(Radical.DOT, R.drawable.radical_dot);

        radicalMap.put(Radical.WHEEL, R.drawable.radical_wheel);

        radicalMap.put(Radical.CIRCLE, R.drawable.radical_circle_large);
        radicalMap.put(Radical.CIRCLE_LARGE, R.drawable.radical_circle_large);
        radicalMap.put(Radical.CIRCLE_SMALL, R.drawable.radical_circle_small);

        radicalMap.put(Radical.SEMICIRCLE, R.drawable.radical_semicircle_north);
        radicalMap.put(Radical.SEMICIRCLE_NORTH, R.drawable.radical_semicircle_north);
        radicalMap.put(Radical.SEMICIRCLE_EAST, R.drawable.radical_semicircle_east);
        radicalMap.put(Radical.SEMICIRCLE_SOUTH, R.drawable.radical_semicircle_south);

        radicalMap.put(Radical.ARC, R.drawable.radical_arc_north);
        radicalMap.put(Radical.ARC_NORTH, R.drawable.radical_arc_north);
        radicalMap.put(Radical.ARC_NORTHEAST, R.drawable.radical_arc_northeast);
        radicalMap.put(Radical.ARC_EAST, R.drawable.radical_arc_east);
        radicalMap.put(Radical.ARC_SOUTHEAST, R.drawable.radical_arc_southeast);
        radicalMap.put(Radical.ARC_SOUTH, R.drawable.radical_arc_south);
        radicalMap.put(Radical.ARC_SOUTHWEST, R.drawable.radical_arc_southwest);
        radicalMap.put(Radical.ARC_WEST, R.drawable.radical_arc_west);
        radicalMap.put(Radical.ARC_NORTHWEST, R.drawable.radical_arc_northwest);

        radicalMap.put(Radical.PARENTHESIS_RIGHT, R.drawable.radical_parenthesis_right);
        radicalMap.put(Radical.PARENTHESIS_LEFT, R.drawable.radical_parenthesis_left);

        radicalMap.put(Radical.SQUARE, R.drawable.radical_square_large);
        radicalMap.put(Radical.SQUARE_LARGE, R.drawable.radical_square_large);
        radicalMap.put(Radical.SQUARE_SMALL, R.drawable.radical_square_small);

        radicalMap.put(Radical.OPEN_SQUARE, R.drawable.radical_open_square_large_north);
        radicalMap.put(Radical.OPEN_SQUARE_LARGE_NORTH, R.drawable.radical_open_square_large_north);
        radicalMap.put(Radical.OPEN_SQUARE_LARGE_EAST, R.drawable.radical_open_square_large_east);
        radicalMap.put(Radical.OPEN_SQUARE_LARGE_SOUTH, R.drawable.radical_open_square_large_south);
        radicalMap.put(Radical.OPEN_SQUARE_SMALL_NORTH, R.drawable.radical_open_square_small_north);
        radicalMap.put(Radical.OPEN_SQUARE_SMALL_SOUTH, R.drawable.radical_open_square_small_south);
        radicalMap.put(Radical.OPEN_SQUARE_SMALL_SOUTHWEST, R.drawable.radical_open_square_small_southwest);

        radicalMap.put(Radical.RECTANGLE, R.drawable.radical_rectangle_large);
        radicalMap.put(Radical.RECTANGLE_LARGE, R.drawable.radical_rectangle_large);
        radicalMap.put(Radical.RECTANGLE_SMALL, R.drawable.radical_rectangle_small);

        radicalMap.put(Radical.OPEN_RECTANGLE, R.drawable.radical_open_rectangle_north);
        radicalMap.put(Radical.OPEN_RECTANGLE_NORTH, R.drawable.radical_open_rectangle_north);
        radicalMap.put(Radical.OPEN_RECTANGLE_EAST, R.drawable.radical_open_rectangle_east);
        radicalMap.put(Radical.OPEN_RECTANGLE_SOUTH_VERTICAL, R.drawable.radical_open_rectangle_south_vertical);
        radicalMap.put(Radical.OPEN_RECTANGLE_SOUTH_HORIZONTAL, R.drawable.radical_open_rectangle_south_horizontal);

        radicalMap.put(Radical.RIGHT_ANGLE, R.drawable.radical_right_angle_large_north);
        radicalMap.put(Radical.RIGHT_ANGLE_LARGE_NORTH, R.drawable.radical_right_angle_large_north);
        radicalMap.put(Radical.RIGHT_ANGLE_LARGE_EAST, R.drawable.radical_right_angle_large_east);
        radicalMap.put(Radical.RIGHT_ANGLE_LARGE_SOUTH, R.drawable.radical_right_angle_large_south);
        radicalMap.put(Radical.RIGHT_ANGLE_LARGE_WEST, R.drawable.radical_right_angle_large_west);
        radicalMap.put(Radical.RIGHT_ANGLE_SMALL_NORTH, R.drawable.radical_right_angle_small_north);
        radicalMap.put(Radical.RIGHT_ANGLE_SMALL_EAST, R.drawable.radical_right_angle_small_east);
        radicalMap.put(Radical.RIGHT_ANGLE_SMALL_WEST, R.drawable.radical_right_angle_small_west);
        radicalMap.put(Radical.RIGHT_ANGLE_SMALL_SOUTHWEST, R.drawable.radical_right_angle_small_southwest);

        radicalMap.put(Radical.RIGHT_TRIANGLE, R.drawable.radical_right_triangle_large_southeast);
        radicalMap.put(Radical.RIGHT_TRIANGLE_LARGE_NORTH, R.drawable.radical_right_triangle_large_north);
        radicalMap.put(Radical.RIGHT_TRIANGLE_LARGE_EAST, R.drawable.radical_right_triangle_large_east);
        radicalMap.put(Radical.RIGHT_TRIANGLE_LARGE_SOUTHEAST, R.drawable.radical_right_triangle_large_southeast);
        radicalMap.put(Radical.RIGHT_TRIANGLE_SMALL_EAST, R.drawable.radical_right_triangle_small_east);
        radicalMap.put(Radical.RIGHT_TRIANGLE_SMALL_SOUTHEAST, R.drawable.radical_right_triangle_small_southeast);

        radicalMap.put(Radical.PIN, R.drawable.radical_pin_large_north);
        radicalMap.put(Radical.PIN_LARGE_NORTH, R.drawable.radical_pin_large_north);
        radicalMap.put(Radical.PIN_LARGE_NORTHEAST, R.drawable.radical_pin_large_northeast);
        radicalMap.put(Radical.PIN_LARGE_EAST, R.drawable.radical_pin_large_east);
        radicalMap.put(Radical.PIN_LARGE_SOUTH, R.drawable.radical_pin_large_south);
        radicalMap.put(Radical.PIN_LARGE_SOUTHWEST, R.drawable.radical_pin_large_southwest);
        radicalMap.put(Radical.PIN_SMALL_EAST, R.drawable.radical_pin_small_east);
        radicalMap.put(Radical.PIN_SMALL_SOUTH, R.drawable.radical_pin_small_south);
        radicalMap.put(Radical.PIN_SMALL_WEST, R.drawable.radical_pin_small_west);

        radicalMap.put(Radical.CROSS, R.drawable.radical_cross_large_orthogonal);
        radicalMap.put(Radical.CROSS_LARGE_ORTHOGONAL, R.drawable.radical_cross_large_orthogonal);
        radicalMap.put(Radical.CROSS_LARGE_DIAGONAL, R.drawable.radical_cross_large_diagonal);
        radicalMap.put(Radical.CROSS_SMALL_ORTHOGONAL, R.drawable.radical_cross_small_orthogonal);
        radicalMap.put(Radical.CROSS_SMALL_DIAGONAL, R.drawable.radical_cross_small_diagonal);

        radicalMap.put(Radical.ACUTE_ANGLE, R.drawable.radical_acute_angle_large_north);
        radicalMap.put(Radical.ACUTE_ANGLE_LARGE_NORTH, R.drawable.radical_acute_angle_large_north);
        radicalMap.put(Radical.ACUTE_ANGLE_LARGE_SOUTH, R.drawable.radical_acute_angle_large_south);
        radicalMap.put(Radical.ACUTE_ANGLE_SMALL_NORTH, R.drawable.radical_acute_angle_small_north);
        radicalMap.put(Radical.ACUTE_ANGLE_SMALL_SOUTH, R.drawable.radical_acute_angle_small_south);

        radicalMap.put(Radical.ACUTE_ANGLE_WEIRD, R.drawable.radical_acute_angle_weird);
        // ACUTE_ANGLE_WEIRD_NORTH,
        // ACUTE_ANGLE_WEIRD_EAST
        // ACUTE_ANGLE_WEIRD_SOUTHEAST
        // ACUTE_ANGLE_WEIRD_SOUTH
        // ACUTE_ANGLE_WEIRD_SOUTHWEST
        // ACUTE_ANGLE_WEIRD_WEST

        radicalMap.put(Radical.ACUTE_TRIANGLE, R.drawable.radical_acute_triangle_large_north);
        radicalMap.put(Radical.ACUTE_TRIANGLE_LARGE_NORTH, R.drawable.radical_acute_triangle_large_north);
        radicalMap.put(Radical.ACUTE_TRIANGLE_SMALL_NORTH, R.drawable.radical_acute_triangle_small_north);
        radicalMap.put(Radical.ACUTE_TRIANGLE_SMALL_SOUTH, R.drawable.radical_acute_triangle_small_south);

        radicalMap.put(Radical.HORIZONTAL_LINE, R.drawable.radical_horizontal_line_large);
        radicalMap.put(Radical.HORIZONTAL_LINE_LARGE, R.drawable.radical_horizontal_line_large);
        radicalMap.put(Radical.HORIZONTAL_LINE_SMALL, R.drawable.radical_horizontal_line_small);

        radicalMap.put(Radical.VERTICAL_LINE, R.drawable.radical_vertical_line_large);
        radicalMap.put(Radical.VERTICAL_LINE_LARGE, R.drawable.radical_vertical_line_large);
        radicalMap.put(Radical.VERTICAL_LINE_SMALL, R.drawable.radical_vertical_line_small);

        radicalMap.put(Radical.DIAGONAL_LINE, R.drawable.radical_diagonal_line_large_northeast);
        radicalMap.put(Radical.DIAGONAL_LINE_LARGE_NORTHEAST, R.drawable.radical_diagonal_line_large_northeast);
        radicalMap.put(Radical.DIAGONAL_LINE_LARGE_SOUTHEAST, R.drawable.radical_diagonal_line_large_southeast);
        radicalMap.put(Radical.DIAGONAL_LINE_SMALL_NORTHEAST, R.drawable.radical_diagonal_line_small_northeast);
        radicalMap.put(Radical.DIAGONAL_LINE_SMALL_SOUTHEAST, R.drawable.radical_diagonal_line_small_southeast);

        radicalMap.put(Radical.POINTER, R.drawable.radical_pointer_south);
        radicalMap.put(Radical.POINTER_NORTH, R.drawable.radical_pointer_north);
        radicalMap.put(Radical.POINTER_NORTHEAST, R.drawable.radical_pointer_northeast);
        radicalMap.put(Radical.POINTER_EAST, R.drawable.radical_pointer_east);
        radicalMap.put(Radical.POINTER_SOUTHEAST, R.drawable.radical_pointer_southeast);
        radicalMap.put(Radical.POINTER_SOUTH, R.drawable.radical_pointer_south);
        radicalMap.put(Radical.POINTER_SOUTHWEST, R.drawable.radical_pointer_southwest);
        radicalMap.put(Radical.POINTER_WEST, R.drawable.radical_pointer_west);
        radicalMap.put(Radical.POINTER_NORTHWEST, R.drawable.radical_pointer_northwest);

        radicalMap.put(Radical.OTHER, R.drawable.radical_other_punctuation_question_mark);
        radicalMap.put(Radical.PUNCTUATION, R.drawable.radical_other_punctuation_question_mark);
        radicalMap.put(Radical.DIGIT, R.drawable.radical_digit_1);
        radicalMap.put(Radical.LETTER, R.drawable.radical_digit_0);

        // Indicators
        indicatorMap.put(Indicator.ACTION, R.drawable.indicator_action);
        indicatorMap.put(Indicator.ACTIVE, R.drawable.indicator_active);
        indicatorMap.put(Indicator.CONDITIONAL, R.drawable.indicator_conditional);
        indicatorMap.put(Indicator.DEFINITE, R.drawable.indicator_definite_form);
        indicatorMap.put(Indicator.DESCRIPTION, R.drawable.indicator_description);
        indicatorMap.put(Indicator.DOT, R.drawable.indicator_dot);
        indicatorMap.put(Indicator.FUTURE_ACTION, R.drawable.indicator_action);
        indicatorMap.put(Indicator.IMPERATIVE, R.drawable.indicator_imperative_form);
        indicatorMap.put(Indicator.PASSIVE, R.drawable.indicator_passive);
        indicatorMap.put(Indicator.PAST_ACTION, R.drawable.indicator_past_action);
        indicatorMap.put(Indicator.PLURAL, R.drawable.indicator_plural);
        indicatorMap.put(Indicator.THING, R.drawable.indicator_thing);
    }

    @DrawableRes
    public static int getDrawableRes(Radical radical) {
        return radicalMap.get(radical);
    }

    @DrawableRes
    public static int getDrawableRes(Indicator indicator) {
        return indicatorMap.get(indicator);
    }
}
