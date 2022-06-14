package sg.edu.np.mad.recipeheist;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        Button editProfileBtn = view.findViewById(R.id.editProfileBtn);

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
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.replaceFragment(editProfileFragment , mainActivity.findViewById(R.id.frameLayout).getId());
                mainActivity.getSupportActionBar().setTitle("Profile Settings");
            }
        });

        return view;
    }

    private void initRecyclerView(View view){
        RecyclerView recyclerView = view.findViewById(R.id.myRecipeRecyc);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(linearLayoutManager);

        //recyclerView.setAdapter();
    }
    public void goToSignIn()
    {
        Intent intent = new Intent(getActivity(), SignIn.class);
        startActivity(intent);
    }
}