package fr.hostux.louis.koelouis;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import fr.hostux.louis.koelouis.helper.SessionManager;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    // UI references.
    private AutoCompleteTextView koelUrlView;
    private AutoCompleteTextView emailView;
    private EditText passwordView;
    private View progressView;
    private View loginFormView;

    private String email;
    private String password;
    private String koel_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        koelUrlView = (AutoCompleteTextView) findViewById(R.id.base_url);
        Config config = new Config(getApplicationContext());
        koelUrlView.setText(config.getBaseUrl());

        emailView = (AutoCompleteTextView) findViewById(R.id.email);
        String lastEmail = getApplicationContext().getSharedPreferences(SessionManager.SHARED_PREFERENCES_NAME, SessionManager.PRIVATE_MODE).getString(SessionManager.LOGIN_KEY_LAST_EMAIL, null);
        emailView.setText(lastEmail);

        passwordView = (EditText) findViewById(R.id.password);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        emailView.setError(null);
        passwordView.setError(null);
        koelUrlView.setError(null);

        // Store values at the time of the login attempt.
        email = emailView.getText().toString();
        password = passwordView.getText().toString();
        koel_url = koelUrlView.getText().toString();

        if(!koel_url.startsWith("http://") && !koel_url.startsWith("https://")) {
            koel_url = "http://" + koel_url;
        }
        if(koel_url.endsWith("/")) {
            koel_url.substring(0, -1);
        }

        String endpoint = koel_url + "/api/me";
        Log.d("login", endpoint);

        showProgress(true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, endpoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            if(jsonResponse.has("error")) {
                                emailView.setError(getString(R.string.error_authentication));
                                emailView.requestFocus();
                            } else {
                                String token = jsonResponse.getString("token");

                                SessionManager sessionManager = new SessionManager(getApplicationContext());
                                sessionManager.setListener(new SessionManager.SessionManagerListener() {
                                    @Override
                                    public void onUserLoggedIn(boolean success) {
                                        if(success) {
                                            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(mainIntent);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Une erreur interne a été détectée (n°140).", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                sessionManager.loginUser(koel_url, email, token);
                            }
                        } catch(JSONException e) {
                            Toast.makeText(getApplicationContext(), "Une erreur interne a été détectée (n°110).", Toast.LENGTH_SHORT).show();
                        }

                        showProgress(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Une erreur interne a été détectée (n°130).", Toast.LENGTH_SHORT).show();

                        Log.d("login", error.getMessage());
                        showProgress(false);
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put(Config.LOGIN_KEY_EMAIL, email);
                map.put(Config.LOGIN_KEY_PASSWORD, password);

                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}

