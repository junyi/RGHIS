package sg.rghis.android.events;

import android.support.v4.app.Fragment;

public class SetDetailFragmentEvent {
    public final Fragment fragment;

    public SetDetailFragmentEvent(Fragment fragment) {
        this.fragment = fragment;
    }
}
