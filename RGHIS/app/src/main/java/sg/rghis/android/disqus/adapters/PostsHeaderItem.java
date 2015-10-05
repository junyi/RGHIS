package sg.rghis.android.disqus.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import sg.rghis.android.R;
import sg.rghis.android.disqus.models.Thread;
import sg.rghis.android.models.User;
import sg.rghis.android.utils.DisqusUtils;
import sg.rghis.android.utils.UserManager;
import timber.log.Timber;

public class PostsHeaderItem extends RecyclerView.ViewHolder implements HeaderPaginatedAdapter.HeaderVH<Thread> {

    @Bind(R.id.title)
    TextView titleTextView;

    @Bind(R.id.message)
    TextView messageTextView;

    @Bind(R.id.author)
    TextView authorTextView;

    @Bind(R.id.num_likes)
    TextView numLikesTextView;

    @Bind(R.id.num_replies)
    TextView numRepliesTextView;

    @Bind(R.id.icon_notification)
    ImageView notificationFrame;

    @Bind(R.id.professional_label)
    View professionalLabel;

    private Context context;

    public PostsHeaderItem(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        context = itemView.getContext();
    }

    public static PostsHeaderItem createInstance(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_header, parent, false);
        return new PostsHeaderItem(view);
    }

    @Override
    public void onBindViewHolder(Thread thread) {
        Pair<String, String> nameRole = DisqusUtils.disqusNameToParse(thread.author.getName());

        titleTextView.setText(thread.title);
        messageTextView.setText(thread.rawMessage);
        authorTextView.setText(nameRole.first);
        numLikesTextView.setText(String.valueOf(thread.likes));
        numRepliesTextView.setText(String.valueOf(thread.posts));

        if (nameRole.second.equals(User.ROLE_HEALTH_PROFESSIONAL)) {
            professionalLabel.setVisibility(View.VISIBLE);
        } else {
            professionalLabel.setVisibility(View.GONE);
        }

        if (UserManager.isLoggedIn()) {
            final String channelName = DisqusUtils.getChannelName(thread.id);
            notificationFrame.setVisibility(View.VISIBLE);
            notificationFrame.setClickable(true);
            boolean isSubscribed = UserManager.isChannelSubscribed(channelName);
            updateNotificationFrame(isSubscribed);

            notificationFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isSubscribed = UserManager.isChannelSubscribed(channelName);
                    notificationClick(channelName, isSubscribed);
                }
            });
        } else {
            notificationFrame.setVisibility(View.INVISIBLE);

        }
    }

    private void updateNotificationFrame(boolean isSubscribed) {
        if (isSubscribed) {
            notificationFrame.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.ic_notifications_on_white_24dp));
        } else {
            notificationFrame.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.ic_notifications_off_white_24dp));
        }
    }

    private void notificationClick(String channelName, boolean isSubscribed) {
        if (isSubscribed) {
            ParsePush.unsubscribeInBackground(channelName, new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Timber.d(Log.getStackTraceString(e));
                    } else {
                        updateNotificationFrame(false);
                        Toast.makeText(context, "You have unsubscribed from this thread.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            ParsePush.subscribeInBackground(channelName, new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Timber.d(Log.getStackTraceString(e));
                    } else {
                        updateNotificationFrame(true);
                        Toast.makeText(context, "You are now subscribed to this thread.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
