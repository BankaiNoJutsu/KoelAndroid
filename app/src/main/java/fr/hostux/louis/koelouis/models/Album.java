package fr.hostux.louis.koelouis.models;

import fr.hostux.louis.koelouis.models.Artist;

/**
 * Created by louis on 11/05/16.
 */
public class Album {
    private Artist artist;
    private String id;
    private String name;
    private String cover;

    private int playCount;
    private double length;
    private Song[] songs;

    public Album(String id, String name, String cover, Artist artist) {
        this.id = id;
        this.name = name;
        this.cover = cover;
        this.artist = artist;
    }
}
