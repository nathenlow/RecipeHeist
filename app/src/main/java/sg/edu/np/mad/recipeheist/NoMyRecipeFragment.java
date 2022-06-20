package sg.edu.np.mad.recipeheist;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NoMyRecipeFragment extends Fragment {

    public NoMyRecipeFragment() {
        // Required empty public constructor
    }


    public static NoMyRecipeFragment newInstance() {
        NoMyRecipeFragment fragment = new NoMyRecipeFragment();
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
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_no_my_recipe, container, false);
        return view;
    }
}