package sg.edu.np.mad.recipeheist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

public class NotificationReceiverDelete extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Start Services
        Intent deleteintent = new Intent(context, UpdateService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(deleteintent.setAction("STOP_ACTION"));
        } else {
            context.startService(deleteintent.setAction("STOP_ACTION"));
        }
    }
}