package sg.edu.np.mad.recipeheist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import sg.edu.np.mad.recipeheist.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    public Fragment browseFragment;
    public Fragment updatesFragment;
    public Fragment historyFragment;
    public Fragment profileFragment;

    @Override
    public void onBackPressed() {
        Fragment f = this.getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if(f instanceof BrowseFragment){
            moveTaskToBack(true);
        }else {
            binding.bottomNavigationView.setSelectedItemId(R.id.browse);
            replaceFragment(browseFragment, R.id.frameLayout);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        browseFragment = new BrowseFragment();
        updatesFragment = new UpdatesFragment();
        historyFragment = new HistoryFragment();
        profileFragment = new ProfileFragment();

        replaceFragment(browseFragment, R.id.frameLayout);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.browse:
                    replaceFragment(browseFragment, R.id.frameLayout);
                    break;
                case R.id.updates:
                    replaceFragment(updatesFragment, R.id.frameLayout);
                    break;
                case R.id.download:
                    replaceFragment(historyFragment, R.id.frameLayout);
                    break;
                case R.id.profile:

                    if (currentUser != null){
                        replaceFragment(profileFragment, R.id.frameLayout);
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

    //methods

    public void setTheme(){
        SharedPreferences settingsSharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        String theme = settingsSharedPreferences.getString("theme", "0");
        switch (theme){
            case "1":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "2":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    public void showbottomnav(Boolean show){
        if(show){
            binding.bottomNavigationView.setVisibility(View.VISIBLE);
        }
        else {
            binding.bottomNavigationView.setVisibility(View.GONE);
        }

    }


    public void showloading(Boolean show){
        if(show){
            binding.loadinglayout.setVisibility(View.VISIBLE);
        }
        else {
            binding.loadinglayout.setVisibility(View.GONE);
        }

    }

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
}