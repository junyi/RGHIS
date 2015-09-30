package sg.rghis.android.models;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.text.Html;
import android.widget.TextView;

import org.sufficientlysecure.htmltextview.HtmlTagHandler;


public class ObservableEmergencyTopic {
    public final ObservableField<EmergencyTopic> emergencyTopic =
            new ObservableField<>();

    @BindingAdapter({"app:html"})
    public static void loadHtml(TextView view, String html) {
        if (html != null) {
            view.setText(Html.fromHtml(html, null, new HtmlTagHandler()));
        }
    }
}
