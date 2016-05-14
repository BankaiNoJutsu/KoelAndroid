package fr.hostux.louis.koelouis.models;

import java.util.List;

import fr.hostux.louis.koelouis.models.Artist;

/**
 * Created by louis on 11/05/16.
 */
public class Album {
    private Artist artist;
    private int id;
    private String name;
    private String cover;

    private int playCount;
    private double length;
    private List<Song> songs;

    public Album(int id, String name, String cover, Artist artist) {
        this.id = id;
        this.name = name;
        this.cover = cover;
        this.artist = artist;
    }

    public int getId() {
        return id;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public String getName() {
        return name;
    }

    public int getSongCount() {
        if(songs == null) {
            return 0;
        }
        return songs.size();
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }
}
