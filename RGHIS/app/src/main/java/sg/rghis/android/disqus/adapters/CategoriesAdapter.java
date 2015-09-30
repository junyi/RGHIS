package sg.rghis.android.disqus.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.schedulers.Schedulers;
import sg.rghis.android.BuildConfig;
import sg.rghis.android.disqus.DisqusSdkProvider;
import sg.rghis.android.disqus.models.Category;
import sg.rghis.android.disqus.models.PaginatedList;
import sg.rghis.android.disqus.services.CategoriesService;
import sg.rghis.android.disqus.utils.UrlUtils;

public class CategoriesAdapter extends PaginatedAdapter<Category> {

    @Inject
    CategoriesService categoriesService;

    private Subscription subscription;
    private List<Integer> viewPixelOffsets = new LinkedList<>();

    @Inject
    public CategoriesAdapter() {
        DisqusSdkProvider.getInstance().getObjectGraph().inject(this);
    }

    @Override
    protected void loadNextPage(String cursorId, Observer<PaginatedList<Category>> callback) {
        subscription = categoriesService.list(
                BuildConfig.FORUM_SHORTNAME,
                UrlUtils.cursor(cursorId))
                .subscribeOn(Schedulers.io())
                .subscribe(callback);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return CategoriesItem.createInstance(parent);
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
