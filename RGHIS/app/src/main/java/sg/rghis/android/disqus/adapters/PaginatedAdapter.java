package sg.rghis.android.disqus.adapters;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observer;
import sg.rghis.android.disqus.models.Cursor;
import sg.rghis.android.disqus.models.PaginatedList;

/**
 * Base adapter implementation for adapters associated with {@link PaginatedList}
 */
public abstract class PaginatedAdapter<T extends Parcelable> extends RecyclerView.Adapter implements Observer<PaginatedList<T>> {
    private static final String ARG_PAGINATED_LISTS = ".paginatedLists";
    private static final String ARG_ALL_RESOURCE_ITEMS = ".allResourceItems";

    private ArrayList<PaginatedList<T>> paginatedLists = new ArrayList<>(); // all paginated lists that have been fetched
    private ArrayList<T> allResourceItems = new ArrayList<>();

    private AtomicBoolean loading = new AtomicBoolean(false);

    protected abstract void loadNextPage(String cursorId, Observer<PaginatedList<T>> callback);

    public void onSaveInstanceState(String prefix, Bundle outState) {
        outState.putParcelableArrayList(prefix + ARG_PAGINATED_LISTS, paginatedLists);
        outState.putParcelableArrayList(prefix + ARG_ALL_RESOURCE_ITEMS, allResourceItems);
    }

    public void onRestoreInstanceState(String prefix, Bundle savedInstanceState) {
        paginatedLists = savedInstanceState.getParcelableArrayList(prefix + ARG_PAGINATED_LISTS);
        allResourceItems = savedInstanceState.getParcelableArrayList(prefix + ARG_ALL_RESOURCE_ITEMS);
    }

    public void addList(PaginatedList<T> list) {
        if (list != null) {
            paginatedLists.add(list);
            allResourceItems.addAll(list.getResponseData());
        }
    }

    public void addItem(T item) {
        if (item != null) {
            allResourceItems.add(0, item);
        }
    }

    public void loadNext() {
        Cursor cursor = paginatedLists.get(paginatedLists.size() - 1).getCursor();
        if (cursor.hasNext()) {
            if (loading.compareAndSet(false, true)) {
                loadNextPage(cursor.getNextPage(), this);
            } else {
                Log.d("", "Already loading next page");
            }
        }
    }

    public Object getItem(int position) {
        return allResourceItems.get(getItemCount() - 1 - position);
    }

    @Override
    public int getItemCount() {
        return allResourceItems.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == getItemCount() - 10) {
            loadNext();
        }
    }

    /**
     * Callback implementation for loading subsequent pages
     */
    @Override
    public void onNext(PaginatedList<T> paginatedList) {
        addList(paginatedList);
        notifyDataSetChanged();
        loading.set(false);
    }

    @Override
    public void onError(Throwable e) {
        loading.set(false);
    }

    @Override
    public void onCompleted() {

    }

}
