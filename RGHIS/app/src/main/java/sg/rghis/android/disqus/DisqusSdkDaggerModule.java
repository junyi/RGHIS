package sg.rghis.android.disqus;

import android.content.Context;

import com.mrebhan.disqus.json.GsonFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import sg.rghis.android.disqus.adapters.CategoriesAdapter;
import sg.rghis.android.disqus.adapters.CategoriesItem;
import sg.rghis.android.disqus.adapters.ThreadsAdapter;
import sg.rghis.android.disqus.adapters.ThreadsItem;
import sg.rghis.android.disqus.services.CategoriesService;
import sg.rghis.android.disqus.url.RequestInterceptor;
import sg.rghis.android.fragments.CategoriesFragment;
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
        }
)
public class DisqusSdkDaggerModule {

    private Context appContext;

    public DisqusSdkDaggerModule(Context appContext) {
        this.appContext = appContext;
    }

    @Singleton
    @Provides
    RestAdapter providesRestAdapter(RequestInterceptor requestInterceptor) {
        return new RestAdapter
                .Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("https://disqus.com/api/3.0")
                .setConverter(new GsonConverter(GsonFactory.newGsonInstance()))
                .setRequestInterceptor(requestInterceptor)
                .build();
    }

    @Provides
    @Singleton
    RequestInterceptor providesRequestInterceptor() {
        return new RequestInterceptor();
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
    CategoriesService providesCategoriesService(RestAdapter restAdapter) {
        return restAdapter.create(CategoriesService.class);
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
