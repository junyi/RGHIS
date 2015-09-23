package sg.rghis.android.disqus.services;


import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import sg.rghis.android.disqus.models.PaginatedList;
import sg.rghis.android.disqus.models.ResponseItem;
import sg.rghis.android.disqus.models.Thread;

public interface ThreadsService {

    /**
     * Closes a thread
     *
     * @param thread
     * @return
     * @see <a href="https://disqus.com/api/docs/threads/close/">Documentation</a>
     */
    @POST("/threads/close.json")
    void close(@Query("thread") long thread,
               Callback<PaginatedList<Thread>> callback);


    /**
     * Creates a new thread
     *
     * @param forum
     * @param title
     * @return
     * @see <a href="https://disqus.com/api/docs/threads/create/">Documentation</a>
     */
    @POST("/threads/create.json")
    void create(@Query("forum") String forum,
                @Query("title") String title,
                Callback<ResponseItem<Thread>> callback);

    /**
     * Creates a new thread
     *
     * @param forum
     * @param title
     * @param optionalParams
     * @return
     * @see <a href="https://disqus.com/api/docs/threads/create/">Documentation</a>
     */
    @POST("/threads/create.json")
    void create(@Query("forum") String forum,
                @Query("title") String title,
                @QueryMap Map<String, String> optionalParams,
                Callback<ResponseItem<Thread>> callback);


    /**
     * Returns thread details
     *
     * @param thread
     * @return
     * @see <a href="https://disqus.com/api/docs/threads/details/">Documentation</a>
     */
    @GET("/threads/details.json")
    void details(@Query("thread") long thread,
                 Callback<ResponseItem<Thread>> callback);

    /**
     * Returns thread details
     *
     * @param thread
     * @param forum
     * @return
     * @see <a href="https://disqus.com/api/docs/threads/details/">Documentation</a>
     */
    @GET("/threads/details.json")
    void details(@Query("thread") long thread,
                 @Query("forum") String forum,
                 Callback<ResponseItem<Thread>> callback);

    /**
     * Returns thread details
     *
     * @param thread
     * @param forum
     * @param related
     * @return
     * @see <a href="https://disqus.com/api/docs/threads/details/">Documentation</a>
     */
    @GET("/threads/details.json")
    void details(@Query("thread") long thread,
                 @Query("forum") String forum,
                 @Query("related") String[] related,
                 Callback<ResponseItem<Thread>> callback);

}
