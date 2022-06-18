package sg.edu.np.mad.recipeheist.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import sg.edu.np.mad.recipeheist.R;

public class MyRecipeVH extends RecyclerView.ViewHolder {

    public CardView myRecipeCard;
    public TextView foodTitle, noOfLikes;
    public ImageView foodImage;

    public MyRecipeVH(View view){
        super(view);
        myRecipeCard = view.findViewById(R.id.myRecipeCard);
        foodTitle = view.findViewById(R.id.foodName);
        noOfLikes = view.findViewById(R.id.noOfLikes);
        foodImage = view.findViewById(R.id.foodImage);
    }
}
