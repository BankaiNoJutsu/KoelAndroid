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

import fr.hostux.louis.koelouis.fragments.ArtistsFragment.OnListFragmentInteractionListener;
import fr.hostux.louis.koelouis.R;
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
    private final Context context;

    private MediaStore mediaStore;

    public ArtistsRecyclerViewAdapter(Context context, List<Artist> artists, OnListFragmentInteractionListener listener) {
        this.artists = artists;
        this.listener = listener;
        this.context = context;
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

        if(artist.getImageUri() != null && !artist.getImageUri().isEmpty()) {
            Picasso.with(context).load(artist.getImageUri()).into(holder.artistImageView);
        } else if(artist.getAlbumCount() > 0) {
            String albumCoverUri = artist.getAlbums().get(0).getCoverUri();
            Picasso.with(context).load(albumCoverUri).into(holder.artistImageView);
        }

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
        public final ImageView artistImageView;
        public Artist artist;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            artistNameView = (TextView) view.findViewById(R.id.artist_name);
            artistAlbumCountView = (TextView) view.findViewById(R.id.artist_albumCount);
            artistImageView = (ImageView) view.findViewById(R.id.artist_image);
        }
    }
}
