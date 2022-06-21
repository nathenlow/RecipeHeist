package sg.edu.np.mad.recipeheist;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

    private int count = 0;
    private int pagecount = 0;
    boolean needanotherpage = false;
    private int perpage = 15;
    private ArrayList<RecipePreview> recipelist;
    private RecyclerView RView;
    private BrowseAdapter browseAdapter;
    private ProgressBar PBLoading;
    private NestedScrollView nestedSV;
    private JSONArray recipearray;
    private String query = "";

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



        //get data from search
        getParentFragmentManager().setFragmentResultListener("search", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                onStart();
                query = result.getString("query");
            }
        });

        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                searchordefault();
            }
        }.start();




        GridLayoutManager manager = new GridLayoutManager(mainActivity, 2);
        RView.setLayoutManager(manager);
        nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    // in this method we are incrementing page number,
                    // making progress bar visible and calling get data method.
                    count++;
                    // on below line we are making our progress bar visible.
                    PBLoading.setVisibility(View.VISIBLE);
                    if (count < 7) {
                        // on below line we are again calling
                        // a method to load data in our array list.
                        if (needanotherpage){
                            searchordefault();
                        }

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
                return false;
            }
        });

    }

    public  void searchordefault(){
        try {
            if (query.equals("")){
                defaultRecipe(pagecount);
            }
            else {
                searchRecipes(query,pagecount);
            }
            pagecount += 1;
            getData();
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
        String response = restDB.get("https://recipeheist-567c.restdb.io/rest/recipe?q={\"title\": {\"$regex\" :\"" + query + "\"}}&h={\"$fields\":{\"_id\":1,\"title\":1,\"userID\":1,\"imagePath\":1},\"$max\":"+perpage+",\"$skip\":"+skip+",\"$orderby\":{\"_created\":1}}");
        recipearray = new JSONArray(response);
        if (recipearray.length() == perpage){
            needanotherpage = true;
        }
        else {
            PBLoading.setVisibility(View.GONE);
        }
    }


    //to get default data from rest db
    public void defaultRecipe(int page) throws IOException, JSONException {
        int skip = perpage * page;
        RestDB restDB = new RestDB();
        String response = restDB.get("https://recipeheist-567c.restdb.io/rest/recipe?h={\"$fields\":{\"_id\":1,\"title\":1,\"userID\":1,\"imagePath\":1},\"$max\":"+perpage+",\"$skip\":"+skip+",\"$orderby\":{\"_created\":1}}");
        recipearray = new JSONArray(response);
        if (recipearray.length() >= perpage){
            needanotherpage = true;
        }
        else {
            PBLoading.setVisibility(View.GONE);
        }
    }

    public void getData() throws JSONException {
        for (int i = 0; i < recipearray.length(); i++) {
            JSONObject recipeobj = (JSONObject) recipearray.get(i);
            String id = recipeobj.getString("_id");
            String title = recipeobj.getString("title");
            String imagePath = recipeobj.getString("imagePath");
            String userapiresponse = getUser(recipeobj.getString("userID"));
            JSONArray userarray = new JSONArray(userapiresponse);
            JSONObject userobj = (JSONObject) userarray.get(0);
            String username = userobj.getString("username");
            recipelist.add(new RecipePreview(id, title, imagePath, username));
            browseAdapter = new BrowseAdapter(mainActivity, recipelist);
            RView.setAdapter(browseAdapter);
        }

    }

    public String getUser(String userid){
        //String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        RestDB example = new RestDB();
        String response = null;
        try {
            response = example.get("https://recipeheist-567c.restdb.io/rest/users?q={\"userID\":\"" + userid + "\"}&h{\"$fields\":{\"username\":1}}");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

}