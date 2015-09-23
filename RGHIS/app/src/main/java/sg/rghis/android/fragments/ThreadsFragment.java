package sg.rghis.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import sg.rghis.android.R;
import sg.rghis.android.disqus.adapters.ThreadsAdapter;
import sg.rghis.android.disqus.models.PaginatedList;
import sg.rghis.android.disqus.models.Thread;
import sg.rghis.android.disqus.services.CategoriesService;
import sg.rghis.android.disqus.utils.UrlUtils;
import timber.log.Timber;

public class ThreadsFragment extends BaseDisqusFragment {
    public static final String PREFIX_ADAPTER = ".ThreadsFragment.MyAdapter";

    @Inject
    CategoriesService categoriesService;

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    private ThreadsAdapter threadsAdapter;
    private long categoryId;

    public static ThreadsFragment newInstance(long categoryId) {
        Bundle args = new Bundle();
        args.putLong(ThreadsContainerFragment.ARG_CATEGORY_ID, categoryId);

        ThreadsFragment fragment = new ThreadsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle == null || !bundle.containsKey(ThreadsContainerFragment.ARG_CATEGORY_ID)) {
            throw new IllegalStateException("Category ID must be provided.");
        } else {
            categoryId = bundle.getLong(ThreadsContainerFragment.ARG_CATEGORY_ID);
        }

        threadsAdapter = new ThreadsAdapter(categoryId);

        if (savedInstanceState != null) {
            threadsAdapter.onRestoreInstanceState(PREFIX_ADAPTER, savedInstanceState);
        } else {
            categoriesService.listThreads(categoryId,
                    UrlUtils.author(),
                    null,
                    new GetThreadsCallback());
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.thread_list_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(threadsAdapter);
    }

    private class GetThreadsCallback implements Callback<PaginatedList<Thread>> {

        @Override
        public void success(PaginatedList<Thread> threadPaginatedList, Response response) {
            threadsAdapter.addList(threadPaginatedList);
            threadsAdapter.notifyDataSetChanged();
        }

        @Override
        public void failure(RetrofitError error) {
            Timber.e(Log.getStackTraceString(error));
        }
    }
}
