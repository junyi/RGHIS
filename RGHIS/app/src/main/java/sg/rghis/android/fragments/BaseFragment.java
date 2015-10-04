package sg.rghis.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import rx.Observer;
import sg.rghis.android.utils.SystemUtils;
import sg.rghis.android.views.MainActivity;

public abstract class BaseFragment extends Fragment {
    public interface OnViewReadyListener {
        void onViewReady(BaseFragment fragment);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof Activity && context instanceof MainActivity)) {
            throw new ClassCastException("Activity must be MainActivity");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity activity = SystemUtils.getMainActivityFromContext(getContext());
        if (activity != null)
            activity.getMainFragment().onViewReady(this);
    }

    protected void navigateToState(@MainFragment.FragmentState int state, Bundle bundle, boolean addToBackStack) {
        ((MainActivity) getActivity()).getMainFragment().navigateToState(state, bundle, addToBackStack);
    }

    public boolean onBackPressed() {
        return false;
    }

    public int getTitleRes() {
        return -1;
    }

    public boolean shouldShowSearchView() {
        return false;
    }

    public Observer<CharSequence> getSearchObserver() {
        return null;
    }

    public void showProgressBar() {
        ((MainActivity) getContext()).getMainFragment().showProgressBar();
    }

    public void hideProgressBar() {
        ((MainActivity) getContext()).getMainFragment().hideProgressBar();
    }

    public void setToolbarTitle(CharSequence s) {
        ((MainActivity) getContext()).getMainFragment().setToolbarTitle(s);
    }
}