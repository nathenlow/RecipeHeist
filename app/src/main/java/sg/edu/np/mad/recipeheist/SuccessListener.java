package sg.edu.np.mad.recipeheist;

import org.json.JSONArray;
import org.json.JSONException;

public interface SuccessListener {
    void onSuccess(String jsonresponse) throws JSONException;
}
