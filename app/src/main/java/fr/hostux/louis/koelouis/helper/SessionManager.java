package fr.hostux.louis.koelouis.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import fr.hostux.louis.koelouis.Config;
import fr.hostux.louis.koelouis.models.User;

/**
 * Created by louis on 12/05/16.
 */
public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    Context _context;

    SessionManagerListener listener;

    public static final int PRIVATE_MODE = 0;
    public static final String SHARED_PREFERENCES_NAME = "koelouis";

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_TOKEN = "userToken";
    public static final String LOGIN_KEY_LAST_EMAIL = "last_email";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(SHARED_PREFERENCES_NAME, PRIVATE_MODE);
        editor = pref.edit();

        this.listener = null;
    }

    public void loginUser(String baseUrl, final String email, String token) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_TOKEN, token);

        Config config = new Config(_context);
        config.setBaseUrl(baseUrl);

        editor.commit();


        final SQLiteHandler db = new SQLiteHandler(_context);
        User user = db.findUserByEmail(email);

        if(user == null) {
            Log.d("sessionmanager", "first sync -> automatically sync users");
            Toast.makeText(_context, "Since it's the first time you use this server, we are syncing the data - this may take a while.", Toast.LENGTH_LONG).show();
            KoelManager koelManager = new KoelManager(_context, email, token);

            koelManager.setListener(new KoelManager.KoelManagerListener() {
                @Override
                public void onDataSync(boolean success) {
                    //
                }

                @Override
                public void onDataSyncOver(boolean success) {
                    User user = db.findUserByEmail(email);

                    if(user == null) {
                        Toast.makeText(_context, "Error on your user account.", Toast.LENGTH_SHORT).show();
                    }

                    if(listener != null) {
                        listener.onUserLoggedIn(true);
                    }
                }
            });

            koelManager.syncAll();
        } else {
            if(listener != null) {
                listener.onUserLoggedIn(true);
            }
        }

    }

    public void logoutUser() {
        String email = pref.getString(KEY_USER_EMAIL, null);
        editor.putString(LOGIN_KEY_LAST_EMAIL, email);

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

        Log.d("sessionmanager", "getting sqlitehandler");
        SQLiteHandler db = new SQLiteHandler(_context);

        User user = db.findUserByEmail(email);

        if(user == null) {
            Toast.makeText(_context, "Error on your user account.", Toast.LENGTH_SHORT).show();
            return new User(0, "Not synced", email, false);
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
