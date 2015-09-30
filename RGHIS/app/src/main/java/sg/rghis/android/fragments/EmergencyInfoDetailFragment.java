package sg.rghis.android.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sg.rghis.android.R;
import sg.rghis.android.databinding.EmergencyInfoDetailFragmentBinding;
import sg.rghis.android.models.EmergencyTopic;
import sg.rghis.android.models.ObservableEmergencyTopic;

public class EmergencyInfoDetailFragment extends Fragment {
    private static final String ARG_LARGE_LAYOUT = "large_layout";

    private boolean isLargeLayout = false;
    private EmergencyInfoDetailFragmentBinding binding;
    private ObservableEmergencyTopic observableEmergencyTopic = new ObservableEmergencyTopic();

    public static EmergencyInfoDetailFragment newInstance(boolean isLargeLayout) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_LARGE_LAYOUT, isLargeLayout);
        EmergencyInfoDetailFragment fragment = new EmergencyInfoDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.emergency_info_detail_fragment,
                container,
                false);
        binding.setData(observableEmergencyTopic);
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
//                    ((EmergencyInfoFragment) getParentFragment()).closeDetailPane();
//                }
//            });
        }
    }

    public void collapseToolbar() {
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
//                binding.appBarLayout.getLayoutParams();
//        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
//        if (behavior != null) {
//            behavior.onNestedFling(binding.coordinatorLayout, binding.appBarLayout, null, 0, 10000, true);
//        }
    }

    public void expandToolbar() {
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
//                binding.appBarLayout.getLayoutParams();
//        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
//        if (behavior != null) {
//            behavior.setTopAndBottomOffset(0);
//            behavior.onNestedPreScroll(binding.coordinatorLayout, binding.appBarLayout
//                    , null, 0, 1, new int[2]);
//        }
    }

    public void resetView() {
        expandToolbar();
    }

    public void bindData(EmergencyTopic emergencyTopic) {
        binding.getData().emergencyTopic.set(emergencyTopic);
    }
}
