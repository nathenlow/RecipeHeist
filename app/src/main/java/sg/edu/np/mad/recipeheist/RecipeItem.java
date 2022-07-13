package sg.edu.np.mad.recipeheist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.appcompat.app.AppCompatActivity;

import android.os.CountDownTimer;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class RecipeItem extends AppCompatActivity {

    private static final String SHARED_PREFS = "history";
    private static final String HISTORY = "history";
    private ImageView foodimage, profileicon;
    private ImageButton like;
    private TextView username, noOfLikes, description, servings, duration, foodcategory, ingredientitems, instructionitems;
    private CollapsingToolbarLayout collapsing_toolbar;
    private FloatingActionButton bookmarkbtn;
    String recipeID;
    JSONArray historylist;

    boolean bookmarkcheck;
    boolean likecheck;
    int numlikes;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_recipe_item);

        getSupportActionBar().hide();

        foodimage = findViewById(R.id.foodimage);
        profileicon = findViewById(R.id.profileicon);
        like = findViewById(R.id.like);
        username = findViewById(R.id.username);
        noOfLikes = findViewById(R.id.noOfLikes);
        ingredientitems = findViewById(R.id.ingredientitems);
        instructionitems = findViewById(R.id.instructionitems);
        collapsing_toolbar = findViewById(R.id.collapsing_toolbar);
        bookmarkbtn = findViewById(R.id.fab);
        description = findViewById(R.id.description);
        servings = findViewById(R.id.servings);
        duration = findViewById(R.id.duration);
        foodcategory = findViewById(R.id.foodcategory);

        bookmarkcheck = false;
        likecheck = false;

        //recieve intent
        Intent recievefrombrowse = getIntent();
        recipeID = recievefrombrowse.getStringExtra("recipeID");

        //save history
        try {
            loaddata();
            for (int i = 0; i < historylist.length(); i++) {
                if (historylist.get(i).equals(recipeID)){
                    deletedata(i);
                }
            }
            savedata(recipeID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //create loading page
        new CountDownTimer(1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                findViewById(R.id.loadinglayout).setVisibility(View.GONE);
                findViewById(R.id.fab).setVisibility(View.VISIBLE);
                Init();
            }
        }.start();
    }

    public void Init(){
        //get from restDB
        String response = getRecipe(recipeID);
        JSONObject recipeobj = null;
        //display recipe data
        try {
            recipeobj = new JSONObject(response);

            collapsing_toolbar.setTitle(recipeobj.getString("title"));
            collapsing_toolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
            noOfLikes.setText(String.valueOf(numlikes));
            description.setText(recipeobj.getString("description"));
            servings.setText(recipeobj.getString("servings") + " pax");
            duration.setText(recipeobj.getString("duration"));
            foodcategory.setText("( " + recipeobj.getString("foodcategory") + " )");

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

        String currentuser = FirebaseAuth.getInstance().getUid();
        //check whether user like this recipe
        try {
            getLike(currentuser);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //check whether user bookmark this recipe
        String currentuserjsonstring = getUser(currentuser);
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
            username.setText(userobj.getString("username"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //----On click functions----------------------------------------------------------------------------------------------------------

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentuser = FirebaseAuth.getInstance().getUid();
                if (currentuser != null){
                    new LikeAsync(RecipeItem.this).execute();
                }
                else {
                    Toast.makeText(RecipeItem.this, "Login is required", Toast.LENGTH_SHORT).show();
                }
            }
        });


        JSONObject finalCurrentuserobj = currentuserobj;
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentuser = FirebaseAuth.getInstance().getUid();
                if (currentuser != null){
                    new BookmarkAsync(RecipeItem.this).execute(finalCurrentuserobj);
                }
                else {
                    Snackbar.make(view, "Login is required", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
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
        RestDB example = new RestDB();
        String response = null;
        try {
            response = example.get("https://recipeheist-567c.restdb.io/rest/users?q={\"userID\":\"" + userid + "\"}");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public void getLike(String userid) throws IOException {
        RestDB example = new RestDB();
        String response = null;
        //check whether user liked the recipe
        example.asyncGet("https://recipeheist-567c.restdb.io/rest/like?q={\"userID\":\"" + userid + "\", \"recipeID\": \"" + recipeID + "\"}&totals=true&count=true", new SuccessListener() {
            @Override
            public void onSuccess(String jsonresponse) throws JSONException {
                JSONObject likejsonobj = new JSONObject(jsonresponse);
                JSONObject totaljsonobj = likejsonobj.getJSONObject("totals");
                if (totaljsonobj.getInt("count") != 0) {
                    like.setImageDrawable(getDrawable(R.drawable.ic_baseline_thumb_up_24));
                    likecheck = true;
                }
            }
        });
        //get the numeber of likes in this recipe
        example.asyncGet("https://recipeheist-567c.restdb.io/rest/like?q={\"recipeID\": \"" + recipeID + "\"}&totals=true&count=true", new SuccessListener() {
            @Override
            public void onSuccess(String jsonresponse) throws JSONException {
                JSONObject likejsonobj = new JSONObject(jsonresponse);
                JSONObject totaljsonobj = likejsonobj.getJSONObject("totals");
                numlikes = totaljsonobj.getInt("count");
                noOfLikes.setText(String.valueOf(numlikes));
            }
        });
    }

    //update like data to restdb
    public void likeRecipe(String recipeID, String currentuser, boolean addorremove) throws IOException {
        RestDB restDB = new RestDB();
        if (addorremove) {
            String json = restDB.likeRecipe(recipeID, currentuser);
            String response = restDB.post("https://recipeheist-567c.restdb.io/rest/like/" + recipeID, json);
        }
        else {
            String response = restDB.delete("https://recipeheist-567c.restdb.io/rest/like/*?q={\"recipeID\":\"" + recipeID + "\",\"userID\":\"" + currentuser + "\"}");
        }

    }


    //update bookmark data to restdb
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

    private static class BookmarkAsync extends AsyncTask<JSONObject,Void,Void>{
        private WeakReference<RecipeItem> activityWeakReference;

        BookmarkAsync(RecipeItem activity){
            activityWeakReference = new WeakReference<RecipeItem>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            RecipeItem activity = activityWeakReference.get();
            activity.findViewById(R.id.fab).setEnabled(false);
        }

        protected Void doInBackground(JSONObject... currentuserobjs) {
            RecipeItem activity = activityWeakReference.get();
            //remove a bookmark
            if (activity.bookmarkcheck){
                activity.bookmarkbtn.setImageDrawable(activity.getDrawable(R.drawable.ic_baseline_bookmarks_24));
                activity.bookmarkcheck = false;
                try {
                    activity.bookmarkRecipe(activity.recipeID, currentuserobjs[0], false);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //add a bookmark
            else{
                activity.bookmarkbtn.setImageDrawable(activity.getDrawable(R.drawable.ic_baseline_bookmark_added_24));
                activity.bookmarkcheck = true;
                try {
                    activity.bookmarkRecipe(activity.recipeID, currentuserobjs[0], true);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

            protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            RecipeItem activity = activityWeakReference.get();
            activity.findViewById(R.id.fab).setEnabled(true);
        }
    }


    //like Async
    private static class LikeAsync extends AsyncTask<Void,Void,Void>{
        private WeakReference<RecipeItem> activityWeakReference;

        LikeAsync(RecipeItem activity){
            activityWeakReference = new WeakReference<RecipeItem>(activity);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            RecipeItem activity = activityWeakReference.get();
            activity.findViewById(R.id.like).setEnabled(false);
        }
        @Override
        protected Void doInBackground(Void... voids) {
            RecipeItem activity = activityWeakReference.get();
            String currentuser = FirebaseAuth.getInstance().getUid();
            // remove a like
            if (activity.likecheck){
                activity.like.setImageDrawable(activity.getDrawable(R.drawable.ic_outline_thumb_up_off_alt_24));
                activity.likecheck = false;
                activity.numlikes -= 1;
                try {
                    activity.likeRecipe(activity.recipeID,currentuser, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //add a like
            else {
                activity.like.setImageDrawable(activity.getDrawable(R.drawable.ic_baseline_thumb_up_24));
                activity.likecheck = true;
                activity.numlikes += 1;
                try {
                    activity.likeRecipe(activity.recipeID,currentuser, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            RecipeItem activity = activityWeakReference.get();
            activity.findViewById(R.id.like).setEnabled(true);
            activity.noOfLikes.setText(String.valueOf(activity.numlikes));
        }
    }

    //sharedprefrences methods
    public void savedata(String newRecipeID) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        historylist.put(newRecipeID.trim());
        editor.putString(HISTORY, historylist.toString());
        editor.apply();
    }

    public void loaddata() throws JSONException {
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String historyString = sharedPreferences.getString(HISTORY, "[]");
        historylist = new JSONArray(historyString);
    }

    public void deletedata(int position){
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        historylist.remove(position);
        editor.putString(HISTORY, historylist.toString());
        editor.apply();
    }



}
