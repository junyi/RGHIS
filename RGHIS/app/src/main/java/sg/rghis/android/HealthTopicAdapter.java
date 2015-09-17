package sg.rghis.android;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.turingtechnologies.materialscrollbar.INameableAdapter;

import sg.rghis.android.model.HealthTopic;

public class HealthTopicAdapter extends RealmRecyclerViewAdapter<HealthTopic, HealthTopicAdapter.SimpleViewHolder>
        implements INameableAdapter {

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;

        public SimpleViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.healthtopic_item, parent, false);
        return new SimpleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder simpleViewHolder, int i) {
        HealthTopic healthTopic = getItem(i);
        simpleViewHolder.title.setText(healthTopic.getTitle());
    }

    /* The inner RealmBaseAdapter
     * view count is applied here.
     *
     * getRealmAdapter is defined in RealmRecyclerViewAdapter.
     */
    @Override
    public int getItemCount() {
        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }

    @Override
    public Character getCharacterForElement(int i) {
        return getItem(i).getTitle().charAt(0);
    }
}