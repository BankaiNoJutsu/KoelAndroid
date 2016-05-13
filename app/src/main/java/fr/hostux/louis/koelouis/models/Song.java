package fr.hostux.louis.koelouis.models;

import fr.hostux.louis.koelouis.models.Album;

/**
 * Created by louis on 11/05/16.
 */
public class Song {
    private Album album;
    private String id;
    private String title;
    private double length;
    private int track;

    private int playCount;
    private boolean liked;

    public Song(String id, String title, double length, int track, int playCount, boolean liked, Album album) {
        this.id = id;
        this.title = title;
        this.length = length;
        this.track = track;
        this.playCount = playCount;
        this.liked = liked;
        this.album = album;
    }
}
