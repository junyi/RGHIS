package sg.rghis.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.trello.rxlifecycle.components.support.RxFragment;

import de.greenrobot.event.EventBus;
import rx.Observer;
import sg.rghis.android.events.NavigationEvent;
import sg.rghis.android.events.OnViewReadyEvent;
import sg.rghis.android.events.SetToolbarTitleEvent;
import sg.rghis.android.utils.SystemUtils;
import sg.rghis.android.views.MainActivity;

public abstract class BaseFragment extends RxFragment {
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

        EventBus.getDefault().post(new OnViewReadyEvent(this));
    }

    protected void navigateToState(@MainFragment.FragmentState int state, Bundle bundle, boolean addToBackStack) {
        EventBus.getDefault().post(new NavigationEvent(state, bundle, addToBackStack));
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
        EventBus.getDefault().post(new SetToolbarTitleEvent(s));
    }
}