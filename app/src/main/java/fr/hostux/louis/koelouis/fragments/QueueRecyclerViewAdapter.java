package fr.hostux.louis.koelouis.fragments;

import android.content.Context;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import fr.hostux.louis.koelouis.fragments.QueueFragment.OnListFragmentInteractionListener;
import fr.hostux.louis.koelouis.R;
import fr.hostux.louis.koelouis.helper.MediaStore;
import fr.hostux.louis.koelouis.helper.SQLiteHandler;
import fr.hostux.louis.koelouis.models.Song;

public class QueueRecyclerViewAdapter extends RecyclerView.Adapter<QueueRecyclerViewAdapter.ViewHolder> {

    private List<MediaSessionCompat.QueueItem> queue;
    private final OnListFragmentInteractionListener listener;
    private final Context context;

    public QueueRecyclerViewAdapter(Context context, List<MediaSessionCompat.QueueItem> queue, OnListFragmentInteractionListener listener) {
        this.queue = queue;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_queue, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        MediaSessionCompat.QueueItem queueItem = queue.get(position);

        SQLiteHandler db = new SQLiteHandler(context);

        Song song = db.findSongById(queueItem.getDescription().getMediaId());

        holder.song = song;
        holder.songTitleView.setText(song.getTitle());
        holder.songArtistView.setText(song.getAlbum().getArtist().getName());
        holder.songAlbumView.setText(song.getAlbum().getName());
        holder.songLengthView.setText(song.getReadableLength());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onListFragmentInteraction(holder.song, position);
                }
            }
        });

        holder.popupMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    listener.onPopupButtonClick(holder.song, view, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(queue == null) {
            return 0;
        }
        return queue.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView songTitleView;
        public final TextView songArtistView;
        public final TextView songAlbumView;
        public final TextView songLengthView;
        public final ImageButton popupMenuButton;
        public Song song;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            songTitleView = (TextView) view.findViewById(R.id.song_title);
            songArtistView = (TextView) view.findViewById(R.id.song_artist);
            songAlbumView = (TextView) view.findViewById(R.id.song_album);
            songLengthView = (TextView) view.findViewById(R.id.song_length);
            popupMenuButton = (ImageButton) view.findViewById(R.id.queue_button_popupMenu);
        }
    }
}
