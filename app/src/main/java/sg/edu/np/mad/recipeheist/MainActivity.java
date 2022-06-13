package sg.edu.np.mad.recipeheist;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
                case R.id.profile:
                    //TODO: if user login
                    if (currentUser != null){

                        User user = new User();
                        ArrayList<String> following = new ArrayList<>();

                        //Get user data from firebase database
                        DatabaseReference myRef = FirebaseDatabase.getInstance("https://recipeheist-ce646-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        myRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                user.setUsername(snapshot.child("username").getValue().toString());
                                user.setUserID(snapshot.child("userID").getValue().toString());
                                user.setEmail(snapshot.child("email").getValue().toString());
                                user.setDescription(snapshot.child("description").getValue().toString());

                                //Check if user has any following
                                if (snapshot.hasChild("following")){
                                    for (int i = 1; i <= snapshot.child("following").getChildrenCount(); i++){
                                        following.add(snapshot.child("following").child(String.valueOf(i)).getValue(String.class));
                                    }
                                }
                                user.setFollowing(following);

                                // Create bundle to pass user data to fragment
                                Bundle user_data = new Bundle();
                                user_data.putParcelable("userData", user);
                                //set argument to ProfileFragment
                                ProfileFragment profileFragment = new ProfileFragment();
                                profileFragment.setArguments(user_data);
                                // Replace fragment
                                replaceFragment(profileFragment, R.id.frameLayout);
                                getSupportActionBar().setTitle("Profile");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

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