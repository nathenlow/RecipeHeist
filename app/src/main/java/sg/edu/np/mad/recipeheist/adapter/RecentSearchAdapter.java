package sg.edu.np.mad.recipeheist.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import sg.edu.np.mad.recipeheist.DeleteListener;
import sg.edu.np.mad.recipeheist.R;
import sg.edu.np.mad.recipeheist.RecentListener;
import sg.edu.np.mad.recipeheist.SearchFoodFragment;

public class RecentSearchAdapter extends RecyclerView.Adapter<RecentSearchAdapter.ViewHolder> {

    Context ct;
    JSONArray recentlist;
    DeleteListener listener;
    RecentListener fill;

    public RecentSearchAdapter(Context ct, JSONArray recentlist, DeleteListener listener, RecentListener fill) {
        this.ct = ct;
        this.recentlist = recentlist;
        this.listener = listener;
        this.fill = fill;
    }

    @NonNull
    @Override
    public RecentSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ct);
        View view = inflater.inflate(R.layout.recent_search, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentSearchAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int pos = recentlist.length()-position-1;
        String query = "";
        try {
            query = recentlist.get(pos).toString();
            holder.recenttxt.setText(query);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String finalQuery = query;
        holder.recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fill.onRecent(finalQuery);
            }
        });
        holder.deleterecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDelete(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recentlist.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView recenttxt;
        ImageView deleterecent;
        View recent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recenttxt = itemView.findViewById(R.id.recenttxt);
            deleterecent = itemView.findViewById(R.id.deleterecent);
            int[] attrs = new int[]{androidx.appcompat.R.attr.selectableItemBackground};
            TypedArray typedArray = ct.obtainStyledAttributes(attrs);
            int backgroundResource = typedArray.getResourceId(0, 0);
            itemView.setBackgroundResource(backgroundResource);
            recent = itemView;
        }
    }
}
