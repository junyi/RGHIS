package sg.rghis.android.utils;

import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;
import com.parse.ParseUser;

import org.apache.commons.codec.binary.Base64;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Formatter;
import java.util.HashMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import sg.rghis.android.BuildConfig;
import sg.rghis.android.models.User;
import timber.log.Timber;

public class DisqusUtils {
    /**
     * This script will calculate the Disqus SSO payload package
     * Please see the Integrating SSO guide to find out how to configure your account first:
     * http://help.disqus.com/customer/portal/articles/236206
     */
    public static String generateSsoString(String id, String username, String email) {
        // User data, replace values with authenticated user data
        HashMap<String, String> message = new HashMap<>();
        message.put("id", id);
        message.put("username", username);
        message.put("email", email);
        //message.put("avatar","http://example.com/path-to-avatar.jpg"); // User's avatar URL (optional)
        //message.put("url","http://example.com/"); // User's website or profile URL (optional)

        // Encode user data
        Gson gson = new Gson();
        String jsonMessage = gson.toJson(message);

        String base64EncodedStr = new String(Base64.encodeBase64(jsonMessage.getBytes()));

        // Get the timestamp
        long timestamp = System.currentTimeMillis() / 1000;

        // Assemble the HMAC-SHA1 signature
        String signature = null;
        try {
            signature = calculateRFC2104HMAC(base64EncodedStr + " " + timestamp, BuildConfig.PRIVATE_KEY);
            return base64EncodedStr + " " + signature + " " + timestamp;
        } catch (SignatureException e) {
            Timber.e(Log.getStackTraceString(e));
        } catch (NoSuchAlgorithmException e) {
            Timber.e(Log.getStackTraceString(e));
        } catch (InvalidKeyException e) {
            Timber.e(Log.getStackTraceString(e));
        }

        return null;
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    public static String calculateRFC2104HMAC(String data, String key)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return toHexString(mac.doFinal(data.getBytes()));
    }

    public static String getChannelName(long threadId) {
        return "thread_" + String.valueOf(threadId);
    }

    public static Pair<String, String> disqusNameToParse(String name) {
        String[] splitted = name.split("@@");
        if (splitted.length == 2)
            return Pair.create(splitted[0], splitted[1]);
        else
            return Pair.create(name, User.ROLE_CONSUMER);
    }

    public static String parseUsernameToDisqus(ParseUser user) {
        return user.getUsername() + "@@" + user.getString(User.KEY_ROLE);
    }
}
