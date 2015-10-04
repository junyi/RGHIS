package sg.rghis.android.events;

import android.os.Bundle;

import sg.rghis.android.fragments.MainFragment;

public class NavigationEvent {
    @MainFragment.FragmentState
    public final int state;
    public final Bundle bundle;
    public final boolean addToBackStack;

    public NavigationEvent(@MainFragment.FragmentState int state, Bundle bundle, boolean addToBackStack) {
        this.state = state;
        this.bundle = bundle;
        this.addToBackStack = addToBackStack;
    }
}
