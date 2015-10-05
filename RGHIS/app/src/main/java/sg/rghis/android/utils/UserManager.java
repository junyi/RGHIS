package sg.rghis.android.utils;

import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserManager {
    public static boolean isLoggedIn() {
        return ParseUser.getCurrentUser() != null;
    }

    public static ParseUser getCurrentUser() {
        return ParseUser.getCurrentUser();
    }

    public static boolean isChannelSubscribed(String channel) {
        List<String> subscribedChannels = ParseInstallation.getCurrentInstallation().getList("channels");
        if (subscribedChannels != null) {
            Set<String> channelSet = new HashSet<>(subscribedChannels);
            return channelSet.contains(channel);
        }
        return false;
    }
}
