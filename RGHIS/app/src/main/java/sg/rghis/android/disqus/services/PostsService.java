package sg.rghis.android.disqus.services;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;
import sg.rghis.android.disqus.models.Post;
import sg.rghis.android.disqus.models.ResponseItem;
import sg.rghis.android.disqus.models.VoteResponseItem;

public interface PostsService {

    @FormUrlEncoded
    @POST("posts/create.json")
    Observable<ResponseItem<Post>> create(@Field("thread") long thread,
                                          @Field("message") String message);

    @FormUrlEncoded
    @POST("posts/vote.json")
    Call<ResponseItem<VoteResponseItem>> vote(@Field("post") String post,
                                        @Field("vote") int vote);

}
