package sg.edu.np.mad.recipeheist;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import sg.edu.np.mad.recipeheist.adapter.BrowseAdapter;


public class BrowseFragment extends Fragment {

    private int pagecount = 0;
    boolean needanotherpage;
    private int perpage = 10;
    private ArrayList<RecipePreview> recipelist;
    private RecyclerView RView;
    private BrowseAdapter browseAdapter;
    private ProgressBar PBLoading;
    private NestedScrollView nestedSV;
    private JSONArray recipearray;
    private String query = "";
    private ConstraintLayout loadingview;

    MainActivity mainActivity;

    public BrowseFragment() {
        // Required empty public constructor
    }

    public static BrowseFragment newInstance() {
        BrowseFragment fragment = new BrowseFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_browse, container, false);
        mainActivity = (MainActivity) getActivity();

        //change action bar back to default
        mainActivity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        mainActivity.getSupportActionBar().setTitle("Browse");
        recipelist = new ArrayList<>();
        RView = rootView.findViewById(R.id.RView);
        PBLoading = rootView.findViewById(R.id.PBLoading);
        nestedSV = rootView.findViewById(R.id.nestedSV);
        loadingview = rootView.findViewById(R.id.loadingview);


        //get data from search
        getParentFragmentManager().setFragmentResultListener("search", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                onStart();
                query = result.getString("query");
            }
        });

        // because sometimes the query from the fragment takes some time to get back
        new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                searchordefault();
            }
        }.start();




        // grid layout splitting display into two columns
        GridLayoutManager manager = new GridLayoutManager(mainActivity, 2);
        RView.setLayoutManager(manager);
        nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView scrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

                if (needanotherpage){
                    // if diff is zero, then the bottom has been reached
                    if (diff == 0) {
                        searchordefault();
                    }
                }
            }
        });

        return rootView;
    }


    //menu bar
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.top_nav_browse_menu, menu);
        MenuItem menusearch = menu.findItem(R.id.app_bar_search);
        MenuItem menubookmark = menu.findItem(R.id.viewbookmark);
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
                mainActivity.replaceFragment(new BookmarkFragment(), R.id.frameLayout);
                return false;
            }
        });

    }

    //combine all the fuctions for get from restdb
    public  void searchordefault(){
        needanotherpage = false;
        PBLoading.setVisibility(View.VISIBLE);
        try {
            if (query.equals("")){
                defaultRecipe(pagecount);
            }
            else {
                searchRecipes(query,pagecount);
            }
            pagecount += 1;
            if (!needanotherpage){PBLoading.setVisibility(View.GONE);}
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
                    recipearray = new JSONArray(jsonresponse);
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

            recipelist.add(new RecipePreview(id, title, imagePath, duration));
            browseAdapter = new BrowseAdapter(mainActivity, recipelist, new RecipeLoadListener() {
                @Override
                public void onLoad(String recipeID) {
                    goToRecipe(recipeID);
                }
            });
            RView.setAdapter(browseAdapter);
        }

    }



    // go to recipe page
    public void goToRecipe(String recipeID)
    {
        loadingview.setVisibility(View.VISIBLE);
        // because sometimes the query from the fragment takes some time to get back
        new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                loadingview.setVisibility(View.GONE);
            }
        }.start();
        Intent intent = new Intent(mainActivity, RecipeItem.class);
        intent.putExtra("recipeID", recipeID);
        mainActivity.startActivity(intent);
    }

}