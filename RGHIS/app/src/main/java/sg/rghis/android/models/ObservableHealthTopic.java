package sg.rghis.android.models;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.support.design.widget.CollapsingToolbarLayout;
import android.text.Html;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import sg.rghis.android.utils.HtmlTagHandler;
import sg.rghis.android.utils.SystemUtils;

public class ObservableHealthTopic {
    public final ObservableField<HealthTopic> healthTopic =
            new ObservableField<>();

    @BindingAdapter({"app:html"})
    public static void html(CollapsingToolbarLayout view, String html) {
        if (html != null) {
            view.setTitle(Html.fromHtml(html, null, new HtmlTagHandler()));
        }
    }

    @BindingAdapter({"app:loadHtml"})
    public static void loadHtml(TextView view, String url) {
        if (url != null) {
            Context context = view.getContext();
            String html = SystemUtils.loadAssetTextAsString(context, "content/" + url);
            if (html != null) {
                html = Jsoup.clean(html, Whitelist.basic());
                view.setText(Html.fromHtml(html, null, new HtmlTagHandler()));
            }
        }
    }

}
