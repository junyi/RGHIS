package sg.rghis.android.utils;

import android.content.Context;

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
}
