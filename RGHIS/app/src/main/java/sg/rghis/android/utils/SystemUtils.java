package sg.rghis.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.util.Range;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import sg.rghis.android.RghisApplication;
import sg.rghis.android.views.MainActivity;
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

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
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

    public static float getDimensAttr(Context context, @AttrRes int attrResId) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{attrResId});
        int resId = a.getResourceId(0, 0);
        float dimen = context.getResources().getDimension(resId);

        a.recycle();

        return dimen;
    }

    public static void hideKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Context context) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static Activity getActivityFromContext(Context context) {
        if (context != null) {
            if (context instanceof Activity) {
                return (Activity) context;
            } else {
                // Application context
                return ((RghisApplication) context).getCurrentActivity();
            }
        }
        return null;
    }

    public static MainActivity getMainActivityFromContext(Context context) {
        Activity activity = getActivityFromContext(context);
        if (activity != null && activity instanceof MainActivity)
            return (MainActivity) activity;
        return null;
    }

    /**
     * Utility method to request user to login
     *
     * @param context
     * @return true if navigated to prompt login
     */
    public static boolean promptLoginIfNecessary(Context context) {
        MainActivity activity = getMainActivityFromContext(context);
        if (activity != null) {
            return activity.promptLogin();
        }
        return !UserManager.isLoggedIn();
    }

    public static void onBackPressed(Context context) {
        MainActivity activity = getMainActivityFromContext(context);
        if (activity != null) {
            activity.onBackPressed();
        }
    }
}
