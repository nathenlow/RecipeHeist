package sg.edu.np.mad.recipeheist;

import android.annotation.SuppressLint;
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
import com.r0adkll.slidr.Slidr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import sg.edu.np.mad.recipeheist.adapter.BrowseAdapter;
import sg.edu.np.mad.recipeheist.adapter.InstructionViewAdapter;

public class RecipeItem extends AppCompatActivity {

    private static final String SHARED_PREFS = "history";
    private static final String HISTORY = "history";
    private ImageView foodimage, profileicon;
    private ImageButton like;
    private TextView username, noOfLikes, description, servings, duration, foodcategory, ingredientitems;
    private RecyclerView instructionitems;
    private CollapsingToolbarLayout collapsing_toolbar;
    private FloatingActionButton bookmarkbtn;
    private CoordinatorLayout recipeItemMainContent;
    String recipeID;
    JSONArray historylist;

    boolean bookmarkcheck;
    boolean likecheck;
    int numlikes;


    @Override
    public void onBackPressed() {
        Thread.interrupted();
        finish();
        super.onBackPressed();
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // to enable swipe back to browse fragment
        Slidr.attach(this);

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
        recipeItemMainContent = findViewById(R.id.recipeItemMainContent);

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
                //get data and display
                Init();
            }
        }.start();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void Init(){
        //get from restDB
        String response = getRecipe(recipeID);
        JSONObject recipeobj = null;
        //display recipe data
        try {
            recipeobj = new JSONObject(response);

            //display data to view
            collapsing_toolbar.setTitle(recipeobj.getString("title"));
            collapsing_toolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
            noOfLikes.setText(String.valueOf(numlikes));
            String descriptionstring = convertSeparatorToNewLine(recipeobj.getString("description"));
            description.setText(descriptionstring);
            servings.setText(recipeobj.getString("servings") + " pax");
            duration.setText(recipeobj.getString("duration"));
            foodcategory.setText("( " + recipeobj.getString("foodcategory") + " )");

            //Display ingredients
            JSONArray ingredient = recipeobj.getJSONArray("ingredient");
            String ingredientlist = "";
            for (int i = 0; i < ingredient.length(); i++) {
                ingredientlist += "\u2022\t\t";
                ingredientlist += ingredient.get(i).toString();
                ingredientlist += "\n\n";
            }
            ingredientitems.setText(ingredientlist);


            //Display instructions
            JSONArray instructionlist = recipeobj.getJSONArray("instructions");
            InstructionViewAdapter instructionViewAdapter = new InstructionViewAdapter(this, instructionlist);
            instructionitems.setAdapter(instructionViewAdapter);
            instructionitems.setNestedScrollingEnabled(false);
            instructionitems.setLayoutManager(new LinearLayoutManager(this));


            //Display image
            String imagefilename = recipeobj.getString("imagePath");
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("Recipe_image/"+imagefilename);
            Glide.with(getApplicationContext())
                    .load(storageReference)
                    .into(foodimage);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        String currentuser = FirebaseAuth.getInstance().getUid();

        new Thread() {
            public void run() {
                //check whether current user like this recipe
                new getLike(RecipeItem.this).execute(currentuser);
            }
        }.start();


        JSONObject[] currentuserobj = {null};
        new Thread() {
            public void run() {
                //check whether current user bookmark this recipe
                String currentuserjsonstring = getUser(currentuser);
                if (currentuserjsonstring != null) {
                    try {
                        JSONArray currentuserarray = new JSONArray(currentuserjsonstring);
                        currentuserobj[0] = (JSONObject) currentuserarray.get(0);
                        JSONArray bookmarklist = currentuserobj[0].getJSONArray("bookmark");
                        for (int i = 0; i < bookmarklist.length(); i++) {
                            if (bookmarklist.get(i).equals(recipeID)) {
                                bookmarkcheck = true;
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bookmarkcheck) {
                            bookmarkbtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_bookmark_added_24));
                        }
                        bookmarkbtn.setEnabled(true);
                    }
                });
            }
        }.start();

        JSONObject finalRecipeobj = recipeobj;
        new Thread() {
            public void run() {
                //get User (chef) profile and display
                try {
                    String userjsonstring = getUser(finalRecipeobj.getString("userID"));
                    JSONArray userarray = new JSONArray(userjsonstring);
                    JSONObject userobj = (JSONObject) userarray.get(0);

                    String profileImagePath = userobj.getString("profileimage");
                    System.out.println(profileImagePath);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                username.setText(userobj.getString("username"));
                                StorageReference storageReference = FirebaseStorage.getInstance().getReference("Profile_image/"+profileImagePath);
                                if (!profileImagePath.equals("")) {
                                    Glide.with(getApplicationContext())
                                            .load(storageReference)
                                            .into(profileicon);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        //----On click functions----------------------------------------------------------------------------------------------------------

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentuser = FirebaseAuth.getInstance().getUid();
                //check whether user login
                if (currentuser != null){
                    new LikeAsync(RecipeItem.this).execute();
                }
                else {
                    Toast.makeText(RecipeItem.this, "Login is required", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentuser = FirebaseAuth.getInstance().getUid();
                //check whether user login
                if (currentuser != null) {
                    new BookmarkAsync(RecipeItem.this).execute(currentuserobj[0]);
                } else {
                    Snackbar.make(view, "Login is required", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });

        // to allow swipe gesture
        recipeItemMainContent.setOnTouchListener(new OnSwipeTouchListener(RecipeItem.this){
            public void onSwipeLeft(){
                Intent intent = new Intent(RecipeItem.this, CountdownTimerActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    //get recipe data from restDB
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


    //get user data from restDB
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

    private static class getLike extends AsyncTask<String,Void,Void>{
        private WeakReference<RecipeItem> activityWeakReference;

        getLike(RecipeItem activity) {
            activityWeakReference = new WeakReference<RecipeItem>(activity);
        }

        @Override
        protected Void doInBackground(String... userid) {
            RecipeItem activity = activityWeakReference.get();
            RestDB example = new RestDB();
            //check whether user liked the recipe
            try {
                String jsonresponse0 = example.get("https://recipeheist-567c.restdb.io/rest/like?q={\"userID\":\"" + userid[0] + "\", \"recipeID\": \"" + activity.recipeID + "\"}&totals=true&count=true");
                JSONObject likejsonobj0 = new JSONObject(jsonresponse0);
                JSONObject totaljsonobj0 = likejsonobj0.getJSONObject("totals");
                if (totaljsonobj0.getInt("count") != 0) {
                    activity.likecheck = true;
                }
                //get the numeber of likes in this recipe
                String jsonresponse1 = example.get("https://recipeheist-567c.restdb.io/rest/like?q={\"recipeID\": \"" + activity.recipeID + "\"}&totals=true&count=true");
                JSONObject likejsonobj1 = new JSONObject(jsonresponse1);
                JSONObject totaljsonobj1 = likejsonobj1.getJSONObject("totals");
                activity.numlikes = totaljsonobj1.getInt("count");
            }
            catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            RecipeItem activity = activityWeakReference.get();
            if (activity.likecheck){
                activity.like.setImageDrawable(activity.getDrawable(R.drawable.ic_baseline_thumb_up_24));
            }
            activity.noOfLikes.setText(String.valueOf(activity.numlikes));
            activity.like.setEnabled(true);
        }
    }

    //update like data to restdb
    public void likeRecipe(String recipeID, String currentuser, boolean addorremove) throws IOException {
        RestDB restDB = new RestDB();
        //if add
        if (addorremove) {
            String json = restDB.likeRecipe(recipeID, currentuser);
            String response = restDB.post("https://recipeheist-567c.restdb.io/rest/like/" + recipeID, json);
        }
        //if remove
        else {
            String response = restDB.delete("https://recipeheist-567c.restdb.io/rest/like/*?q={\"recipeID\":\"" + recipeID + "\",\"userID\":\"" + currentuser + "\"}");
        }

    }


    //update bookmark data to restdb
    public void bookmarkRecipe(String recipeID, JSONObject currentuserobj0, boolean addorremove) throws IOException, JSONException {
        String restdbuserid = currentuserobj0.getString("_id");
        JSONArray bookmark = currentuserobj0.getJSONArray("bookmark");
        //if add
        if (addorremove){
            bookmark.put(recipeID);
        }
        //if remove
        else {
            for (int i = 0; i < bookmark.length(); i++) {
                if(bookmark.get(i).equals(recipeID)){
                    bookmark.remove(i);
                    break;
                }

            }
        }

        //patch data to restDB
        RestDB restDB = new RestDB();
        String json = restDB.bookmarkRecipe(bookmark);
        String response = restDB.patch("https://recipeheist-567c.restdb.io/rest/users/" + restdbuserid, json);
    }

    //add or remove a bookmark running in background
    private static class BookmarkAsync extends AsyncTask<JSONObject,Void,Void>{
        private WeakReference<RecipeItem> activityWeakReference;

        BookmarkAsync(RecipeItem activity){
            activityWeakReference = new WeakReference<RecipeItem>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            RecipeItem activity = activityWeakReference.get();
            //disable btn
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
            //enable btn
            activity.findViewById(R.id.fab).setEnabled(true);
        }
    }


    //add or remove a like running in background
    private static class LikeAsync extends AsyncTask<Void,Void,Void>{
        private WeakReference<RecipeItem> activityWeakReference;

        LikeAsync(RecipeItem activity){
            activityWeakReference = new WeakReference<RecipeItem>(activity);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            RecipeItem activity = activityWeakReference.get();
            //disable btn
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
            //enable btn
            activity.findViewById(R.id.like).setEnabled(true);
            //update the number of likes
            activity.noOfLikes.setText(String.valueOf(activity.numlikes));
        }
    }

    //sharedprefrences methods to save history data
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

    // function to replace "1,3&5!" to \n
    public String convertSeparatorToNewLine(String string){
        return string.replaceAll("1,3&5!", "\n");
    }

}
