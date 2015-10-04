package sg.rghis.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import sg.rghis.android.R;
import sg.rghis.android.views.widgets.RGSlidingPaneLayout;

public class NewsFragment extends BaseMasterDetailFragment {

    private NewsListFragment listFragment;
    private NewsDetailFragment detailFragment;
    private RGSlidingPaneLayout slidingPaneLayout;

    public static NewsFragment newInstance() {

        Bundle args = new Bundle();

        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getTitleRes() {
        return R.string.news;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_two_pane_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listFragment = NewsListFragment.newInstance();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.left_container, listFragment)
                .commit();
        detailFragment = NewsDetailFragment.newInstance(isLargeLayout, null);
        setDetailFragment(detailFragment);
//        getChildFragmentManager()
//                .beginTransaction()
//                .replace(R.id.right_container, detailFragment)
//                .commit();

    }

    public void loadDetail(String url) {
        detailFragment.loadUrl(url);
        if (!isLargeLayout)
            showDetailFragment();
//        openDetailPane();
    }

    @Override
    public void closeDetailPane() {
        detailFragment.stopLoading();
        super.closeDetailPane();
    }
}
