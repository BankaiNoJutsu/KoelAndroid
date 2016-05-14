package fr.hostux.louis.koelouis;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.hostux.louis.koelouis.AlbumFragment.OnListFragmentInteractionListener;
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Song song = songs.get(position);

        holder.song = song;
        holder.songTitleView.setText(song.getTitle());
        holder.songLengthView.setText(song.getReadableLength());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onListFragmentInteraction(holder.song);
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
        public Song song;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            songTitleView = (TextView) view.findViewById(R.id.song_title);
            songLengthView = (TextView) view.findViewById(R.id.song_length);
        }
    }
}
