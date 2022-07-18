package sg.edu.np.mad.recipeheist;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
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

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import sg.edu.np.mad.recipeheist.adapter.BrowseAdapter;

public class UpdatesFragment extends Fragment {

    private MainActivity mainActivity;
    private RecyclerView RView;
    private BrowseAdapter browseAdapter;
    private ProgressBar PBLoading;
    private NestedScrollView nestedSV;
    private View rootView;
    int lastpage = 0;
    private int perpage = 16;
    private ArrayList<RecipePreview> showlist;
    private SwipeRefreshLayout swipeRefreshLayout;
    DataBaseHandler dataBaseHandler;


    public UpdatesFragment() {
        // Required empty public constructor
    }


    public static UpdatesFragment newInstance() {
        UpdatesFragment fragment = new UpdatesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        setHasOptionsMenu(true);

        mainActivity.setActionBarTitle("Updates");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView =  inflater.inflate(R.layout.fragment_browse, container, false);

        RView = rootView.findViewById(R.id.RView);
        PBLoading = rootView.findViewById(R.id.PBLoading);
        nestedSV = rootView.findViewById(R.id.nestedSV);
        dataBaseHandler = new DataBaseHandler(mainActivity);
        showlist = getData(dataBaseHandler.chefUpdates());
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);

        // grid layout splitting display into two columns
        int columns = 2;
        GridLayoutManager manager = new GridLayoutManager(mainActivity, columns);
        RView.addItemDecoration(new GridSpacingItemDecoration(columns, 12, false));
        RView.setLayoutManager(manager);

        //to check if user reached the bottom
        nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView scrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

                // check whether if showlist contain the same amount of data as the current database
                if (showlist.size() != dataBaseHandler.chefUpdates().size()){
                    // if diff is zero, then the bottom has been reached
                    if (diff == 0) {
                        //load next page
                        showlist = getData(dataBaseHandler.chefUpdates());
                        browseAdapter = new BrowseAdapter(mainActivity, showlist, new RecipeLoadListener() {
                            @Override
                            public void onLoad(String recipeID) {
                                goToRecipe(recipeID);
                            }
                        });
                        RView.setAdapter(browseAdapter);
                    }
                }
            }
        });

        //load showlist into recycler view
        browseAdapter = new BrowseAdapter(mainActivity, showlist, new RecipeLoadListener() {
            @Override
            public void onLoad(String recipeID) {
                goToRecipe(recipeID);
            }
        });
        RView.setAdapter(browseAdapter);

        //refresh page by using Init()
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
        inflater.inflate(R.menu.top_nav_updates_menu, menu);
        MenuItem menuRefresh = menu.findItem(R.id.refreshbtn);
        //for Refresh
        menuRefresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(FirebaseAuth.getInstance().getCurrentUser() == null){
                    Toast.makeText(mainActivity, "Login is required", Toast.LENGTH_SHORT).show();
                }
                else {
                    //update recipes
                    Intent intent = new Intent(mainActivity, UpdateService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mainActivity.startForegroundService(intent);
                    } else {
                        mainActivity.startService(intent);
                    }
                }
                return false;
            }
        });

    }

    //to initialize the page
    public void Init(){
        showlist = getData(dataBaseHandler.chefUpdates());
        browseAdapter = new BrowseAdapter(mainActivity, showlist, new RecipeLoadListener() {
            @Override
            public void onLoad(String recipeID) {
                goToRecipe(recipeID);
            }
        });
        RView.setAdapter(browseAdapter);
    }

    //to get data in pages from SQLite
    public ArrayList<RecipePreview> getData(ArrayList<RecipePreview> updatelist){
        ArrayList<RecipePreview> recipePreviews = new ArrayList<>();
        lastpage += perpage;

        //remove loading bar if another page is not needed
        if (lastpage >= updatelist.size()){
            lastpage = updatelist.size();
            PBLoading.setVisibility(View.GONE);
        }
        for (int i = 0; i < lastpage; i++) {
            recipePreviews.add(updatelist.get(i));
        }
        return recipePreviews;
    }

    // go to recipe page
    public void goToRecipe(String recipeID)
    {
        Intent intent = new Intent(mainActivity, RecipeItem.class);
        intent.putExtra("recipeID", recipeID);
        mainActivity.startActivity(intent);
    }
}