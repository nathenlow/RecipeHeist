package sg.edu.np.mad.recipeheist.viewholder;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import sg.edu.np.mad.recipeheist.R;

public class IngredientVH extends RecyclerView.ViewHolder {

    public TextView noItem;
    public EditText itemContent;
    public ImageView cancelBtn;
    public CardView cardView;

    public IngredientVH (View view){
        super(view);
        noItem = view.findViewById(R.id.noItem);
        itemContent = view.findViewById(R.id.itemContent);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        cardView = view.findViewById(R.id.cardView);
    }
}
