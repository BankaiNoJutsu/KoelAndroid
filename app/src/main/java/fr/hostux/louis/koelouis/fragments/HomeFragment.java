package fr.hostux.louis.koelouis.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.hostux.louis.koelouis.Config;
import fr.hostux.louis.koelouis.R;
import fr.hostux.louis.koelouis.models.User;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String USER_NAME = "userName";
    private static final String API_URL = "apiUrl";

    private User user;

    private OnFragmentInteractionListener listener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(User user) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();

        args.putString(USER_NAME, user.getName());
        args.putString(API_URL, Config.API_URL);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String userEmail = getArguments().getString(USER_NAME);
            String apiUrl = getArguments().getString(API_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        TextView nameView = (TextView) rootView.findViewById(R.id.userNameView);
        nameView.setText(getArguments().getString(USER_NAME));

        if(listener != null) {
            listener.updateActivityTitle("Koelouis player");
        }

        return rootView;
    }

    public interface OnFragmentInteractionListener {
        void updateActivityTitle(String title);
    }

    public void setListener(OnFragmentInteractionListener listener) {
        this.listener = listener;
    }
}
