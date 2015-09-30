package sg.rghis.android.disqus.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikepenz.iconics.view.IconicsTextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import sg.rghis.android.R;
import sg.rghis.android.disqus.DisqusSdkProvider;
import sg.rghis.android.disqus.models.Thread;

public class ThreadsItem extends RecyclerView.ViewHolder implements ViewHolderItem {
    @Inject
    Context context;

    @Bind(R.id.title)
    TextView titleTextView;

    @Bind(R.id.message)
    TextView messageTextView;

    @Bind(R.id.author)
    TextView authorTextView;

    @Bind(R.id.num_likes)
    IconicsTextView numLikesTextView;

    @Bind(R.id.num_replies)
    IconicsTextView numRepliesTextView;

    public ThreadsItem(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        injectThis();
    }

    public static ThreadsItem createInstance(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.threads_item, parent, false);
        return new ThreadsItem(view);
    }

    @Override
    public ViewHolderType getViewItemType() {
        return ViewHolderType.THREADS;
    }

    @Override
    public void onBindViewHolder(Object data) {
        Thread thread = (Thread) data;

        titleTextView.setText(thread.title);
        if (!TextUtils.isEmpty(thread.rawMessage))
            messageTextView.setText(thread.rawMessage);
        else
            messageTextView.setVisibility(View.GONE);
        authorTextView.setText(thread.author.getName());

        numLikesTextView.setText(String.valueOf(thread.likes));
        numRepliesTextView.setText(String.valueOf(thread.posts));
    }

    @Override
    public void injectThis() {
        DisqusSdkProvider.getInstance().getObjectGraph().inject(this);
    }
}
