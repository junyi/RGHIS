package sg.rghis.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import sg.rghis.android.R;
import sg.rghis.android.disqus.adapters.ThreadsAdapter;
import sg.rghis.android.views.widgets.RGSlidingPaneLayout;

public class ThreadsContainerFragment extends Fragment {
    public final static String ARG_CATEGORY_ID = "categoryId";

    @Bind(R.id.left_container)
    FrameLayout leftContainer;
    @Bind(R.id.right_container)
    FrameLayout rightContainer;

    private long categoryId;
    private ThreadsAdapter adapter;
    private ThreadsFragment listFragment;
    private PostsFragment postsFragment;
    private RGSlidingPaneLayout slidingPaneLayout;
    private boolean isLargeLayout = false;

    public static ThreadsContainerFragment newInstance(long categoryId) {

        Bundle args = new Bundle();
        args.putLong(ARG_CATEGORY_ID, categoryId);

        ThreadsContainerFragment fragment = new ThreadsContainerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null || !bundle.containsKey(ThreadsContainerFragment.ARG_CATEGORY_ID)) {
            throw new IllegalStateException("Category ID must be provided.");
        } else {
            categoryId = bundle.getLong(ThreadsContainerFragment.ARG_CATEGORY_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.threads_two_pane_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        determineCurrentLayout();

        listFragment = ThreadsFragment.newInstance(categoryId);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.left_container, listFragment)
                .commit();

    }

    private void determineCurrentLayout() {
        if (getView() != null) {
            isLargeLayout = getView().findViewById(R.id.two_pane_divider) != null;

            if (!isLargeLayout) {
                slidingPaneLayout = (RGSlidingPaneLayout) getView()
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

    public void loadPosts(long threadId) {
        postsFragment = PostsFragment.newInstance(threadId);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.right_container, postsFragment)
                .commit();
        if (!isLargeLayout && slidingPaneLayout != null) {
            slidingPaneLayout.closePane();
        }
    }
}
