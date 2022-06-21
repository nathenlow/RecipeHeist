package sg.edu.np.mad.recipeheist.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import sg.edu.np.mad.recipeheist.R;
import sg.edu.np.mad.recipeheist.RecipeItem;
import sg.edu.np.mad.recipeheist.RecipePreview;

public class BrowseAdapter extends RecyclerView.Adapter<BrowseAdapter.ViewHolder> {
    private Context ct;
    private ArrayList<RecipePreview> recipeArrayList;
    public BrowseAdapter(Context ct, ArrayList<RecipePreview> recipeArrayList) {
        this.ct = ct;
        this.recipeArrayList = recipeArrayList;
    }

    @NonNull
    @Override
    public BrowseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ct);
        View view = inflater.inflate(R.layout.browse_cardview, parent,false);
        return new BrowseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrowseAdapter.ViewHolder holder, int position) {
        RecipePreview recipePreview = recipeArrayList.get(position);
        holder.foodtitle.setText(recipePreview.getTitle());
        holder.username.setText(recipePreview.getUsername());


        //Display image
        String imagefilename = recipePreview.getImagePath();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Recipe_image/"+imagefilename);
        Glide.with(ct)
                .load(storageReference)
                .into(holder.foodimagepreview);

        holder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRecipe(ct, recipePreview.getId());
            }
        });


    }

    @Override
    public int getItemCount() {
        return recipeArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView username, foodtitle;
        ImageView foodimagepreview;
        View main;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodtitle = itemView.findViewById(R.id.foodtitle);
            username = itemView.findViewById(R.id.ausername);
            foodimagepreview = itemView.findViewById(R.id.foodimagepreview);
            main = itemView;
        }
    }


    // go to recipe page
    public void goToRecipe(Context context, String recipeID)
    {
        Intent intent = new Intent(context, RecipeItem.class);
        intent.putExtra("recipeID", recipeID);
        context.startActivity(intent);
    }
}
