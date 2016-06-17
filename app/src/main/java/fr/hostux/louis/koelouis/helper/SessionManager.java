package fr.hostux.louis.koelouis.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import fr.hostux.louis.koelouis.models.User;

/**
 * Created by louis on 12/05/16.
 */
public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    Context _context;

    SessionManagerListener listener;

    private static final int PRIVATE_MODE = 0;
    private static final String SHARED_PREFERENCES_NAME = "koelouis";

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_TOKEN = "userToken";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(SHARED_PREFERENCES_NAME, PRIVATE_MODE);
        editor = pref.edit();

        this.listener = null;
    }

    public void loginUser(String email, String token) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_TOKEN, token);

        editor.commit();

        if(listener != null) {
            listener.onUserLoggedIn(true);
        }
    }

    public void logoutUser() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.remove(KEY_USER_TOKEN);
        editor.remove(KEY_USER_EMAIL);

        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getToken() {
        return pref.getString(KEY_USER_TOKEN, null);
    }

    public User getUser() {
        if(!this.isLoggedIn()) {
            return null;
        }

        String email = pref.getString(KEY_USER_EMAIL, null);
        String token = this.getToken();

        SQLiteHandler db = new SQLiteHandler(_context);

        User user = db.findUserByEmail(email);

        if(user == null) {
            KoelManager koelManager = new KoelManager(_context);
            koelManager.syncUsers();

            user = db.findUserByEmail(email);

            if(user == null) {
                Toast.makeText(_context, "Error on your user account.", Toast.LENGTH_SHORT).show();
                return new User(0, "Not synced", email, false);
            }
        }

        user.setToken(token);

        return user;
    }

    public interface SessionManagerListener {
        public void onUserLoggedIn(boolean success);
    }

    public void setListener(SessionManagerListener listener) {
        this.listener = listener;
    }
}
