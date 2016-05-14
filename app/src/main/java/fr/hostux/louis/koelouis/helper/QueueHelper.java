package fr.hostux.louis.koelouis.helper;

import java.util.Deque;
import java.util.LinkedList;

import fr.hostux.louis.koelouis.models.Song;

/**
 * Created by louis on 14/05/16.
 */
public class QueueHelper {
    private Song current;
    private LinkedList<Song> queue;
    private LinkedList<Song> history;

    public QueueHelper() {
        queue = new LinkedList<Song>();
        history = new LinkedList<Song>();
    }

    public void addNext(Song song) {
        queue.offerFirst(song);
    }
    public void add(Song song) {
        queue.offer(song);
    }

    public Song getCurrent() {
        return current;
    }

    public void setCurrent(Song current) {
        this.current = current;
    }

    // Retrieves and removes head of queue
    // return null if empty
    public Song next() {
        return queue.poll();
    }
    public Song prev() {
        return history.poll();
    }

    public void addToHistory(Song song) {
        history.offer(song);
    }

    public void clearHistory() {
        history.clear();
    }

    public LinkedList<Song> getQueue() {
        return queue;
    }

    public void removeFromQueue(int position) {
        queue.remove(position);
    }

    public void clearQueue() {
        queue.clear();
    }
}
