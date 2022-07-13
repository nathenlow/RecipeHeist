package sg.edu.np.mad.recipeheist;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import sg.edu.np.mad.recipeheist.adapter.BrowseAdapter;


public class HistoryFragment extends Fragment {

    private static final String SHARED_PREFS = "history";
    private static final String HISTORY = "history";
    private int lastpage;
    boolean needanotherpage;
    boolean loadbefore = false;
    private int perpage = 10;
    private ArrayList<RecipePreview> recipelist = new ArrayList<>();
    private RecyclerView RView;
    private BrowseAdapter browseAdapter;
    private ProgressBar PBLoading;
    private NestedScrollView nestedSV;
    private JSONArray recipearray;
    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    MainActivity mainActivity;
    JSONArray historylist;
    

    public HistoryFragment() {
        // Required empty public constructor
    }


    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();

        if (!loadbefore) {
            try {
                loaddata();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            lastpage = historylist.length() - 1;
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_browse, container, false);


        //change action bar back to default
        mainActivity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        mainActivity.setActionBarTitle("History");
        RView = rootView.findViewById(R.id.RView);
        PBLoading = rootView.findViewById(R.id.PBLoading);
        nestedSV = rootView.findViewById(R.id.nestedSV);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);

        if (!loadbefore) {
            loadbefore = true;
            startRecipieGet();
        }

        // grid layout splitting display into two columns
        int columns = 2;
        GridLayoutManager manager = new GridLayoutManager(mainActivity, columns);
        RView.addItemDecoration(new GridSpacingItemDecoration(columns, 12, false));
        RView.setLayoutManager(manager);
        nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView scrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

                if (needanotherpage){
                    // if diff is zero, then the bottom has been reached
                    if (diff == 0) {
                        startRecipieGet();
                    }
                }
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

        browseAdapter = new BrowseAdapter(mainActivity, recipelist, new RecipeLoadListener() {
            @Override
            public void onLoad(String recipeID) {
                goToRecipe(recipeID);
            }
        });
        RView.setAdapter(browseAdapter);
        if (!needanotherpage){PBLoading.setVisibility(View.GONE);}
    }

    public void Init(){
        mainActivity.historyFragment = new HistoryFragment();
        mainActivity.replaceFragment(mainActivity.historyFragment, R.id.frameLayout);
    }

    //combine all the fuctions for get from restdb
    public  void startRecipieGet(){
        needanotherpage = false;
        PBLoading.setVisibility(View.VISIBLE);
        try {
            recentlyViewedRecipe();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //to get recentlyViewedRecipedata from rest db
    public void recentlyViewedRecipe() throws IOException, JSONException {
        RestDB restDB = new RestDB();
        int firstpage = lastpage;
        JSONArray querylist = new JSONArray();
        if (lastpage - perpage <= -1){
            needanotherpage = false;
            lastpage = -1;
        }
        else {
            lastpage -= perpage;
            needanotherpage = true;
        }
        for (int i = firstpage; i > lastpage; i--) {
            querylist.put(historylist.get(i));
        }
        restDB.asyncGet("https://recipeheist-567c.restdb.io/rest/recipe?q={\"_id\":{\"$in\":" + querylist.toString() + "}}&h={\"$fields\":{\"_id\":1,\"title\":1,\"duration\":1,\"imagePath\":1}}",
                new SuccessListener() {
                    @Override
                    public void onSuccess(String jsonresponse) throws JSONException {
                        if (jsonresponse != null) {
                            recipearray = new JSONArray(jsonresponse);
                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        getData(querylist);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        else {
                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mainActivity, "Check your Internet connection", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
        );
    }

    //to convert json object into RecipePreview object to pass to recycler view
    public void getData(JSONArray querylist) throws JSONException {

        ArrayList<RecipePreview> tempRecipeList = new ArrayList<>();
        for (int i = 0; i < recipearray.length(); i++) {
            JSONObject recipeobj = (JSONObject) recipearray.get(i);
            String id = recipeobj.getString("_id");
            String title = recipeobj.getString("title");
            String imagePath = recipeobj.getString("imagePath");
            String duration = recipeobj.getString("duration");
            RecipePreview recipePreview = new RecipePreview(id, title, imagePath, duration);
            //In case a new recipe comes out, resulting a Recipe that is already displayed to move to the next page. Causing a duplicate display.
            if (!recipelist.contains(recipePreview)){
                tempRecipeList.add(new RecipePreview(id, title, imagePath, duration));
            }
            if (!needanotherpage){PBLoading.setVisibility(View.GONE);}
        }
        sort(querylist, tempRecipeList);
        browseAdapter = new BrowseAdapter(mainActivity, recipelist, new RecipeLoadListener() {
            @Override
            public void onLoad(String recipeID) {
                goToRecipe(recipeID);
            }
        });
        RView.setAdapter(browseAdapter);
    }

    public void sort(JSONArray querylist, ArrayList<RecipePreview> tempRecipeList) throws JSONException {
        for (int j = 0; j < querylist.length() ; j++) {
            for (int i = 0; i < tempRecipeList.size(); i++) {
                RecipePreview recipeobj = tempRecipeList.get(i);
                if (recipeobj.getId().equals(querylist.get(j))) {
                    recipelist.add(recipeobj);
                    break;
                }
            }
        }
    }

    //sharedprefrences methods
    //load history
    public void loaddata() throws JSONException {
        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String historyString = sharedPreferences.getString(HISTORY, "[]");
        historylist = new JSONArray(historyString);
    }
    

    // go to recipe page
    public void goToRecipe(String recipeID)
    {
        Intent intent = new Intent(mainActivity, RecipeItem.class);
        intent.putExtra("recipeID", recipeID);
        mainActivity.startActivity(intent);

    }
}