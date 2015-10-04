package sg.rghis.android.events;

public class SetToolbarTitleEvent {
    public final CharSequence charSequence;

    public SetToolbarTitleEvent(CharSequence charSequence) {
        this.charSequence = charSequence;
    }
}
