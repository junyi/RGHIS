package sg.rghis.android;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.MaterialScrollBar;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import sg.rghis.android.model.HealthTopic;

public class HealthInfoFragment extends Fragment {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.container)
    FrameLayout container;

    private Realm realm;
    private HealthTopicAdapter adapter;
    private Subscription searchSubscription;
    private HealthInfoDetailFragment detailFragment;
    private SlidingPaneLayout slidingPaneLayout;
    private boolean isLargeLayout = false;

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

        determineCurrentLayout();

        detailFragment = HealthInfoDetailFragment.newInstance(isLargeLayout);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.container, detailFragment)
                .commit();

        toolbar.inflateMenu(R.menu.menu_health_info_list);
        final MenuItem searchMenuItem = toolbar.getMenu().findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(
                searchMenuItem);
        SearchManager searchManager = (SearchManager) getContext().getSystemService(
                Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                getActivity().getComponentName()));
        searchSubscription = RxSearchView.queryTextChanges(searchView)
                .debounce(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSearchObserver());
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open search when toolbar is clicked
                MenuItemCompat.expandActionView(searchMenuItem);
            }
        });

        realm = Realm.getDefaultInstance();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(
                ContextCompat.getDrawable(getContext(), R.color.divider)));

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

    private void determineCurrentLayout() {
        if (getView() != null) {
            isLargeLayout = getView().findViewById(R.id.two_pane_divider) != null;

            if (!isLargeLayout) {
                slidingPaneLayout = (SlidingPaneLayout) getView()
                        .findViewById(R.id.sliding_panel_layout);
                slidingPaneLayout.setShadowResourceLeft(
                        R.drawable.material_drawer_shadow_left);
                slidingPaneLayout.openPane();
            }
        }
    }

    public void closeDetailPane() {
        if (!isLargeLayout && slidingPaneLayout != null) {
            slidingPaneLayout.openPane();
        }
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

    private Observer<CharSequence> getSearchObserver() {
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
        if (searchSubscription != null) {
            searchSubscription.unsubscribe();
        }
        realm.close();
    }
}
