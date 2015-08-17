package technology.mainthread.apps.isitup.background.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import java.util.Random;

import javax.inject.Inject;

import technology.mainthread.apps.isitup.IsItUpApp;
import timber.log.Timber;

import static technology.mainthread.apps.isitup.background.receiver.JitterAlarmReceiver.getJitterAlarmReceiverIntent;


public class AlarmReceiver extends BroadcastReceiver {

    @Inject
    AlarmManager alarmManager;

    // Distribute update requests over a period of 5 minutes.
    private static final int MAX_JITTER_MILLIS = 5 * 60 * 1000;

    public static Intent getAlarmReceiverIntent(Context context) {
        return new Intent(context, AlarmReceiver.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        IsItUpApp.get(context).inject(this);

        int relativeTime = new Random().nextInt(MAX_JITTER_MILLIS);
        long time = SystemClock.elapsedRealtime() + relativeTime;
        Timber.d("Jitter receiver will be fired in: %d seconds", (relativeTime / 1000));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, getJitterAlarmReceiverIntent(context), PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, pendingIntent);
    }
}
