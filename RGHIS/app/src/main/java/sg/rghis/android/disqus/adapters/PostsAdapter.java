package sg.rghis.android.disqus.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import sg.rghis.android.disqus.DisqusSdkProvider;
import sg.rghis.android.disqus.models.PaginatedList;
import sg.rghis.android.disqus.models.Post;
import sg.rghis.android.disqus.services.ThreadsService;

public class PostsAdapter extends PaginatedAdapter<Post> {

    @Inject
    ThreadsService threadsService;

    private Subscription subscription;
    private long threadId;

    private List<Integer> viewPixelOffsets = new LinkedList<>();

    public PostsAdapter(long threadId) {
        DisqusSdkProvider.getInstance().getObjectGraph().inject(this);
        this.threadId = threadId;
    }

    @Override
    protected void loadNextPage(String cursorId, Observer<PaginatedList<Post>> callback) {
        subscription = threadsService.listPosts(cursorId)
                .subscribe(callback);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        PostsItem postsItem = PostsItem.createInstance(parent);
        postsItem.setOnVoteListener(new PostsItem.OnVoteListener() {
            @Override
            public void onVoteResult(Post post, int voteDelta) {
                if (voteDelta == 0)
                    Toast.makeText(parent.getContext(), "You have already voted this post.",
                            Toast.LENGTH_SHORT).show();
            }
        });
        return postsItem;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ((ViewHolderItem) holder).onBindViewHolder(getItem(position));
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        if (subscription != null)
            subscription.unsubscribe();
    }
}
