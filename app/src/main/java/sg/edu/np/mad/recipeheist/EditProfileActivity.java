package sg.edu.np.mad.recipeheist;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editUsername, editBio;
    private CircleImageView userImage;
    private ImageView editImageBtn;
    private User user;
    private Button saveBtn;
    private Bundle bundle;

    private ActivityResultLauncher<String> getImageFromGallery;

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

        // for getting image from gallery
        getImageFromGallery = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    // resize image obtained from gallery
                    try {
                        Bitmap bitmap = decodeUri(EditProfileActivity.this, result, 100);
                        userImage.setImageBitmap(bitmap);

                        // set user image path
                        user.setProfileImage(result.toString());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(EditProfileActivity.this, "No image selected, please select an image!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // set on click listener for editImageBtn
        editImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery.launch("image/*");
            }
        });

        // set onclick listener for save button
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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

    // function to resize image
    public static Bitmap decodeUri(Context context, Uri uri, final int requiredSize)
            throws FileNotFoundException {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, option);

        int width_tmp = option.outWidth,
                height_tmp = option.outHeight;
        int scale = 1;

        while(true) {
            if(width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options option2 = new BitmapFactory.Options();
        option2.inSampleSize = scale;
        return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, option2);
    }

}