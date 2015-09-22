package sg.rghis.android.views;

import android.content.Context;

import io.realm.RealmResults;
import sg.rghis.android.models.HealthTopic;
import sg.rghis.android.views.adapters.RealmModelAdapter;

public class RealmHealthTopicAdapter extends RealmModelAdapter<HealthTopic> {
    public RealmHealthTopicAdapter(Context context, RealmResults<HealthTopic> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }
}