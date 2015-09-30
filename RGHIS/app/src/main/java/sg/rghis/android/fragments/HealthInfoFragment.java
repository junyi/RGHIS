package sg.rghis.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.MaterialScrollBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observer;
import rx.Subscription;
import sg.rghis.android.R;
import sg.rghis.android.models.HealthTopic;
import sg.rghis.android.views.RecyclerItemClickListener;
import sg.rghis.android.views.adapters.HealthTopicAdapter;
import sg.rghis.android.views.adapters.RealmHealthTopicAdapter;
import sg.rghis.android.views.widgets.DividerItemDecoration;

public class HealthInfoFragment extends BaseMasterDetailFragment {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.right_container)
    FrameLayout container;

    private Realm realm;
    private HealthTopicAdapter adapter;
    private Subscription searchSubscription;
    private HealthInfoDetailFragment detailFragment;

    public static HealthInfoFragment newInstance() {

        Bundle args = new Bundle();

        HealthInfoFragment fragment = new HealthInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getTitleRes() {
        return R.string.health_info;
    }

    @Override
    public boolean shouldShowSearchView() {
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.health_info_two_pane_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        detailFragment = HealthInfoDetailFragment.newInstance(isLargeLayout);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.right_container, detailFragment)
                .commit();

//        toolbar.inflateMenu(R.menu.menu_health_info_list);
//        final MenuItem searchMenuItem = toolbar.getMenu().findItem(R.id.action_search);
//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(
//                searchMenuItem);
//        SearchManager searchManager = (SearchManager) getContext().getSystemService(
//                Context.SEARCH_SERVICE);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(
//                getActivity().getComponentName()));
//        searchSubscription = RxSearchView.queryTextChanges(searchView)
//                .debounce(200, TimeUnit.MILLISECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(getSearchObserver());
//        toolbar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Open search when toolbar is clicked
//                MenuItemCompat.expandActionView(searchMenuItem);
//            }
//        });

        realm = Realm.getDefaultInstance();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));

        adapter = new HealthTopicAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        HealthTopic healthTopic = adapter.getItem(position);
                        detailFragment.bindData(healthTopic);
                        if (!isLargeLayout && slidingPaneLayout != null) {
                            slidingPaneLayout.closePane();
                        }
                        detailFragment.resetView();
                    }
                }));

        MaterialScrollBar materialScrollBar = new MaterialScrollBar(getContext(), recyclerView, true);
        materialScrollBar.addIndicator(new AlphabetIndicator(getContext()));

    }

    @Override
    public void onResume() {
        super.onResume();
        RealmResults<HealthTopic> results = realm.where(HealthTopic.class).findAllSorted(HealthTopic.TITLE);
        RealmHealthTopicAdapter realmAdapter = new RealmHealthTopicAdapter(
                getActivity().getApplicationContext(), results, true);
        adapter.setRealmAdapter(realmAdapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public Observer<CharSequence> getSearchObserver() {
        return new Observer<CharSequence>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(CharSequence searchTerm) {
                RealmResults<HealthTopic> results = realm.where(HealthTopic.class)
                        .contains(HealthTopic.TITLE, searchTerm.toString(), RealmQuery.CASE_INSENSITIVE)
                        .findAllSorted(HealthTopic.TITLE);

                RealmHealthTopicAdapter realmAdapter = new RealmHealthTopicAdapter(
                        getContext().getApplicationContext(), results, true);
                adapter.setRealmAdapter(realmAdapter);
                adapter.notifyDataSetChanged();
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
