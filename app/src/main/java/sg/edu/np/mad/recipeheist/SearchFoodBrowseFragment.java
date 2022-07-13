package sg.edu.np.mad.recipeheist;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.CountDownTimer;
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


public class SearchFoodBrowseFragment extends Fragment {

    private int pagecount = 0;
    boolean needanotherpage;
    private int perpage = 10;
    private ArrayList<RecipePreview> recipelist;
    private String query;
    private RecyclerView RView;
    private BrowseAdapter browseAdapter;
    private ProgressBar PBLoading;
    private NestedScrollView nestedSV;
    private JSONArray recipearray;
    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;

    MainActivity mainActivity;

    public SearchFoodBrowseFragment() {
        // Required empty public constructor
    }

    public static SearchFoodBrowseFragment newInstance() {
        SearchFoodBrowseFragment fragment = new SearchFoodBrowseFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        mainActivity.showbottomnav(true);
        setHasOptionsMenu(true);

        //get data from search
        getParentFragmentManager().setFragmentResultListener("search", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                onStart();
                query = result.getString("query");
                mainActivity.getSupportActionBar().setTitle(query);
                startSearch();
            }
        });
    }

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_browse, container, false);


        //change action bar back to default
        mainActivity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        recipelist = new ArrayList<>();
        RView = rootView.findViewById(R.id.RView);
        PBLoading = rootView.findViewById(R.id.PBLoading);
        nestedSV = rootView.findViewById(R.id.nestedSV);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);


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
                        startSearch();
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


    //menu bar
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.top_nav_browse_menu, menu);
        MenuItem menusearch = menu.findItem(R.id.app_bar_search);
        MenuItem menubookmark = menu.findItem(R.id.bookmarkbtn);
        //for search
        menusearch.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mainActivity.replaceFragment(new SearchFoodFragment(), R.id.frameLayout);
                return false;
            }
        });
        //for bookmark
        menubookmark.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent bookmarkIntent = new Intent(getActivity(), BookmarkActivity.class);
                startActivity(bookmarkIntent);
                return false;
            }
        });

    }

    public void Init(){
        pagecount = 0;
        recipelist = new ArrayList<>();
        startSearch();
    }

    //combine all the fuctions to the search results from restdb
    public  void startSearch(){
        needanotherpage = false;
        PBLoading.setVisibility(View.VISIBLE);
        try {
            searchRecipes(query,pagecount);
            pagecount += 1;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // to get query data from rest db
    public void searchRecipes(String query, int page) throws IOException, JSONException {
        int skip = perpage * page;
        RestDB restDB = new RestDB();
        restDB.asyncGet("https://recipeheist-567c.restdb.io/rest/recipe?q={\"title\": {\"$regex\" :\"" + query + "\"}}&h={\"$fields\":{\"_id\":1,\"title\":1,\"duration\":1,\"imagePath\":1},\"$max\":"+perpage+",\"$skip\":"+skip+",\"$orderby\":{\"_created\":-1}}",
                new SuccessListener() {
                    @Override
                    public void onSuccess(String jsonresponse) throws JSONException {
                        if (jsonresponse != null){
                            recipearray = new JSONArray(jsonresponse);
                            if (recipearray.length() == 0){
                                needanotherpage = false;
                                mainActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        PBLoading.setVisibility(View.GONE);
                                    }
                                });
                            }
                            else{
                                if (recipearray.length() == perpage){
                                    needanotherpage = true;
                                }
                                mainActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            getData();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                        else {
                            Toast.makeText(mainActivity, "Check your Internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }


    //to convert json object into RecipePreview object to pass to recycler view
    public void getData() throws JSONException {
        for (int i = 0; i < recipearray.length(); i++) {
            JSONObject recipeobj = (JSONObject) recipearray.get(i);
            String id = recipeobj.getString("_id");
            String title = recipeobj.getString("title");
            String imagePath = recipeobj.getString("imagePath");
            String duration = recipeobj.getString("duration");
            RecipePreview recipePreview = new RecipePreview(id, title, imagePath, duration);
            //In case a new recipe comes out, resulting a Recipe that is already displayed to move to the next page. Causing a duplicate display.
            if (!recipelist.contains(recipePreview)){
                recipelist.add(new RecipePreview(id, title, imagePath, duration));
                browseAdapter = new BrowseAdapter(mainActivity, recipelist, new RecipeLoadListener() {
                    @Override
                    public void onLoad(String recipeID) {
                        goToRecipe(recipeID);
                    }
                });
                RView.setAdapter(browseAdapter);
            }
            if (!needanotherpage){PBLoading.setVisibility(View.GONE);}
        }
    }

    // go to recipe page
    public void goToRecipe(String recipeID)
    {
        Intent intent = new Intent(mainActivity, RecipeItem.class);
        intent.putExtra("recipeID", recipeID);
        mainActivity.startActivity(intent);

    }



}