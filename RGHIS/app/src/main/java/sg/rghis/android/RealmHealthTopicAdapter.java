package sg.rghis.android;

import android.content.Context;

import com.turingtechnologies.materialscrollbar.INameableAdapter;

import io.realm.RealmResults;
import sg.rghis.android.model.HealthTopic;

public class RealmHealthTopicAdapter extends RealmModelAdapter<HealthTopic> {
    public RealmHealthTopicAdapter(Context context, RealmResults<HealthTopic> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }
}