package fr.hostux.louis.koelouis.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import fr.hostux.louis.koelouis.fragments.PlaylistsFragment.OnListFragmentInteractionListener;
import fr.hostux.louis.koelouis.R;
import fr.hostux.louis.koelouis.helper.MediaStore;
import fr.hostux.louis.koelouis.models.Playlist;

public class PlaylistsRecyclerViewAdapter extends RecyclerView.Adapter<PlaylistsRecyclerViewAdapter.ViewHolder> {

    private final List<Playlist> playlists;
    private final OnListFragmentInteractionListener listener;

    private MediaStore mediaStore;

    public PlaylistsRecyclerViewAdapter(List<Playlist> playlists, OnListFragmentInteractionListener listener) {
        this.playlists = playlists;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_playlists, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);

        holder.playlist = playlist;
        holder.playlistNameView.setText(playlist.getName());
        holder.playlistPlaylistCountView.setText(playlist.getSongCount() + " songs");

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onListFragmentInteraction(holder.playlist);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(playlists == null) {
            return 0;
        }
        return playlists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView playlistNameView;
        public final TextView playlistPlaylistCountView;
        public Playlist playlist;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            playlistNameView = (TextView) view.findViewById(R.id.playlist_name);
            playlistPlaylistCountView = (TextView) view.findViewById(R.id.playlist_songCount);
        }
    }
}
