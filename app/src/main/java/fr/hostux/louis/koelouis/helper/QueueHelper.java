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
    private OnQueueChangedListener listener;

    public QueueHelper() {
        queue = new LinkedList<Song>();
        history = new LinkedList<Song>();
    }

    public void addNext(Song song) {
        queue.offerFirst(song);

        if(listener != null) {
            listener.updateQueue(queue);
        }
    }
    public void add(Song song) {
        queue.offer(song);

        if(listener != null) {
            listener.updateQueue(queue);
        }
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
        if(listener != null) {
            listener.updateQueue(queue);
        }

        return queue.poll();
    }
    public Song prev() {
        if(listener != null) {
            listener.updateQueue(queue);
        }

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

        if(listener != null) {
            listener.updateQueue(queue);
        }
    }

    public void clearQueue() {
        queue.clear();

        if(listener != null) {
            listener.updateQueue(queue);
        }
    }

    public void setQueue(LinkedList<Song> queue) {
        this.queue = queue;

        if(listener != null) {
            listener.updateQueue(queue);
        }
    }

    public interface OnQueueChangedListener {
        public void updateQueue(LinkedList<Song> newQueue);
    }

    public void setListener(OnQueueChangedListener listener) {
        this.listener = listener;
    }
}
