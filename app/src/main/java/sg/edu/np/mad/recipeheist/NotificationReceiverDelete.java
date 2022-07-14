package sg.edu.np.mad.recipeheist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiverDelete extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Start Services
        context.startService(new Intent(context, UpdateService.class).setAction("STOP_ACTION"));
    }
}