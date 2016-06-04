package fr.hostux.louis.koelouis;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import fr.hostux.louis.koelouis.fragments.AlbumFragment;
import fr.hostux.louis.koelouis.fragments.AlbumsFragment;
import fr.hostux.louis.koelouis.fragments.ArtistFragment;
import fr.hostux.louis.koelouis.fragments.ArtistsFragment;
import fr.hostux.louis.koelouis.fragments.HomeFragment;
import fr.hostux.louis.koelouis.fragments.PlaylistFragment;
import fr.hostux.louis.koelouis.fragments.PlaylistsFragment;
import fr.hostux.louis.koelouis.fragments.QueueFragment;
import fr.hostux.louis.koelouis.fragments.SettingsFragment;
import fr.hostux.louis.koelouis.fragments.SongsFragment;
import fr.hostux.louis.koelouis.helper.KoelManager;
import fr.hostux.louis.koelouis.helper.SessionManager;
import fr.hostux.louis.koelouis.models.Album;
import fr.hostux.louis.koelouis.models.Artist;
import fr.hostux.louis.koelouis.models.Playlist;
import fr.hostux.louis.koelouis.models.Song;
import fr.hostux.louis.koelouis.models.User;
import fr.hostux.louis.koelouis.services.PlayerService;

// TODO: refactor a lot to a MusicService

public class MainActivity extends AppCompatActivity {

    private CharSequence title;

    private static FragmentManager fragmentManager;
    private Fragment currentFragment;
    private HomeFragment homeFragment;
    private QueueFragment queueFragment;
    private ArtistsFragment artistsFragment;
    private AlbumsFragment albumsFragment;
    private SongsFragment songsFragment;
    private PlaylistsFragment playlistsFragment;
    private SettingsFragment settingsFragment;

    private String[] drawerItemsTitles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    private View progressView;

    private LinearLayout playerControls;
    private LinearLayout albumLayout;
    private ImageView albumCoverView;
    private TextView artistNameView;
    private TextView songTitleView;
    private ImageButton playerPlayButton;
    private ImageButton playerPrevButton;
    private ImageButton playerNextButton;
    private ProgressBar progressBar;


