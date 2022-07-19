package sg.edu.np.mad.recipeheist.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sg.edu.np.mad.recipeheist.R;
import sg.edu.np.mad.recipeheist.viewholder.IngredientVH;

public class InstructionAdapter extends RecyclerView.Adapter<IngredientVH> {
    private Context context;
    private ArrayList<String> data;
    private Boolean itemRemoved = false;

    public InstructionAdapter(Context context, ArrayList<String> data){
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public IngredientVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        IngredientVH viewHolder;
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_instruction_item, parent, false);
        viewHolder = new IngredientVH(item);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientVH holder, @SuppressLint("RecyclerView") int position) {
        String itemInList = data.get(position);
        holder.noItem.setText(String.valueOf(holder.getAdapterPosition() + 1) + ".");
        holder.itemContent.setText(itemInList);

        // when user focus on edit text
        holder.itemContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @SuppressLint({"NotifyDataSetChanged", "ResourceAsColor"})
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // get current input
                    String newText = replaceNextLineToSpace(holder.itemContent.getText().toString());

                    // check if input is empty
                    if (newText.equals("")) {
                        data.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                    } else if (itemRemoved) {
                        itemRemoved = false;
                    } else {
                        data.set(holder.getAdapterPosition(), newText);
                        notifyItemChanged(holder.getAdapterPosition());
                    }

                    // remove cancel button visibility
                    holder.cancelBtn.setVisibility(View.GONE);

                    // change background color of cardView
                    holder.cardView.setCardBackgroundColor(Color.parseColor("#666DAA"));
                } else {
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
                itemRemoved = true;
                data.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    // ------------------------------------------------ Start of functions ------------------------------------------------
    // function to replace \n to " "
    public String replaceNextLineToSpace(String string){
        return string.replaceAll("\n", " ");
    }

}
