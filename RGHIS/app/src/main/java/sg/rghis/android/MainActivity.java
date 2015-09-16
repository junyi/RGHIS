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
import android.util.Log;
import android.view.Menu;

import com.quinny898.library.persistentsearch.SearchBox;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;
import sg.rghis.android.model.HealthTopic;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.searchbox)
    SearchBox search;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    private Realm realm;
    private HealthTopicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        RealmResults<HealthTopic> results = realm.where(HealthTopic.class)
                .beginsWith(HealthTopic.TITLE, "ca", RealmQuery.CASE_INSENSITIVE)
                .findAll();

        int length = results.size();
        int maximum = 20;
        int limit = Math.min(maximum, length);
        for (int i = 0; i < limit; i++) {
            Log.d("HealthTopic", stringHealthTopic(results.get(i)));
        }

//        search.enableVoiceRecognition(this);
        setSupportActionBar(toolbar);
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                openSearch();
//                return true;
//            }
//        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(
                ContextCompat.getDrawable(this, R.drawable.abc_list_divider_mtrl_alpha)));

        adapter = new HealthTopicAdapter();
        recyclerView.setAdapter(adapter);

//        //This is the code to provide a sectioned list
//        List<SimpleSectionedRecyclerViewAdapter.Section> sections =
//                new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();
//
//        //Sections
//        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Section 1"));
//        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(5, "Section 2"));
//        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(12, "Section 3"));
//        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(14, "Section 4"));
//        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(20, "Section 5"));
//
//        //Add your adapter to the sectionAdapter
//        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
//        SimpleSectionedRecyclerViewAdapter mSectionedAdapter = new
//                SimpleSectionedRecyclerViewAdapter(this, R.layout.section, R.id.section_text, adapter);
//        mSectionedAdapter.setSections(sections.toArray(dummy));
//
//        //Apply this adapter to the RecyclerView
//        recyclerView.setAdapter(mSectionedAdapter);
    }

    private String stringHealthTopic(HealthTopic topic) {
        return "HealthTopic {" + topic.getTitle() + "}";
    }

    @Override
    protected void onResume() {
        super.onResume();

        RealmResults<HealthTopic> results = realm.where(HealthTopic.class).findAll();
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

        return true;
    }
//
//    public void openSearch() {
//        toolbar.setTitle("");
//        search.revealFromMenuItem(R.id.action_search, this);
//        for (int x = 0; x < 10; x++) {
//            SearchResult option = new SearchResult("Result "
//                    + Integer.toString(x), ContextCompat.getDrawable(this,
//                    R.drawable.ic_history_black_24dp));
//            search.addSearchable(option);
//        }
//        search.setMenuListener(new SearchBox.MenuListener() {
//
//            @Override
//            public void onMenuClick() {
//                // Hamburger has been clicked
//                Toast.makeText(MainActivity.this, "Menu click",
//                        Toast.LENGTH_LONG).show();
//            }
//
//        });
//        search.setSearchListener(new SearchBox.SearchListener() {
//
//            @Override
//            public void onSearchOpened() {
//                // Use this to tint the screen
//
//            }
//
//            @Override
//            public void onSearchClosed() {
//                // Use this to un-tint the screen
//                closeSearch();
//            }
//
//            @Override
//            public void onSearchTermChanged() {
//                // React to the search term changing
//                // Called after it has updated results
//            }
//
//            @Override
//            public void onSearch(String searchTerm) {
//                RealmResults<HealthTopic> results = realm.where(HealthTopic.class)
//                        .contains(HealthTopic.TITLE, searchTerm, RealmQuery.CASE_INSENSITIVE)
//                        .findAll();
//                RealmHealthTopicAdapter realmAdapter = new RealmHealthTopicAdapter(getApplicationContext(), results, true);
//                adapter.setRealmAdapter(realmAdapter);
//                adapter.notifyDataSetChanged();
//
//                Toast.makeText(MainActivity.this, searchTerm + " Searched",
//                        Toast.LENGTH_LONG).show();
//                toolbar.setTitle(searchTerm);
//
//            }
//
//            @Override
//            public void onSearchCleared() {
//
//            }
//
//        });
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 1234 && resultCode == RESULT_OK) {
//            ArrayList<String> matches = data
//                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//            search.populateEditText(matches);
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    protected void closeSearch() {
//        search.hideCircularly(this);
//        if (search.getSearchText().isEmpty()) toolbar.setTitle("");
//    }

}
