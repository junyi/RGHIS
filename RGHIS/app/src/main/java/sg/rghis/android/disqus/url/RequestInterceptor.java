package sg.rghis.android.disqus.url;


import com.parse.ParseUser;

import sg.rghis.android.disqus.DisqusSdkProvider;
import sg.rghis.android.models.User;
import sg.rghis.android.utils.DisqusUtils;

public class RequestInterceptor implements retrofit.RequestInterceptor {
    private static final String PARAM_API_KEY = "api_key";
    private static final String PARAM_ACCESS_TOKEN = "access_token";
    private static final String PARAM_REMOTE_AUTH = "remote_auth";

    @Override
    public void intercept(RequestFacade request) {
        request.addQueryParam(PARAM_API_KEY, DisqusSdkProvider.publicKey);

        // add the authenticated user to the request if available
        User currentUser = User.wrap(ParseUser.getCurrentUser());
        if (currentUser != null) {
            String remoteAuthString = DisqusUtils.generateSsoString(
                    currentUser.getSessionToken(),
                    currentUser.getUsername(),
                    currentUser.getEmail());

            request.addQueryParam(PARAM_REMOTE_AUTH, remoteAuthString);
        }
    }
}
