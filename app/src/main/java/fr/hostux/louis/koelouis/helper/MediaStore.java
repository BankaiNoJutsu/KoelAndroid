package fr.hostux.louis.koelouis.helper;

import android.content.Context;

import java.util.List;

import fr.hostux.louis.koelouis.models.Album;
import fr.hostux.louis.koelouis.models.Artist;
import fr.hostux.louis.koelouis.models.Playlist;
import fr.hostux.louis.koelouis.models.Song;

/**
 * Created by louis on 14/05/16.
 */
public class MediaStore {
    private Context context;
    private SQLiteHandler db;

    public MediaStore(Context context) {
        this.context = context;
        this.db = new SQLiteHandler(context);
    }

    public List<Artist> getArtists() {
        return this.getArtists(false, false);
    }

    public List<Artist> getArtists(boolean withAlbums, boolean withSongs) {
        List<Artist> artists = db.getArtists();

        if(withAlbums) {
            for(int a=0; a < artists.size(); a++) {
                Artist artist = artists.get(a);

                List<Album> albums = db.findAlbumsByArtistId(artist.getId());
                artist.setAlbums(albums);


                if(withSongs) {
                    for(int al=0; al < albums.size(); al++) {
                        Album album = albums.get(al);

                        List<Song> songs = db.findSongsByAlbumId(album.getId());
                        album.setSongs(songs);
                    }
                }
            }
        }

        return artists;
    }


    public List<Album> getAlbums() {
        return this.getAlbums(0, false);
    }

    public List<Album> getAlbums(int artistId, boolean withSongs) {
        List<Album> albums = null;

        if(artistId == 0) {
            albums = db.getAlbums();
        } else {
            albums = db.findAlbumsByArtistId(artistId);
        }

        if(albums != null && withSongs) {
            for(int al=0; al < albums.size(); al++) {
                Album album = albums.get(al);

                List<Song> songs = db.findSongsByAlbumId(album.getId());
                album.setSongs(songs);
            }
        }

        return albums;
    }

    public List<Song> getSongs() {
        return db.getSongs();
    }
    public List<Song> getSongsByAlbum(int albumId) {
        return db.findSongsByAlbumId(albumId);
    }
    public List<Song> getSongsByArtist(int artistId) {
        return db.findSongsByArtistId(artistId);
    }
    public List<Song> getSongsByPlaylist(int playlistId) {
        return db.findSongsByPlaylistId(playlistId);
    }

    public List<Playlist> getPlaylists(int userId) {
        return this.getPlaylists(userId, false);
    }

    public List<Playlist> getPlaylists(int userId, boolean withSongs) {
        List<Playlist> playlists = db.getPlaylists(userId);


        if(playlists != null && withSongs) {
            for(int p=0; p < playlists.size(); p++) {
                Playlist playlist = playlists.get(p);

                List<Song> songs = db.findSongsByPlaylistId(playlist.getId());
                playlist.setSongs(songs);
            }
        }

        return playlists;
    }

    public List<Song> getSongsByAlbum(Album album) {
        return getSongsByAlbum(album.getId());
    }
    public List<Song> getSongsByArtist(Artist artist) {
        return getSongsByArtist(artist.getId());
    }
}
