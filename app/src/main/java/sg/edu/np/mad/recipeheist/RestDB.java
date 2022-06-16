package sg.edu.np.mad.recipeheist;

import org.json.JSONArray;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RestDB {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    final OkHttpClient client = new OkHttpClient();

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .header("content-type", "application/json")
                .header("x-apikey", "f5ea7cf6ab1df99619a5f6f3300f1edd2f293")
                .header("cache-control", "no-cache")
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }


    String createNewUser(String email, String userID, String username) {
        return "{\"email\":\"" + email + "\","
                + "\"active\":true,"
                + "\"userID\":\"" + userID + "\","
                + "\"username\":\"" + username + "\","
                + "\"description\":\"\","
                + "\"following\":[],"
                + "\"bookmark\":[]}";
    }

    String createRecipe(String title, String description, String duration, int servings, String imagePath, String foodcategory, JSONArray ingredient, JSONArray instructions, String userID) {
        return "{\"title\":\"" + title + "\","
                + "\"description\":\"" + description + "\","
                + "\"duration\":\"" + duration + "\","
                + "\"servings\":" + servings + ","
                + "\"imagePath\":\"" + imagePath + "\","
                + "\"foodcategory\":\"" + foodcategory + "\","
                + "\"ingredient\":" + ingredient + ","
                + "\"instructions\":" + instructions + ","
                + "\"like\":[],"
                + "\"userID\":\"" + userID + "\"}";
    }



}
