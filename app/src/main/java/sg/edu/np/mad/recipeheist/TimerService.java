package sg.edu.np.mad.recipeheist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Service {

    private static final String CHANNEL_ID = "NotificationChannelID";
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder notifications;
    private Integer initialTimer;
    private boolean firstLoad = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final Integer[] timerRemaining = {intent.getIntExtra("TimeValue", 60)};
        if (firstLoad){
            initialTimer = timerRemaining[0];

            // create notification intent
            Intent notificationIntent = new Intent(this, CountdownTimerActivity.class);
            final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            notifications = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Recipe Heist Timer")
                    .setContentText("Timer: " + initialTimer.toString())
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setColor(Color.RED)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setOnlyAlertOnce(true)
                    .setProgress(initialTimer,100, false);

            notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(1,notifications.build());
            startForeground(1, notifications.build());

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Recipe Heist Timer", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(notificationChannel);

            firstLoad = false;
        }

        System.out.println(initialTimer);
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // reduce time
                timerRemaining[0]--;
                // display it on notification
                notifications.setProgress(initialTimer, timerRemaining[0], false);
                notifications.setContentText("Timer: " + timerRemaining[0].toString());
                notificationManager.notify(1, notifications.build());
                if (timerRemaining[0] <= 0){
                    timer.cancel();
                }
            }
        }, 0, 1000);

        return super.onStartCommand(intent, flags, startId);
    }

}
