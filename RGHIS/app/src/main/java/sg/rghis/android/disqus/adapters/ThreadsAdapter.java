package sg.rghis.android.disqus.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import sg.rghis.android.disqus.DisqusSdkProvider;
import sg.rghis.android.disqus.models.PaginatedList;
import sg.rghis.android.disqus.models.Thread;
import sg.rghis.android.disqus.services.CategoriesService;
import sg.rghis.android.disqus.utils.UrlUtils;

public class ThreadsAdapter extends PaginatedAdapter<Thread> {

    @Inject
    CategoriesService categoriesService;

    private long categoryId;

    private List<Integer> viewPixelOffsets = new LinkedList<>();

    public ThreadsAdapter(long categoryId) {
        DisqusSdkProvider.getInstance().getObjectGraph().inject(this);
        this.categoryId = categoryId;
    }

    @Override
    protected void loadNextPage(String cursorId, Callback<PaginatedList<Thread>> callback) {
        categoriesService.listThreads(
                categoryId,
                UrlUtils.author(),
                UrlUtils.cursor(cursorId),
                callback);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ThreadsItem.createInstance(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ((ViewHolderItem) holder).onBindViewHolder(getItem(position), getLeftPixelOffset(position));
    }

}
