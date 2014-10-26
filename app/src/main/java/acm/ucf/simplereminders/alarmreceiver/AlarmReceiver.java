package acm.ucf.simplereminders.alarmreceiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import acm.ucf.simplereminders.R;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String description = intent.getStringExtra("Description");
        Notification notification = new Notification.Builder(context).setContentTitle(description).setContentText(description).setSmallIcon(R.drawable.ic_launcher).build();
        nm.notify(0, notification);
    }
}
