package sg.rghis.android.disqus;

import android.content.Context;

import dagger.ObjectGraph;

/**
 * Client facing class to the Disqus SDK including configurations, authentication and DI setup
 */
public class DisqusSdkProvider {

    public static String publicKey;
    public static String privateKey;
    public static String redirectUri;
    private static DisqusSdkProvider disqusSdkProvider;
    private ObjectGraph objectGraph;

    private DisqusSdkProvider(Builder builder) {
        this.objectGraph = ObjectGraph.create(new DisqusSdkDaggerModule(builder.appContext));
        disqusSdkProvider = this;
        publicKey = builder.publicKey;
        privateKey = builder.privateKey;
        redirectUri = builder.redirectUri;
    }

    public static DisqusSdkProvider getInstance() {
        if (disqusSdkProvider == null) {
            throw new NullPointerException("Disqus SDK Provider must be initialized in your application. " +
                    "Add a builder to the onCreate() method on a class extending application");
        }

        return disqusSdkProvider;
    }

    public ObjectGraph getObjectGraph() {
        return objectGraph;
    }

    public static class Builder {
        // TODO add api scoping
        private String publicKey;
        private String privateKey;
        private String redirectUri;
        private Context appContext;

        public Builder() {
        }

        public DisqusSdkProvider build() {
            Check.checkNotNull(publicKey, "A non null public key must be set!");
            Check.checkNotNull(privateKey, "A non null private key must be set!");
            Check.checkNotNull(appContext, "A context must be set!");
            Check.checkNotNull(redirectUri, "A redirect Uri must be set!");

            return new DisqusSdkProvider(this);
        }

        public Builder setPublicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public Builder setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
            return this;
        }

        public Builder setRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public Builder setContext(Context appContext) {
            this.appContext = appContext;
            return this;
        }
    }

}
