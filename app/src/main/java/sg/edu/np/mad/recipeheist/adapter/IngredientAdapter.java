package sg.edu.np.mad.recipeheist.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.logging.Handler;

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
    public void onBindViewHolder(@NonNull IngredientVH holder, @SuppressLint("RecyclerView") int position) {
        String itemInList = data.get(position);
        holder.noItem.setText(String.valueOf(position + 1) + ".");
        holder.itemContent.setText(itemInList);

        // when user focus on edit text
        holder.itemContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @SuppressLint({"NotifyDataSetChanged", "ResourceAsColor"})
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    // get current input
                    String newText = holder.itemContent.getText().toString();

                    // check if input is empty
                    if (newText.equals("")){
                        data.remove(position);
                        notifyItemRemoved(position);
                    }
                    else{
                        data.set(position, newText);
                        notifyItemChanged(position);
                    }

                    // remove cancel button visibility
                    holder.cancelBtn.setVisibility(View.GONE);

                    // change background color of cardView
                    holder.cardView.setCardBackgroundColor(Color.parseColor("#666DAA"));
                }
                else{
                    // change background color of card view
                    holder.cardView.setCardBackgroundColor(Color.parseColor("#FF6161"));
                    holder.cancelBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        // when cancel btn is clicked
        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                data.remove(position);
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    

}
