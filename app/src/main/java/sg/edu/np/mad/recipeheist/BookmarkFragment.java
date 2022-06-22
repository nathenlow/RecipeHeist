package sg.edu.np.mad.recipeheist;

import android.app.ActionBar;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONArray;

import java.util.ArrayList;

import sg.edu.np.mad.recipeheist.adapter.BrowseAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookmarkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookmarkFragment extends Fragment {

    private ArrayList<RecipePreview> recipelist;
    private RecyclerView RView;
    private BrowseAdapter browseAdapter;
    private ProgressBar PBLoading;
    private NestedScrollView nestedSV;
    private JSONArray recipearray;
    private String query = "";
    private ConstraintLayout loadingview;
    MainActivity mainActivity;

    public BookmarkFragment() {
        // Required empty public constructor
    }

    public static BookmarkFragment newInstance(String param1, String param2) {
        BookmarkFragment fragment = new BookmarkFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bookmark, container, false);
        mainActivity = (MainActivity) getActivity();

        //change action bar back to default
        //mainActivity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        mainActivity.getSupportActionBar().setTitle("Bookmark Recipies");
        return rootView;
    }
}