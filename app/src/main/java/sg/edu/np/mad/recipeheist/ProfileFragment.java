package sg.edu.np.mad.recipeheist;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private User user = new User();
    private TextView noOfRecipes, description, username;
    private Bundle user_data = new Bundle();
    private JSONArray recipeJArray;
    private View rootView;
    private ProgressBar progressBar;
    private ArrayList<Recipe> recipeList;
    private MainActivity mainActivity;
    private Boolean loadbefore = false;
    private Boolean loadbeforerecipies = false;
    CircleImageView profileImage;
    private Button editProfileBtn;
    private FloatingActionButton addRecipeBtn;
    private SwipeRefreshLayout swipeRefreshLayout;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // to use methods from MainActivity
        mainActivity = (MainActivity) getActivity();
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
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);

        mainActivity.setActionBarTitle("Profile");

        if (!loadbefore) {
            // update profile page
            String uid = FirebaseAuth.getInstance().getUid();
            // function to get users from restDB
            RestDB restDB = new RestDB();
            try {
                restDB.asyncGet("https://recipeheist-567c.restdb.io/rest/users?q={\"userID\":\"" + uid + "\"}", new SuccessListener() {
                    @Override
                    public void onSuccess(String jsonresponse) throws JSONException {
                        System.out.println(jsonresponse);
                        String dataBaseUsers = jsonresponse;
                        dataBaseUsers = dataBaseUsers.substring(1, dataBaseUsers.length() - 1);

                        JSONObject jsonObject = new JSONObject(dataBaseUsers);

                        user.setUserID(jsonObject.getString("userID"));
                        user.setEmail(jsonObject.getString("email"));
                        user.setUsername(jsonObject.getString("username"));
                        user.setDescription(jsonObject.getString("description"));
                        user.setFollowing(convertJArrayToArrayList(jsonObject.getJSONArray("following")));
                        user.setBookmark(convertJArrayToArrayList(jsonObject.getJSONArray("bookmark")));

                        user_data.putParcelable("userData", user);
                        // Remove loading page
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                profileImage.setImageResource(R.drawable.default_profile_1);
                                username.setText(user.getUsername());
                                description.setText(user.getDescription());
                                loadbefore = true;
                                try {
                                    getUserRecipes(user.getUserID());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // when user clicks on "edit profile" button
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // pass data to next fragment
                EditProfileFragment editProfileFragment = new EditProfileFragment();
                editProfileFragment.setArguments(user_data);

                // pass data from this fragment to MainActivity
                assert mainActivity != null;
                mainActivity.replaceFragment(editProfileFragment , mainActivity.findViewById(R.id.frameLayout).getId());
                Objects.requireNonNull(mainActivity.getSupportActionBar()).setTitle("Profile Settings");
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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(mainActivity, "Reloading page", Toast.LENGTH_SHORT).show();
                Init();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (loadbefore){
            profileImage.setImageResource(R.drawable.default_profile_1);
            username.setText(user.getUsername());
            description.setText(user.getDescription());
            if (!loadbeforerecipies){
                try {
                    getUserRecipes(user.getUserID());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                getData();
            }
        }
    }

    public void Init(){
        mainActivity.profileFragment = new ProfileFragment();
        mainActivity.replaceFragment(mainActivity.profileFragment, R.id.frameLayout);
    }

    // ------------------------------- Start of functions -------------------------------

    public void getData(){

        // get users own recipe from db
        if (recipeJArray.length() >= 1) {
            recipeList = new ArrayList<>();

            // extract data and create a recipe object
            for (int i = 0; i < recipeJArray.length(); i++) {
                System.out.println(recipeJArray);
                Recipe recipe = new Recipe();
                try {
                    JSONObject jsonObject = recipeJArray.getJSONObject(i);

                    // set recipe details
                    recipe.setRecipeID(jsonObject.getString("_id"));
                    recipe.setUserID(jsonObject.getString("userID"));
                    recipe.setTitle(jsonObject.getString("title"));
                    recipe.setDescription(jsonObject.getString("description"));
                    recipe.setDuration(jsonObject.getString("duration"));
                    recipe.setServings(jsonObject.getInt("servings"));
                    recipe.setImagePath(jsonObject.getString("imagePath"));
                    recipe.setFoodcategory(jsonObject.getString("foodcategory"));
                    recipe.setIngridient(convertJArrayToArrayList(jsonObject.getJSONArray("ingredient")));
                    recipe.setInstructions(convertJArrayToArrayList(jsonObject.getJSONArray("instructions")));
                    //recipe.setLike(convertJArrayToArrayList(jsonObject.getJSONArray("like")));

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
            mainActivity.replaceFragment(myRecipeFragment, rootView.findViewById(R.id.profileFrameLayout).getId());
        }
    }

    // function to get users from restDB
    public void getUserRecipes(String uid) throws IOException {
        RestDB restDB = new RestDB();
        restDB.asyncGet("https://recipeheist-567c.restdb.io/rest/recipe?q={\"userID\":\"" + uid + "\"}", new SuccessListener() {
            @Override
            public void onSuccess(String jsonresponse) throws JSONException {
                loadbeforerecipies = true;
                String dbResults = "{ \"recipe\": " + jsonresponse + "}";
                JSONObject jsonObject = new JSONObject(dbResults);
                recipeJArray = jsonObject.getJSONArray("recipe");
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getData();
                    }
                });
            }
        });
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

}