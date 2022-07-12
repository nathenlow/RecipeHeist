package sg.edu.np.mad.recipeheist.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import sg.edu.np.mad.recipeheist.R;
import sg.edu.np.mad.recipeheist.Recipe;
import sg.edu.np.mad.recipeheist.RecipeItem;
import sg.edu.np.mad.recipeheist.viewholder.MyRecipeVH;

public class MyRecipeAdapter extends RecyclerView.Adapter<MyRecipeVH> {

    private Context context;
    private ArrayList<Recipe> recipes;
    private StorageReference storageReference;

    public MyRecipeAdapter(Context context, ArrayList<Recipe> recipeArrayList){
        this.context = context;
        this.recipes = recipeArrayList;
    }

    @NonNull
    @Override
    public MyRecipeVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyRecipeVH viewHolder;
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_my_recipe, parent, false);
        viewHolder = new MyRecipeVH(item);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecipeVH holder, int position) {
        Recipe recipe = recipes.get(position);

        System.out.println(recipe.getRecipeID());

        // set image
        storageReference = FirebaseStorage.getInstance().getReference().child("Recipe_image/"+recipe.getImagePath());
        try {
            File localFile = File.createTempFile("tempfile", "jpeg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    Bitmap resizedBM = Bitmap.createScaledBitmap(bitmap, 250, 250, true);
                    holder.foodImage.setImageBitmap(resizedBM);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        // set name
        holder.foodTitle.setText(recipe.getTitle());
        // set noOfLikes
        //holder.noOfLikes.setText(String.valueOf(recipe.getLike().size()));

        // set on click for the card view
        holder.myRecipeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, RecipeItem.class);
                intent.putExtra("recipeID", recipe.getRecipeID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

}
