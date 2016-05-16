package fr.hostux.louis.koelouis.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.hostux.louis.koelouis.R;
import fr.hostux.louis.koelouis.helper.MediaStore;
import fr.hostux.louis.koelouis.models.Song;

public class AlbumFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int columnCount = 1;
    private static final String ARG_ALBUM_ID = "albumId";
    private int albumId;
    private static final String ARG_ALBUM_NAME = "albumName";
    private String albumName;


    private OnListFragmentInteractionListener listener;

    public AlbumFragment() {
    }

    public static AlbumFragment newInstance(int columnCount, int albumId, String albumName) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_ALBUM_ID, albumId);
        args.putString(ARG_ALBUM_NAME, albumName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            columnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            albumId = getArguments().getInt(ARG_ALBUM_ID);
            albumName = getArguments().getString(ARG_ALBUM_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (columnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
            }

            MediaStore mediaStore = new MediaStore(context);
            List<Song> songs = mediaStore.getSongs(albumId);

            recyclerView.setAdapter(new AlbumRecyclerViewAdapter(songs, listener));
        }

        if(listener != null) {
            listener.updateActivityTitle(albumName);
        }

        return view;
    }

    public interface OnListFragmentInteractionListener {
        void updateActivityTitle(String title);
        void setQueueAndPlay(List<Song> queue);
        void onPopupButtonClick(Song song, View view);
    }

    public void setListener(OnListFragmentInteractionListener listener) {
        this.listener = listener;
    }
}
