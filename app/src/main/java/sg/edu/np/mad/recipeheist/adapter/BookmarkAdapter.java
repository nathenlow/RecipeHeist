package sg.edu.np.mad.recipeheist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import sg.edu.np.mad.recipeheist.R;
import sg.edu.np.mad.recipeheist.RecipeLoadListener;
import sg.edu.np.mad.recipeheist.RecipePreview;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    private Context ct;
    private ArrayList<RecipePreview> recipeArrayList;
    private RecipeLoadListener load;

    public BookmarkAdapter(Context ct, ArrayList<RecipePreview> recipeArrayList, RecipeLoadListener load) {
        this.ct = ct;
        this.recipeArrayList = recipeArrayList;
        this.load = load;
    }

    @NonNull
    @Override
    public BookmarkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(ct).inflate(
                R.layout.bookmark_cardview,parent,false);
        return new BookmarkAdapter.ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkAdapter.ViewHolder holder, int position) {
        RecipePreview recipePreview = recipeArrayList.get(position);
        holder.foodtitle.setText(recipePreview.getTitle());
        holder.duration.setText(recipePreview.getduration());


        //Display image
        String imagefilename = recipePreview.getImagePath();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Recipe_image/"+imagefilename);
        Glide.with(ct)
                .load(storageReference)
                .into(holder.foodimagepreview);

        holder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load.onLoad(recipePreview.getId());
            }
        });


    }

    @Override
    public int getItemCount() {
        return recipeArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView duration, foodtitle;
        ImageView foodimagepreview;
        View main;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodtitle = itemView.findViewById(R.id.foodtitle);
            duration = itemView.findViewById(R.id.aduration);
            foodimagepreview = itemView.findViewById(R.id.foodimagepreview);
            main = itemView;
        }
    }
}
