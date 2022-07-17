package sg.edu.np.mad.recipeheist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editUsername, editBio;
    private CircleImageView userImage;
    private ImageView editImageBtn;
    private User user;
    private Button saveBtn;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        bundle = getIntent().getExtras();
        user = bundle.getParcelable("userData");

        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get widget
        editUsername = findViewById(R.id.editUsername);
        editBio = findViewById(R.id.editBio);
        userImage = findViewById(R.id.userImage);
        editImageBtn = findViewById(R.id.userImageBtn);
        saveBtn = findViewById(R.id.saveProfileBtn);

        // update inputs
        updateDetails();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // --------------------------------------- Start of functions ---------------------------------------

    // function to update page based on current data
    public void updateDetails(){

        // set profile image into view


        // set username into editUsername
        editUsername.setText(user.getUsername());

        // set Bio into editBio
        editBio.setText(user.getDescription());
    }

}