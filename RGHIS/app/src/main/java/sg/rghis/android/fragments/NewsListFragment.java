package sg.rghis.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import sg.rghis.android.R;
import sg.rghis.android.rss.RssProvider;
import sg.rghis.android.rss.adapters.NewsAdapter;
import sg.rghis.android.rss.models.Item;
import sg.rghis.android.rss.models.Rss;
import sg.rghis.android.rss.services.RssService;
import sg.rghis.android.views.RecyclerItemClickListener;
import sg.rghis.android.views.widgets.DividerItemDecoration;
import timber.log.Timber;

public class NewsListFragment extends Fragment {
    public static final String PREFIX_ADAPTER = ".NewsFragment.MyAdapter";

    @Inject
    RssService rssService;

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    private Subscription subscription;
    private NewsAdapter newsAdapter;
    private DynamicBox dynamicBox;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        RssProvider.getInstance().getObjectGraph().inject(this);
    }

    public static NewsListFragment newInstance() {
        Bundle args = new Bundle();

        NewsListFragment fragment = new NewsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newsAdapter = new NewsAdapter();
        Observable<Rss> observable = rssService.getNews();
        subscription = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GetRssObserver());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_list_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dynamicBox = new DynamicBox(getContext(), recyclerView);
        dynamicBox.showLoadingLayout();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        recyclerView.setAdapter(newsAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Item rssItem = newsAdapter.getItem(position);
                        String url = rssItem.getLink();
                        ((NewsFragment) getParentFragment()).loadDetail(url);
                    }
                }));

    }

    private class GetRssObserver implements Observer<Rss> {
        @Override
        public void onCompleted() {
            dynamicBox.hideAll();
        }

        @Override
        public void onError(Throwable e) {
            Timber.e(Log.getStackTraceString(e));
        }

        @Override
        public void onNext(Rss rss) {
            newsAdapter.addList(rss.getChannel().getItems());
            newsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null)
            subscription.unsubscribe();
    }
}
