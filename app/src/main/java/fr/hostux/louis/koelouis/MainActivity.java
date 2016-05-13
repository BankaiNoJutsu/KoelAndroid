package fr.hostux.louis.koelouis;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import fr.hostux.louis.koelouis.helper.KoelManager;
import fr.hostux.louis.koelouis.helper.SessionManager;
import fr.hostux.louis.koelouis.models.User;

public class MainActivity extends AppCompatActivity {

    private CharSequence title;

    private String[] drawerItemsTitles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    private View progressView;

    private User user;
    private KoelManager koelManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        if(!sessionManager.isLoggedIn()) {
            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginIntent);
        }

        user = sessionManager.getUser();
        koelManager = new KoelManager(getApplicationContext());

        koelManager.setListener(new KoelManager.KoelManagerListener() {
            @Override
            public void onDataSync(boolean success) {
                showProgress(true);
            }
            @Override
            public void onDataSyncOver(boolean success) {
                Toast.makeText(getApplicationContext(), "Data has just been synced with server! Enjoy!", Toast.LENGTH_SHORT).show();
                showProgress(false);
            }
        });

        progressView = findViewById(R.id.login_progress);

        makeApplicationDrawer();
    }

    private void makeApplicationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.drawer_list);

        drawerItemsTitles = getResources().getStringArray(R.array.drawer_items);

        ArrayList<DrawerItem> items = new ArrayList<DrawerItem>();
        items.add(new DrawerItem(drawerItemsTitles[0], R.drawable.ic_home));
        items.add(new DrawerItem(drawerItemsTitles[1], R.drawable.ic_albums));
        items.add(new DrawerItem(drawerItemsTitles[2], R.drawable.ic_artist));
        items.add(new DrawerItem(drawerItemsTitles[3], R.drawable.ic_album));
        items.add(new DrawerItem(drawerItemsTitles[4], R.drawable.ic_playlists));
        items.add(new DrawerItem(drawerItemsTitles[5], R.drawable.ic_plus));

        drawerList.setAdapter(new DrawerListAdapter(this, items));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        Fragment fragment = null;
        switch(position) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                fragment = HomeFragment.newInstance(user);

                break;
            case 5:

                SettingsFragment settingsFragment = SettingsFragment.newInstance();
                settingsFragment.setListener(new SettingsFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onRequestDataSync() {
                        koelManager.syncAll();
                    }
                });

                fragment = settingsFragment;
                break;
            default:
                // should not be reached
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        drawerList.setItemChecked(position, true);
        setTitle(drawerItemsTitles[position]);
        drawerLayout.closeDrawer(drawerList);
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
        }
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    @Override
    public void setTitle(CharSequence newTitle) {
        title = newTitle;
        getSupportActionBar().setTitle(title);
    }
}
