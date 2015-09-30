package sg.rghis.android.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class Foreground implements Application.ActivityLifecycleCallbacks {
    private static Foreground instance;

    public interface CurrentActivityCallback {
        void setCurrentActivity(Activity activity);

        Activity getCurrentActivity();
    }

    private Foreground() {
    }

    public static Foreground init(Application application) {
        if (instance == null) {
            if (!(application instanceof CurrentActivityCallback)) {
                throw new RuntimeException("Application must implement CurrentActivityCallback!");
            }
            instance = new Foreground();
            application.registerActivityLifecycleCallbacks(instance);
        }
        return instance;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        setCurrentActivity(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        clearReferences(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        clearReferences(activity);
    }

    private void clearReferences(Activity activity) {
        if (activity == null)
            return;
        Application app = activity.getApplication();
        CurrentActivityCallback appCallback = ((CurrentActivityCallback) app);
        Activity currentActivity = appCallback.getCurrentActivity();
        if (currentActivity != null && currentActivity.equals(activity))
            appCallback.setCurrentActivity(null);
    }

    private void setCurrentActivity(Activity activity) {
        if (activity == null)
            return;
        Application app = activity.getApplication();
        CurrentActivityCallback appCallback = ((CurrentActivityCallback) app);
        appCallback.setCurrentActivity(activity);
    }
}
