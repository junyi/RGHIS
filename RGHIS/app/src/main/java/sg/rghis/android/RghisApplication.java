package sg.rghis.android;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.parse.Parse;
import com.parse.ParseObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import sg.rghis.android.disqus.DisqusSdkProvider;
import sg.rghis.android.models.User;
import sg.rghis.android.utils.DisqusUtils;
import timber.log.Timber;

public class RghisApplication extends Application {
    private static final String KEY_DATABASE_VERSION = "version";
    private DisqusSdkProvider disqusSdkProvider;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.contains(KEY_DATABASE_VERSION)) {
            createDefaultConfig();
        } else {
            copyBundledRealmFile(getResources().openRawResource(R.raw.default0), "default");
            createDefaultConfig();
            preferences.edit().putLong(KEY_DATABASE_VERSION, 0).apply();
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "LQdlEterOdWEML1bsYZTNm2HUo6FpU6hFHEMmsgX", "6sMtC1NVyM479vLn9RrB1XooERiTjdRGMpxqDSwV");

        ParseObject.registerSubclass(User.class);

        disqusSdkProvider = new DisqusSdkProvider.Builder()
                .setPublicKey(BuildConfig.PUBLIC_KEY)
                .setPrivateKey(BuildConfig.PRIVATE_KEY)
                .setRedirectUri(BuildConfig.REDIRECT_URI)
                .setContext(getApplicationContext())
                .build();

        Timber.d(DisqusUtils.generateSsoString("1", "junyihjy@gmail.com", "junyihjy@gmail.com"));

    }

    private void createDefaultConfig() {
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name("default")
                .schemaVersion(0)
                .build();

        Realm.setDefaultConfiguration(config);
    }


    private String copyBundledRealmFile(InputStream inputStream, String outFileName) {
        try {
            File file = new File(this.getFilesDir(), outFileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            outputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
