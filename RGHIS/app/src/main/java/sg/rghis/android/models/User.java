package sg.rghis.android.models;

import com.parse.ParseUser;

public class User extends ParseUser {
    public final static String ROLE_CONSUMER = "consumer";
    public final static String ROLE_HEALTH_PROFESSIONAL = "health_professional";

    public final static String KEY_FIRST_NAME = "firstName";
    public final static String KEY_LAST_NAME = "lastName";
    public final static String KEY_ROLE = "role";

    public User() {
        super();
    }

    public static User newConsumer() {
        User user = new User();
        user.setRole(ROLE_CONSUMER);
        return user;
    }

    public void setFirstName(String firstName) {
        put(KEY_FIRST_NAME, firstName);
    }

    public void setLastName(String lastName) {
        put(KEY_LAST_NAME, lastName);
    }

    private void setRole(String role) {
        put(KEY_ROLE, role);
    }

    public String getFirstName() {
        return getString(KEY_FIRST_NAME);
    }

    public String getLastName() {
        return getString(KEY_LAST_NAME);
    }

    public String getRole() {
        return getString(KEY_ROLE);
    }

    public static User wrap(ParseUser parseUser) {
        return (User) parseUser;
    }

}