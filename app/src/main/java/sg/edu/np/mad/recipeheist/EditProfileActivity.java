package sg.edu.np.mad.recipeheist;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editUsername, editBio;
    private CircleImageView userImage;
    private ImageView editImageBtn;
    private User user;
    private Button saveBtn;
    private Bundle bundle;

    private String dbID;
    private StorageReference storageReference;
    private ActivityResultLauncher<String> getImageFromGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        bundle = getIntent().getExtras();
        user = bundle.getParcelable("userData");
        dbID = getIntent().getStringExtra("database_id");

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
                findViewById(R.id.progressBarCover).setVisibility(View.VISIBLE);
                updateProfilePage();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // --------------------------------------- Start of functions ---------------------------------------

    // function to update page based on current data (contains other functions)
    public void updateDetails(){

        // set profile image into view
        if (!user.getProfileImage().equals("")){
            // call function to update image
            updateUserProfileImage();
        }
        else{
            userImage.setImageResource(R.drawable.default_profile_1);
        }

        // set username into editUsername
        editUsername.setText(user.getUsername());

        // set Bio into editBio
        editBio.setText(convertSeparatorToNewLine(user.getDescription()));
    }

    // function to update user profile page
    public void updateProfilePage(){

        // get current input for change in username
        String username = editUsername.getText().toString().trim();
        // get current input for change in bio
        String bio = editBio.getText().toString().trim();

        // update user bio
        user.setDescription(convertNewLineToSeparator(bio));

        // validate current input for username
        if (!username.isEmpty()){
            // update username if its not empty
            user.setUsername(username);
        }

        // check if user profile image is null
        if (!user.getProfileImage().equals("") && !user.getProfileImage().equals(user.getUserID())){
            // upload image to firebase
            uploadImage(user, Uri.parse(user.getProfileImage()));
            user.setProfileImage(user.getUserID());
        }

        // update user data in database
        try {
            updateUserInRestDB(user);

            // message to notify users of update
            Toast.makeText(EditProfileActivity.this, "Profile updated. Refresh page to see changes!", Toast.LENGTH_SHORT).show();

            // remove progress bar
            findViewById(R.id.progressBarCover).setVisibility(View.GONE);

            // sent user back to profile page
            onBackPressed();

        } catch (IOException e) {
            e.printStackTrace();
            findViewById(R.id.progressBarCover).setVisibility(View.GONE);
            Toast.makeText(EditProfileActivity.this, "Profile update unsuccessful!", Toast.LENGTH_SHORT).show();
        }


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

    // function to get and set user image from firebase
    public void updateUserProfileImage(){
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile_image/"+user.getProfileImage());
        try {
            File localFile = File.createTempFile("tempfile", "jpeg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    Bitmap resizedBM = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                    userImage.setImageBitmap(resizedBM);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // function to upload image to firebase storage
    private void uploadImage(User user, Uri uri){
        // create a name of file
        String fileName = user.getUserID();
        storageReference = FirebaseStorage.getInstance().getReference("Profile_image/"+fileName);

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Upload is unsuccessful, please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // function to update user details in restDB
    public void updateUserInRestDB(User user) throws IOException {
        RestDB restDB = new RestDB();
        String json = restDB.updateUserDetails(user.getEmail(),user.getUserID(),user.getUsername(), user.getDescription(), user.getProfileImage());
        System.out.println(json);
        String response = restDB.put("https://recipeheist-567c.restdb.io/rest/users/" + dbID , json);
        System.out.println(response);
    }

    // function to replace \n to "1,3&5!"
    public String convertNewLineToSeparator(String string){
        return string.replaceAll("\n", "1,3&5!");
    }

    // function to replace "1,3&5!" to \n
    public String convertSeparatorToNewLine(String string){
        return string.replaceAll("1,3&5!", "\n");
    }

}