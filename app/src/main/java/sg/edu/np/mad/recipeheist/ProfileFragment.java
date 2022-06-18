package sg.edu.np.mad.recipeheist;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private User user;
    private TextView username, description,  noOfRecipes, following;
    private CircleImageView profileImage;
    private Button editProfileBtn;
    private FloatingActionButton addRecipeBtn;
    private Bundle user_data;
    private JSONArray recipeJArray;
    private View rootView;
    private ProgressBar progressBar;


    public ProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            user_data = getArguments();
            user = user_data.getParcelable("userData");

            String query = "\"" + user.getUserID() + "\"";

            // get data from database
            try {
                String dbResults = "{ \"recipe\": " + getUserRecipes(query) + "}";
                JSONObject jsonObject = new JSONObject(dbResults);
                recipeJArray = jsonObject.getJSONArray("recipe");

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = rootView.findViewById(R.id.profileImage);
        username = rootView.findViewById(R.id.profileName);
        description = rootView.findViewById(R.id.profileDescription);
        editProfileBtn = rootView.findViewById(R.id.editProfileBtn);
        addRecipeBtn = rootView.findViewById(R.id.addRecipeBtn);
        noOfRecipes = rootView.findViewById(R.id.noOfRecipes);
        progressBar = rootView.findViewById(R.id.progressBarProfile);

        // to use methods from MainActivity
        MainActivity mainActivity = (MainActivity) getActivity();

        // update profile page
        profileImage.setImageResource(R.drawable.default_profile_1);
        username.setText(user.getUsername());
        description.setText(user.getDescription());

        // when user clicks on "edit profile" button
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // pass data to next fragment
                EditProfileFragment editProfileFragment = new EditProfileFragment();
                editProfileFragment.setArguments(user_data);

                // pass data from this fragment to MainActivity
                mainActivity.replaceFragment(editProfileFragment , mainActivity.findViewById(R.id.frameLayout).getId());
                mainActivity.getSupportActionBar().setTitle("Profile Settings");
            }
        });

        // when user clicks on "add recipe" button
        addRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddRecipeActivity.class);
                intent.putExtras(user_data);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // to use methods from MainActivity
        MainActivity mainActivity = (MainActivity) getActivity();

        // get users own recipe from db
        if (recipeJArray.length() >= 1) {
            ArrayList<Recipe> recipeList = new ArrayList<>();

            // extract data and create a recipe object
            for (int i = 0; i < recipeJArray.length(); i++){
                System.out.println(recipeJArray);
                Recipe recipe = new Recipe();
                try {
                    JSONObject jsonObject = recipeJArray.getJSONObject(i);

                    // set recipe details
                    recipe.setUserID(jsonObject.getString("userID"));
                    recipe.setTitle(jsonObject.getString("title"));
                    recipe.setDescription(jsonObject.getString("description"));
                    recipe.setDuration(jsonObject.getString("duration"));
                    recipe.setServings(jsonObject.getInt("servings"));
                    recipe.setImagePath(jsonObject.getString("imagePath"));
                    recipe.setFoodcategory(jsonObject.getString("foodcategory"));
                    recipe.setIngridient(convertJArrayToArrayList(jsonObject.getJSONArray("ingredient")));
                    recipe.setInstructions(convertJArrayToArrayList(jsonObject.getJSONArray("instructions")));
                    recipe.setLike(convertJArrayToArrayList(jsonObject.getJSONArray("like")));

                    System.out.println(recipe.getTitle());
                    // add recipe to recipe list
                    recipeList.add(recipe);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            noOfRecipes.setText(String.valueOf(recipeJArray.length()));

            // pass data to recycler view fragment:

            // Create bundle to pass user data to fragment
            Bundle userRecipe_data = new Bundle();
            userRecipe_data.putSerializable("userRecipeData", recipeList);
            //set argument to ProfileFragment
            MyRecipeFragment myRecipeFragment = new MyRecipeFragment();
            myRecipeFragment.setArguments(userRecipe_data);
            // remove progressbar
            progressBar.setVisibility(View.GONE);
            // replace fragment
            assert mainActivity != null;
            mainActivity.replaceFragment(myRecipeFragment, rootView.findViewById(R.id.profileFrameLayout).getId());

        }
        else {
            // if its empty or null
            progressBar.setVisibility(View.GONE);
            NoMyRecipeFragment noMyRecipeFragment = new NoMyRecipeFragment();
            assert mainActivity != null;
            mainActivity.replaceFragment(noMyRecipeFragment, rootView.findViewById(R.id.profileFrameLayout).getId());
        }
    }




    // function to convert jsonArray to ArrayList
    public ArrayList<String> convertJArrayToArrayList(JSONArray jsonArray) throws JSONException {
        ArrayList<String> arrayList = new ArrayList<>();

        if (jsonArray != null){
            for (int i = 0; i < jsonArray.length(); i++){
                arrayList.add(jsonArray.getString(i));
            }
            return arrayList;
        }
        else{
            return arrayList;
        }
    }

    // function to get users from restDB
    public String getUserRecipes(String uid) throws IOException {
        RestDB restDB = new RestDB();
        String response = restDB.get("https://recipeheist-567c.restdb.io/rest/recipe?q={\"userID\": " + uid + "}");
        return response;
    }
    
    public void goToSignIn()
    {
        Intent intent = new Intent(getActivity(), SignIn.class);
        startActivity(intent);
    }
}