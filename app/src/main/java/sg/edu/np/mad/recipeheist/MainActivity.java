package sg.edu.np.mad.recipeheist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sg.edu.np.mad.recipeheist.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
     ActivityMainBinding binding;

    private FirebaseAuth mAuth;

    @Override
    public void onBackPressed() {
        Fragment f = this.getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if(f instanceof BrowseFragment){

            moveTaskToBack(true);
        }else {
            binding.bottomNavigationView.setSelectedItemId(R.id.browse);
            replaceFragment(new BrowseFragment(), R.id.frameLayout);
            getSupportActionBar().setTitle("Browse");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new BrowseFragment(), R.id.frameLayout);
        getSupportActionBar().setTitle("Browse");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.browse:
                    replaceFragment(new BrowseFragment(), R.id.frameLayout);
                    getSupportActionBar().setTitle("Browse");
                    break;
                case R.id.updates:
                    replaceFragment(new UpdatesFragment(), R.id.frameLayout);
                    getSupportActionBar().setTitle("Updates");
                    break;
                case R.id.download:
                    replaceFragment(new DownloadFragment(), R.id.frameLayout);
                    getSupportActionBar().setTitle("Download");
                    break;
                case R.id.me:
                    //TODO: if user login
                    if (currentUser != null){
                        replaceFragment(new MeFragment(), R.id.frameLayout);
                        getSupportActionBar().setTitle("Me");
                    }
                    else {
                        Intent intent = new Intent(this, SignIn.class);
                        startActivity(intent);
                    }

                    break;
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment, int Rid){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(Rid, fragment);
        fragmentTransaction.commit();
    }

    private void addFragment(Fragment fragment, int Rid){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(Rid, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_browse_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView)  menuItem.getActionView();
        searchView.setQueryHint("Search Umami");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}