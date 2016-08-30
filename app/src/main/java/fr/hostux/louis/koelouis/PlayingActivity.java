package fr.hostux.louis.koelouis;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import fr.hostux.louis.koelouis.models.Song;
import fr.hostux.louis.koelouis.services.PlayerService;


public class PlayingActivity extends AppCompatActivity {
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

    private View progressView;
    private ImageView albumCoverView;
    private TextView songTitleView;
    private TextView albumNameView;
    private TextView artistNameView;
    private TextView positionView;
    private TextView lengthView;

    private ImageButton playerPlayButton;
    private ImageButton playerPrevButton;
    private ImageButton playerNextButton;
    private ImageButton playerShuffleButton;
    private ImageButton playerPlayModeButton;

    private SeekBar seekBar;

    private PlayerService playerService;
    private MediaSessionCompat mediaSession;
    private Intent playerServiceIntent;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        progressView = findViewById(R.id.login_progress);

        albumCoverView = (ImageView) findViewById(R.id.album_cover);
        songTitleView = (TextView) findViewById(R.id.song_title);
        albumNameView = (TextView) findViewById(R.id.album_name);
        artistNameView = (TextView) findViewById(R.id.artist_name);
        positionView = (TextView) findViewById(R.id.current_position);
        lengthView = (TextView) findViewById(R.id.length);

        playerPrevButton = (ImageButton) findViewById(R.id.prev_button);
        playerPlayButton = (ImageButton) findViewById(R.id.play_button);
        playerNextButton = (ImageButton) findViewById(R.id.next_button);

        playerShuffleButton = (ImageButton) findViewById(R.id.shuffle_button);
        playerPlayModeButton = (ImageButton) findViewById(R.id.playmode_button);


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

        playerPlayModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerService.getPlayMode() == PlayerService.PlayMode.Normal) {
                    playerService.setPlayMode(PlayerService.PlayMode.RepeatOne);
                } else if (playerService.getPlayMode() == PlayerService.PlayMode.RepeatOne) {
                    playerService.setPlayMode(PlayerService.PlayMode.RepeatAll);
                } else if (playerService.getPlayMode() == PlayerService.PlayMode.RepeatAll) {
                    playerService.setPlayMode(PlayerService.PlayMode.Normal);
                }

                updatePlayMode();
            }
        });


        seekBar = (SeekBar) findViewById(R.id.seek_bar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
                positionView.setText(String.format("%d:%02d", position / 1000 / 60, position / 1000 % 60));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopSeekbarUpdate();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getSupportMediaController().getTransportControls().seekTo(seekBar.getProgress());
                scheduleSeekbarUpdate();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private ServiceConnection playerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PlayerService.PlayerBinder binder = (PlayerService.PlayerBinder) iBinder;
            playerService = binder.getService();

            mediaSession = playerService.getMediaSession();
            try {
                connectToSession(mediaSession.getSessionToken());
            } catch (RemoteException e) {
                Log.e("main", "could not connect to media controller");
            }

            updatePlayMode();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();

        if (playerServiceIntent == null) {
            playerServiceIntent = new Intent(this, PlayerService.class);
            startService(playerServiceIntent);
            bindService(playerServiceIntent, playerConnection, Context.BIND_AUTO_CREATE);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Playing Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://fr.hostux.louis.koelouis/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    protected void onDestroy() {
        stopSeekbarUpdate();
        super.onDestroy();
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
                PlayingActivity.this, token);

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
        if (state == null) {
            return;
        }

        lastPlaybackState = state;

        if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            showProgress(false);
            scheduleSeekbarUpdate();
            playerPlayButton.setImageResource(R.drawable.ic_bigpause);
        } else if (state.getState() == PlaybackStateCompat.STATE_BUFFERING) {
            showProgress(true);
            stopSeekbarUpdate();
        } else {
            stopSeekbarUpdate();
            showProgress(false);
            playerPlayButton.setImageResource(R.drawable.ic_bigplay);
        }
    }

    private void updateProgress() {
        if (lastPlaybackState == null) {
            return;
        }

        long currentPosition = lastPlaybackState.getPosition();

        if (lastPlaybackState.getState() != PlaybackStateCompat.STATE_PAUSED) {
            long timeDelta = SystemClock.elapsedRealtime() - lastPlaybackState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * lastPlaybackState.getPlaybackSpeed();
        }

        seekBar.setProgress((int) currentPosition);
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
        if (currentSong != null) {
            artistNameView.setText(currentSong.getAlbum().getArtist().getName());
            songTitleView.setText(currentSong.getTitle());
            albumNameView.setText(currentSong.getAlbum().getName());
            lengthView.setText(currentSong.getReadableLength());
            seekBar.setProgress(0);
            seekBar.setMax((int) currentSong.getLength() * 1000);
            Picasso.with(getApplicationContext()).load(currentSong.getAlbum().getCoverUri()).into(albumCoverView);
        } else {
            artistNameView.setText("-");
            songTitleView.setText("-");
            albumCoverView.setImageResource(R.drawable.ic_song);
        }
    }

    private void updatePlayMode() {
        if (playerService.getPlayMode() == PlayerService.PlayMode.Normal) {
            playerPlayModeButton.setImageResource(R.drawable.ic_playnormal);
        } else if (playerService.getPlayMode() == PlayerService.PlayMode.RepeatOne) {
            playerPlayModeButton.setImageResource(R.drawable.ic_repeat_1);
        } else if (playerService.getPlayMode() == PlayerService.PlayMode.RepeatAll) {
            playerPlayModeButton.setImageResource(R.drawable.ic_repeat);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Playing Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://fr.hostux.louis.koelouis/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
