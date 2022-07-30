package sg.edu.np.mad.recipeheist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Service {

    private static final String CHANNEL_ID = "NotificationChannelID";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final Integer[] timerRemaining = {intent.getIntExtra("TimeValue", 60)};

        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                // reduce time
                timerRemaining[0]--;
                // display it on notification
                notificationUpdate(timerRemaining[0]);
                if (timerRemaining[0] <= 0){
                    timer.cancel();
                }
            }
        }, 0, 1000);

        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void notificationUpdate(Integer timeLeft){

        try{

            Intent notificationIntent = new Intent(this, CountdownTimerActivity.class);
            final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            final Notification[] notifications = {new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Recipe Heist Timer")
                    .setContentText("Timer: " + timeLeft.toString())
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .build()
            };

            startForeground(1, notifications[0]);

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Recipe Heist Timer", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }
}
