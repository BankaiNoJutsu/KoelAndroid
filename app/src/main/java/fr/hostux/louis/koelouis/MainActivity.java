package fr.hostux.louis.koelouis;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import fr.hostux.louis.koelouis.helper.KoelManager;
import fr.hostux.louis.koelouis.helper.SessionManager;
import fr.hostux.louis.koelouis.models.Album;
import fr.hostux.louis.koelouis.models.Artist;
import fr.hostux.louis.koelouis.models.Song;
import fr.hostux.louis.koelouis.models.User;

public class MainActivity extends AppCompatActivity {

    private CharSequence title;

    private String[] drawerItemsTitles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    private View progressView;

    private TextView artistNameView;
    private TextView songTitleView;
    private ImageButton playerPlay;
    private ImageButton playerPrev;
    private ImageButton playerNext;

    private User user;
    private KoelManager koelManager;

    private MediaPlayer mediaPlayer;

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
            // TODO: remettre ça
            /*
            @Override
            public void onDataSyncError(int errorNumber) {
                Toast.makeText(getApplicationContext(), "Une erreur interne a été détectée (n° " + Integer.toString(errorNumber) + ").", Toast.LENGTH_SHORT).show();
            }*/
        });

        progressView = findViewById(R.id.login_progress);
        artistNameView = (TextView) findViewById(R.id.player_artist);
        songTitleView = (TextView) findViewById(R.id.player_song);

        makeApplicationDrawer();

        mediaPlayer = new MediaPlayer();
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
            // HOME
            case 0:
            // QUEUE
            case 1:
                fragment = HomeFragment.newInstance(user);
                break;

            // ARTISTS LIST
            case 2:
                ArtistsFragment artistsFragment = ArtistsFragment.newInstance(1);
                artistsFragment.setListener(new ArtistsFragment.OnListFragmentInteractionListener() {
                    @Override
                    public void onListFragmentInteraction(Artist artist) {
                        ArtistFragment artistFragment = ArtistFragment.newInstance(1, artist.getId());

                        artistFragment.setListener(new ArtistFragment.OnListFragmentInteractionListener() {
                            @Override
                            public void onListFragmentInteraction(Album album) {
                                AlbumFragment albumFragment = AlbumFragment.newInstance(1, album.getId());

                                albumFragment.setListener(new AlbumFragment.OnListFragmentInteractionListener() {
                                    @Override
                                    public void onListFragmentInteraction(Song song) {
                                        playSong(song);
                                    }
                                });

                                changeFragment(albumFragment, album.getName(), true);
                            }
                        });

                        changeFragment(artistFragment, artist.getName(), true);
                    }
                });

                fragment = artistsFragment;

                break;

            // ALBUMS LIST
            case 3:
                AlbumsFragment albumsFragment = AlbumsFragment.newInstance(1);
                albumsFragment.setListener(new AlbumsFragment.OnListFragmentInteractionListener() {
                    @Override
                    public void onListFragmentInteraction(Album album) {
                        AlbumFragment albumFragment = AlbumFragment.newInstance(1, album.getId());

                        albumFragment.setListener(new AlbumFragment.OnListFragmentInteractionListener() {
                            @Override
                            public void onListFragmentInteraction(Song song) {
                                playSong(song);
                            }
                        });

                        changeFragment(albumFragment, album.getName(), true);
                    }
                });

                fragment = albumsFragment;

                break;

            // PLAYLISTS
            case 4:
                fragment = HomeFragment.newInstance(user);

                break;

            // SETTINGS
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

        changeFragment(fragment, drawerItemsTitles[position], false);
        drawerList.setItemChecked(position, true);
        drawerLayout.closeDrawer(drawerList);
    }

    private void changeFragment(Fragment fragment, String title, boolean addToStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(title)
                .commit();

        setTitle(title);
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



    private void playSong(Song song) {
        artistNameView.setText(song.getAlbum().getArtist().getName());
        songTitleView.setText(song.getTitle());

        String endpoint = Config.API_URL + "/" + song.getId() + "/play?jwt-token=" + user.getToken();
        Log.d("main", endpoint);

        Uri uri = Uri.parse(endpoint);

        try {
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        } catch(IOException e) {
            Toast.makeText(getApplicationContext(), "Error (650)", Toast.LENGTH_SHORT).show();
            Log.e("main", e.getMessage());
        }
    }
}
