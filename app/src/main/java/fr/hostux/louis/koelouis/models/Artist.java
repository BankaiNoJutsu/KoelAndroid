package fr.hostux.louis.koelouis.models;

import java.util.List;

/**
 * Created by louis on 11/05/16.
 */
public class Artist {
    private String id;
    private String name;
    private String image;

    private int playCount;
    private List<Album> albums;
    private List<Song> songs;

    public Artist(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAlbumCount() {
        if(albums == null) {
            return 0;
        }
        return albums.size();
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public String getImageUri() {
        return image;
    }
}