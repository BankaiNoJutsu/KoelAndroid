package fr.hostux.louis.koelouis.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import fr.hostux.louis.koelouis.fragments.AlbumsFragment.OnListFragmentInteractionListener;
import fr.hostux.louis.koelouis.R;
import fr.hostux.louis.koelouis.helper.MediaStore;
import fr.hostux.louis.koelouis.models.Album;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Album} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class AlbumsRecyclerViewAdapter extends RecyclerView.Adapter<AlbumsRecyclerViewAdapter.ViewHolder> {

    private final List<Album> albums;
    private final OnListFragmentInteractionListener listener;
    private final Context context;

    private MediaStore mediaStore;

    public AlbumsRecyclerViewAdapter(Context context, List<Album> albums, OnListFragmentInteractionListener listener) {
        this.context = context;
        this.albums = albums;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_albums, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Album album = albums.get(position);

        holder.album = album;
        holder.albumNameView.setText(album.getName());
        holder.albumAlbumCountView.setText(album.getSongCount() + " songs");

        Picasso.with(context).load(album.getCoverUri()).into(holder.albumCoverView);

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
        public final TextView albumAlbumCountView;
        public final ImageView albumCoverView;
        public Album album;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            albumNameView = (TextView) view.findViewById(R.id.album_name);
            albumAlbumCountView = (TextView) view.findViewById(R.id.album_songCount);
            albumCoverView = (ImageView) view.findViewById(R.id.album_cover);
        }
    }
}
