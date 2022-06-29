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

    private ArrayList<RecipePreview> recipeArrayList;
    private RecipeLoadListener load;
    private Context ct;

    public BookmarkAdapter(Context ct, ArrayList<RecipePreview> recipeArrayList, RecipeLoadListener load){
        this.ct = ct;
        this.recipeArrayList = recipeArrayList;
        this.load = load;
    }

    @NonNull
    @Override
    public BookmarkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ct);
        View item = inflater.inflate(R.layout.bookmark_cardview, parent, false);
        return new BookmarkAdapter.ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipePreview recipePreview = recipeArrayList.get(position);
        holder.Title.setText(recipePreview.getTitle());

        String imagefile = recipePreview.getImagePath();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Recipe_image"+imagefile);
        Glide.with(ct).load(storageReference).into(holder.foodimagepreview);

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
        TextView Title;
        ImageView foodimagepreview;
        View main;
        public ViewHolder(View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.savedfoodtitle);
            foodimagepreview = itemView.findViewById(R.id.savedimagepreview);
        }
    }
}
