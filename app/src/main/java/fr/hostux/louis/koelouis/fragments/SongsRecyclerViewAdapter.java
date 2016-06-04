package fr.hostux.louis.koelouis.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import fr.hostux.louis.koelouis.R;
import fr.hostux.louis.koelouis.fragments.SongsFragment.OnListFragmentInteractionListener;
import fr.hostux.louis.koelouis.helper.MediaStore;
import fr.hostux.louis.koelouis.models.Song;

public class SongsRecyclerViewAdapter extends RecyclerView.Adapter<SongsRecyclerViewAdapter.ViewHolder> {

    private final List<Song> songs;
    private final OnListFragmentInteractionListener listener;
    private final Context context;

    private MediaStore mediaStore;

    public SongsRecyclerViewAdapter(Context context, List<Song> songs, OnListFragmentInteractionListener listener) {
        this.songs = songs;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_songs, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Song song = songs.get(position);

        holder.song = song;
        holder.songTitleView.setText(song.getTitle());
        holder.songArtistView.setText(song.getAlbum().getArtist().getName());
        holder.songAlbumView.setText(song.getAlbum().getName());
        holder.songLengthView.setText(song.getReadableLength());

        Picasso.with(context).load(song.getAlbum().getCoverUri()).into(holder.songIcon);

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
        public final TextView songArtistView;
        public final TextView songAlbumView;
        public final TextView songLengthView;
        public final ImageButton popupMenuButton;
        public final ImageView songIcon;
        public Song song;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            songTitleView = (TextView) view.findViewById(R.id.song_title);
            songArtistView = (TextView) view.findViewById(R.id.song_artist);
            songAlbumView = (TextView) view.findViewById(R.id.song_album);
            songLengthView = (TextView) view.findViewById(R.id.song_length);
            songIcon = (ImageView) view.findViewById(R.id.song_icon);
            popupMenuButton = (ImageButton) view.findViewById(R.id.song_button_popupMenu);
        }
    }
}
