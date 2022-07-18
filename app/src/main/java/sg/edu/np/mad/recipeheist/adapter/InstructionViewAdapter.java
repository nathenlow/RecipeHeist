package sg.edu.np.mad.recipeheist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import sg.edu.np.mad.recipeheist.R;

public class InstructionViewAdapter extends RecyclerView.Adapter<InstructionViewAdapter.ViewHolder> {
    private JSONArray instructionlist;
    private Context ct;

    public InstructionViewAdapter(Context ct, JSONArray instructionlist){
        this.ct = ct;
        this.instructionlist = instructionlist;
    }

    @NonNull
    @Override
    public InstructionViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ct);
        View view = inflater.inflate(R.layout.instuction_list, parent,false);
        return new InstructionViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionViewAdapter.ViewHolder holder, int position) {
        try {
            holder.instructionNo.setText(String.valueOf(position + 1));
            String instruction = instructionlist.getString(position);
            holder.instructiontxt.setText(instruction);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return instructionlist.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView instructiontxt, instructionNo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            instructiontxt = itemView.findViewById(R.id.instructiontxt);
            instructionNo = itemView.findViewById(R.id.instructioNo);
        }
    }
}
