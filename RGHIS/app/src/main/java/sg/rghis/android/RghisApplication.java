package sg.rghis.android;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.parse.Parse;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.internal.ColumnType;
import io.realm.internal.Table;
import sg.rghis.android.disqus.DisqusSdkProvider;
import sg.rghis.android.models.EmergencyTopic;
import sg.rghis.android.models.User;
import sg.rghis.android.rss.RssProvider;
import sg.rghis.android.utils.Foreground;
import timber.log.Timber;

public class RghisApplication extends Application implements Foreground.CurrentActivityCallback {
    private static final String KEY_DATABASE_VERSION = "version";
    private DisqusSdkProvider disqusSdkProvider;
    private RssProvider rssProvider;
    private Activity currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.contains(KEY_DATABASE_VERSION)) {
            createDefaultConfig();
        } else {
            copyBundledRealmFile(getResources().openRawResource(R.raw.default1), "default");
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

        rssProvider = new RssProvider.Builder()
                .setContext(getApplicationContext())
                .build();

//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
////                        .setDefaultFontPath("fonts/ClearSans-Regular.ttf")
//                        .setFontAttrId(R.attr.fontPath)
//                        .build()
//        );

        Foreground.init(this);
    }

    private void createDefaultConfig() {
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name("default")
                .schemaVersion(0)
                .build();

        Realm.setDefaultConfiguration(config);
//
//        try {
//            loadJsonFromStream();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    private void loadJsonFromStream() throws IOException {
        // Use streams if you are worried about the size of the JSON whether it was persisted on disk
        // or received from the network.
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name("default")
                .migration(new RealmMigration() {
                    @Override
                    public long execute(Realm realm, long version) {
                        Table emergencyTable = realm.getTable(EmergencyTopic.class);
                        emergencyTable.addColumn(ColumnType.STRING, "title");
                        emergencyTable.addColumn(ColumnType.STRING, "url");
                        emergencyTable.addColumn(ColumnType.STRING, "html");
                        long keyIndex = emergencyTable.getColumnIndex("title");
                        emergencyTable.addSearchIndex(keyIndex);
                        emergencyTable.setPrimaryKey("title");
                        return 0;
                    }
                })
                .schemaVersion(0)
                .build();
//        Realm.deleteRealm(config);
        Realm realm = Realm.getInstance(config);
        InputStream stream = getAssets().open("emergency.json");

        // Open a transaction to store items into the realm
        realm.beginTransaction();
        try {
            realm.createAllFromJson(EmergencyTopic.class, stream);
            realm.commitTransaction();
        } catch (IOException e) {
            // Remember to cancel the transaction if anything goes wrong.
            realm.cancelTransaction();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    public String readFully(InputStream inputStream, String encoding)
            throws IOException {
        return new String(readFully(inputStream), encoding);
    }

    private byte[] readFully(InputStream inputStream)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toByteArray();
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

    @Override
    public void setCurrentActivity(Activity activity) {
        this.currentActivity = activity;
    }

    @Override
    public Activity getCurrentActivity() {
        return currentActivity;
    }
}
