package sg.rghis.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import sg.rghis.android.BuildConfig;
import sg.rghis.android.R;
import sg.rghis.android.disqus.adapters.CategoriesAdapter;
import sg.rghis.android.disqus.models.Category;
import sg.rghis.android.disqus.models.PaginatedList;
import sg.rghis.android.disqus.services.CategoriesService;
import sg.rghis.android.views.MainActivity;
import sg.rghis.android.views.RecyclerItemClickListener;
import sg.rghis.android.views.widgets.WrappingGridLayoutManager;
import timber.log.Timber;

public class CategoriesFragment extends BaseDisqusFragment {
    public static final String PREFIX_ADAPTER = ".CategoriesFragment.MyAdapter";

    @Inject
    CategoriesService categoriesService;

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    private CategoriesAdapter categoriesAdapter;

    public static CategoriesFragment newInstance() {
        Bundle args = new Bundle();

        CategoriesFragment fragment = new CategoriesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoriesAdapter = new CategoriesAdapter();

        if (savedInstanceState != null) {
            categoriesAdapter.onRestoreInstanceState(PREFIX_ADAPTER, savedInstanceState);
        } else {
            categoriesService.list(BuildConfig.FORUM_SHORTNAME, new GetCategoriesCallback());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.categories_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new WrappingGridLayoutManager(
                getContext(),
                3,
                WrappingGridLayoutManager.VERTICAL,
                false
        ));
        recyclerView.setAdapter(categoriesAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Category category = (Category) categoriesAdapter.getItem(position);
                gotoThreadsFragment(category);
            }
        }));
    }

    private void gotoThreadsFragment(Category category) {
        long categoryId = category.id;
        Bundle bundle = new Bundle();
        bundle.putLong(ThreadsContainerFragment.ARG_CATEGORY_ID, categoryId);
        boolean addToBackStack = true;
        navigateToState(MainActivity.STATE_THREADS, bundle, addToBackStack);
    }

    private class GetCategoriesCallback implements Callback<PaginatedList<Category>> {

        @Override
        public void success(PaginatedList<Category> categoryPaginatedList, Response response) {
            categoriesAdapter.addList(categoryPaginatedList);
            categoriesAdapter.notifyDataSetChanged();
        }

        @Override
        public void failure(RetrofitError error) {
            Timber.e(Log.getStackTraceString(error));
        }
    }
}
