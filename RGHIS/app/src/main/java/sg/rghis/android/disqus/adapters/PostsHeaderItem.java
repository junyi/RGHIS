package sg.rghis.android.disqus.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import sg.rghis.android.R;
import sg.rghis.android.disqus.models.Thread;

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

    public PostsHeaderItem(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public static PostsHeaderItem createInstance(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_header, parent, false);
        return new PostsHeaderItem(view);
    }

    @Override
    public void onBindViewHolder(Thread thread) {
        titleTextView.setText(thread.title);
        messageTextView.setText(thread.rawMessage);
        authorTextView.setText(thread.author.getName());
        numLikesTextView.setText(String.valueOf(thread.likes));
        numRepliesTextView.setText(String.valueOf(thread.posts));

    }

}
