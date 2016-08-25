package fr.hostux.louis.koelouis;

import android.content.Context;
import android.content.SharedPreferences;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by louis on 11/05/16.
 */
public class Config {

    private static final int PRIVATE_MODE = 0;
    private static final String SHARED_PREFERENCES_NAME = "koelouis";

    private static final String KEY_BASE_URL = "base_url";

    public static final String API_URL = "/api";


    public static final String LOGIN_KEY_EMAIL = "email";
    public static final String LOGIN_KEY_PASSWORD = "password";
    public static final String LOGIN_KEY_KOEL_URL = "koel_url";

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public Config(Context context) {
        pref = context.getSharedPreferences(SHARED_PREFERENCES_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    // full : https://host
    public String getBaseUrl() {
        return pref.getString(KEY_BASE_URL, "");
    }
    public String getApiUrl() {
        return getBaseUrl() + API_URL;
    }

    public String getHost() {
        try {
            URL url = new URL(getBaseUrl());

            return url.getHost();
        } catch (MalformedURLException e) {
            return "";
        }
    }

    public void setBaseUrl(String baseUrl) {
        editor.putString(KEY_BASE_URL, baseUrl);
        editor.commit();
    }
}
