package sg.rghis.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import sg.rghis.android.views.MainActivity;

public abstract class BaseFragment extends Fragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof Activity && context instanceof MainActivity)) {
            throw new ClassCastException("Activity must be MainActivity");
        }
    }

    protected void navigateToState(@MainActivity.FragmentState int state, Bundle bundle, boolean addToBackStack) {
        ((MainActivity) getActivity()).navigateToState(state, bundle, addToBackStack);
    }
}
