package sg.rghis.android.disqus.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Toast;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.schedulers.Schedulers;
import sg.rghis.android.disqus.DisqusSdkProvider;
import sg.rghis.android.disqus.models.PaginatedList;
import sg.rghis.android.disqus.models.Post;
import sg.rghis.android.disqus.models.Thread;
import sg.rghis.android.disqus.services.ThreadsService;
import sg.rghis.android.utils.SystemUtils;

public class PostsAdapter extends HeaderPaginatedAdapter<Post, Thread> {

    @Inject
    ThreadsService threadsService;

    private Subscription subscription;
    private Thread thread;

    public PostsAdapter(Thread thread) {
        DisqusSdkProvider.getInstance().getObjectGraph().inject(this);
        this.thread = thread;
        setHeaderData(thread);
        setHasStableIds(true);
    }

    @Override
    protected void loadNextPage(String cursorId, Observer<PaginatedList<Post>> callback) {
        subscription = threadsService.listPosts(cursorId)
                .subscribeOn(Schedulers.io())
                .subscribe(callback);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            PostsItem postsItem = PostsItem.createInstance(parent);
            postsItem.setPostHandler(new PostsItem.PostHandler() {
                @Override
                public void onVoteResult(Post post, int voteDelta, int position) {
                    if (voteDelta == 0)
                        Toast.makeText(parent.getContext(), "You have already voted this post.",
                                Toast.LENGTH_SHORT).show();
                    else
                        updateItem(position, post);
                }

                @Override
                public void onDelete(Post post, final int position) {
                    SystemUtils.getActivityFromContext(parent.getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            removeAt(position);
                            Toast.makeText(parent.getContext(), "Post deleted successfully.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onUpdate(final Post post, final int position) {
                    SystemUtils.getActivityFromContext(parent.getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateItem(position, post);
                            Toast.makeText(parent.getContext(), "Post updated successfully.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            return postsItem;
        } else {
            return PostsHeaderItem.createInstance(parent);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ((ViewBinderInterface) holder).onBindViewHolder(getItem(position));
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        if (subscription != null)
            subscription.unsubscribe();
    }

    @Override
    public long getItemId(int position) {
        Object item = getItem(position);
        if (item instanceof Post) {
            return Long.valueOf(((Post) item).getPostId());
        }
        return -1;
    }
}
