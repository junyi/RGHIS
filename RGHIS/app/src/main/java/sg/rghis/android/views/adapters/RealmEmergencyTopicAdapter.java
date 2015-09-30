package sg.rghis.android.views.adapters;

import android.content.Context;

import io.realm.RealmResults;
import sg.rghis.android.models.EmergencyTopic;

public class RealmEmergencyTopicAdapter extends RealmModelAdapter<EmergencyTopic> {
    public RealmEmergencyTopicAdapter(Context context, RealmResults<EmergencyTopic> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }
}