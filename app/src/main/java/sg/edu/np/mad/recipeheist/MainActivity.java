package sg.edu.np.mad.recipeheist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new BrowseFragment(), R.id.frameLayout);
        getSupportActionBar().setTitle("Browse");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        checkIfUidStillExist();

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

                        String uid = "\"" + currentUser.getUid().toString() + "\"";

                        User user = new User();

                        // get user info from database
                        try {
                            String dataBaseUsers = getUser(uid);
                            dataBaseUsers = dataBaseUsers.substring(1, dataBaseUsers.length()-1);
                            System.out.println(dataBaseUsers);

                            JSONObject jsonObject = new JSONObject(dataBaseUsers);
                            System.out.println(jsonObject);

                            user.setUserID(jsonObject.getString("userID"));
                            user.setEmail(jsonObject.getString("email"));
                            user.setUsername(jsonObject.getString("username"));
                            user.setDescription(jsonObject.getString("description"));
                            user.setFollowing(convertJArrayToArrayList(jsonObject.getJSONArray("following")));
                            user.setBookmark(convertJArrayToArrayList(jsonObject.getJSONArray("bookmark")));


                            System.out.println(user.getUserID() + user.getUsername());
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }


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
                    else {
                        Intent intent = new Intent(this, SignIn.class);
                        startActivity(intent);
                    }

                    break;
            }

            return true;
        });
    }

    public void replaceFragment(Fragment fragment, int Rid){
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

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null){
            if (networkInfo.isConnected()) {
                return true;
            }
            else {
                return false;
            }
        }
        else{
            return false;
        }
    }

    public void checkIfUidStillExist(){
        if (isConnected()) {
            try {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                currentUser.reload().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthInvalidUserException) {
                            Toast.makeText(MainActivity.this, "User is deleted or disabled", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                        }
                    }
                });
            }catch (Exception e){

            }

        }
    }

    // function to convert jsonArray to ArrayList
    public ArrayList<String> convertJArrayToArrayList(JSONArray jsonArray) throws JSONException {
        ArrayList<String> arrayList = new ArrayList<>();

        if (jsonArray != null){
            for (int i = 0; i < jsonArray.length(); i++){
                arrayList.add(jsonArray.getString(i));
            }
             return arrayList;
        }
        else{
            return arrayList;
        }
    }


    // function to get users from restDB
    public String getUser(String uid) throws IOException {
        RestDB restDB = new RestDB();
        String response = restDB.get("https://recipeheist-567c.restdb.io/rest/users?q={\"userID\": " + uid + "}");
        System.out.println(response);
        return response;
    }





    public void checkIfUsernameExists(){
        if (isConnected() == true){
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.child("users").child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(currentUser.getUid())){
                        // use "username" already exists
                        // Let the user know he needs to pick another username.
                    } else {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
}