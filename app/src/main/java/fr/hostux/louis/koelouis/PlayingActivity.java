package fr.hostux.louis.koelouis;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
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

    private SeekBar seekBar;

    private PlayerService playerService;
    private MediaSessionCompat mediaSession;
    private Intent playerServiceIntent;

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


        seekBar = (SeekBar) findViewById(R.id.seek_bar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
                positionView.setText(String.format("%d:%02d", position/1000/60, position/1000%60));
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
        if(currentSong != null) {
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

}
