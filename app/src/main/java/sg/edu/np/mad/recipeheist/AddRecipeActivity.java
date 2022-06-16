package sg.edu.np.mad.recipeheist;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;

import sg.edu.np.mad.recipeheist.databinding.ActivityMainBinding;

public class AddRecipeActivity extends AppCompatActivity {

    private User user;
    private Recipe recipe;
    private TextView editFoodName, editFoodDescription, editDuration, editCategory, editIngredients, editInstructions;
    private ImageButton editFoodImage;
    private Button createBtn;

    private ActivityResultLauncher<String> getImageFromGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        // retrieve data passed
        user = getIntent().getParcelableExtra("userData");

        editFoodName = findViewById(R.id.editFoodName);
        editFoodDescription = findViewById(R.id.editFoodDescription);
        editDuration = findViewById(R.id.editDuration);
        editCategory = findViewById(R.id.editCategory);
        editIngredients = findViewById(R.id.editIngredient);
        editInstructions = findViewById(R.id.editInstruction);
        editFoodImage = findViewById(R.id.editFoodImage);
        createBtn = findViewById(R.id.createBtn);

        getImageFromGallery = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                // resize image obtained from gallery
                try {
                    editFoodImage.setImageBitmap(decodeUri(AddRecipeActivity.this, result, 480));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });


        // set onclick listener for food image
        editFoodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery.launch("image/*");
            }
        });

        // set onclick listener for "create button"
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private User fetchUser(User currentUser){
        User user = currentUser;
        return user;
    }

    // function to resize image
    public static Bitmap decodeUri(Context context, Uri uri, final int requiredSize)
            throws FileNotFoundException {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, option);

        int width_tmp = option.outWidth
                , height_tmp = option.outHeight;
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