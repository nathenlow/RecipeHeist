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
    private ArrayList<RecipePreview> BookmarkList;
    private ArrayList<String> BookmarkID;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        //Indentation from browseFragment.
        Intent receive = getIntent();
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

        RestDB restDB = new RestDB();
        try {
            restDB.asyncGet("https://recipeheist-567c.restdb.io/rest/users?q={\"userID\":\"" + uid + "\"}", new SuccessListener() {
                @Override
                public void onSuccess(String jsonresponse) throws JSONException {
                    String dataBaseUsers = jsonresponse;
                    dataBaseUsers = dataBaseUsers.substring(1, dataBaseUsers.length() - 1);

                    JSONObject jsonObject = new JSONObject(dataBaseUsers);
                    user.setUserID(jsonObject.getString("userID"));
                    user.setEmail(jsonObject.getString("email"));
                    user.setUsername(jsonObject.getString("username"));
                    user.setDescription(jsonObject.getString("description"));
                    user.setFollowing(convertJArrayToArrayList(jsonObject.getJSONArray("following")));
                    user.setBookmark(convertJArrayToArrayList(jsonObject.getJSONArray("bookmark")));
                    user.setProfileImage(jsonObject.getString("profileimage"));

                    BookmarkID = user.getBookmark();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!loadbefore){
            loadbefore = true;
            startFunction();
        }
        // because sometimes the query from the fragment takes some time to get back
        new CountDownTimer(1000, 1000) {

            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                startFunction();
            }
        }.start();



        // Grid layout used to have 2 rows for recycler view.
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        SavedRView.addItemDecoration(new GridSpacingItemDecoration(2, 12, false));
        SavedRView.setLayoutManager(manager);

        SavedRView.setLayoutManager(new GridLayoutManager(this, 2));

        nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView scrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

                if (needanotherpage){
                    // if diff is zero, then the bottom has been reached
                    if (diff == 0) {
                        startFunction();
                    }
                }
            }
        });

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

    @Override
    public void onResume() {
        super.onResume();
        //load the recipelist into the recycler view only when fragment have been used before
        if (loadbefore) {
            bookmarkAdapter = new BookmarkAdapter(BookmarkActivity.this, BookmarkList, new RecipeLoadListener() {
                @Override
                public void onLoad(String recipeID) {
                    goToRecipe(recipeID);
                }
            });
            SavedRView.setAdapter(bookmarkAdapter);
            if (!needanotherpage) {
                BookmarkPB.setVisibility(View.GONE);
            }
        }
    }

    public void Init(){
        pagecount = 0;
        BookmarkList = new ArrayList<>();
        startFunction();

    }

    public void startFunction(){
        needanotherpage = false;
        BookmarkPB.setVisibility(View.VISIBLE);
        try {
            defaultRecipe(pagecount);
            pagecount += 1;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //to get default data from rest db
    public void defaultRecipe(int page) throws IOException, JSONException {
        //skip is the number of recipes that will be skipped when getting data from restdb
        int skip = perpage * page;
        RestDB restDB = new RestDB();
        restDB.asyncGet("https://recipeheist-567c.restdb.io/rest/recipe?h={\"$fields\":{\"_id\":1,\"title\":1,\"duration\":1,\"imagePath\":1},\"$max\":" + perpage + ",\"$skip\":" + skip + ",\"$orderby\":{\"_created\":-1}}",
                new SuccessListener() {
                    @Override
                    public void onSuccess(String jsonresponse) throws JSONException {
                        //if response is successful
                        if (jsonresponse != null) {
                            recipearray = new JSONArray(jsonresponse);
                            //check whether if all the recipes had been gotten so that we will not send another api request
                            if (recipearray.length() >= perpage) {
                                needanotherpage = true;
                            }
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
                    }
                }
        );
    }

    public void getData() throws JSONException {
        for (int i = 0; i < recipearray.length(); i++) {
            JSONObject recipeobj = (JSONObject) recipearray.get(i);
            String id = recipeobj.getString("_id");

            for(int x = 0; x < BookmarkID.size(); i++){
                if(id == BookmarkID.get(x)){
                    String title = recipeobj.getString("title");
                    String imagePath = recipeobj.getString("imagePath");
                    String duration = recipeobj.getString("duration");
                    RecipePreview recipePreview = new RecipePreview(id, title, imagePath, duration);

                    BookmarkList.add(recipePreview);

                    bookmarkAdapter = new BookmarkAdapter(BookmarkActivity.this, BookmarkList, new RecipeLoadListener(){
                        @Override
                        public void onLoad(String recipeID) {goToRecipe(recipeID);}
                    });
                    SavedRView.setAdapter(bookmarkAdapter);
                }
            }

        }
        //remove loading bar if user have already loaded all the recipes
        if (!needanotherpage){BookmarkPB.setVisibility(View.GONE);}
    }



    public void goToRecipe(String recipeID)
    {

        Intent intent = new Intent(BookmarkActivity.this, RecipeItem.class);
        intent.putExtra("recipeID", recipeID);
        startActivity(intent);

    }

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