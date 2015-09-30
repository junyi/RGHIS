package sg.rghis.android.rss;

import android.content.Context;

import dagger.ObjectGraph;
import sg.rghis.android.disqus.Check;

public class RssProvider {

    private static RssProvider rssProvider;
    private ObjectGraph objectGraph;

    private RssProvider(Builder builder) {
        this.objectGraph = ObjectGraph.create(new RssDaggerModule(builder.appContext));
        rssProvider = this;
    }

    public static RssProvider getInstance() {
        if (rssProvider == null) {
            throw new NullPointerException("HPB RSS Provider must be initialized in your application. " +
                    "Add a builder to the onCreate() method on a class extending application");
        }

        return rssProvider;
    }

    public ObjectGraph getObjectGraph() {
        return objectGraph;
    }

    public static class Builder {
        private Context appContext;

        public Builder() {
        }

        public RssProvider build() {
            Check.checkNotNull(appContext, "A context must be set!");
            return new RssProvider(this);
        }

        public Builder setContext(Context appContext) {
            this.appContext = appContext;
            return this;
        }
    }

}
