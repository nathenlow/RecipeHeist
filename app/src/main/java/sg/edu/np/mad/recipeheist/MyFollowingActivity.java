package sg.edu.np.mad.recipeheist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public class MyFollowingActivity extends AppCompatActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_following);

        Bundle bundle = getIntent().getExtras();
        user = bundle.getParcelable("userData");

        getSupportActionBar().setTitle("Following");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // check if there's user has any following
        if (user.getFollowing().size() != 0){

            MyFollowingFragment fragment = new MyFollowingFragment();
            Bundle args = new Bundle();
            args.putParcelable("userData", user);
            fragment.setArguments(args);

            replaceFragment(fragment, R.id.followingFrame);

        }
        else{
            replaceFragment(new EmptyFollowingFragment(), R.id.followingFrame);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //-------------------------------------------------- Start of functions --------------------------------------------------

    public void replaceFragment(Fragment fragment, int Rid){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(Rid, fragment);
        fragmentTransaction.commit();
    }
}