package technology.mainthread.apps.isitup.background.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import javax.inject.Inject;

import technology.mainthread.apps.isitup.data.db.FavouritesTable;
import technology.mainthread.apps.isitup.data.db.SyncFavourites;
import technology.mainthread.apps.isitup.data.pref.IsItUpPreferences;
import timber.log.Timber;

import static technology.mainthread.apps.isitup.background.receiver.AlarmReceiver.getAlarmReceiverIntent;

public class CheckerAlarmManager {

    private static final int ALARM_ID = 404;

    private final Context context;
    private final AlarmManager alarmManager;
    private final IsItUpPreferences preferences;
    private final FavouritesTable favouritesTable;
    private final Intent intent;

    @Inject
    public CheckerAlarmManager(Context context,
                               AlarmManager alarmManager,
                               IsItUpPreferences preferences,
                               @SyncFavourites FavouritesTable favouritesTable) {
        this.context = context;
        this.alarmManager = alarmManager;
        this.preferences = preferences;
        this.favouritesTable = favouritesTable;
        this.intent = getAlarmReceiverIntent(context);
    }

    public void startOrUpdateAlarm() {
        Timber.d("startOrUpdateAlarm");
        long intervalMillis = preferences.refreshFrequencyInMillis();
        if (intervalMillis == 0 || !favouritesTable.hasFavourites()) {
            cancelAlarm();
        } else {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, getAlarmReceiverIntent(context), PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), intervalMillis, pendingIntent);
        }
    }

    private void cancelAlarm() {
        Timber.d("cancelAlarm");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    private boolean isActive() {
        // if PendingIntent.getBroadcast with the FLAG_NO_CREATE flag is null then it does not exist
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE) != null;
    }
}
