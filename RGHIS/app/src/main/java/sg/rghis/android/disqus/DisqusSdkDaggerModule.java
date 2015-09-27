package sg.rghis.android.disqus;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import sg.rghis.android.disqus.adapters.CategoriesAdapter;
import sg.rghis.android.disqus.adapters.CategoriesItem;
import sg.rghis.android.disqus.adapters.PostsAdapter;
import sg.rghis.android.disqus.adapters.PostsItem;
import sg.rghis.android.disqus.adapters.ThreadsAdapter;
import sg.rghis.android.disqus.adapters.ThreadsItem;
import sg.rghis.android.disqus.interceptors.ApiKeyRequestInterceptor;
import sg.rghis.android.disqus.interceptors.LoggingInterceptor;
import sg.rghis.android.disqus.json.GsonFactory;
import sg.rghis.android.disqus.services.CategoriesService;
import sg.rghis.android.disqus.services.PostsService;
import sg.rghis.android.disqus.services.ThreadsService;
import sg.rghis.android.fragments.CategoriesFragment;
import sg.rghis.android.fragments.PostsFragment;
import sg.rghis.android.fragments.ThreadsFragment;

@Module(
        injects = {
                CategoriesFragment.class,
                CategoriesAdapter.class,
                CategoriesItem.class,
                ThreadsFragment.class,
                ThreadsAdapter.class,
                ThreadsItem.class,
                ThreadsAdapter.class,
                PostsFragment.class,
                PostsAdapter.class,
                PostsItem.class,
        }
)
public class DisqusSdkDaggerModule {

    private Context appContext;

    public DisqusSdkDaggerModule(Context appContext) {
        this.appContext = appContext;
    }

    @Singleton
    @Provides
    Retrofit providesRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit
                .Builder()
                .client(okHttpClient)
                .baseUrl("https://disqus.com/api/3.0/")
                .addConverterFactory(GsonConverterFactory.create(GsonFactory.newGsonInstance()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient() {
        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new LoggingInterceptor());
        client.networkInterceptors().add(new ApiKeyRequestInterceptor());
        return client;
    }

//    @Provides
//    ThreadPostsService providesThreadPostService(RestAdapter restAdapter) {
//        return restAdapter.create(ThreadPostsService.class);
//    }
//
//    @Provides
//    AccessTokenService providesAccessTokenService(RestAdapter restAdapter) {
//        return restAdapter.create(AccessTokenService.class);
//    }

    @Provides
    CategoriesService providesCategoriesService(Retrofit retrofit) {
        return retrofit.create(CategoriesService.class);
    }

    @Provides
    ThreadsService providesThreadsService(Retrofit retrofit) {
        return retrofit.create(ThreadsService.class);
    }

    @Provides
    PostsService providesPostsService(Retrofit retrofit) {
        return retrofit.create(PostsService.class);
    }

//    @Provides
//    UserService providesUserService(RestAdapter restAdapter) {
//        return restAdapter.create(UserService.class);
//    }

//    @Singleton
//    @Provides
//    Picasso providesPicasso() {
//        return new Picasso.Builder(appContext)
//                .downloader(new OkHttpDownloader(appContext))
//                .memoryCache(new LruCache(appContext))
//                .build();
//    }

    @Provides
    @Singleton
    Context providesContext() {
        return appContext;
    }

//    @Provides
//    @Singleton
//    AuthManager providesAuthManager(AccessTokenService accessTokenService, com.mrebhan.disqus.url.RequestInterceptor requestInterceptor, CurrentUserManager currentUserManager) {
//        return new AuthManager(appContext, accessTokenService, requestInterceptor, currentUserManager);
//    }
//
//    @Provides
//    @Singleton
//    CurrentUserManager providesCurrentUserManager(UserService userService) {
//        return new CurrentUserManager(userService);
//    }
}
