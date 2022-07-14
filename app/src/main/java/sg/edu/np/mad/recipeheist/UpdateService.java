package sg.edu.np.mad.recipeheist;

import static sg.edu.np.mad.recipeheist.NotificationApp.CHANNEL_1_ID;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class UpdateService extends Service {
    private static final String SHARED_PREFS = "chefUpdates";
    NotificationCompat.Builder notification;
    NotificationManagerCompat notificationManager;
    private Thread t;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressLint("NotificationTrampoline")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null && intent.getAction().equals("STOP_ACTION")) {
            onDestroy();
        }
        else {
            DataBaseHandler dataBaseHandler = new DataBaseHandler(this);

            ////to view updates by clicking notification
            Intent activityIntent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this,
                    0, activityIntent, 0);

            //to stop service from notification
            Intent broadcastIntent = new Intent(this, NotificationReceiverDelete.class);
            PendingIntent actionIntent = PendingIntent.getBroadcast(this,
                    0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_baseline_refresh_24)
                    .setContentTitle("Updating")
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                    .setColor(Color.RED)
                    .setContentIntent(contentIntent)
                    .setOngoing(true)
                    .setOnlyAlertOnce(true)
                    .setProgress(100, 0, false)
                    .addAction(R.mipmap.ic_launcher, "Cancel", actionIntent);

            notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(1, notification.build());
            startForeground(1, notification.build());


            t = new Thread() {
                public void run() {
                    RestDB restDB = new RestDB();
                    String followingResponse = null;
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    //get following array from restDB
                    try {
                        followingResponse = restDB.get("https://recipeheist-567c.restdb.io/rest/users?q={\"userID\":\"" + userID + "\"}&h={\"$fields\":{\"following\":1}}");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (followingResponse != null) {
                        try {
                            //convert following array from restDB
                            JSONArray followingResponseArray = new JSONArray(followingResponse);
                            JSONObject jsonObject = (JSONObject) followingResponseArray.get(0);
                            JSONArray followingArray = jsonObject.getJSONArray("following");
                            if (followingArray.length() != 0) {
                                //go through the following array to get each chef recipe
                                for (int i = 0; i < followingArray.length(); i++) {
                                    String followeduser = followingArray.getString(i);
                                    //display notification of user
                                    notification.setProgress(followingArray.length(), i + 1, false)
                                            .setContentTitle(followeduser);
                                    notificationManager.notify(1, notification.build());
                                    //get followed user recipes
                                    String reciperesponse = restDB.get("https://recipeheist-567c.restdb.io/rest/recipe?q={\"userID\":\"" + followeduser + "\",\"_changed\":{\"$gte\":{\"$date\":\"" + getLastUpdateDate(followeduser) + "\"}}}");
                                    //if successful request
                                    if (reciperesponse != null) {
                                        //save today date for the chef
                                        saveDate(followeduser);
                                        //convert Jsonarray string to RecipeItem class
                                        JSONArray userRecipeArray = new JSONArray(reciperesponse);
                                        if (userRecipeArray.length() != 0) {
                                            for (int j = 0; j < userRecipeArray.length(); j++) {
                                                //Convert json string to RecipePreview object
                                                JSONObject recipeobj = (JSONObject) userRecipeArray.get(j);
                                                String id = recipeobj.getString("_id");
                                                String title = recipeobj.getString("title");
                                                String imagePath = recipeobj.getString("imagePath");
                                                String duration = recipeobj.getString("duration");
                                                RecipePreview recipePreview = new RecipePreview(id, title, imagePath, duration);
                                                //save in SQLite Database
                                                dataBaseHandler.addUpdates(recipePreview);
                                            }
                                        }
                                    }
                                }
                            } else {
                                onDestroy();
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        onDestroy();
                    }
                    onDestroy();
                }
            };
            t.start();
        }
        return START_NOT_STICKY;
    }

    public  String getDefaultDate(){
        //get data from shared preferences
        SharedPreferences sharedPreferences = this.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        //display date data from shared preferences in summary
        return sharedPreferences.getString("defaultupdatedate", "2000-01-01");
    }

    public String getLastUpdateDate(String chefID){
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String lastupdate = sharedPreferences.getString(chefID, getDefaultDate());
        return lastupdate;
    }

    public void saveDate(String chefID) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(chefID, getTodayDate());
        editor.apply();
    }

    public String getTodayDate(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedDate = simpleDateFormat.format(date);

        return formattedDate;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onDestroy() {
        t.interrupt();
        notification.setContentTitle("Update completed")
                .setProgress(0, 0, false)
                .setOngoing(false)
                .mActions.clear();
        notificationManager.notify(1, notification.build());
        stopForeground(false);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
