package sg.edu.np.mad.recipeheist;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class UpdatesFragment extends Fragment {

    private MainActivity mainActivity;




    public UpdatesFragment() {
        // Required empty public constructor
    }


    public static UpdatesFragment newInstance() {
        UpdatesFragment fragment = new UpdatesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_updates, container, false);

        mainActivity = (MainActivity) getActivity();

        mainActivity.setActionBarTitle("Updates");


        

        return rootView;
    }
}