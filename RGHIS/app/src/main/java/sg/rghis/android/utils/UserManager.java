package sg.rghis.android.utils;

import com.parse.ParseUser;

import sg.rghis.android.models.User;

public class UserManager {
    public static boolean isLoggedIn() {
        return ParseUser.getCurrentUser() != null;
    }

    public static User getCurrentUser() {
        return User.wrap(ParseUser.getCurrentUser());
    }
}
