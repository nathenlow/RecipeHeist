package sg.edu.np.mad.recipeheist;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import sg.edu.np.mad.recipeheist.adapter.BrowseAdapter;
import sg.edu.np.mad.recipeheist.adapter.MyRecipeAdapter;


public class MyRecipeFragment extends Fragment {

    private ArrayList<Recipe> recipes;
    private ArrayList<RecipePreview> recipePreviews;
    private MainActivity mainActivity;

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
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_recipe, container, false);


        RecyclerView recyclerView = view.findViewById(R.id.myRecipeRecycler);

        // grid layout splitting display into two columns
        int columns = 2;
        GridLayoutManager manager = new GridLayoutManager(mainActivity, columns);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(columns, 12, false));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(manager);

        // convert recipeList to recipePreview list
        recipePreviews = convertRecipesToRPreview(recipes);

        BrowseAdapter myRecipeAdapter = new BrowseAdapter(getActivity(), recipePreviews, new RecipeLoadListener() {
            @Override
            public void onLoad(String recipeID) {
                goToRecipe(recipeID);
            }
        });

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);


        recyclerView.setAdapter(myRecipeAdapter);

        return view;
    }

    // function to convert list of recipes to list of recipe preview
    public ArrayList<RecipePreview> convertRecipesToRPreview(ArrayList<Recipe> recipeList){
        ArrayList<RecipePreview> previews = new ArrayList<>();
        for (Recipe recipe:
             recipeList) {
            RecipePreview recipePreview = new RecipePreview(recipe.getRecipeID(), recipe.getTitle(), recipe.getImagePath(), recipe.getDuration());

            // add into arraylist
            previews.add(recipePreview);
        }

        return previews;
    }

    // go to recipe page
    public void goToRecipe(String recipeID)
    {
        Intent intent = new Intent(mainActivity, RecipeItem.class);
        intent.putExtra("recipeID", recipeID);
        mainActivity.startActivity(intent);

    }


}