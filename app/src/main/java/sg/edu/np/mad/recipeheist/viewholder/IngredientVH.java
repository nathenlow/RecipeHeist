package sg.edu.np.mad.recipeheist.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import sg.edu.np.mad.recipeheist.R;

public class IngredientVH extends RecyclerView.ViewHolder {

    public TextView noItem, itemContent;

    public IngredientVH (View view){
        super(view);
        noItem = view.findViewById(R.id.noItem);
        itemContent = view.findViewById(R.id.itemContent);
    }
}
