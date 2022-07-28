package sg.edu.np.mad.recipeheist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private User user = new User();
    private TextView followText, following, noOfRecipes, description, username, email;
    private Bundle user_data = new Bundle();
    private JSONArray recipeJArray, followingArray;
    private View rootView;
    private ProgressBar progressBar;
    private ArrayList<Recipe> recipeList;
    private MainActivity mainActivity;
    private Boolean loadbefore = false;
    private CircleImageView profileImage;
    private Button editProfileBtn;
    private FloatingActionButton addRecipeBtn;
    private SwipeRefreshLayout swipeRefreshLayout;
    private StorageReference storageReference;
    private String dbID;


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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = rootView.findViewById(R.id.profileImage);
        username = rootView.findViewById(R.id.profileName);
        email = rootView.findViewById(R.id.profileEmail);
        description = rootView.findViewById(R.id.profileDescription);
        editProfileBtn = rootView.findViewById(R.id.editProfileBtn);
        addRecipeBtn = rootView.findViewById(R.id.addRecipeBtn);
        noOfRecipes = rootView.findViewById(R.id.noOfRecipes);
        following = rootView.findViewById(R.id.following);
        followText = rootView.findViewById(R.id.followingText);
        progressBar = rootView.findViewById(R.id.progressBarProfile);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);

        mainActivity.setActionBarTitle("Profile");

        if (!loadbefore) {
            addRecipeBtn.setEnabled(false);
            editProfileBtn.setEnabled(false);
            followText.setEnabled(false);
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

                        dbID = (jsonObject.getString("_id"));
                        user.setUserID(jsonObject.getString("userID"));
                        user.setEmail(jsonObject.getString("email"));
                        user.setUsername(jsonObject.getString("username"));
                        user.setDescription(jsonObject.getString("description"));
                        user.setFollowing(convertJArrayToArrayList(jsonObject.getJSONArray("following")));
                        user.setBookmark(convertJArrayToArrayList(jsonObject.getJSONArray("bookmark")));
                        user.setProfileImage(jsonObject.getString("profileimage"));

                        user_data.putParcelable("userData", user);
                        // Remove loading page
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // load profile image
                                if (!user.getProfileImage().equals("") && user.getProfileImage() != null){
                                    updateUserProfileImage();
                                }
                                else{
                                    profileImage.setImageResource(R.drawable.default_profile_1);
                                }
                                username.setText(user.getUsername());
                                email.setText(user.getEmail());
                                description.setText(convertSeparatorToNewLine(user.getDescription()));
                                following.setText(String.valueOf(user.getFollowing().size()));
                                loadbefore = true;
                                try {
                                    getUserRecipes(user.getUserID());
                                    addRecipeBtn.setEnabled(true);
                                    editProfileBtn.setEnabled(true);
                                    followText.setEnabled(true);
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

        // when user clicks on the following text
        followText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pass user data to new activity
                Intent followingActivity = new Intent(getActivity(), MyFollowingActivity.class);
                followingActivity.putExtras(user_data);
                startActivity(followingActivity);
            }
        });

        // when user clicks on "edit profile" button
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // pass user data to new activity
                Intent editProfileActivity = new Intent(getActivity(), EditProfileActivity.class);
                editProfileActivity.putExtras(user_data);
                editProfileActivity.putExtra("database_id", dbID);

                startActivity(editProfileActivity);
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
            username.setText(user.getUsername());
            email.setText(user.getEmail());
            description.setText(convertSeparatorToNewLine(user.getDescription()));
            following.setText(String.valueOf(user.getFollowing().size()));
            // load profile image
            if (!user.getProfileImage().equals("") && user.getProfileImage() != null){
                updateUserProfileImage();
            }
            else{
                profileImage.setImageResource(R.drawable.default_profile_1);
            }
            if (recipeJArray == null){
                Init();
            }
            else {
                getData();
            }
        }
    }

    //menu bar
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.top_nav_profile_menu, menu);
        MenuItem menusettings = menu.findItem(R.id.settingsbtn);
        //for menusettings
        menusettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent settingsintent = new Intent(mainActivity,SettingsActivity.class);
                startActivity(settingsintent);
                return false;
            }
        });
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
            try {
                if (mainActivity.findViewById(R.id.profileFrameLayout) != null) {
                    mainActivity.replaceFragment(myRecipeFragment, R.id.profileFrameLayout);
                }
            }
            catch (Exception e){}
        }
        else {
            progressBar.setVisibility(View.GONE);
            // replace fragment
            try {
               if (mainActivity.findViewById(R.id.profileFrameLayout) != null) {
                    mainActivity.replaceFragment(new NoMyRecipeFragment(), R.id.profileFrameLayout);
                }
            }
            catch (Exception e){}
        }
    }

    // function to get user's recipe from restDB
    public void getUserRecipes(String uid) throws IOException {
        RestDB restDB = new RestDB();
        restDB.asyncGet("https://recipeheist-567c.restdb.io/rest/recipe?q={\"userID\":\"" + uid + "\"}", new SuccessListener() {
            @Override
            public void onSuccess(String jsonresponse) throws JSONException {
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

    // function to get and set user image from firebase
    public void updateUserProfileImage(){
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile_image/"+user.getProfileImage());
        try {
            File localFile = File.createTempFile("tempfile", "jpeg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    Bitmap resizedBM = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                    profileImage.setImageBitmap(resizedBM);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // function to replace "1,3&5!" to \n
    public String convertSeparatorToNewLine(String string){
        return string.replaceAll("1,3&5!", "\n");
    }

}