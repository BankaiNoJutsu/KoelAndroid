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
import fr.hostux.louis.koelouis.models.Album;

public class ArtistFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int columnCount = 1;
    private static final String ARG_ARTIST_ID = "artistId";
    private String artistId;
    private static final String ARG_ARTIST_NAME = "artistName";
    private String artistName;


    private OnListFragmentInteractionListener listener;

    public ArtistFragment() {
    }

    public static ArtistFragment newInstance(int columnCount, String artistId, String artistName) {
        ArtistFragment fragment = new ArtistFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(ARG_ARTIST_ID, artistId);
        args.putString(ARG_ARTIST_NAME, artistName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            columnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            artistId = getArguments().getString(ARG_ARTIST_ID);
            artistName = getArguments().getString(ARG_ARTIST_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_list, container, false);

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
            List<Album> albums = mediaStore.getAlbums(artistId, true);

            recyclerView.setAdapter(new ArtistRecyclerViewAdapter(albums, listener));
        }

        if(listener != null) {
            listener.updateActivityTitle(artistName);
        }

        return view;
    }

    public interface OnListFragmentInteractionListener {
        void updateActivityTitle(String title);
        void onListFragmentInteraction(Album album);
    }

    public void setListener(OnListFragmentInteractionListener listener) {
        this.listener = listener;
    }
}
