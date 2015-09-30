package sg.rghis.android.rss.services;

import retrofit.http.GET;
import rx.Observable;
import sg.rghis.android.rss.models.Rss;

public interface RssService {
    @GET("news?cf=all&hl=en&pz=1&ned=en_sg&topic=m&output=rss")
    Observable<Rss> getNews();
}
