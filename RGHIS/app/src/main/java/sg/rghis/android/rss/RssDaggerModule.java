package sg.rghis.android.rss;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import retrofit.SimpleXmlConverterFactory;
import sg.rghis.android.disqus.interceptors.ApiKeyRequestInterceptor;
import sg.rghis.android.disqus.interceptors.LoggingInterceptor;
import sg.rghis.android.fragments.NewsListFragment;
import sg.rghis.android.rss.services.RssService;

@Module(
        injects = {
                NewsListFragment.class
        },
        library = true
)
public class RssDaggerModule {

    private Context appContext;

    public RssDaggerModule(Context appContext) {
        this.appContext = appContext;
    }

    @Singleton
    @Provides
    Retrofit providesRetrofit(OkHttpClient okHttpClient) {
        Serializer ser = new Persister(new AnnotationStrategy());
        return new Retrofit
                .Builder()
                .client(okHttpClient)
                .baseUrl("http://news.google.com.sg/")
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict(ser))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Provides
    RssService providesRssService(Retrofit retrofit) {
        return retrofit.create(RssService.class);
    }

    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient() {
        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new LoggingInterceptor());
        client.networkInterceptors().add(new ApiKeyRequestInterceptor());
        return client;
    }

    @Provides
    @Singleton
    Context providesContext() {
        return appContext;
    }

}
