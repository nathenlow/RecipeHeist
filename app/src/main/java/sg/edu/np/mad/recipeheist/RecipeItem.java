package sg.edu.np.mad.recipeheist;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.navigation.ui.AppBarConfiguration;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import sg.edu.np.mad.recipeheist.databinding.ActivityRecipeItemBinding;

public class RecipeItem extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityRecipeItemBinding binding;

    private ImageView foodimage, profileicon;
    private ImageButton download, like;
    private TextView username, noOfLikes, ingredientitems, instructionitems;
    private CollapsingToolbarLayout collapsing_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRecipeItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
                ingredientlist += "\n";
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


        //get User profile and display
        try {
            String userjsonstring = getUser(recipeobj.getString("userID"));

            JSONArray userarray = new JSONArray(userjsonstring);
            JSONObject userobj = (JSONObject) userarray.get(0);
            username.setText(userobj.getString("username"));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {






                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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



    public void bookmarkRecipe(String recipeID) throws IOException, JSONException {
        String userjsonstring = getUser(FirebaseAuth.getInstance().getUid());
        JSONArray userarray = new JSONArray(userjsonstring);
        JSONObject userobj = (JSONObject) userarray.get(0);
        String restdbuserid = userobj.getString("_id");
        JSONArray bookmark = userobj.getJSONArray("bookmark");
        bookmark.put(recipeID);
        RestDB restDB = new RestDB();
        String json = restDB.bookmarkRecipe(bookmark);
        String response = restDB.patch("https://recipeheist-567c.restdb.io/rest/users/" + restdbuserid, json);
    }


}