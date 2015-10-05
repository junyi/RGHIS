package sg.rghis.android.disqus.interceptors;

import com.parse.ParseUser;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import sg.rghis.android.disqus.DisqusSdkProvider;
import sg.rghis.android.models.User;
import sg.rghis.android.utils.DisqusUtils;
import timber.log.Timber;

public class ApiKeyRequestInterceptor implements Interceptor {
    private static final String PARAM_API_KEY = "api_key";
    private static final String PARAM_REMOTE_AUTH = "remote_auth";
    private static final String PARAM_ACCESS_TOKEN = "access_token";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        String key, remoteAuthString, accessToken = null;
        Timber.d(originalRequest.method());

        HttpUrl.Builder builder = originalRequest.httpUrl().newBuilder();
        key = DisqusSdkProvider.publicKey;

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            remoteAuthString = DisqusUtils.generateSsoString(
                    currentUser.getSessionToken(),
                    DisqusUtils.parseUsernameToDisqus(currentUser),
                    currentUser.getEmail());
            Timber.d("remote_auth %s", remoteAuthString);
            builder.addQueryParameter(PARAM_REMOTE_AUTH, remoteAuthString);
        }

        if (accessToken != null)
            builder.addQueryParameter(PARAM_ACCESS_TOKEN, accessToken);

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
