package sg.rghis.android.disqus.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.Response;
import sg.rghis.android.R;
import sg.rghis.android.disqus.DisqusSdkProvider;
import sg.rghis.android.disqus.models.Post;
import sg.rghis.android.disqus.models.ResponseItem;
import sg.rghis.android.disqus.models.VoteResponseItem;
import sg.rghis.android.disqus.services.PostsService;
import sg.rghis.android.utils.UserManager;
import timber.log.Timber;

public class PostsItem extends RecyclerView.ViewHolder implements ViewHolderItem {
    @Inject
    Context context;

    @Inject
    PostsService postsService;

    @Bind(R.id.txt_comment)
    TextView messageTextView;

    @Bind(R.id.txt_user_name)
    TextView authorTextView;

    @Bind(R.id.txt_up_votes)
    TextView numLikesTextView;

    @Bind(R.id.txt_time_ago)
    TextView timeAgoTextView;

    @Bind(R.id.frame_up_vote)
    View upVoteFrame;

    @Bind(R.id.frame_down_vote)
    View downVoteFrame;

    interface OnVoteListener {
        void onVoteResult(Post post, int voteDelta);
    }

    private OnVoteListener onVoteListener;

    public void setOnVoteListener(OnVoteListener onVoteListener) {
        this.onVoteListener = onVoteListener;
    }

    public PostsItem(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        injectThis();
    }

    public static PostsItem createInstance(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_item, parent, false);
        return new PostsItem(view);
    }

    @Override
    public ViewHolderType getViewItemType() {
        return ViewHolderType.POSTS;
    }

    @Override
    public void onBindViewHolder(Object data) {
        final Post post = (Post) data;

        messageTextView.setText(post.getRawMessage());
        authorTextView.setText(post.getAuthor().getName());
        timeAgoTextView.setText(buildTimeAgo(post.getCreatedDate()));
        numLikesTextView.setText(String.valueOf(post.getLikes()));

        upVoteFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!UserManager.isLoggedIn())

                    postsService.vote(post.getPostId(), 1).enqueue(new Callback<ResponseItem<VoteResponseItem>>() {
                        @Override
                        public void onResponse(Response<ResponseItem<VoteResponseItem>> response) {
                            VoteResponseItem vote = response.body().getResponse();
                            Post post = vote.getPost();
                            numLikesTextView.setText(String.valueOf(post.getLikes()));
                            if (onVoteListener != null)
                                onVoteListener.onVoteResult(post, vote.getDelta());
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Timber.d(Log.getStackTraceString(t));
                        }
                    });
                else{

                }
            }
        });

        downVoteFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postsService.vote(post.getPostId(), -1).enqueue(new Callback<ResponseItem<VoteResponseItem>>() {
                    @Override
                    public void onResponse(Response<ResponseItem<VoteResponseItem>> response) {
                        VoteResponseItem vote = response.body().getResponse();
                        Post post = vote.getPost();
                        numLikesTextView.setText(String.valueOf(post.getLikes()));
                        if (onVoteListener != null)
                            onVoteListener.onVoteResult(post, vote.getDelta());
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Timber.d(Log.getStackTraceString(t));
                    }
                });
            }
        });

    }

    @Override
    public void injectThis() {
        DisqusSdkProvider.getInstance().getObjectGraph().inject(this);
    }

    private String buildTimeAgo(Date createDate) {
        Calendar now = new GregorianCalendar();
        Calendar create = new GregorianCalendar();
        create.setTime(createDate);

        int nowYear = now.get(Calendar.YEAR);
        int nowDay = now.get(Calendar.DAY_OF_YEAR);
        int nowHour = now.get(Calendar.HOUR);
        int nowMinute = now.get(Calendar.MINUTE);

        int createYear = create.get(Calendar.YEAR);
        int createDay = create.get(Calendar.DAY_OF_YEAR);
        int createHour = create.get(Calendar.HOUR);
        int createMinute = create.get(Calendar.MINUTE);

        if (nowYear == createYear && nowDay == createDay && nowHour == createHour && nowMinute == createMinute) {
            return context.getString(R.string.moments_ago);
        } else if (nowYear == createYear && nowDay == createDay && nowHour == createHour) {
            int diff = nowMinute - createMinute;
            return diff == 1 ? context.getString(R.string.minute_ago) : String.format(context.getString(R.string.minutes_ago), diff);
        } else if (nowYear == createYear && nowDay == createDay) {
            if (nowHour - 1 == createHour && nowMinute < createMinute) {
                int diff = nowMinute + (60 - createMinute);
                return diff == 1 ? context.getString(R.string.minute_ago) : String.format(context.getString(R.string.minutes_ago), diff);
            } else {
                int diff = nowHour - createHour;
                return diff == 1 ? context.getString(R.string.hour_ago) : String.format(context.getString(R.string.hours_ago), diff);
            }
        } else if (nowYear == createYear) {
            if (nowDay - 1 == createDay && nowHour < createHour) {
                int diff = nowHour + (24 - createHour);
                return diff == 1 ? context.getString(R.string.hour_ago) : String.format(context.getString(R.string.hours_ago), diff);
            } else {
                int diff = nowDay - createDay;
                return diff == 1 ? context.getString(R.string.day_ago) : String.format(context.getString(R.string.days_ago), diff);
            }
        } else {
            if (nowYear - 1 == createYear && nowDay < createDay) {
                int diff = nowDay + (365 - createDay);
                return diff == 1 ? context.getString(R.string.day_ago) : String.format(context.getString(R.string.days_ago), diff);
            } else {
                int diff = nowYear - createYear;
                if (nowDay < createDay) {
                    diff--;
                }
                return diff == 1 ? context.getString(R.string.year_ago) : String.format(context.getString(R.string.years_ago), diff);
            }
        }
    }

}