    private final Handler handler = new Handler();
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
    private final Runnable updateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };
    private final ScheduledExecutorService executorService =
            Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> scheduleFuture;
    private PlaybackStateCompat lastPlaybackState;

    private static User user;
    private static KoelManager koelManager;

    private PlayerService playerService;
    private MediaSessionCompat mediaSession;
    private Intent playerServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        if(!sessionManager.isLoggedIn()) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
        } else {

            if (savedInstanceState == null) {
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
            }

            progressView = findViewById(R.id.login_progress);
            albumCoverView = (ImageView) findViewById(R.id.album_cover);
            artistNameView = (TextView) findViewById(R.id.player_artist);
            songTitleView = (TextView) findViewById(R.id.player_song);

            playerControls = (LinearLayout) findViewById(R.id.player_controls);

            playerControls.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent playingActivityIntent = new Intent(MainActivity.this, PlayingActivity.class);
                    startActivity(playingActivityIntent);
                }
            });

            playerPlayButton = (ImageButton) findViewById(R.id.play_button);
            playerPrevButton = (ImageButton) findViewById(R.id.prev_button);
            playerNextButton = (ImageButton) findViewById(R.id.next_button);

            playerPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MediaControllerCompat.TransportControls controls = getSupportMediaController().getTransportControls();

                    if (playerService.isPlaying()) {
                        controls.pause();
                    } else {
                        controls.play();
                    }

                    // playerService.processPlayPause();
                }
            });
            playerPrevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MediaControllerCompat.TransportControls controls = getSupportMediaController().getTransportControls();
                    controls.skipToPrevious();
                    //playerService.processPrev();
                }
            });
            playerNextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MediaControllerCompat.TransportControls controls = getSupportMediaController().getTransportControls();
                    controls.skipToNext();
                    //playerService.processNext();
                }
            });

            progressBar = (ProgressBar) findViewById(R.id.progress_bar);

            makeApplicationDrawer();

            makeFragments();
        }
    }

    @Override
    protected void onDestroy() {
        stopSeekbarUpdate();
        stopService(playerServiceIntent);
        super.onDestroy();
    }

    private ServiceConnection playerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PlayerService.PlayerBinder binder = (PlayerService.PlayerBinder)iBinder;
            playerService = binder.getService();

            mediaSession = playerService.getMediaSession();
            try {
                connectToSession(mediaSession.getSessionToken());
            } catch(RemoteException e) {
                Log.e("main", "could not connect to media controller");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        if(playerServiceIntent == null) {
            playerServiceIntent = new Intent(this, PlayerService.class);
            startService(playerServiceIntent);
            bindService(playerServiceIntent, playerConnection, Context.BIND_AUTO_CREATE);
        }
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
        fragmentManager = getSupportFragmentManager();

        final HomeFragment.OnFragmentInteractionListener homeFragmentListener = new HomeFragment.OnFragmentInteractionListener() {
            @Override
            public void updateActivityTitle(String title) {
                setTitle(title);
            }
        };
        final QueueFragment.OnListFragmentInteractionListener queueFragmentListener = new QueueFragment.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(Song song, int position) {
                //playerService.processPlaySong(song);
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
            public void setQueueAndPlay(List<Song> queue) {
                playerService.processSetQueueAndPlay(queue);
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
            public void setQueueAndPlay(List<Song> queue) {
                playerService.processSetQueueAndPlay(queue);
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

        final PlaylistFragment.OnListFragmentInteractionListener playlistFragmentListener = new PlaylistFragment.OnListFragmentInteractionListener() {
            @Override
            public void setQueueAndPlay(List<Song> queue) {
                playerService.processSetQueueAndPlay(queue);
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
        final PlaylistsFragment.OnListFragmentInteractionListener playlistsFragmentListener = new PlaylistsFragment.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(Playlist playlist) {
                PlaylistFragment playlistFragment = PlaylistFragment.newInstance(1, playlist.getId(), playlist.getName());

                playlistFragment.setListener(playlistFragmentListener);

                changeFragment(playlistFragment, true);
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

            @Override
            public void onRequestLogout() {
                SessionManager sessionManager = new SessionManager(getApplicationContext());
                sessionManager.logoutUser();

                if(!sessionManager.isLoggedIn()) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }
        };

        homeFragment = HomeFragment.newInstance(user);
        homeFragment.setListener(homeFragmentListener);
        homeFragment.setRetainInstance(true);

        queueFragment = QueueFragment.newInstance(1);
        queueFragment.setListener(queueFragmentListener);
        queueFragment.setRetainInstance(true);

        artistsFragment = ArtistsFragment.newInstance(1);
        artistsFragment.setListener(artistsFragmentListener);
        artistsFragment.setRetainInstance(true);

        albumsFragment = AlbumsFragment.newInstance(1);
        albumsFragment.setListener(albumsFragmentListener);
        albumsFragment.setRetainInstance(true);

        songsFragment = SongsFragment.newInstance(1);
        songsFragment.setListener(songsFragmentListener);
        songsFragment.setRetainInstance(true);

        playlistsFragment = PlaylistsFragment.newInstance(1, user.getId());
        playlistsFragment.setListener(playlistsFragmentListener);
        playlistsFragment.setRetainInstance(true);

        settingsFragment = SettingsFragment.newInstance();
        settingsFragment.setListener(settingsFragmentListener);
        settingsFragment.setRetainInstance(true);
    }

    private void createPopupMenu(final Song song, View view) {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);

        popupMenu.getMenuInflater().inflate(R.menu.popup_song, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.addToQueueButton:
                        playerService.addToQueue(song);
                        break;

                    case R.id.playNextButton:
                        playerService.addNext(song);
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
                        playerService.removeFromQueue(position);
                        //requestUpdateQueueFragment();
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
                fragment = playlistsFragment;
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
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, fragment.getTag())
                .addToBackStack(fragment.getTag())
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

    private final MediaControllerCompat.Callback mediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            Log.d("main", "onPlaybackstate changed");
            updatePlaybackState(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata != null) {
                updateMetadata(metadata);
            }
        }
    };

    private void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        MediaControllerCompat mediaController = new MediaControllerCompat(
                MainActivity.this, token);

        setSupportMediaController(mediaController);
        mediaController.registerCallback(mediaControllerCallback);
        PlaybackStateCompat state = mediaController.getPlaybackState();
        updatePlaybackState(state);
        MediaMetadataCompat metadata = mediaController.getMetadata();
        if (metadata != null) {
            updateMetadata(metadata);
        }
        updateProgress();
        if (state != null && (state.getState() == PlaybackStateCompat.STATE_PLAYING ||
                state.getState() == PlaybackStateCompat.STATE_BUFFERING)) {
            scheduleSeekbarUpdate();
        }
    }


    private void updatePlaybackState(PlaybackStateCompat state) {
        if(state == null) {
            return;
        }

        lastPlaybackState = state;

        if(state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            showProgress(false);
            scheduleSeekbarUpdate();
            playerPlayButton.setImageResource(R.drawable.ic_bigpause);
        } else if(state.getState() == PlaybackStateCompat.STATE_BUFFERING) {
            showProgress(true);
            stopSeekbarUpdate();
        } else {
            stopSeekbarUpdate();
            showProgress(false);
            playerPlayButton.setImageResource(R.drawable.ic_bigplay);
        }
    }

    private void updateProgress() {
        if(lastPlaybackState == null) {
            return;
        }

        long currentPosition = lastPlaybackState.getPosition();

        if(lastPlaybackState.getState() != PlaybackStateCompat.STATE_PAUSED) {
            long timeDelta = SystemClock.elapsedRealtime() - lastPlaybackState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * lastPlaybackState.getPlaybackSpeed();
        }

        progressBar.setProgress((int) currentPosition);
    }


    private void scheduleSeekbarUpdate() {
        stopSeekbarUpdate();
        if (!executorService.isShutdown()) {
            scheduleFuture = executorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            handler.post(updateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    private void stopSeekbarUpdate() {
        if (scheduleFuture != null) {
            scheduleFuture.cancel(false);
        }
    }

    private void updateMetadata(MediaMetadataCompat metadata) {
        Song currentSong = playerService.getCurrent();
        if(currentSong != null) {
            artistNameView.setText(currentSong.getAlbum().getArtist().getName());
            songTitleView.setText(currentSong.getTitle());
            progressBar.setMax((int) currentSong.getLength() * 1000);
            Picasso.with(getApplicationContext()).load(currentSong.getAlbum().getCoverUri()).into(albumCoverView);

        } else {
            artistNameView.setText("-");
            songTitleView.setText("-");
            albumCoverView.setImageResource(R.drawable.ic_song);
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
}
