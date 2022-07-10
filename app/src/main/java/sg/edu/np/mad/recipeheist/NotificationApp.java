package sg.edu.np.mad.recipeheist;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationApp extends Application {
    public static final String CHANNEL_1_ID = "updates";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel updateChannel = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Updates",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            updateChannel.setDescription("Get the latest recipe updates from your favourite chefs");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(updateChannel);
        }
    }
}