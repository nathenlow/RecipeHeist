package sg.edu.np.mad.recipeheist;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import sg.edu.np.mad.recipeheist.adapter.BookmarkAdapter;
import sg.edu.np.mad.recipeheist.adapter.BrowseAdapter;

public class BookmarkActivity extends AppCompatActivity {

    
    private ArrayList<RecipePreview> recipelist = new ArrayList<>();
    private RecyclerView RView;
    private BrowseAdapter browseAdapter;
    private ProgressBar PBLoading;
    private NestedScrollView nestedSV;
    private JSONArray recipearray;
    private SwipeRefreshLayout swipeRefreshLayout;
    String userID;
    RestDB restDB = new RestDB();

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setTitle("Bookmark");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        RView = findViewById(R.id.RView);
        PBLoading = findViewById(R.id.PBLoading);
        nestedSV = findViewById(R.id.nestedSV);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        userID = mAuth.getUid();
        
        getBookmark();

        // grid layout splitting display into two columns
        int columns = 2;
        GridLayoutManager manager = new GridLayoutManager(this, columns);
        RView.addItemDecoration(new GridSpacingItemDecoration(columns, 12, false));
        RView.setLayoutManager(manager);

        //refresh page by using Init()
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(BookmarkActivity.this, "Reloading page", Toast.LENGTH_SHORT).show();
                Init();
            }
        });

    }

    //to initialize the page
    public void Init(){
        recipelist = new ArrayList<>();
        getBookmark();
    }

    //to get default bookmark recipe data from rest db
    public void getBookmark(){
        try {
            PBLoading.setVisibility(View.VISIBLE);
            restDB.asyncGet("https://recipeheist-567c.restdb.io/rest/users?q={\"userID\":\"" + userID + "\"}&h={\"$fields\":{\"bookmark\":1}}", new SuccessListener() {
                @Override
                public void onSuccess(String jsonresponse){
                    try {
                        //get json array(bookmarks from user)
                        JSONArray followingResponseArray = new JSONArray(jsonresponse);
                        JSONObject jsonObject = (JSONObject) followingResponseArray.get(0);
                        JSONArray bookmarkarray = jsonObject.getJSONArray("bookmark");
                        //get recipes of bookmark

                        getRecipe(bookmarkarray);
                    }catch (IOException | JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //get recipes of bookmark 
    public void getRecipe(JSONArray jsonArray) throws IOException {
        restDB.asyncGet("https://recipeheist-567c.restdb.io/rest/recipe?q={\"_id\":{\"$in\":" + jsonArray +"}}&h={\"$fields\":{\"_id\":1,\"title\":1,\"duration\":1,\"imagePath\":1},\"$orderby\":{\"_created\":-1}}",
                new SuccessListener() {
                    @Override
                    public void onSuccess(String jsonresponse) throws JSONException {
                        //if response is successful
                        if (jsonresponse != null) {
                            recipearray = new JSONArray(jsonresponse);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        //display data
                                        getData();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        //if response is unsuccessful
                        else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(BookmarkActivity.this, "Check your Internet connection", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
        );
    }


    //to display recipes from restdb jsonarray
    public void getData() throws JSONException {
        for (int i = 0; i < recipearray.length(); i++) {
            //to convert json object into RecipePreview object to pass to recycler view
            JSONObject recipeobj = (JSONObject) recipearray.get(i);
            String id = recipeobj.getString("_id");
            String title = recipeobj.getString("title");
            String imagePath = recipeobj.getString("imagePath");
            String duration = recipeobj.getString("duration");
            RecipePreview recipePreview = new RecipePreview(id, title, imagePath, duration);

            recipelist.add(recipePreview);
            //load recipelist into recyclerview
            browseAdapter = new BrowseAdapter(this, recipelist, new RecipeLoadListener() {
                @Override
                public void onLoad(String recipeID) {
                    goToRecipe(recipeID);
                }
            });
            RView.setAdapter(browseAdapter);
        }
        PBLoading.setVisibility(View.GONE);
    }

    // go to recipe page
    public void goToRecipe(String recipeID)
    {
        Intent intent = new Intent(this, RecipeItem.class);
        intent.putExtra("recipeID", recipeID);
        this.startActivity(intent);
    }
}