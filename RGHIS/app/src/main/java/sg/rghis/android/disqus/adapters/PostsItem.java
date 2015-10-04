package sg.rghis.android.disqus.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.Response;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import sg.rghis.android.R;
import sg.rghis.android.disqus.DisqusSdkProvider;
import sg.rghis.android.disqus.models.IdList;
import sg.rghis.android.disqus.models.Post;
import sg.rghis.android.disqus.models.ResponseItem;
import sg.rghis.android.disqus.models.VoteResponseItem;
import sg.rghis.android.disqus.services.PostsService;
import sg.rghis.android.utils.SystemUtils;
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

    @Bind(R.id.post_menu)
    View menuFrame;

    interface PostHandler {
        void onVoteResult(Post post, int voteDelta, int position);

        void onDelete(Post post, int position);

        void onUpdate(Post post, int position);
    }

    private PostHandler postHandler;
    private MaterialDialog menuDialog;
    private MaterialDialog deleteDialog;
    private MaterialDialog updateDialog;
    private int position;

    private Subscription updateSubscription;

    public void setPostHandler(PostHandler postHandler) {
        this.postHandler = postHandler;
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
        position = getAdapterPosition();

        messageTextView.setText(post.getRawMessage());
        authorTextView.setText(post.getAuthor().getName());
        timeAgoTextView.setText(buildTimeAgo(post.getCreatedDate()));
        numLikesTextView.setText(String.valueOf(post.getLikes()));

        upVoteFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upVote(post);
            }
        });

        downVoteFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downVote(post);
            }
        });

        ParseUser user = UserManager.getCurrentUser();
        if (UserManager.isLoggedIn() && user.getUsername().equals(post.getAuthor().getName())) {
            menuFrame.setVisibility(View.VISIBLE);
            menuFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showMenu(post);
                }
            });
        } else {
            menuFrame.setVisibility(View.GONE);
        }
    }

    private void upVote(Post post) {
        if (!SystemUtils.promptLoginIfNecessary())
            postsService.vote(post.getPostId(), 1).enqueue(new Callback<ResponseItem<VoteResponseItem>>() {
                @Override
                public void onResponse(Response<ResponseItem<VoteResponseItem>> response) {
                    VoteResponseItem vote = response.body().getResponse();
                    Post post = vote.getPost();
                    numLikesTextView.setText(String.valueOf(post.getLikes()));
                    if (postHandler != null)
                        postHandler.onVoteResult(post, vote.getDelta(), position);
                }

                @Override
                public void onFailure(Throwable t) {
                    Timber.d(Log.getStackTraceString(t));
                }
            });
    }

    private void downVote(Post post) {
        if (!SystemUtils.promptLoginIfNecessary())

            postsService.vote(post.getPostId(), -1).enqueue(new Callback<ResponseItem<VoteResponseItem>>() {
                @Override
                public void onResponse(Response<ResponseItem<VoteResponseItem>> response) {
                    VoteResponseItem vote = response.body().getResponse();
                    Post post = vote.getPost();
                    numLikesTextView.setText(String.valueOf(post.getLikes()));
                    if (postHandler != null)
                        postHandler.onVoteResult(post, vote.getDelta(), position);
                }

                @Override
                public void onFailure(Throwable t) {
                    Timber.d(Log.getStackTraceString(t));
                }
            });
    }

    private void deletePost(final Post post) {
        if (!SystemUtils.promptLoginIfNecessary())

            postsService.remove(post.getPostId()).enqueue(new Callback<IdList>() {
                @Override
                public void onResponse(Response<IdList> response) {
                    if (response.body().getCode() == 0) {
                        if (postHandler != null)
                            postHandler.onDelete(post, position);
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
    }

    private void updatePost(final Post post, String message) {
        if (!SystemUtils.promptLoginIfNecessary())

            postsService.update(post.getPostId(), message).enqueue(new Callback<ResponseItem<Post>>() {
                @Override
                public void onResponse(Response<ResponseItem<Post>> response) {
                    if (response.body().getCode() == 0) {
                        if (postHandler != null)
                            postHandler.onUpdate(response.body().getResponse(), position);
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
    }

    private void showMenu(final Post post) {
        if (menuDialog != null)
            menuDialog.cancel();
        menuDialog = new MaterialDialog.Builder(SystemUtils.getActivityFromContext(context))
                .items(context.getResources().getStringArray(R.array.post_item_menu))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {

                        switch (i) {
                            case 0:
                                showUpdateDialog(post);
                                break;
                            case 1:
                                showDeleteDialog(post);
                                break;

                        }
                    }
                })
                .build();
        menuDialog.show();
    }

    private void showUpdateDialog(final Post post) {
        if (updateDialog != null)
            updateDialog.cancel();
        boolean wrapInScrollView = true;
        updateDialog = new MaterialDialog.Builder(SystemUtils.getActivityFromContext(context))
                .title("Update Post")
                .customView(R.layout.dialog_create_post, wrapInScrollView)
                .positiveText("OK")
                .negativeText("Cancel")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        EditText messageEditText = (EditText) updateDialog.findViewById(R.id.message_edit_text);
                        String message = messageEditText.getText().toString();

                        updatePost(post, message);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }
                })
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        updateDialog = null;
                        if (updateSubscription != null)
                            updateSubscription.unsubscribe();
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        updateDialog = null;
                        if (updateSubscription != null)
                            updateSubscription.unsubscribe();
                    }
                })
                .build();

        EditText messageEditText = (EditText) updateDialog.findViewById(R.id.message_edit_text);
        updateSubscription = RxTextView.textChanges(messageEditText)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CharSequence>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(CharSequence charSequence) {
                        if (TextUtils.isEmpty(charSequence)) {
                            updateDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        } else {
                            updateDialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                        }
                    }
                });
        updateDialog.show();
    }

    private void showDeleteDialog(final Post post) {
        if (updateDialog != null)
            updateDialog.cancel();
        deleteDialog = new MaterialDialog.Builder(SystemUtils.getActivityFromContext(context))
                .title("Delete post?")
                .positiveText("Delete")
                .negativeText("Cancel")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        deletePost(post);
                    }
                })
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        deleteDialog = null;
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        deleteDialog = null;
                    }
                })
                .build();
        deleteDialog.show();
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
