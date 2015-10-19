package sg.rghis.android.models;

import com.parse.ParseUser;

public class User extends ParseUser {
    public final static String ROLE_CONSUMER = "consumer";
    public final static String ROLE_HEALTH_PROFESSIONAL = "health_professional";

    public final static String KEY_FIRST_NAME = "firstName";
    public final static String KEY_LAST_NAME = "lastName";
    public final static String KEY_ROLE = "role";
    public final static String KEY_AVATAR_URL = "avatarUrl";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";

    private ParseUser delegatedUser;

    public User() {
        super();
    }

    public static User newConsumer() {
        User user = new User();
        user.setRole(ROLE_CONSUMER);
        return user;
    }

    public static User delegatedUser(ParseUser delegatedUser) {
        User user = new User();
        user.delegatedUser = delegatedUser;
        return user;
    }

    public void setFirstName(String firstName) {
        if (delegatedUser == null)
            put(KEY_FIRST_NAME, firstName);
        else
            delegatedUser.put(KEY_FIRST_NAME, firstName);
    }

    public void setLastName(String lastName) {
        if (delegatedUser == null)
            put(KEY_LAST_NAME, lastName);
        else
            delegatedUser.put(KEY_LAST_NAME, lastName);
    }

    public void setAvatarUrl(String avatarUrl) {
        if (delegatedUser == null)
            put(KEY_AVATAR_URL, avatarUrl);
        else
            delegatedUser.put(KEY_AVATAR_URL, avatarUrl);
    }

    private void setRole(String role) {
        if (delegatedUser == null)
            put(KEY_ROLE, role);
        else
            delegatedUser.put(KEY_ROLE, role);
    }

    public String getFirstName() {
        if (delegatedUser == null)
            return getString(KEY_FIRST_NAME);
        else
            return delegatedUser.getString(KEY_FIRST_NAME);
    }

    public String getLastName() {
        if (delegatedUser == null)
            return getString(KEY_LAST_NAME);
        else
            return delegatedUser.getString(KEY_LAST_NAME);
    }

    public String getAvatarUrl() {
        if (delegatedUser == null)
            return getString(KEY_AVATAR_URL);
        else
            return delegatedUser.getString(KEY_AVATAR_URL);
    }

    public String getRole() {
        if (delegatedUser == null)
            return getString(KEY_ROLE);
        else
            return delegatedUser.getString(KEY_ROLE);
    }

    @Override
    public String getUsername() {
        if (delegatedUser == null)
            return super.getUsername();
        else
            return delegatedUser.getUsername();
    }

    public static User wrap(ParseUser parseUser) {
        return delegatedUser(parseUser);
    }

}