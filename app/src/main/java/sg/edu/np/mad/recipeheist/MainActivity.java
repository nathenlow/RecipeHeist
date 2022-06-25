package sg.edu.np.mad.recipeheist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

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

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        checkIfUidStillExist();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.browse:
                    replaceFragment(new BrowseFragment(), R.id.frameLayout);
                    break;
                case R.id.updates:
                    replaceFragment(new UpdatesFragment(), R.id.frameLayout);
                    break;
                case R.id.download:
                    replaceFragment(new DownloadFragment(), R.id.frameLayout);
                    break;
                case R.id.profile:

                    if (currentUser != null){
                        try {
                            getUserProfile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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

    public void getUserProfile() throws IOException {
        binding.loadinglayout.setVisibility(View.VISIBLE);
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String uid = "\"" + currentUserID + "\"";

        User user = new User();

        RestDB restDB = new RestDB();
        restDB.asyncGet("https://recipeheist-567c.restdb.io/rest/users?q={\"userID\": " + uid + "}", new SuccessListener() {
            @Override
            public void onSuccess(String jsonresponse) throws JSONException {
                String dataBaseUsers = jsonresponse;
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

                // Create bundle to pass user data to fragment
                Bundle user_data = new Bundle();
                user_data.putParcelable("userData", user);
                //set argument to ProfileFragment
                ProfileFragment profileFragment = new ProfileFragment();
                profileFragment.setArguments(user_data);
                // Remove loading page
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.loadinglayout.setVisibility(View.GONE);
                    }
                });
                // Replace fragment
                replaceFragment(profileFragment, R.id.frameLayout);
            }
        });
    }

    //methods

    public void stack(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, fragment);
        ft.addToBackStack(null);
        ft.commit();
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

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
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
        return response;
    }

}