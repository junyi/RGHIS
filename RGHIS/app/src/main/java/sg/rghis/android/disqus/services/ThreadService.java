package sg.rghis.android.disqus.services;

import com.mrebhan.disqus.datamodel.PaginatedList;
import com.mrebhan.disqus.datamodel.Post;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface ThreadService {

    @GET("/3.0/threads/create.json")
    void getPosts(@Query("forum") String forumId, @Query("title") String title,
                  Callback<PaginatedList<Post>> posts);

    @GET("/3.0/forums/listPosts.json")
    void getNextPage(@Query("cursor") String cursorId, Callback<PaginatedList<Post>> posts);

    @GET("/3.0/forums/listThreads.json")
    void getThreads(@Query("cursor") String cursorId, Callback<PaginatedList<Post>> posts);

}
