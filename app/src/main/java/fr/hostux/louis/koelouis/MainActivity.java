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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import fr.hostux.louis.koelouis.helper.KoelManager;
import fr.hostux.louis.koelouis.helper.MediaStore;
import fr.hostux.louis.koelouis.helper.QueueHelper;
import fr.hostux.louis.koelouis.helper.SessionManager;
import fr.hostux.louis.koelouis.models.Album;
import fr.hostux.louis.koelouis.models.Artist;
import fr.hostux.louis.koelouis.models.Song;
import fr.hostux.louis.koelouis.models.User;

public class MainActivity extends AppCompatActivity {

    private CharSequence title;

    private Fragment currentFragment;
    private HomeFragment homeFragment;
    private QueueFragment queueFragment;
    private ArtistsFragment artistsFragment;
    private AlbumsFragment albumsFragment;
    private SongsFragment songsFragment;
    private SettingsFragment settingsFragment;

    private String[] drawerItemsTitles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    private View progressView;

    private TextView artistNameView;
    private TextView songTitleView;
    private ImageButton playerPlayButton;
    private ImageButton playerPrevButton;
    private ImageButton playerNextButton;

    private User user;
    private KoelManager koelManager;

    private QueueHelper queueHelper;
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

        playerPlayButton = (ImageButton) findViewById(R.id.play_button);
        playerPrevButton = (ImageButton) findViewById(R.id.prev_button);
        playerNextButton = (ImageButton) findViewById(R.id.next_button);

        playerPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePlayPause();
            }
        });
        playerPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevSong();
            }
        });
        playerNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextSong();
            }
        });

        initializePlayer();

        makeApplicationDrawer();

        makeFragments();
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
        items.add(new DrawerItem(drawerItemsTitles[4], R.drawable.ic_song));
        items.add(new DrawerItem(drawerItemsTitles[5], R.drawable.ic_playlists));
        items.add(new DrawerItem(drawerItemsTitles[6], R.drawable.ic_settings));

        drawerList.setAdapter(new DrawerListAdapter(this, items));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private void makeFragments() {

        final HomeFragment.OnFragmentInteractionListener homeFragmentListener = new HomeFragment.OnFragmentInteractionListener() {
            @Override
            public void updateActivityTitle(String title) {
                setTitle(title);
            }
        };
        final QueueFragment.OnListFragmentInteractionListener queueFragmentListener = new QueueFragment.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(Song song, int position) {
                //playSong(song);
            }

            @Override
            public void onPopupButtonClick(Song song, View view, int position) {
                createQueuePopupMenu(song, view, position);
            }

            @Override
            public void updateActivityTitle(String title) {
                setTitle(title);
            }
        };
        final AlbumFragment.OnListFragmentInteractionListener albumFragmentListener = new AlbumFragment.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(Song song) {
                playSong(song);
                queueHelper.clearQueue();
            }

            @Override
            public void onPopupButtonClick(Song song, View view) {
                createPopupMenu(song, view);
            }

            @Override
            public void updateActivityTitle(String title) {
                setTitle(title);
            }
        };
        final ArtistsFragment.OnListFragmentInteractionListener artistsFragmentListener = new ArtistsFragment.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(Artist artist) {
                ArtistFragment artistFragment = ArtistFragment.newInstance(1, artist.getId(), artist.getName());

                artistFragment.setListener(new ArtistFragment.OnListFragmentInteractionListener() {
                    @Override
                    public void onListFragmentInteraction(Album album) {
                        AlbumFragment albumFragment = AlbumFragment.newInstance(1, album.getId(), album.getName());

                        albumFragment.setListener(albumFragmentListener);

                        changeFragment(albumFragment, true);
                    }

                    @Override
                    public void updateActivityTitle(String title) {
                        setTitle(title);
                    }
                });

                changeFragment(artistFragment, true);
            }

            @Override
            public void updateActivityTitle(String title) {
                setTitle(title);
            }
        };
        final AlbumsFragment.OnListFragmentInteractionListener albumsFragmentListener = new AlbumsFragment.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(Album album) {
                AlbumFragment albumFragment = AlbumFragment.newInstance(1, album.getId(), album.getName());

                albumFragment.setListener(albumFragmentListener);

                changeFragment(albumFragment, true);
            }

            @Override
            public void updateActivityTitle(String title) {
                setTitle(title);
            }
        };
        final SongsFragment.OnListFragmentInteractionListener songsFragmentListener = new SongsFragment.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(Song song) {
                playSong(song);
                queueHelper.clearQueue();
            }

            @Override
            public void onPopupButtonClick(Song song, View view) {
                createPopupMenu(song, view);
            }

            @Override
            public void updateActivityTitle(String title) {
                setTitle(title);
            }
        };
        final SettingsFragment.OnFragmentInteractionListener settingsFragmentListener = new SettingsFragment.OnFragmentInteractionListener() {
            @Override
            public void onRequestDataSync() {
                koelManager.syncAll();
            }

            @Override
            public void updateActivityTitle(String title) {
                setTitle(title);
            }
        };

        homeFragment = HomeFragment.newInstance(user);
        homeFragment.setListener(homeFragmentListener);

        queueFragment = QueueFragment.newInstance(1, queueHelper);
        queueFragment.setListener(queueFragmentListener);

        artistsFragment = ArtistsFragment.newInstance(1);
        artistsFragment.setListener(artistsFragmentListener);

        albumsFragment = AlbumsFragment.newInstance(1);
        albumsFragment.setListener(albumsFragmentListener);

        songsFragment = SongsFragment.newInstance(1);
        songsFragment.setListener(songsFragmentListener);

        settingsFragment = SettingsFragment.newInstance();
        settingsFragment.setListener(settingsFragmentListener);
    }

    private void createPopupMenu(final Song song, View view) {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);

        popupMenu.getMenuInflater().inflate(R.menu.popup_song, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.addToQueueButton:
                        queueHelper.add(song);
                        break;

                    case R.id.playNextButton:
                        queueHelper.addNext(song);
                        break;

                    case R.id.addToPlaylistButton:
                        Toast.makeText(getApplicationContext(), "Soon...", Toast.LENGTH_SHORT).show();
                        break;
                }

                return true;
            }
        });
        popupMenu.show();
    }
    private void createQueuePopupMenu(final Song song, View view, final int position) {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);

        popupMenu.getMenuInflater().inflate(R.menu.popup_queue, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.removeFromQueueButton:
                        queueHelper.removeFromQueue(position);
                        updateQueueFragment();
                        break;
                }

                return true;
            }
        });
        popupMenu.show();
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        Fragment fragment = null;
        switch(position) {
            // HOME
            case 0:
                fragment = homeFragment;
                break;

            // QUEUE
            case 1:
                fragment = queueFragment;
                break;

            // ARTISTS LIST
            case 2:
                fragment = artistsFragment;
                break;

            // ALBUMS LIST
            case 3:
                fragment = albumsFragment;
                break;

            // SONGS LIST
            case 4:
                fragment = songsFragment;
                break;

            // PLAYLISTS
            case 5:
                fragment = homeFragment;
                break;

            // SETTINGS
            case 6:
                fragment = settingsFragment;
                break;
            default:
                // should not be reached
        }

        changeFragment(fragment, false);
        drawerList.setItemChecked(position, true);
        drawerLayout.closeDrawer(drawerList);
    }

    private void changeFragment(Fragment fragment, boolean addToStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack("my fragment")
                .commit();

        currentFragment = fragment;
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

    private void updateQueueFragment() {
        if(currentFragment != null && currentFragment == queueFragment) {
            queueFragment.getAdapter().notifyDataSetChanged();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void initializePlayer() {
        queueHelper = new QueueHelper();
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Song next = queueHelper.next();
                if(next != null) {
                    playSong(next);
                } else {
                    playerPlayButton.setImageResource(R.drawable.ic_bigplay);
                }
            }
        });
    }

    private void playSong(final Song song) {
        mediaPlayer.reset();
        queueHelper.setCurrent(song);

        artistNameView.setText(song.getAlbum().getArtist().getName());
        songTitleView.setText(song.getTitle());

        String endpoint = Config.API_URL + "/" + song.getId() + "/play?jwt-token=" + user.getToken();
        Log.d("main", endpoint);

        Uri uri = Uri.parse(endpoint);

        try {
            showProgress(true);
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    showProgress(false);
                    togglePlayPause();
                }
            });
        } catch(IOException e) {
            Toast.makeText(getApplicationContext(), "Error (650)", Toast.LENGTH_SHORT).show();
            Log.e("main", e.getMessage());
        }
    }

    private void prevSong() {
        queueHelper.addNext(queueHelper.getCurrent());
        updateQueueFragment();
        Song prev = queueHelper.prev();
        if(prev != null) {
            playSong(prev);
        }
    }
    private void nextSong() {
        queueHelper.addToHistory(queueHelper.getCurrent());
        Song next = queueHelper.next();
        updateQueueFragment();
        if(next != null) {
            playSong(next);
        }
    }

    private void togglePlayPause() {
        if(queueHelper.getCurrent() == null) {
            MediaStore mediaStore = new MediaStore(getApplicationContext());
            if(mediaStore.getSongs().size() > 0) {
                playSong(mediaStore.getSongs().get(0));
            }
        }
        else if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playerPlayButton.setImageResource(R.drawable.ic_bigplay);

        } else {
            mediaPlayer.start();
            playerPlayButton.setImageResource(R.drawable.ic_bigpause);
        }
    }
}
