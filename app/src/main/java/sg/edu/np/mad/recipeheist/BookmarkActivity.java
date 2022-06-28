package sg.edu.np.mad.recipeheist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

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
    private ArrayList<RecipePreview> recipeList;
    private JSONArray recipearray;
    private RecyclerView SavedRView;
    private String query = "";
    private NestedScrollView BmScroll;
    private BookmarkAdapter bookmarkAdapter;
    private ProgressBar BookmarkPB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        //Indentation from browseFragment.
        Intent receive = getIntent();
        getSupportActionBar().setTitle("Bookmark");


        SavedRView = (RecyclerView) findViewById(R.id.SavedRview);
        BmScroll = findViewById(R.id.nestedSV);
        BookmarkPB = findViewById(R.id.savedPB);

        // Grid layout used to have 2 rows for recycler view.
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        SavedRView.setLayoutManager(new GridLayoutManager(this, 2));
        //


        //

        String currentuserjsonstring = getUser(FirebaseAuth.getInstance().getUid());
        JSONObject currentuserobj = null;
        if (currentuserjsonstring != null){
            try {
                JSONArray currentuserarray = new JSONArray(currentuserjsonstring);
                currentuserobj = (JSONObject) currentuserarray.get(0);
                System.out.println(currentuserobj);
                JSONArray bookmarkArray = currentuserobj.getJSONArray("bookmark");
                BookmarkList = null;


                for(int i = 0; i < recipearray.length(); i++){
                    JSONObject recipeobj = (JSONObject) recipearray.get(i);
                    String id = recipeobj.getString("_id");

                    for (int x = 0; x < bookmarkArray.length(); x++) {
                        if (bookmarkArray.get(x).equals(id)){
                            String title = recipeobj.getString("title");
                            String imagePath = recipeobj.getString("imagePath");
                            String duration = recipeobj.getString("duration");

                            BookmarkList.add(new RecipePreview(id, title, imagePath, duration));
                            bookmarkAdapter = new BookmarkAdapter(BookmarkList, new RecipeLoadListener() {
                                @Override
                                public void onLoad(String recipeID) {
                                    goToRecipe(recipeID);
                                }
                            });
                            SavedRView.setAdapter(bookmarkAdapter);
                            if (!needanotherpage){BookmarkPB.setVisibility(View.GONE);}
                        }
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }






    }

    public void searchRecipes(String query, int page) throws IOException, JSONException {
        int skip = perpage * page;
        RestDB restDB = new RestDB();
        restDB.asyncGet("https://recipeheist-567c.restdb.io/rest/recipe?q={\"title\": {\"$regex\" :\"" + query + "\"}}&h={\"$fields\":{\"_id\":1,\"title\":1,\"duration\":1,\"imagePath\":1},\"$max\":"+perpage+",\"$skip\":"+skip+",\"$orderby\":{\"_created\":-1}}",
                new SuccessListener() {
                    @Override
                    public void onSuccess(String jsonresponse) throws JSONException {
                        recipearray = new JSONArray(jsonresponse);
                        if (recipearray.length() == perpage){
                            needanotherpage = true;
                        }
                    }
                }
        );
    }


    //to get default data from rest db
    public void defaultRecipe(int page) throws IOException, JSONException {
        int skip = perpage * page;
        RestDB restDB = new RestDB();
        restDB.asyncGet("https://recipeheist-567c.restdb.io/rest/recipe?h={\"$fields\":{\"_id\":1,\"title\":1,\"duration\":1,\"imagePath\":1},\"$max\":" + perpage + ",\"$skip\":" + skip + ",\"$orderby\":{\"_created\":-1}}",
                new SuccessListener() {
                    @Override
                    public void onSuccess(String jsonresponse) throws JSONException {
                        System.out.println(jsonresponse);
                        recipearray = new JSONArray(jsonresponse);
                        if (recipearray.length() >= perpage){
                            needanotherpage = true;
                        }
                    }
                }
        );
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

    public void goToRecipe(String recipeID)
    {
        BookmarkPB.setVisibility(View.VISIBLE);
        // because sometimes the query from the fragment takes some time to get back
        new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                BookmarkPB.setVisibility(View.GONE);
            }
        }.start();
        Intent intent = new Intent(BookmarkActivity.this, RecipeItem.class);
        intent.putExtra("recipeID", recipeID);
        startActivity(intent);

    }
}