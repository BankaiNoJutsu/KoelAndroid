package fr.hostux.louis.koelouis.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.hostux.louis.koelouis.Config;
import fr.hostux.louis.koelouis.models.Album;
import fr.hostux.louis.koelouis.models.Artist;
import fr.hostux.louis.koelouis.models.Playlist;
import fr.hostux.louis.koelouis.models.Song;
import fr.hostux.louis.koelouis.models.User;

/**
 * Created by louis on 12/05/16.
 */
public class SQLiteHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "koelouis";

    private static final String TABLE_USER = "user";
    private static final String TABLE_ARTIST = "artist";
    private static final String TABLE_ALBUM = "album";
    private static final String TABLE_SONG = "song";
    private static final String TABLE_PLAYLIST = "playlist";
    private static final String TABLE_PLAYLIST_SONG = "playlist_song";

    private static final String KEY_ID = "id";
    private static final String KEY_HOST = "host";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_ADMIN = "isAdmin";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_COVER = "cover";
    private static final String KEY_TITLE = "title";
    private static final String KEY_LENGTH = "length";
    private static final String KEY_TRACK = "track";
    private static final String KEY_LOCAL_FILENAME = "local_filename";

    private static final String KEY_ARTIST_ID = "artist_id";
    private static final String KEY_ALBUM_ID = "album_id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PLAYLIST_ID = "playlist_id";
    private static final String KEY_SONG_ID = "song_id";

    private final Config config;
    private final String host;


    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        config = new Config(context);
        host = config.getHost();
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER, " + KEY_HOST + " TEXT, " + KEY_NAME + " TEXT, " + KEY_EMAIL + " TEXT, " + KEY_IS_ADMIN + " BOOLEAN"
                + ")";

        String CREATE_ARTIST_TABLE = "CREATE TABLE " + TABLE_ARTIST + "("
                + KEY_ID + " INTEGER, " + KEY_HOST + " TEXT, " +  KEY_NAME + " TEXT, " + KEY_IMAGE + " TEXT"
                + ")";

        String CREATE_ALBUM_TABLE = "CREATE TABLE " + TABLE_ALBUM + "("
                + KEY_ID + " INTEGER, " + KEY_HOST + " TEXT, " +  KEY_ARTIST_ID + " INTEGER, " + KEY_NAME + " TEXT, " + KEY_COVER + " TEXT"
                + ")";

        String CREATE_SONG_TABLE = "CREATE TABLE " + TABLE_SONG + "("
                + KEY_ID + " TEXT, " + KEY_HOST + " TEXT, " +  KEY_ALBUM_ID + " INTEGER, " + KEY_TITLE + " TEXT, " + KEY_LENGTH + " DOUBLE, " + KEY_TRACK + " INTEGER, " + KEY_LOCAL_FILENAME + " TEXT"
                + ")";

        String CREATE_PLAYLIST_TABLE = "CREATE TABLE " + TABLE_PLAYLIST + "("
                + KEY_ID + " TEXT, " + KEY_HOST + " TEXT, " +  KEY_USER_ID + " INTEGER, " + KEY_NAME + " TEXT"
                + ")";

        String CREATE_PLAYLIST_SONG_TABLE = "CREATE TABLE " + TABLE_PLAYLIST_SONG + "("
                + KEY_ID + " TEXT, " + KEY_HOST + " TEXT, " + KEY_PLAYLIST_ID + " INTEGER, " + KEY_SONG_ID + " INTEGER"
                + ")";


        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_ARTIST_TABLE);
        db.execSQL(CREATE_ALBUM_TABLE);
        db.execSQL(CREATE_SONG_TABLE);
        db.execSQL(CREATE_PLAYLIST_TABLE);
        db.execSQL(CREATE_PLAYLIST_SONG_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do better: db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    public void deleteFromUserTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_USER +" WHERE "+ KEY_HOST +" = ?", new String[] {host});
    }
    public void deleteFromArtistTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_ARTIST +" WHERE "+ KEY_HOST +" = ?", new String[] {host});
    }
    public void deleteFromAlbumTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_ALBUM +" WHERE "+ KEY_HOST +" = ?", new String[] {host});
    }
    public void deleteFromSongTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_SONG +" WHERE "+ KEY_HOST +" = ?", new String[] {host});
    }
    public void deleteFromPlaylistTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_PLAYLIST +" WHERE "+ KEY_HOST +" = ?", new String[] {host});
        db.execSQL("DELETE FROM "+ TABLE_PLAYLIST_SONG +" WHERE "+ KEY_HOST +" = ?", new String[] {host});
    }

    /**
     * Storing user details in database
     * */
    public void addUser(int id, String name, String email, boolean isAdmin) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_HOST, host);
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);
        values.put(KEY_IS_ADMIN, isAdmin);

        long insertedId = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection
    }
    /**
     * Storing artist details in database
     * */
    public void addArtist(int id, String name, String image) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_HOST, host);
        values.put(KEY_NAME, name);
        values.put(KEY_IMAGE, image);

        long insertedId = db.insert(TABLE_ARTIST, null, values);
        db.close(); // Closing database connection
    }
    /**
     * Storing album details in database
     * */
    public void addAlbum(int id, int artist_id, String name, String cover) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_HOST, host);
        values.put(KEY_ARTIST_ID, artist_id);
        values.put(KEY_NAME, name);
        values.put(KEY_COVER, cover);

        long insertedId = db.insert(TABLE_ALBUM, null, values);
        db.close(); // Closing database connection
    }
    /**
     * Storing song details in database
     * */
    public void addSong(String id, int album_id, String title, double length, int track) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_HOST, host);
        values.put(KEY_ALBUM_ID, album_id);
        values.put(KEY_TITLE, title);
        values.put(KEY_LENGTH, length);
        values.put(KEY_TRACK, track);

        long insertedId = db.insert(TABLE_SONG, null, values);
        db.close(); // Closing database connection
    }

    /**
     * Storing song details in database
     * */
    public void addPlaylist(int id, int user_id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_HOST, host);
        values.put(KEY_USER_ID, user_id);
        values.put(KEY_NAME, name);

        long insertedId = db.insert(TABLE_PLAYLIST, null, values);
        db.close(); // Closing database connection
    }

    /**
     * Storing song details in database
     * */
    public void addPlaylistSong(int playlist_id, String song_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_HOST, host);
        values.put(KEY_PLAYLIST_ID, playlist_id);
        values.put(KEY_SONG_ID, song_id);

        long insertedId = db.insert(TABLE_PLAYLIST_SONG, null, values);
        db.close(); // Closing database connection
    }

    public User findUserById(int id) {
        String selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE host = ? AND id = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { host, Integer.toString(id) });

        if(cursor.moveToFirst()) {
            User user = cursorToUser(cursor);

            cursor.close();
            db.close();

            return user;
        }

        cursor.close();
        db.close();

        return null;
    }

    public User findUserByEmail(String email) {
        String selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE host = ? AND email = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { host, email });

        if(cursor.moveToFirst()) {
            User user = cursorToUser(cursor);

            cursor.close();
            db.close();

            return user;
        }

        cursor.close();
        db.close();

        return null;
    }

    public Artist findArtistById(int id) {
        String selectQuery = "SELECT  * FROM " + TABLE_ARTIST + " WHERE host = ? AND id = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { host, Integer.toString(id) });

        if(cursor.moveToFirst()) {
            Artist artist = cursorToArtist(cursor);

            cursor.close();
            db.close();

            return artist;
        }

        cursor.close();
        db.close();

        return null;
    }
    public Album findAlbumById(int id) {
        String selectQuery = "SELECT  * FROM " + TABLE_ALBUM + " WHERE host = ? AND id = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { host, Integer.toString(id) });

        if(cursor.moveToFirst()) {
            Album album = cursorToAlbum(cursor);

            cursor.close();
            db.close();

            return album;
        }

        cursor.close();
        db.close();

        return null;
    }

    public List<Artist> getArtists() {
        String selectQuery = "SELECT  * FROM " + TABLE_ARTIST + " WHERE host = ? ORDER BY " + KEY_NAME + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {host});

        ArrayList<Artist> artists = new ArrayList<Artist>();

        if(cursor.moveToFirst()) {
            do {
                Artist artist = cursorToArtist(cursor);
                artists.add(artist);
            } while (cursor.moveToNext());

            cursor.close();
            db.close();

            return artists;
        }

        cursor.close();
        db.close();

        return null;
    }
    public List<Album> getAlbums() {
        String selectQuery = "SELECT  * FROM " + TABLE_ALBUM + " WHERE host = ? ORDER BY " + KEY_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { host });

        ArrayList<Album> albums = new ArrayList<Album>();

        if(cursor.moveToFirst()) {
            do {
                Album album = cursorToAlbum(cursor);
                albums.add(album);
            } while (cursor.moveToNext());

            cursor.close();
            db.close();

            return albums;
        }

        cursor.close();
        db.close();

        return null;
    }

    public List<Album> findAlbumsByArtistId(int artistId) {
        String selectQuery = "SELECT * FROM " + TABLE_ALBUM + " WHERE host = ? AND " + KEY_ARTIST_ID + " = ? ORDER BY " + KEY_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { host, Integer.toString(artistId) });

        ArrayList<Album> albums = new ArrayList<Album>();

        if(cursor.moveToFirst()) {
            do {
                Album album = cursorToAlbum(cursor);
                albums.add(album);
            } while (cursor.moveToNext());

            cursor.close();
            db.close();

            return albums;
        }

        cursor.close();
        db.close();

        return null;
    }

    public List<Song> getSongs() {
        String selectQuery = "SELECT  * FROM " + TABLE_SONG + " WHERE host = ? ORDER BY " + KEY_TITLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { host });

        ArrayList<Song> songs = new ArrayList<Song>();

        if(cursor.moveToFirst()) {
            do {
                Song song = cursorToSong(cursor);
                songs.add(song);
            } while (cursor.moveToNext());

            cursor.close();
            db.close();

            return songs;
        }

        cursor.close();
        db.close();

        return null;
    }

    public Song findSongById(String id) {
        String selectQuery = "SELECT  * FROM " + TABLE_SONG + " WHERE host = ? AND " + KEY_ID + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { host, id });

        Song song = null;

        if(cursor.moveToFirst()) {
            song = cursorToSong(cursor);
        }

        cursor.close();
        db.close();

        return song;
    }

    public void updateSongLocalFilename(String id, String newLocalFilename) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOCAL_FILENAME, newLocalFilename);

        db.update(TABLE_SONG, values, "host = ? AND " + KEY_ID + " = ?", new String[] { host, id });
        db.close();
    }

    public List<Song> findSongsByArtistId(int artistId) {
        String selectQuery = "SELECT * FROM " + TABLE_SONG + " s JOIN " + TABLE_ALBUM + " al ON s.album_id = al.id WHERE s.host = ? AND al." + KEY_ARTIST_ID + " = ? ORDER BY " + KEY_TITLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { host, Integer.toString(artistId) });

        ArrayList<Song> songs = new ArrayList<Song>();

        if(cursor.moveToFirst()) {
            do {
                Song song = cursorToSong(cursor);
                songs.add(song);
            } while (cursor.moveToNext());

            cursor.close();
            db.close();

            return songs;
        }

        cursor.close();
        db.close();

        return null;
    }

    public List<Song> findSongsByAlbumId(int albumId) {
        String selectQuery = "SELECT * FROM " + TABLE_SONG + " WHERE host = ? AND " + KEY_ALBUM_ID + " = ? ORDER BY " + KEY_TRACK + ", " + KEY_TITLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { host, Integer.toString(albumId) });

        ArrayList<Song> songs = new ArrayList<Song>();

        if(cursor.moveToFirst()) {
            do {
                Song song = cursorToSong(cursor);
                songs.add(song);
            } while (cursor.moveToNext());

            cursor.close();
            db.close();

            return songs;
        }

        cursor.close();
        db.close();

        return null;
    }

    public List<Song> findSongsByPlaylistId(int playlistId) {
        String selectQuery = "SELECT * FROM " + TABLE_SONG + " s JOIN " + TABLE_PLAYLIST_SONG + " ps ON s." + KEY_ID + " = ps." + KEY_SONG_ID + " WHERE s.host = ? AND ps." + KEY_PLAYLIST_ID + " = ? ";
        // TODO (when koel ready): ORDER BY " + KEY_TRACK + ", " + KEY_TITLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { host, Integer.toString(playlistId) });

        ArrayList<Song> songs = new ArrayList<Song>();

        if(cursor.moveToFirst()) {
            do {
                Song song = cursorToSong(cursor);
                songs.add(song);
            } while (cursor.moveToNext());

            cursor.close();
            db.close();

            return songs;
        }

        cursor.close();
        db.close();

        return null;
    }

    public List<Playlist> getPlaylists(int userId) {
        String selectQuery = "SELECT  * FROM " + TABLE_PLAYLIST + " WHERE host = ? AND " + KEY_USER_ID + " = ? ORDER BY " + KEY_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { host, Integer.toString(userId)} );

        ArrayList<Playlist> playlists = new ArrayList<Playlist>();

        if(cursor.moveToFirst()) {
            do {
                Playlist playlist = cursorToPlaylist(cursor);
                playlists.add(playlist);
            } while (cursor.moveToNext());

            cursor.close();
            db.close();

            return playlists;
        }

        cursor.close();
        db.close();

        return null;
    }

    private User cursorToUser(Cursor cursor) {
        int id = Integer.parseInt(cursor.getString(0));
        String name = cursor.getString(2);
        String email = cursor.getString(3);
        boolean isAdmin = Boolean.parseBoolean(cursor.getString(4));

        return new User(id, name, email, isAdmin);
    }
    private Artist cursorToArtist(Cursor cursor) {
        int id = cursor.getInt(0);
        String name = cursor.getString(2);
        String image = cursor.getString(3);

        return new Artist(id, name, image);
    }
    private Album cursorToAlbum(Cursor cursor) {
        int id = cursor.getInt(0);
        Artist artist = this.findArtistById(cursor.getInt(2));
        String name = cursor.getString(3);
        String cover = cursor.getString(4);

        return new Album(id, name, cover, artist);
    }
    private Song cursorToSong(Cursor cursor) {
        String id = cursor.getString(0);
        Album album = this.findAlbumById(cursor.getInt(2));
        String title = cursor.getString(3);
        double length = cursor.getDouble(4);
        int track = cursor.getInt(5);

        return new Song(id, title, length, track, 0, false, album);
    }
    private Playlist cursorToPlaylist(Cursor cursor) {
        int id = cursor.getInt(0);
        User user = this.findUserById(cursor.getInt(2));
        String name = cursor.getString(3);

        return new Playlist(id, user, name);
    }
};