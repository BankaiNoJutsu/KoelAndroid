package fr.hostux.louis.koelouis;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;

import fr.hostux.louis.koelouis.helper.QueueHelper;
import fr.hostux.louis.koelouis.models.Song;

public class QueueFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int columnCount = 1;
    private OnListFragmentInteractionListener listener;
    private QueueHelper queueHelper;
    private QueueRecyclerViewAdapter adapter;

    public QueueFragment() {
    }

    @SuppressWarnings("unused")
    public static QueueFragment newInstance(int columnCount, QueueHelper queueHelper) {
        QueueFragment fragment = new QueueFragment();
        fragment.setQueueHelper(queueHelper);
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            columnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_queue_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (columnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
            }

            LinkedList<Song> songs = queueHelper.getQueue();

            adapter = new QueueRecyclerViewAdapter(songs, listener);
            recyclerView.setAdapter(adapter);
        }

        if(listener != null) {
            listener.updateActivityTitle("Current queue");
        }

        return view;
    }

    public interface OnListFragmentInteractionListener {
        void updateActivityTitle(String title);
        void onListFragmentInteraction(Song song, int position);
        void onPopupButtonClick(Song song, View view, int position);
    }

    public void setListener(OnListFragmentInteractionListener listener) {
        this.listener = listener;
    }

    public void setQueueHelper(QueueHelper queueHelper) {
        this.queueHelper = queueHelper;
    }

    public QueueRecyclerViewAdapter getAdapter() {
        return adapter;
    }
}
