package sg.rghis.android.utils;

import android.graphics.Color;

import java.util.Arrays;

import sg.rghis.android.R;

public class ColorUtils {
    private static ColorGenerator colorGenerator;
    private final static Integer[] materialColorResList = new Integer[]{
            R.color.amber_700,
            R.color.blue_700,
            R.color.blue_grey_700,
            R.color.brown_700,
            R.color.cyan_700,
            R.color.deep_orange_700,
            R.color.deep_purple_700,
            R.color.green_700,
            R.color.grey_700,
            R.color.indigo_700,
            R.color.light_blue_700,
            R.color.light_green_700,
            R.color.lime_700,
            R.color.orange_700,
            R.color.pink_700,
            R.color.purple_700,
            R.color.red_700,
            R.color.teal_700,
            R.color.yellow_700
    };

    private final static Integer[] palette1 = new Integer[]{
            R.color.amber_700,
            R.color.blue_700,
            R.color.brown_700,
            R.color.cyan_700,
            R.color.deep_orange_700,
            R.color.deep_purple_700,
            R.color.green_700,
            R.color.indigo_700,
            R.color.light_blue_700,
            R.color.light_green_700,
            R.color.lime_700,
            R.color.orange_700,
            R.color.pink_700,
            R.color.purple_700,
            R.color.red_700,
            R.color.teal_700,
            R.color.yellow_700
    };

    static {
        colorGenerator = ColorGenerator.create(Arrays.asList(palette1));
    }

    public static ColorGenerator getColorGenerator() {
        return colorGenerator;
    }

    public static int combineColors(float alpha, int foregroundColor, int backgroundColor) {
        return Color.rgb(
                (int) ((1 - alpha) * Color.red(backgroundColor) + alpha * Color.red(foregroundColor)),
                (int) ((1 - alpha) * Color.green(backgroundColor) + alpha * Color.green(foregroundColor)),
                (int) ((1 - alpha) * Color.blue(backgroundColor) + alpha * Color.blue(foregroundColor))
        );
    }
}
