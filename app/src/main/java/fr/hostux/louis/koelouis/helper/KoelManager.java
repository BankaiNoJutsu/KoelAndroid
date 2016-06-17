package fr.hostux.louis.koelouis.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import fr.hostux.louis.koelouis.Config;
import fr.hostux.louis.koelouis.models.Album;
import fr.hostux.louis.koelouis.models.Artist;
import fr.hostux.louis.koelouis.models.Playlist;
import fr.hostux.louis.koelouis.models.Song;
import fr.hostux.louis.koelouis.models.User;

/**
 * Created by louis on 13/05/16.
 */
public class KoelManager {
    private Artist[] artists;
    private Album[] albums;
    private Song[] songs;
    private Playlist[] playlists;
    private User[] users;

    private Context context;
    private String token;
    int userId;

    private KoelManagerListener listener;

    public KoelManager(Context context) {
        this.context = context;

        SessionManager sessionManager = new SessionManager(context);
        this.token = sessionManager.getToken();
        this.userId = sessionManager.getUser().getId();

        this.listener = null;
    }

    private class AsyncSyncDatabaseTask extends AsyncTask {
        private JSONObject jsonResponse;
        private boolean syncUsers;
        private boolean syncArtists;
        private boolean syncAlbums;
        private boolean syncSongs;
        private boolean syncPlaylists;


        public AsyncSyncDatabaseTask(final JSONObject jsonResponse, final boolean syncUsers, final boolean syncArtists, final boolean syncAlbums, final boolean syncSongs, final boolean syncPlaylists) {
            this.jsonResponse = jsonResponse;
            this.syncUsers = syncUsers;
            this.syncArtists = syncArtists;
            this.syncAlbums = syncAlbums;
            this.syncSongs = syncSongs;
            this.syncPlaylists = syncPlaylists;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if(listener != null) {
                listener.onDataSyncOver(true);
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            SQLiteHandler db = new SQLiteHandler(context);

            try {
                // ON CLEAN D'ABORD LES DBS
                if(syncUsers) {
                    db.deleteFromUserTable();
                }
                if(syncArtists) {
                    db.deleteFromArtistTable();
                }
                if(syncAlbums) {
                    db.deleteFromAlbumTable();
                }
                if(syncSongs) {
                    db.deleteFromSongTable();
                }
                if(syncPlaylists) {
                    db.deleteFromPlaylistTable();
                }


                if (syncUsers) {
                    JSONArray users = jsonResponse.getJSONArray("users");

                    for (int u = 0; u < users.length(); u++) {
                        JSONObject user = users.getJSONObject(u);

                        db.addUser(user.getInt("id"), user.getString("name"), user.getString("email"), user.getBoolean("is_admin"));
                    }
                }
                if (syncArtists) {
                    JSONArray artists = jsonResponse.getJSONArray("artists");

                    for (int a = 0; a < artists.length(); a++) {
                        JSONObject artist = artists.getJSONObject(a);

                        String image = null;
                        if(!artist.isNull("image")) {
                            image = artist.getString("image");
                        }

                        db.addArtist(artist.getInt("id"), artist.getString("name"), image);

                        if (syncAlbums) {
                            JSONArray albums = artist.getJSONArray("albums");

                            for (int al = 0; al < albums.length(); al++) {
                                JSONObject album = albums.getJSONObject(al);

                                db.addAlbum(album.getInt("id"), album.getInt("artist_id"), album.getString("name"), album.getString("cover"));

                                if (syncSongs) {
                                    JSONArray songs = album.getJSONArray("songs");

                                    for (int s = 0; s < songs.length(); s++) {
                                        JSONObject song = songs.getJSONObject(s);

                                        db.addSong(song.getString("id"), song.getInt("album_id"), song.getString("title"), song.getDouble("length"), song.getInt("track"));
                                    }
                                }
                            }
                        }
                    }
                }
                if (syncPlaylists) {
                    JSONArray playlists = jsonResponse.getJSONArray("playlists");

                    for(int p = 0; p < playlists.length(); p++) {
                        JSONObject playlist = playlists.getJSONObject(p);

                        db.addPlaylist(playlist.getInt("id"), userId, playlist.getString("name"));

                        JSONArray songs = playlist.getJSONArray("songs");

                        for (int s = 0; s < songs.length(); s++) {
                            String songId = songs.getString(s);

                            db.addPlaylistSong(playlist.getInt("id"), songId);
                        }
                    }
                }
            } catch (JSONException e) {
                Log.e("koelManager", e.getMessage());
                Toast.makeText(context, "Une erreur interne a été détectée (n°510).", Toast.LENGTH_SHORT).show();
            }

            return null;
        }
    }

    /**
     *
     * @param syncUsers : independent
     * @param syncArtists
     * @param syncAlbums : requires syncArtists
     * @param syncSongs : requires syncAlbums
     * @param syncPlaylists : requires syncUsers
     */
    private void syncData(final boolean syncUsers, final boolean syncArtists, final boolean syncAlbums, final boolean syncSongs, final boolean syncPlaylists) {

        if(listener != null) {
            listener.onDataSync(true);
        }

        String endpoint = Config.API_URL + "/data";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, endpoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            AsyncSyncDatabaseTask asyncSyncDatabaseTask = new AsyncSyncDatabaseTask(jsonResponse, syncUsers, syncArtists, syncAlbums, syncSongs, syncPlaylists);
                            asyncSyncDatabaseTask.execute();

                        } catch(JSONException e) {
                            Log.e("koelManager", e.getMessage());
                            Toast.makeText(context, "Une erreur interne a été détectée (n°510).", Toast.LENGTH_SHORT).show();

                            if(listener != null) {
                                listener.onDataSyncOver(true);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Une erreur interne a été détectée (n°530).", Toast.LENGTH_SHORT).show();

                        if(listener != null) {
                            listener.onDataSyncOver(true);
                        }
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                headers.put("Authorization", "Bearer " + token);
                headers.put("X-Requested-With", "XMLHttpRequest");

                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void syncAll() {
        this.syncData(true, true, true, true, true);
    }

    public void syncUsers() {
        this.syncData(true, false, false, false, false);
    }

    public void syncArtists() {

        this.syncData(true, true, false, false, false);
    }

    public void syncAlbums() {

        this.syncData(false, true, true, false, false);
    }

    public void syncSongs() {

        this.syncData(false, true, true, true, false);
    }

    public void syncPlaylists() {
        this.syncData(true, false, false, false, true);
    }


    public void setListener(KoelManagerListener listener) {
        this.listener = listener;
    }

    public interface KoelManagerListener {
        public void onDataSync(boolean success);
        public void onDataSyncOver(boolean success);
    }
}
