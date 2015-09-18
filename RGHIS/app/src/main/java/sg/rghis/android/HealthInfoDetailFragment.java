package sg.rghis.android;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sg.rghis.android.databinding.HealthInfoDetailFragmentBinding;
import sg.rghis.android.model.HealthTopic;
import sg.rghis.android.model.ObservableHealthTopic;

public class HealthInfoDetailFragment extends Fragment {
    private static final String ARG_LARGE_LAYOUT = "large_layout";
    //    @Bind(R.id.toolbar)
//    Toolbar toolbar;
//    @Bind(R.id.tab_layout)
//    TabLayout tabLayout;
//    @Bind(R.id.viewpager)
//    ViewPager viewPager;
    private boolean isLargeLayout = false;
    private HealthInfoDetailFragmentBinding binding;
    private HealthDetailFragmentPagerAdapter adapter;
    private ObservableHealthTopic observableHealthTopic = new ObservableHealthTopic(null);

    public static HealthInfoDetailFragment newInstance(boolean isLargeLayout) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_LARGE_LAYOUT, isLargeLayout);
        HealthInfoDetailFragment fragment = new HealthInfoDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.health_info_detail_fragment,
                container,
                false);
        binding.setData(observableHealthTopic);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isLargeLayout = getArguments().getBoolean(ARG_LARGE_LAYOUT);

        if (!isLargeLayout) {
            binding.toolbar.setNavigationIcon(ContextCompat.getDrawable(getContext(),
                    R.drawable.ic_arrow_back_white_24dp));
            binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((HealthInfoFragment) getParentFragment()).closeDetailPane();
                }
            });
        }

        FragmentManager fm = getChildFragmentManager();
        adapter = new HealthDetailFragmentPagerAdapter(getContext(), fm, binding.viewpager.getId());
        binding.viewpager.setAdapter(adapter);

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Overview"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Symptoms"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Treatment"));
        binding.tabLayout.setupWithViewPager(binding.viewpager);

    }

    public void collapseToolbar() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
                binding.appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            behavior.onNestedFling(binding.coordinatorLayout, binding.appBarLayout, null, 0, 10000, true);
        }
    }

    public void expandToolbar() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
                binding.appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            behavior.setTopAndBottomOffset(0);
            behavior.onNestedPreScroll(binding.coordinatorLayout, binding.appBarLayout
                    , null, 0, 1, new int[2]);
        }
    }

    public void resetView() {
        expandToolbar();
        TabLayout.Tab tab = binding.tabLayout.getTabAt(0);
        if (tab != null)
            tab.select();
    }

    public void bindData(HealthTopic healthTopic) {
        observableHealthTopic.setHealthTopic(healthTopic);
        adapter.setHealthTopic(healthTopic);
        binding.collapsingToolbar.setTitle(Html.fromHtml(healthTopic.getTitle()));
//        ObservableHealthTopic data = new ObservableHealthTopic(healthTopic);
//        binding.setData(data);
//        binding.setVariable(sg.rghis.android.BR.title, healthTopic.getTitle());
//        binding.setData();
////        binding.invalidateAll();
//        binding.toolbar.setTitle(healthTopic.getTitle());
//        Timber.d(toolbar.getTitle().toString());
//        Timber.d(binding.toolbar.getTitle().toString());
    }
}
