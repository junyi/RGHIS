package sg.rghis.android.events;

import sg.rghis.android.fragments.BaseFragment;

public class OnViewReadyEvent {
    public final BaseFragment fragment;

    public OnViewReadyEvent(BaseFragment fragment) {
        this.fragment = fragment;
    }
}
