package fr.hostux.louis.koelouis.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import fr.hostux.louis.koelouis.R;

public class SettingsFragment extends Fragment {

    private OnFragmentInteractionListener listener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        Button syncData = (Button) rootView.findViewById(R.id.buttonSyncData);
        Button buttonLogout = (Button) rootView.findViewById(R.id.buttonLogout);

        syncData.setOnClickListener(syncDataHandler);
        buttonLogout.setOnClickListener(logout);

        if(listener != null) {
            listener.updateActivityTitle("Koelouis settings");
        }

        return rootView;
    }

    View.OnClickListener syncDataHandler = new View.OnClickListener() {
        public void onClick(View v) {
            if(listener != null) {
                listener.onRequestDataSync();
            }
        }
    };

    View.OnClickListener logout = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(listener != null) {
                listener.onRequestLogout();
            }
        }
    };

    public interface OnFragmentInteractionListener {
        void updateActivityTitle(String title);
        void onRequestDataSync();
        void onRequestLogout();
    }

    public void setListener(OnFragmentInteractionListener listener) {
        this.listener = listener;
    }
}
