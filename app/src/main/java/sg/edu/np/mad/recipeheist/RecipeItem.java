package sg.edu.np.mad.recipeheist;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.navigation.ui.AppBarConfiguration;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import sg.edu.np.mad.recipeheist.databinding.ActivityRecipeItemBinding;

public class RecipeItem extends AppCompatActivity {

    private ImageView foodimage, profileicon;
    private ImageButton download, like;
    private TextView username, noOfLikes, ingredientitems, instructionitems;
    private CollapsingToolbarLayout collapsing_toolbar;
    private FloatingActionButton bookmarkbtn;

    boolean bookmarkcheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_recipe_item);

        getSupportActionBar().hide();

        foodimage = findViewById(R.id.foodimage);
        profileicon = findViewById(R.id.profileicon);
        download = findViewById(R.id.download);
        like = findViewById(R.id.like);
        username = findViewById(R.id.username);
        noOfLikes = findViewById(R.id.noOfLikes);
        ingredientitems = findViewById(R.id.ingredientitems);
        instructionitems = findViewById(R.id.instructionitems);
        collapsing_toolbar = findViewById(R.id.collapsing_toolbar);
        bookmarkbtn = findViewById(R.id.fab);

        bookmarkcheck = false;

        Intent recievefrombrowse = getIntent();
        String recipeID = recievefrombrowse.getStringExtra("recipeID");
        String response = getRecipe(recipeID);
        JSONObject recipeobj = null;
        //display recipe data
        try {
            recipeobj = new JSONObject(response);

            collapsing_toolbar.setTitle(recipeobj.getString("title"));
            noOfLikes.setText(String.valueOf(recipeobj.getJSONArray("like").length()));

            //Display ingredients
            JSONArray ingredient = recipeobj.getJSONArray("ingredient");
            String ingredientlist = "";
            for (int i = 0; i <ingredient.length(); i++) {
                ingredientlist += "\u2022\t\t";
                ingredientlist += ingredient.get(i).toString();
                ingredientlist += "\n\n";
            }
            ingredientitems.setText(ingredientlist);

            //Display instructions
            JSONArray instruction = recipeobj.getJSONArray("instructions");
            String instructionlist = "";
            for (int i = 0; i <instruction.length(); i++) {
                instructionlist += (i+1) + ".\t\t";
                instructionlist += instruction.get(i).toString();
                instructionlist += "\n\n\n";
            }
            instructionitems.setText(instructionlist);

            //Display image
            String imagefilename = recipeobj.getString("imagePath");
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("Recipe_image/"+imagefilename);
            Glide.with(this)
                    .load(storageReference)
                    .into(foodimage);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //check whether user bookmark this page
        String currentuserjsonstring = getUser(FirebaseAuth.getInstance().getUid());
        JSONObject currentuserobj = null;
        if (currentuserjsonstring != null){
            try {
                JSONArray currentuserarray = new JSONArray(currentuserjsonstring);
                currentuserobj = (JSONObject) currentuserarray.get(0);
                System.out.println(currentuserobj);
                JSONArray bookmarklist = currentuserobj.getJSONArray("bookmark");
                for (int i = 0; i < bookmarklist.length(); i++) {
                    if (bookmarklist.get(i).equals(recipeID)){
                        bookmarkbtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_bookmark_added_24));
                        bookmarkcheck = true;
                        break;
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        //get User profile and display
        try {
            String userjsonstring = getUser(recipeobj.getString("userID"));

            JSONArray userarray = new JSONArray(userjsonstring);
            JSONObject userobj = (JSONObject) userarray.get(0);
            username.setText("@" + userobj.getString("username"));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONObject finalCurrentuserobj = currentuserobj;
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentuserjsonstring != null){
                    if (bookmarkcheck){
                        bookmarkbtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_bookmarks_24));
                        bookmarkcheck = false;
                        try {
                            bookmarkRecipe(recipeID, finalCurrentuserobj, false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(RecipeItem.this, "Bookmark removed", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        bookmarkbtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_bookmark_added_24));
                        bookmarkcheck = true;
                        try {
                            bookmarkRecipe(recipeID, finalCurrentuserobj, true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(RecipeItem.this, "Bookmark added", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(RecipeItem.this, "User need to be registered", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public String getRecipe(String id){
        RestDB example = new RestDB();
        String response = null;
        try {
            response = example.get("https://recipeheist-567c.restdb.io/rest/recipe/" + id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String getUser(String userid){
        //String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        RestDB example = new RestDB();
        String response = null;
        try {
            response = example.get("https://recipeheist-567c.restdb.io/rest/users?q={\"userID\":\"" + userid + "\"}");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public void likeRecipe(String RecipeID) {

    }



    public void bookmarkRecipe(String recipeID, JSONObject currentuserobj0, boolean addorremove) throws IOException, JSONException {
        String restdbuserid = currentuserobj0.getString("_id");
        JSONArray bookmark = currentuserobj0.getJSONArray("bookmark");
        if (addorremove){
            bookmark.put(recipeID);
        }
        else {
            for (int i = 0; i < bookmark.length(); i++) {
                if(bookmark.get(i).equals(recipeID)){
                    bookmark.remove(i);
                    break;
                }

            }

        }


        RestDB restDB = new RestDB();
        String json = restDB.bookmarkRecipe(bookmark);
        String response = restDB.patch("https://recipeheist-567c.restdb.io/rest/users/" + restdbuserid, json);
    }


}