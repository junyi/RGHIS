package sg.rghis.android.utils;

import com.parse.ParseUser;

public class UserManager {
    public static boolean isLoggedIn() {
        return ParseUser.getCurrentUser() != null;
    }

    public static ParseUser getCurrentUser() {
        return ParseUser.getCurrentUser();
    }
}
