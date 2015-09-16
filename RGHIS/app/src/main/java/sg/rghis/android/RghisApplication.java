package sg.rghis.android;

import android.app.Application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RghisApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        buildDatabase();
    }

    private void buildDatabase() {
        copyBundledRealmFile(getResources().openRawResource(R.raw.default0), "default");

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
