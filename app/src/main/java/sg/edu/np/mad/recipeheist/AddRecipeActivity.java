package sg.edu.np.mad.recipeheist;

import static java.lang.Integer.parseInt;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class AddRecipeActivity extends AppCompatActivity {

    private User user = new User();
    private Recipe recipe = new Recipe();
    private TextView editRecipeName, editRecipeDescription, editDuration, editCategory, editServing, editIngredients, editInstructions;
    private ImageButton editFoodImage;

    private ActivityResultLauncher<String> getImageFromGallery;

    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // update page header
        getSupportActionBar().setTitle("Create recipe");

        // retrieve data passed
        user = getIntent().getParcelableExtra("userData");
        // update recipe's userID
        recipe.setUserID(user.getUserID());

        editRecipeName = findViewById(R.id.editRecipeName);
        editRecipeDescription = findViewById(R.id.editRecipeDescription);
        editDuration = findViewById(R.id.editDuration);
        editCategory = findViewById(R.id.editCategory);
        editServing = findViewById(R.id.editServing);
        editIngredients = findViewById(R.id.editIngredient);
        editInstructions = findViewById(R.id.editInstruction);
        editFoodImage = findViewById(R.id.editFoodImage);
        Button createRecipeBtn = findViewById(R.id.createRecipeBtn);

        getImageFromGallery = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    // resize image obtained from gallery
                    try {
                        Bitmap bitmap = decodeUri(AddRecipeActivity.this, result, 480);
                        editFoodImage.setImageBitmap(bitmap);

                        // set recipes image path
                        recipe.setImagePath(result.toString());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(AddRecipeActivity.this, "No image selected, please select an image!", Toast.LENGTH_SHORT).show();
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
        createRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("here");
                // set title of recipe
                String title = editRecipeName.getText().toString();
                recipe.setTitle(title);
                // set description of recipe
                String description = editRecipeDescription.getText().toString();
                // set duration of recipe
                String duration = editDuration.getText().toString();
                // set serving size of recipe
                int serving = parseInt(editServing.getText().toString());
                // set category of recipe
                String category = editCategory.getText().toString();
                // set ingredients of recipe
                ArrayList<String> ingredientList = separateString(editIngredients.getText().toString());
                // set instructions of recipe
                ArrayList<String> instructionList = separateString(editInstructions.getText().toString());
                // set image name
                String imagePath = recipe.getUserID() + "_" + recipe.getTitle();

                // convert arrayList to JsonArray
                JSONArray ingredients = new JSONArray(ingredientList);
                JSONArray instructions = new JSONArray(instructionList);

                // upload image to firebase
                uploadImage(recipe, Uri.parse(recipe.getImagePath()));

                // push info to database
                try {
                    pushRecipeToDB(title, description, duration, serving, imagePath, category, ingredients, instructions, recipe.getUserID());

                    // message to notify users of post
                    Toast.makeText(AddRecipeActivity.this, "Recipe created successfully!", Toast.LENGTH_SHORT).show();

                    // sent user back to user page
                    Intent intent = new Intent(AddRecipeActivity.this, MainActivity.class);
                    startActivity(intent);

                } catch (IOException e) {
                    Toast.makeText(AddRecipeActivity.this, "Recipe creation is unsuccessful, please try again!", Toast.LENGTH_SHORT).show();
                }


            }
        });

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

    // function to filter/sort string to an array (;)
    private ArrayList<String> separateString(String stringToBeSeparated){
        ArrayList<String> stringArrayList = new ArrayList<>();
        // separate the string
        String[] splitArray = stringToBeSeparated.split(";");
        // add each section into the arrayList
        for (int i = 0; i < splitArray.length; i++){
            stringArrayList.add(splitArray[i]);
        }

        return stringArrayList;
    }

    // function to update recipe to database
    public void pushRecipeToDB (String title, String description, String duration, int serving, String imagePath, String foodCategory, JSONArray ingredient, JSONArray instruction, String userID) throws IOException {
        RestDB restDB = new RestDB();
        System.out.println("still working");
        String json = restDB.createRecipe(title, description, duration, serving, imagePath, foodCategory, ingredient, instruction, userID);
        System.out.println(restDB.client);
        System.out.println(json);
        String response = restDB.post("https://recipeheist-567c.restdb.io/rest/recipe", json);
        System.out.println(restDB.JSON);
        System.out.println(response);
    }

    // function to convert bitmap to jpeg


    // function to upload image
    private void uploadImage(Recipe recipe, Uri uri){
        // create a name of file
        String fileName = recipe.getUserID() + "_" + recipe.getTitle();
        storageReference = FirebaseStorage.getInstance().getReference("Recipe_image/"+fileName);

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddRecipeActivity.this, "Upload is unsuccessful, please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}