package fr.hostux.louis.koelouis;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class Playing extends Fragment {

    private OnFragmentInteractionListener listener;

    public Playing() {
    }

    public static Playing newInstance() {
        Playing fragment = new Playing();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_playing, container, false);
    }

    public interface OnFragmentInteractionListener {
        void onRequestPlay();
        void onRequestPause();
        void onRequestPrevious();
        void onRequestNext();
    }
}
