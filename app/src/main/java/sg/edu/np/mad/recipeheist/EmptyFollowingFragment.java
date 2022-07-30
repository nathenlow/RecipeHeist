package sg.edu.np.mad.recipeheist;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmptyFollowingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmptyFollowingFragment extends Fragment {

    public EmptyFollowingFragment() {
        // Required empty public constructor
    }


    public static EmptyFollowingFragment newInstance() {
        return new EmptyFollowingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_empty_following, container, false);
    }
}