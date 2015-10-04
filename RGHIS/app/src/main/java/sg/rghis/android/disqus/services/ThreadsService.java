package sg.rghis.android.disqus.services;


import java.util.Map;

import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;
import sg.rghis.android.BuildConfig;
import sg.rghis.android.disqus.models.AuthorlessThread;
import sg.rghis.android.disqus.models.PaginatedList;
import sg.rghis.android.disqus.models.Post;
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
    @FormUrlEncoded
    @POST("threads/close.json")
    Observable<PaginatedList<Thread>> close(@Field("thread") long thread);


    /**
     * Creates a new thread
     *
     * @param forum
     * @param title
     * @return
     * @see <a href="https://disqus.com/api/docs/threads/create/">Documentation</a>
     */
    @FormUrlEncoded
    @POST("threads/create.json")
    Observable<ResponseItem<AuthorlessThread>> create(@Field("forum") String forum,
                                            @Field("title") String title);

    /**
     * Creates a new thread
     *
     * @param forum
     * @param title
     * @param optionalParams
     * @return
     * @see <a href="https://disqus.com/api/docs/threads/create/">Documentation</a>
     */
    @FormUrlEncoded
    @POST("threads/create.json")
    Observable<ResponseItem<AuthorlessThread>> create(@Field("forum") String forum,
                                            @Field("title") String title,
                                            @FieldMap Map<String, String> optionalParams);


    /**
     * Updates a thread
     *
     * @param thread
     * @param category
     * @param optionalParams
     * @return
     * @see <a href="https://disqus.com/api/docs/threads/create/">Documentation</a>
     */
    @FormUrlEncoded
    @POST("threads/update.json?access_token=" + BuildConfig.ACCESS_TOKEN)
    Observable<ResponseItem<AuthorlessThread>> update(@Field("thread") String thread,
                                            @Field("category") String category,
                                            @FieldMap Map<String, String> optionalParams);


    /**
     * Returns thread details
     *
     * @param thread
     * @return
     * @see <a href="https://disqus.com/api/docs/threads/details/">Documentation</a>
     */
    @GET("threads/details.json")
    Observable<ResponseItem<Thread>> details(@Query("thread") long thread);

    /**
     * Returns thread details
     *
     * @param thread
     * @param forum
     * @return
     * @see <a href="https://disqus.com/api/docs/threads/details/">Documentation</a>
     */
    @GET("threads/details.json")
    Observable<ResponseItem<Thread>> details(@Query("thread") long thread,
                                             @Query("forum") String forum);

    /**
     * Returns thread details
     *
     * @param thread
     * @param forum
     * @param related
     * @return
     * @see <a href="https://disqus.com/api/docs/threads/details/">Documentation</a>
     */
    @GET("threads/details.json")
    Observable<ResponseItem<Thread>> details(@Query("thread") long thread,
                                             @Query("forum") String forum,
                                             @Query("related") String[] related);

    @GET("threads/listPosts.json")
    Observable<PaginatedList<Post>> listPosts(@Query("thread") long thread);

    @GET("threads/listPosts.json")
    Observable<PaginatedList<Post>> listPosts(@Query("cursor") String cursor);

}
