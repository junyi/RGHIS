package sg.rghis.android.utils;

import android.content.Context;
import android.os.Build;
import android.util.TypedValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import timber.log.Timber;

public class SystemUtils {
    public static String loadAssetTextAsString(Context context, String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = context.getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ((str = in.readLine()) != null) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            try {
                in.close();
            } catch (IOException e) {
                Timber.e("Error closing asset " + name);
            }
            return buf.toString();
        } catch (IOException e) {
            Timber.e("Error opening asset " + name);
        }

        return null;
    }

    public static int getActionBarHeight(Context context) {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv,
                    true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data, context.getResources().getDisplayMetrics());
        } else {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                    context.getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }
}
