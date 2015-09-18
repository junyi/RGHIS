package sg.rghis.android;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import sg.rghis.android.model.HealthTopic;

public class HealthTopicAdapter extends RealmRecyclerViewAdapter<HealthTopic, HealthTopicAdapter.SimpleViewHolder>
        implements INameableAdapter {

    private final Context context;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.title)
        TextView title;

        public SimpleViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public HealthTopicAdapter(Context context) {
        this.context = context;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.health_topic_item, parent, false);
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