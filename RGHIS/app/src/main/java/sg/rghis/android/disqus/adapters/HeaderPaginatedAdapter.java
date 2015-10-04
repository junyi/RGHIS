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

public abstract class HeaderPaginatedAdapter<T extends Parcelable, U extends Parcelable>
        extends RecyclerView.Adapter implements Observer<PaginatedList<T>> {
    private static final String ARG_PAGINATED_LISTS = ".paginatedLists";
    private static final String ARG_ALL_RESOURCE_ITEMS = ".allResourceItems";
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    private ArrayList<PaginatedList<T>> paginatedLists = new ArrayList<>(); // all paginated lists that have been fetched
    protected ArrayList<T> allResourceItems = new ArrayList<>();

    private AtomicBoolean loading = new AtomicBoolean(false);

    private U headerData;

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

    public void removeAt(int position) {
        if (!isPositionHeader(position)) {
            allResourceItems.remove(position - 1);
            notifyItemRemoved(position);
        }
    }

    public void updateItem(int position, T item) {
        if (!isPositionHeader(position) && item != null) {
            allResourceItems.set(position - 1, item);
            notifyItemChanged(position, item);
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
        if (isPositionHeader(position)) {
            return headerData;
        } else {
            return allResourceItems.get(position - 1);
        }
    }

    @Override
    public int getItemCount() {
        return allResourceItems.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public void setHeaderData(U data) {
        headerData = data;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderVH) {
            ((HeaderVH) holder).onBindViewHolder(headerData);
        } else {
            if (position == getItemCount() - 10) {
                loadNext();
            }
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

    public interface HeaderVH<U> extends ViewBinderInterface<U> {
        void onBindViewHolder(U data);
    }

}
