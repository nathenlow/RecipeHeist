package sg.edu.np.mad.recipeheist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.PersistableBundle;
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

import android.os.Bundle;

public class UserSearch extends AppCompatActivity {
    private EditText editTextUsersearch;
    private ImageView backarrow;
    private JSONArray recentlist;
    MainActivity mainActivity;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String RECENT_SEARCH = "recentSearches";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        editTextUsersearch = findViewById(R.id.editTextUsersearch);
        backarrow = findViewById(R.id.back);

        //click search on keyboard
        editTextUsersearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performUserSearch();
                    return true;
                }
                return false;
            }
        });
    }

    public void performUserSearch(){
        String query = editTextUsersearch.getText().toString().trim();
        editTextUsersearch.clearFocus();
        Bundle result = new Bundle();
        result.putString("User", query);

        mainActivity.stack(new BrowseFragment());
    }




}