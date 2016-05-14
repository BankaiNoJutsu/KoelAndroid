package fr.hostux.louis.koelouis.helper;

import android.content.Context;

import java.util.List;

import fr.hostux.louis.koelouis.models.Album;
import fr.hostux.louis.koelouis.models.Artist;
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
        return this.getAlbums(null, false);
    }

    public List<Album> getAlbums(String artistId, boolean withSongs) {
        List<Album> albums = null;

        if(artistId == null) {
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
    public List<Song> getSongs(int albumId) {
        return db.findSongsByAlbumId(albumId);
    }
}
