package fr.hostux.louis.koelouis.models;

import java.util.List;

/**
 * Created by louis on 11/05/16.
 */
public class Playlist {
    private int id;
    private User user;
    private String name;
    private List<Song> songs;

    public Playlist(int id, User user, String name) {
        this.id = id;
        this.user = user;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public List<Song> getSongs() {
        return songs;
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
}
