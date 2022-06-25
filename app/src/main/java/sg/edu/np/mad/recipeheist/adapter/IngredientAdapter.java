package sg.edu.np.mad.recipeheist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sg.edu.np.mad.recipeheist.R;
import sg.edu.np.mad.recipeheist.viewholder.IngredientVH;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientVH> {

    private Context context;
    private ArrayList<String> data;

    public IngredientAdapter(Context context, ArrayList<String> data){
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public IngredientVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        IngredientVH viewHolder;
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ingredient_item, parent, false);
        viewHolder = new IngredientVH(item);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientVH holder, int position) {
        String itemInList = data.get(position);
        holder.noItem.setText(String.valueOf(position + 1) + ".");
        holder.itemContent.setText(itemInList);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
