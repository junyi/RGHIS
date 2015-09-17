package sg.rghis.android;

import android.app.SearchManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.MaterialScrollBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import sg.rghis.android.databinding.HealthTopicSheetBinding;
import sg.rghis.android.model.HealthTopic;
import sg.rghis.android.model.ObservableHealthTopic;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.bottomsheet)
    BottomSheetLayout bottomSheet;

    private Realm realm;
    private HealthTopicAdapter adapter;
    private Subscription searchSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        setSupportActionBar(toolbar);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(
                ContextCompat.getDrawable(this, R.drawable.abc_list_divider_mtrl_alpha)));

        adapter = new HealthTopicAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                HealthTopicSheetBinding binding = HealthTopicSheetBinding.inflate(
                        LayoutInflater.from(MainActivity.this), bottomSheet, false);
                binding.setData(new ObservableHealthTopic(adapter.getItem(position)));
                bottomSheet.showWithSheetView(binding.getRoot());
            }
        }));

        MaterialScrollBar materialScrollBar = new MaterialScrollBar(this, recyclerView, true);
        materialScrollBar.addIndicator(new AlphabetIndicator(this));
//        realm.beginTransaction();
//        try {
//            realm.where(HealthTopic.class).equalTo(HealthTopic.TITLE, "W").findFirst().removeFromRealm();
//            realm.where(HealthTopic.class).equalTo(HealthTopic.TITLE, "X").findFirst().removeFromRealm();
//            realm.where(HealthTopic.class).equalTo(HealthTopic.TITLE, "Y").findFirst().removeFromRealm();
//            realm.where(HealthTopic.class).equalTo(HealthTopic.TITLE, "Z").findFirst().removeFromRealm();
//        } catch (NullPointerException ex) {
//
//        }
//
//        realm.commitTransaction();


    }


    @Override
    protected void onResume() {
        super.onResume();
        RealmResults<HealthTopic> results = realm.where(HealthTopic.class).findAllSorted(HealthTopic.TITLE);
        RealmHealthTopicAdapter realmAdapter = new RealmHealthTopicAdapter(getApplicationContext(), results, true);
        adapter.setRealmAdapter(realmAdapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchSubscription = RxSearchView.queryTextChanges(searchView)
                .debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSearchObserver());
        return true;
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

                RealmHealthTopicAdapter realmAdapter = new RealmHealthTopicAdapter(getApplicationContext(), results, true);
                adapter.setRealmAdapter(realmAdapter);
                adapter.notifyDataSetChanged();
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchSubscription != null) {
            searchSubscription.unsubscribe();
        }
        realm.close();
    }
}
