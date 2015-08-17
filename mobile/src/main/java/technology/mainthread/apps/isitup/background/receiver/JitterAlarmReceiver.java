package technology.mainthread.apps.isitup.background.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import timber.log.Timber;

import static technology.mainthread.apps.isitup.background.service.CheckerIntentService.getCheckerIntentService;

public class JitterAlarmReceiver extends BroadcastReceiver {

    public static Intent getJitterAlarmReceiverIntent(Context context) {
        return new Intent(context, JitterAlarmReceiver.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("onReceive");
        context.startService(getCheckerIntentService(context));
    }
}
