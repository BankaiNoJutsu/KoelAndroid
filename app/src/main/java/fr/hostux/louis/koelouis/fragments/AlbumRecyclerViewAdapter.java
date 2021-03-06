package fr.hostux.louis.koelouis.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import fr.hostux.louis.koelouis.R;
import fr.hostux.louis.koelouis.fragments.AlbumFragment.OnListFragmentInteractionListener;
import fr.hostux.louis.koelouis.models.Song;

import java.util.List;

public class AlbumRecyclerViewAdapter extends RecyclerView.Adapter<AlbumRecyclerViewAdapter.ViewHolder> {

    private final List<Song> songs;
    private final OnListFragmentInteractionListener listener;

    public AlbumRecyclerViewAdapter(List<Song> songs, OnListFragmentInteractionListener listener) {
        this.songs = songs;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_album, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Song song = songs.get(position);

        holder.song = song;
        holder.songTitleView.setText(song.getTitle());
        holder.songLengthView.setText(song.getReadableLength());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.setQueueAndPlay(songs.subList(position, songs.size()));
                }
            }
        });

        holder.popupMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    listener.onPopupButtonClick(holder.song, view);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(songs == null) {
            return 0;
        }
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView songTitleView;
        public final TextView songLengthView;
        public final ImageButton popupMenuButton;
        public Song song;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            songTitleView = (TextView) view.findViewById(R.id.song_title);
            songLengthView = (TextView) view.findViewById(R.id.song_length);
            popupMenuButton = (ImageButton) view.findViewById(R.id.song_button_popupMenu);
        }
    }
}
