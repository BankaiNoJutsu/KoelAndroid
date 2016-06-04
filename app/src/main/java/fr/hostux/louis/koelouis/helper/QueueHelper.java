package fr.hostux.louis.koelouis.helper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import fr.hostux.louis.koelouis.models.Album;
import fr.hostux.louis.koelouis.models.Artist;
import fr.hostux.louis.koelouis.models.Song;

/**
 * Created by louis on 14/05/16.
 */
public class QueueHelper {
    private OnQueueChangedListener listener;
    private List<Song> queue;
    private int currentIndex;
    private MediaStore mediaStore;

    public QueueHelper(MediaStore mediaStore) {
        queue = new ArrayList<Song>();
        currentIndex = 0;
        this.mediaStore = mediaStore;
    }

    public void addNext(Song song) {
        int nextIndex = currentIndex + 1;

        if(nextIndex < 0) { nextIndex = 0; } // Si inférieur à 0, on le met en première position
        if(nextIndex > queue.size()) { add(song); return; } // Si supérieur à la liste, alors on l'ajoute à la fin

        queue.add(nextIndex, song);

        if(listener != null) {
            listener.updateQueue(queue);
        }
    }

    public void add(Song song) {
        queue.add(song);

        if(listener != null) {
            listener.updateQueue(queue);
        }
    }

    public void add(List<Song> songs) {
        queue.addAll(songs);

        if(listener != null) {
            listener.updateQueue(queue);
        }
    }

    public void clearAndAdd(Song song) {
        clearQueue(false);
        add(song);
    }
    public void clearAndAdd(List<Song> songs) {
        clearQueue(false);
        add(songs);
    }

    public Song getCurrent() {
        if(currentIndex < 0) {
            return null;
        }

        return queue.get(currentIndex);
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentPosition(int newPos) {
        if(newPos < 0) { newPos = 0; }
        if(newPos > queue.size()) { newPos = queue.size(); }

        currentIndex = newPos;
    }

    public List<Song> getQueue() {
        return queue;
    }

    public void skip(int amount) {
        int index = currentIndex + amount;
        if (index < 0) {
            index = 0;
        } else {
            // skip forwards when in last song will cycle back to start of the queue
            index %= queue.size();
        }

        currentIndex = index;
    }

    public void removeFromQueue(int position) {
        queue.remove(position);

        if(listener != null) {
            listener.updateQueue(queue);
        }
    }

    public void clearQueue() {
        clearQueue(true);
    }

    public void clearQueue(boolean callListener) {
        queue.clear();
        currentIndex = 0;

        if(callListener && listener != null) {
            listener.updateQueue(queue);
        }
    }

    public void clearAndAddAllSongs() {
        clearQueue(false);
        addAllSongs();
    }
    public void clearAndAddArtist(Artist artist) {
        clearQueue(false);
        addArtist(artist);
    }
    public void clearAndAddAlbum(Album album) {
        clearQueue(false);
        addAlbum(album);
    }
    public void addAllSongs() {
        add(mediaStore.getSongs());
    }
    public void addArtist(Artist artist) {
        add(mediaStore.getSongsByArtist(artist));
    }
    public void addAlbum(Album album) {
        add(mediaStore.getSongsByAlbum(album));
    }

    public interface OnQueueChangedListener {
        void updateQueue(List<Song> newQueue);
    }

    public void setListener(OnQueueChangedListener listener) {
        this.listener = listener;
    }
}
