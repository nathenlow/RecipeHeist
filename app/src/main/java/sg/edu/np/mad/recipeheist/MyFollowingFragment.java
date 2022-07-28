package sg.edu.np.mad.recipeheist;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyFollowingFragment extends Fragment {

    private User user;
    private RecyclerView recyclerView;
    private ConstraintLayout pBarCover;
    private View rootView;

    public MyFollowingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable("userData");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_my_following, container, false);

        // get widget
        pBarCover = rootView.findViewById(R.id.pBarCover);

        return rootView;
    }
}