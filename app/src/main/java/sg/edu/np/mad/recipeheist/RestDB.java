package sg.edu.np.mad.recipeheist;


import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RestDB {
    public static final MediaType JSON = MediaType.get("application/json;charset=utf-8");

    final OkHttpClient client = new OkHttpClient();

    // method to get from restdb
    String get(String url) throws IOException{
        Request request = new Request.Builder()
                .header("x-apikey", "f5ea7cf6ab1df99619a5f6f3300f1edd2f293")
                .header("cache-control", "no-cache")
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()){
            return response.body().string();
        }
        catch (Exception e){
            return null;
        }
    }

    void asyncGet(String url, SuccessListener successListener) throws IOException {
        Request request = new Request.Builder()
                .header("x-apikey", "f5ea7cf6ab1df99619a5f6f3300f1edd2f293")
                .header("cache-control", "no-cache")
                .url(url)
                .build();
        // Get a handler that can be used to post to the main thread
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        successListener.onSuccess(response.body().string());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        successListener.onSuccess(null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // method to post to rest db (Create a new document in a collection)
    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
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
        catch (Exception e){
            return null;
        }
    }

    // method to post to rest db (Update a document in a collection)
    String put(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .header("content-type", "application/json")
                .header("x-apikey", "f5ea7cf6ab1df99619a5f6f3300f1edd2f293")
                .header("cache-control", "no-cache")
                .url(url)
                .put(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
        catch (Exception e){
            return null;
        }
    }

    // method to post to rest db (Update one or more properties on a document in a collection)
    String patch(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .header("content-type", "application/json")
                .header("x-apikey", "f5ea7cf6ab1df99619a5f6f3300f1edd2f293")
                .header("cache-control", "no-cache")
                .url(url)
                .patch(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
        catch (Exception e){
            return null;
        }
    }

    String delete(String url) throws IOException{
        Request request = new Request.Builder()
                .header("content-type", "application/json")
                .header("x-apikey", "f5ea7cf6ab1df99619a5f6f3300f1edd2f293")
                .header("cache-control", "no-cache")
                .url(url)
                .delete()
                .build();
        try (Response response = client.newCall(request).execute()){
            return response.body().string();
        }
        catch (Exception e){
            return null;
        }
    }






    String createNewUser(String email, String userID, String username) {
        return "{\"email\":\"" + email + "\","
                + "\"active\":true,"
                + "\"userID\":\"" + userID + "\","
                + "\"username\":\"" + username + "\","
                + "\"description\":\"\","
                + "\"following\":[],"
                + "\"bookmark\":[],"
                + "\"profileimage\":\"\"}";
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
                + "\"userID\":\"" + userID + "\"}";
    }

    String likeRecipe(String recipeID, String userID) {
        return "{\"recipeID\":\"" + recipeID + "\","
                + "\"userID\":\"" + userID + "\"}";
    }

    String bookmarkRecipe(JSONArray bookmark) {
        return "{\"bookmark\":" + bookmark + "}";
    }


    String updateUserDetails(String email, String userId, String username, String description, String profileimage){
        return "{\"email\":\"" + email + "\","
                + "\"active\":true,"
                + "\"userID\":\"" + userId + "\","
                + "\"username\":\"" + username + "\","
                + "\"description\":\"" + description + "\","
                + "\"profileimage\":\"" + profileimage + "\"}";
    }



}
