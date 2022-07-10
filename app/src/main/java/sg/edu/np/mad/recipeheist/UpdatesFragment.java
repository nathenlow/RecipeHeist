package sg.edu.np.mad.recipeheist;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
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
    private ConstraintLayout loadingview;
    int lastpage = 0;
    private int perpage = 16;
    private ArrayList<RecipePreview> showlist;


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
        mainActivity.showbottomnav(true);
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
        loadingview = rootView.findViewById(R.id.loadinglayout);
        DataBaseHandler dataBaseHandler = new DataBaseHandler(mainActivity);
        showlist = getData(dataBaseHandler.chefUpdates());

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

                // if diff is zero, then the bottom has been reached
                if (showlist.size() != dataBaseHandler.chefUpdates().size()){
                    if (diff == 0) {
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

        browseAdapter = new BrowseAdapter(mainActivity, showlist, new RecipeLoadListener() {
            @Override
            public void onLoad(String recipeID) {
                goToRecipe(recipeID);
            }
        });
        RView.setAdapter(browseAdapter);

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
                    mainActivity.startService(intent);
                }
                return false;
            }
        });

    }

    public ArrayList<RecipePreview> getData(ArrayList<RecipePreview> updatelist){
        ArrayList<RecipePreview> recipePreviews = new ArrayList<>();
        lastpage += perpage;
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