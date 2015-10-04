package sg.rghis.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import sg.rghis.android.R;
import sg.rghis.android.views.MainActivity;
import sg.rghis.android.views.widgets.RGSlidingPaneLayout;

public abstract class BaseMasterDetailFragment extends BaseFragment {
    protected RGSlidingPaneLayout slidingPaneLayout;
    protected boolean isLargeLayout = false;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        determineCurrentLayout();
    }

    protected void determineCurrentLayout() {
        if (getView() != null) {
            String tag = (String) getView().getTag();
            isLargeLayout = tag != null && tag.equals(getString(R.string.large_layout));

            if (!isLargeLayout) {
                slidingPaneLayout = (RGSlidingPaneLayout) getView()
                        .findViewById(R.id.sliding_panel_layout);
                slidingPaneLayout.setShadowResourceLeft(
                        R.drawable.material_drawer_shadow_left);
                slidingPaneLayout.setShadowResourceRight(
                        R.drawable.material_drawer_shadow_right);
                slidingPaneLayout.openPane();
            }
        }
    }

    public void setDetailFragment(Fragment f) {
        if (!isLargeLayout) {
            ((MainActivity) getContext()).getMainFragment().setDetailFragment(f);
        } else {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.right_container, f)
                    .commit();
        }
    }

    public void showDetailFragment() {
        ((MainActivity) getContext()).getMainFragment().showDetailFragment();
    }

    public void closeDetailPane() {
        if (!isLargeLayout && slidingPaneLayout != null) {
            slidingPaneLayout.openPane();
        }
    }

    public void openDetailPane() {
        if (!isLargeLayout && slidingPaneLayout != null) {
            slidingPaneLayout.closePane();
        }
    }

    protected boolean isDetailShowing() {
        return slidingPaneLayout != null && !slidingPaneLayout.isOpen();
    }

    @Override
    public boolean onBackPressed() {
        if (isDetailShowing()) {
            closeDetailPane();
            return true;
        }
        return false;
    }
}
