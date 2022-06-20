package sg.edu.np.mad.recipeheist;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import sg.edu.np.mad.recipeheist.adapter.MyRecipeAdapter;


public class MyRecipeFragment extends Fragment {

    private ArrayList<Recipe> recipes;

    public MyRecipeFragment() {
        // Required empty public constructor
    }


    public static MyRecipeFragment newInstance() {
        MyRecipeFragment fragment = new MyRecipeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipes = (ArrayList<Recipe>) getArguments().getSerializable("userRecipeData");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_recipe, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.myRecipeRecycler);
        MyRecipeAdapter myRecipeAdapter = new MyRecipeAdapter(getActivity(), recipes);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);


        recyclerView.setAdapter(myRecipeAdapter);

        return view;
    }


}