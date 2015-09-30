package sg.rghis.android.disqus.interceptors;

import com.parse.ParseUser;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import sg.rghis.android.disqus.DisqusSdkProvider;
import sg.rghis.android.utils.DisqusUtils;
import timber.log.Timber;

public class ApiKeyRequestInterceptor implements Interceptor {
    private static final String PARAM_API_KEY = "api_key";
    private static final String PARAM_REMOTE_AUTH = "remote_auth";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        String key, remoteAuthString;
        Timber.d(originalRequest.method());

        HttpUrl.Builder builder = originalRequest.httpUrl().newBuilder();

//        if (originalRequest.method().equals("GET")) {
        key = DisqusSdkProvider.publicKey;
//        } else {
//            key = DisqusSdkProvider.privateKey;
//        }

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            remoteAuthString = DisqusUtils.generateSsoString(
                    currentUser.getSessionToken(),
                    currentUser.getUsername(),
                    currentUser.getEmail());
            Timber.d("remote_auth %s", remoteAuthString);
            builder.addQueryParameter(PARAM_REMOTE_AUTH, remoteAuthString);
        }

        HttpUrl httpUrl = builder
                .addQueryParameter(PARAM_API_KEY, key)
                .build();

        Timber.d("URL: %s", httpUrl.toString());

        Request signedRequest = originalRequest.newBuilder()
                .url(httpUrl)
                .build();

        return chain.proceed(signedRequest);
    }


}
