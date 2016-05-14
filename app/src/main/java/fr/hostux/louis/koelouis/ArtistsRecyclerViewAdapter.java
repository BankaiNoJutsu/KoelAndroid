package fr.hostux.louis.koelouis;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import fr.hostux.louis.koelouis.ArtistsFragment.OnListFragmentInteractionListener;
import fr.hostux.louis.koelouis.helper.MediaStore;
import fr.hostux.louis.koelouis.models.Artist;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Artist} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ArtistsRecyclerViewAdapter extends RecyclerView.Adapter<ArtistsRecyclerViewAdapter.ViewHolder> {

    private final List<Artist> artists;
    private final OnListFragmentInteractionListener listener;

    private MediaStore mediaStore;

    public ArtistsRecyclerViewAdapter(List<Artist> artists, OnListFragmentInteractionListener listener) {
        this.artists = artists;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_artists, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Artist artist = artists.get(position);

        holder.artist = artist;
        holder.artistNameView.setText(artist.getName());
        holder.artistAlbumCountView.setText(artist.getAlbumCount() + " albums");

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onListFragmentInteraction(holder.artist);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(artists == null) {
            return 0;
        }
        return artists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView artistNameView;
        public final TextView artistAlbumCountView;
        public Artist artist;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            artistNameView = (TextView) view.findViewById(R.id.artist_name);
            artistAlbumCountView = (TextView) view.findViewById(R.id.artist_albumCount);
        }
    }
}
