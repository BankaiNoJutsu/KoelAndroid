package fr.hostux.louis.koelouis.models;

import fr.hostux.louis.koelouis.Config;
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

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getLength() {
        return length;
    }

    public int getLengthMs() {
        return (int) Math.round(length * 1000);
    }

    public String getReadableLength() {
        int intLength = (int) Math.round(length);
        return String.format("%d:%02d", intLength/60, intLength%60);
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public int getTrack() {
        return track;
    }
}
