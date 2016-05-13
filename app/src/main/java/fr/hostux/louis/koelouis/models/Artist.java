package fr.hostux.louis.koelouis.models;

/**
 * Created by louis on 11/05/16.
 */
public class Artist {
    private String id;
    private String name;
    private String image;

    private int playCount;
    private Album[] albums;
    private Song[] songs;

    public Artist(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }
}
