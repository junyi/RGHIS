package sg.rghis.android.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class HealthTopic extends RealmObject {

    public static final String TITLE = "title";
    public static final String OVERVIEW = "overview";
    public static final String SYMPTOMS = "symptoms";
    public static final String TREATMENT = "treatment";
    public static final String TAGS = "tags";
    public static final String INSTITUTION = "institution";

    @PrimaryKey
    private String title;
    private String overview;
    private String symptoms;
    private String treatment;
    private String tags;
    private String institution;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

}
