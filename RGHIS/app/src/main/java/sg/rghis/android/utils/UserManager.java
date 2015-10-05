package sg.rghis.android.utils;

import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sg.rghis.android.models.User;

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

    public static boolean isUserProfessional() {
        if (getCurrentUser() != null) {
            String role = getCurrentUser().getString(User.KEY_ROLE);
            return role.equals(User.ROLE_HEALTH_PROFESSIONAL);
        }
        return false;
    }
}
