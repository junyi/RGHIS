package sg.rghis.android.disqus.services;


import java.util.Map;

import retrofit.http.GET;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import rx.Observable;
import sg.rghis.android.disqus.models.Category;
import sg.rghis.android.disqus.models.PaginatedList;
import sg.rghis.android.disqus.models.Post;
import sg.rghis.android.disqus.models.ResponseItem;
import sg.rghis.android.disqus.models.Thread;


/**
 * Categories resource
 *
 * @see <a href="https://disqus.com/api/docs/categories/">Documentation</a>
 */
public interface CategoriesService {

    /**
     * Returns category details
     *
     * @param category The category id
     * @return The category details
     * @see <a href="https://disqus.com/api/docs/categories/details/">Documentation</a>
     */
    @GET("categories/details.json")
    Observable<ResponseItem<Category>> details(@Query("category") long category);

    /**
     * Returns a list of categories within a forum
     *
     * @param forum The forum short name
     * @return A list of categories
     * @see <a href="https://disqus.com/api/docs/categories/list/">Documentation</a>
     */
    @GET("categories/list.json")
    Observable<PaginatedList<Category>> list(@Query("forum") String forum);


    /**
     * Returns a list of categories within a forum
     *
     * @param forum          The forum short name
     * @param optionalParams A map of optional parameters
     * @return A list of categories
     * @see <a href="https://disqus.com/api/docs/categories/list/">Documentation</a>
     */
    @GET("categories/list.json")
    Observable<PaginatedList<Category>> list(@Query("forum") String forum,
                                             @QueryMap Map<String, String> optionalParams);


    /**
     * Returns a list of posts within a category
     *
     * @param category The category id
     * @return A list of posts
     * @see <a href="https://disqus.com/api/docs/categories/listPosts/">Documentation</a>
     */
    @GET("categories/listPosts.json")
    Observable<PaginatedList<Post>> listPosts(@Query("category") long category);

    /**
     * Returns a list of posts within a category
     *
     * @param category       The category id
     * @param optionalParams A map of optional parameters
     * @return A list of posts
     * @see <a href="https://disqus.com/api/docs/categories/listPosts/">Documentation</a>
     */
    @GET("categories/listPosts.json")
    Observable<PaginatedList<Post>> listPosts(@Query("category") long category,
                                              @QueryMap Map<String, String> optionalParams);

    /**
     * Returns a list of posts within a category
     *
     * @param category       The category id
     * @param related        Specify relations to include with the response. Allows: forum, thread
     * @param include        Filter posts by status. Allows: unapproved, approved, spam, deleted,
     *                       flagged, highlighted
     * @param optionalParams A map of optional parameters
     * @return A list of posts
     * @see <a href="https://disqus.com/api/docs/categories/listPosts/">Documentation</a>
     */
    @GET("categories/listPosts.json")
    Observable<PaginatedList<Post>> listPosts(@Query("category") long category,
                                              @Query("related") String[] related,
                                              @Query("include") String[] include,
                                              @QueryMap Map<String, String> optionalParams);


    /**
     * Returns a list of threads within a category sorted by the date created
     *
     * @param category The category id
     * @return A list of threads
     * @see <a href="https://disqus.com/api/docs/categories/listThreads/">Documentation</a>
     */
    @GET("categories/listThreads.json")
    Observable<PaginatedList<Thread>> listThreads(@Query("category") long category);

    /**
     * Returns a list of threads within a category sorted by the date created
     *
     * @param category       The category id
     * @param optionalParams A map of optional parameters
     * @return A list of threads
     * @see <a href="https://disqus.com/api/docs/categories/listThreads/">Documentation</a>
     */
    @GET("categories/listThreads.json")
    Observable<PaginatedList<Thread>> listThreads(@Query("category") long category,
                                                  @QueryMap Map<String, String> optionalParams);


    /**
     * Returns a list of threads within a category sorted by the date created
     *
     * @param category       The category id
     * @param related        Specify relations to include with the response. Allows: forum, author
     * @param optionalParams A map of optional parameters
     * @return A list of threads
     * @see <a href="https://disqus.com/api/docs/categories/listThreads/">Documentation</a>
     */
    @GET("categories/listThreads.json")
    Observable<PaginatedList<Thread>> listThreads(@Query("category") long category,
                                                  @Query("related") String[] related,
                                                  @QueryMap Map<String, String> optionalParams);

}