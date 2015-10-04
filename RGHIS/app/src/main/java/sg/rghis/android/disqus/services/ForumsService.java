package sg.rghis.android.disqus.services;

import java.util.Map;

import retrofit.http.GET;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import rx.Observable;
import sg.rghis.android.disqus.models.PaginatedList;
import sg.rghis.android.disqus.models.Thread;

public interface ForumsService {

    /**
     * Returns a list of threads within a category sorted by the date created
     *
     * @param related        Specify relations to include with the response. Allows: forum, author
     * @param optionalParams A map of optional parameters
     * @return A list of threads
     * @see <a href="https://disqus.com/api/docs/categories/listThreads/">Documentation</a>
     */
    @GET("forums/listThreads.json")
    Observable<PaginatedList<Thread>> listThreads(@Query("forum") String forum,
                                                  @Query("related") String[] related,
                                                  @Query("include") String[] include,
                                                  @QueryMap Map<String, String> optionalParams);

}
