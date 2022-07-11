package sg.edu.np.mad.recipeheist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import sg.edu.np.mad.recipeheist.adapter.RecentSearchAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFoodFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFoodFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    private static final String SHARED_PREFS = "recentSearch";
    private static final String RECENT_SEARCH = "recentSearches";
    private View rootview;
    private JSONArray recentlist;
    private EditText editTextsearch;
    private TextView clearall;
    private ImageView backarrow;
    private ImageView UserButton;
    MainActivity mainActivity;


    // TODO: Rename and change types of parameters

    public SearchFoodFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static SearchFoodFragment newInstance() {
        SearchFoodFragment fragment = new SearchFoodFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
        mainActivity.showbottomnav(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_search_food, container, false);


        //change actionbar
        mainActivity.getSupportActionBar().setCustomView(R.layout.search_action_bar);
        mainActivity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mainActivity.getSupportActionBar().setDisplayShowCustomEnabled(true);
        mainActivity.getSupportActionBar().setCustomView(R.layout.search_action_bar);
        mainActivity.getSupportActionBar().setElevation(0);

        editTextsearch = mainActivity.findViewById(R.id.editTextsearch);
        backarrow = mainActivity.findViewById(R.id.back);
        clearall = rootview.findViewById(R.id.clearall);
        UserButton = rootview.findViewById(R.id.UserButton);


        mrecycler();

        //click search on keyboard
        editTextsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        //click clear all text
        clearall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteall();
                mrecycler();
            }
        });

        //click arrow back
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onBackPressed();
                editTextsearch.clearFocus();
            }
        });

        //click arrow next to user button
        UserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSearch();
            }
        });




        
        return rootview;
    }


    public void performSearch(){
        String query = editTextsearch.getText().toString().trim();
        editTextsearch.clearFocus();
        Bundle result = new Bundle();
        result.putString("query", query);
        getParentFragmentManager().setFragmentResult("search", result);
        savedata(query);
        mrecycler();
        mainActivity.replaceFragment(new SearchFoodBrowseFragment(), R.id.frameLayout);
    }

    public void fillSearch(String query){
        editTextsearch.setText(query);
        showSoftKeyboard(editTextsearch);
    }

    public void showSoftKeyboard(View view) {
        if(view.requestFocus()){
            InputMethodManager imm =(InputMethodManager)
                    mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
        }
    }



    public void savedata(String query) {
        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        recentlist.put(query.trim());
        editor.putString(RECENT_SEARCH, recentlist.toString());
        editor.apply();
    }

    public void deletedata(int position){
        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        recentlist.remove(position);
        editor.putString(RECENT_SEARCH, recentlist.toString());
        editor.apply();
    }

    public void deleteall(){
        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void loaddata() throws JSONException {
        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String recentsearch = sharedPreferences.getString(RECENT_SEARCH, "[]");
        recentlist = new JSONArray(recentsearch);
    }


    //recyclerview adapter
    public void mrecycler (){
        try {
            loaddata();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RecyclerView recyclerView;
        recyclerView = rootview.findViewById(R.id.recentitems);
        if (!recentlist.equals(new JSONArray())){
            recyclerView.setVisibility(View.VISIBLE);
            RecentSearchAdapter adapter = new RecentSearchAdapter(this.mainActivity, recentlist,
                new DeleteListener() {
                    @Override
                    public void onDelete(int position) {
                        deletedata(position);
                        mrecycler();
                    }
                },
                new RecentListener() {
                    @Override
                    public void onRecent(String query) {
                        fillSearch(query);
                    }
                }
            );
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this.mainActivity));
        }
        else {
            recyclerView.setVisibility(View.GONE);
        }
    }

}