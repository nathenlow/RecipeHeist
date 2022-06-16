package sg.edu.np.mad.recipeheist;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private User user;
    private TextView username, description,  noOfRecipes, following;
    private CircleImageView profileImage;
    private Button editProfileBtn;
    private FloatingActionButton addRecipeBtn;
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;


    public ProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = view.findViewById(R.id.profileImage);
        username = view.findViewById(R.id.profileName);
        description = view.findViewById(R.id.profileDescription);
        editProfileBtn = view.findViewById(R.id.editProfileBtn);
        addRecipeBtn = view.findViewById(R.id.addRecipeBtn);

        // to use methods from MainActivity
        MainActivity mainActivity = (MainActivity) getActivity();

        // get users own recipe from db

        // if its empty or null
        NoMyRecipeFragment noMyRecipeFragment = new NoMyRecipeFragment();
        mainActivity.replaceFragment(noMyRecipeFragment, view.findViewById(R.id.profileFrameLayout).getId());

        // get arguments from bundle
        Bundle user_data = getArguments();

        user = user_data.getParcelable("userData");

        // update profile page
        profileImage.setImageResource(R.drawable.default_profile_1);
        username.setText(user.getUsername());
        description.setText(user.getDescription());

        // when user clicks on "edit profile" button
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // pass data to next fragment
                EditProfileFragment editProfileFragment = new EditProfileFragment();
                editProfileFragment.setArguments(user_data);

                // pass data from this fragment to MainActivity
                mainActivity.replaceFragment(editProfileFragment , mainActivity.findViewById(R.id.frameLayout).getId());
                mainActivity.getSupportActionBar().setTitle("Profile Settings");
            }
        });

        // when user clicks on "add recipe" button
        addRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddRecipeActivity.class);
                intent.putExtras(user_data);
                startActivity(intent);
            }
        });

        return view;
    }

    private void initRecyclerView(View view){
        //RecyclerView recyclerView = view.findViewById();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        //recyclerView.setLayoutManager(linearLayoutManager);

        //recyclerView.setAdapter();
    }
    
    public void goToSignIn()
    {
        Intent intent = new Intent(getActivity(), SignIn.class);
        startActivity(intent);
    }
}