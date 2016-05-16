package fr.hostux.louis.koelouis.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.hostux.louis.koelouis.fragments.ArtistFragment.OnListFragmentInteractionListener;
import fr.hostux.louis.koelouis.R;
import fr.hostux.louis.koelouis.models.Album;

import java.util.List;

public class ArtistRecyclerViewAdapter extends RecyclerView.Adapter<ArtistRecyclerViewAdapter.ViewHolder> {

    private final List<Album> albums;
    private final OnListFragmentInteractionListener listener;

    public ArtistRecyclerViewAdapter(List<Album> albums, OnListFragmentInteractionListener listener) {
        this.albums = albums;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_artist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Album album = albums.get(position);

        holder.album = album;
        holder.albumNameView.setText(album.getName());
        holder.albumCountSongsView.setText(album.getSongCount() + " songs");

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onListFragmentInteraction(holder.album);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(albums == null) {
            return 0;
        }
        return albums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView albumNameView;
        public final TextView albumCountSongsView;
        public Album album;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            albumNameView = (TextView) view.findViewById(R.id.album_name);
            albumCountSongsView = (TextView) view.findViewById(R.id.album_songCount);
        }
    }
}
