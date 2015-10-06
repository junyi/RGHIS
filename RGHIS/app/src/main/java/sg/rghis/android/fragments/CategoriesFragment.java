package sg.rghis.android.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import mehdi.sakout.dynamicbox.DynamicBox;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import sg.rghis.android.BuildConfig;
import sg.rghis.android.R;
import sg.rghis.android.disqus.adapters.CategoriesAdapter;
import sg.rghis.android.disqus.models.Category;
import sg.rghis.android.disqus.models.PaginatedList;
import sg.rghis.android.disqus.services.CategoriesService;
import sg.rghis.android.views.RecyclerItemClickListener;
import sg.rghis.android.views.widgets.AutofitRecyclerView;
import sg.rghis.android.views.widgets.RoundedLetterView;
import timber.log.Timber;

public class CategoriesFragment extends BaseDisqusFragment {
    public static final String PREFIX_ADAPTER = ".CategoriesFragment.MyAdapter";

    @Inject
    CategoriesService categoriesService;

    @Bind(R.id.recycler_view)
    AutofitRecyclerView recyclerView;

    private CategoriesAdapter categoriesAdapter;
    private Subscription subscription;
    private DynamicBox dynamicBox;

    public static CategoriesFragment newInstance() {
        Bundle args = new Bundle();

        CategoriesFragment fragment = new CategoriesFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public int getTitleRes() {
        return R.string.forum;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoriesAdapter = new CategoriesAdapter();

        if (savedInstanceState != null) {
            categoriesAdapter.onRestoreInstanceState(PREFIX_ADAPTER, savedInstanceState);
        } else {
            Observable<PaginatedList<Category>> observable = categoriesService.list(BuildConfig.FORUM_SHORTNAME);
            subscription = observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(this.<PaginatedList<Category>>bindToLifecycle())
                    .subscribe(new GetCategoriesObserver());
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

        dynamicBox = new DynamicBox(getContext(), recyclerView);
        dynamicBox.showLoadingLayout();
        recyclerView.setAdapter(categoriesAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Category category = (Category) categoriesAdapter.getItem(position);
                gotoThreadsFragment(view, category);
            }
        }));
    }

    private void gotoThreadsFragment(View view, Category category) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ThreadsFragment f = ThreadsFragment.newInstance(category);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Inflate transitions to apply
            Transition changeTransform = TransitionInflater.from(getContext()).
                    inflateTransition(R.transition.change_avatar_transform);
            Transition explodeTransform = TransitionInflater.from(getContext()).
                    inflateTransition(android.R.transition.fade);

            // Setup exit transition on first fragment
            setSharedElementReturnTransition(changeTransform);
            setExitTransition(explodeTransform);

            // Setup enter transition on second fragment
            f.setSharedElementEnterTransition(changeTransform);
            f.setEnterTransition(explodeTransform);

            // Prevent transitions for overlapping
            f.setAllowEnterTransitionOverlap(true);
            f.setAllowReturnTransitionOverlap(true);


            // Find the shared element (in Fragment A)
            RoundedLetterView avatar = (RoundedLetterView) view.findViewById(R.id.image);

            // Add second fragment by replacing first
            ft.addSharedElement(avatar, avatar.getTransitionName());
        }

        ft.replace(R.id.main_content, f);
        ft.addToBackStack(null);
        ft.commit();
    }

    private class GetCategoriesObserver implements Observer<PaginatedList<Category>> {
        @Override
        public void onCompleted() {
            dynamicBox.hideAll();
        }

        @Override
        public void onError(Throwable e) {
            Timber.e(Log.getStackTraceString(e));
        }

        @Override
        public void onNext(PaginatedList<Category> categoryPaginatedList) {
            categoriesAdapter.addList(categoryPaginatedList);
            categoriesAdapter.notifyDataSetChanged();
        }
    }
}
