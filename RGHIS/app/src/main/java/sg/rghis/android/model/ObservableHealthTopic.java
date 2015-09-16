package sg.rghis.android.model;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.text.Html;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import sg.rghis.android.utils.HtmlTagHandler;
import sg.rghis.android.utils.SystemUtils;

public class ObservableHealthTopic extends BaseObservable {
    private HealthTopic healthTopic;

    public ObservableHealthTopic(HealthTopic healthTopic) {
        this.healthTopic = healthTopic;
    }

    @Bindable
    public CharSequence getTitle() {
        return Html.fromHtml(healthTopic.getTitle());
    }

    public void setTitle(String title) {
        healthTopic.setTitle(title);
    }

    public String getOverview() {
        return healthTopic.getOverview();
    }

    public void setOverview(String overview) {
        healthTopic.setOverview(overview);
    }

    public String getSymptoms() {
        return healthTopic.getSymptoms();
    }

    public void setSymptoms(String symptoms) {
        healthTopic.setSymptoms(symptoms);
    }

    public String getTreatment() {
        return healthTopic.getTreatment();
    }

    public void setTreatment(String treatment) {
        healthTopic.setTreatment(treatment);
    }

    public String getTags() {
        return healthTopic.getTags();
    }

    public void setTags(String tags) {
        healthTopic.setTags(tags);
    }

    public String getInstitution() {
        return healthTopic.getInstitution();
    }

    public void setInstitution(String institution) {
        healthTopic.setInstitution(institution);
    }

    @BindingAdapter({"app:html"})
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
