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

    private int pagecount = 0;
    boolean needanotherpage;
    private int perpage = 10;
    private ArrayList<RecipePreview> BookmarkList = new ArrayList<>();
    private JSONArray BookmarkID;
    private User user = new User();
    private JSONArray recipearray;
    private RecyclerView SavedRView;
    private String query = "";
    private boolean loadbefore = false;
    private NestedScrollView BmScroll;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BookmarkAdapter bookmarkAdapter;
    private ProgressBar BookmarkPB;
    private NestedScrollView nestedSV;

    private BrowseAdapter browseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setTitle("Bookmark");

        //Assigning values
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        SavedRView = (RecyclerView) findViewById(R.id.SavedRview);
        BmScroll = findViewById(R.id.nestedSV);
        BookmarkPB = findViewById(R.id.savedPB);
        nestedSV = findViewById(R.id.savedSV);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        String uid = mAuth.getUid();

        JSONObject[] currentuserobj = {null};

        String currentuserjsonstring = getUser(uid);
        if (currentuserjsonstring != null) {
            try {
                JSONArray currentuserarray = new JSONArray(currentuserjsonstring);
                currentuserobj[0] = (JSONObject) currentuserarray.get(0);
                BookmarkID = currentuserobj[0].getJSONArray("bookmark");
                startFunction();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //if (!loadbefore){
        //    loadbefore = true;
        //    startFunction();
        //}



        // Grid layout used to have 2 rows for recycler view.
        int columns = 2;
        GridLayoutManager manager = new GridLayoutManager(BookmarkActivity.this,columns);
        SavedRView.addItemDecoration(new GridSpacingItemDecoration(columns, 12, false));
        SavedRView.setLayoutManager(manager);
        /*
        nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView scrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

                // if diff is zero, then the bottom has been reached
                if (diff == 0) {
                    startFunction();
                }
            }
        });*/

        //refresh page by using Init()
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getApplicationContext(), "Reloading page", Toast.LENGTH_SHORT).show();
                Init();
            }
        });

    }

    public void Init(){
        pagecount = 0;

    }

    public void startFunction(){
        needanotherpage = false;
        BookmarkPB.setVisibility(View.VISIBLE);
        try {
            defaultRecipe();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //to get default data from rest db
    public void defaultRecipe() throws IOException, JSONException {


        RestDB restDB = new RestDB();
        restDB.asyncGet("https://recipeheist-567c.restdb.io/rest/recipe?h=",
                new SuccessListener() {
                    @Override
                    public void onSuccess(String jsonresponse) throws JSONException {
                        //if response is successful
                        if (jsonresponse != null) {
                            recipearray = new JSONArray(jsonresponse);
                            //check whether if all the recipes had been gotten so that we will not send another api request

                            try {
                                getData();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //if response is unsuccessful
                        else {
                            Toast.makeText(getApplicationContext(),"Check your Internet connection", Toast.LENGTH_SHORT).show();
                        }

                        //remove loading bar if user have already loaded all the recipes
                        BookmarkPB.setVisibility(View.GONE);
                    }
                }
        );
    }

    public void getData() throws JSONException {
        for (int i = 0; i < recipearray.length(); i++) {
            JSONObject recipeobj = (JSONObject) recipearray.get(i);
            String id = recipeobj.getString("_id");
            if (BookmarkID.length()!= 0){
                for(int x = 0; x < BookmarkID.length(); i++){
                    if(BookmarkID.get(x).equals(id)){
                        String title = recipeobj.getString("title");
                        String imagePath = recipeobj.getString("imagePath");
                        String duration = recipeobj.getString("duration");
                        RecipePreview recipePreview = new RecipePreview(id, title, imagePath, duration);

                        BookmarkList.add(recipePreview);

                        if(BookmarkList.size() != 0){
                            browseAdapter = new BrowseAdapter(BookmarkActivity.this, BookmarkList, new RecipeLoadListener(){
                                @Override
                                public void onLoad(String recipeID) {goToRecipe(recipeID);}
                            });
                            SavedRView.setAdapter(browseAdapter);
                        }
                    }
                }
            }


        }

    }

    public void goToRecipe(String recipeID)
    {

        Intent intent = new Intent(BookmarkActivity.this, RecipeItem.class);
        intent.putExtra("recipeID", recipeID);
        startActivity(intent);

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
}