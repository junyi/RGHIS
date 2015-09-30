package sg.rghis.android.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import sg.rghis.android.R;
import sg.rghis.android.databinding.HealthInfoDetailFragmentBinding;
import sg.rghis.android.models.HealthTopic;
import sg.rghis.android.models.ObservableHealthTopic;
import sg.rghis.android.views.adapters.HealthDetailFragmentPagerAdapter;
import timber.log.Timber;

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
    private ObservableHealthTopic observableHealthTopic = new ObservableHealthTopic();

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
//            binding.toolbar.setNavigationIcon(ContextCompat.getDrawable(getContext(),
//                    R.drawable.ic_arrow_back_white_24dp));
//            binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    ((HealthInfoFragment) getParentFragment()).closeDetailPane();
//                }
//            });
        }

        FragmentManager fm = getChildFragmentManager();
        adapter = new HealthDetailFragmentPagerAdapter(getContext(), fm, null);
        binding.viewpager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.viewpager);

    }

//    public void collapseToolbar() {
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
//                binding.appBarLayout.getLayoutParams();
//        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
//        if (behavior != null) {
//            behavior.onNestedFling(binding.coordinatorLayout, binding.appBarLayout, null, 0, 10000, true);
//        }
//    }
//
//    public void expandToolbar() {
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
//                binding.appBarLayout.getLayoutParams();
//        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
//        if (behavior != null) {
//            behavior.setTopAndBottomOffset(0);
//            behavior.onNestedPreScroll(binding.coordinatorLayout, binding.appBarLayout
//                    , null, 0, 1, new int[2]);
//        }
//    }

    public void resetView() {
//        expandToolbar();
        TabLayout.Tab tab = binding.tabLayout.getTabAt(0);
        if (tab != null)
            tab.select();
    }

    public void bindData(HealthTopic healthTopic) {
        binding.getData().healthTopic.set(healthTopic);
//        binding.tabLayout.removeAllTabs();
        Map<Integer, String> urlMap = new HashMap<>();
        if (!healthTopic.getOverview().equals("null")) {
//            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Overview"));
            Timber.d("Overview: %s", healthTopic.getOverview());
            urlMap.put(0, healthTopic.getOverview());
        }
        if (!healthTopic.getSymptoms().equals("null")) {
//            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Symptoms"));
            urlMap.put(1, healthTopic.getSymptoms());
        }
        if (!healthTopic.getTreatment().equals("null")) {
//            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Treatment"));
            Timber.d("Treatment: %s", healthTopic.getTreatment());
            urlMap.put(2, healthTopic.getTreatment());
        }
        adapter = new HealthDetailFragmentPagerAdapter(getContext(), getFragmentManager(), urlMap);
        binding.viewpager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.viewpager);
    }
}
