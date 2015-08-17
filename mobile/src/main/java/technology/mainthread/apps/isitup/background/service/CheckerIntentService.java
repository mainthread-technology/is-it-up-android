package technology.mainthread.apps.isitup.background.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.List;

import javax.inject.Inject;

import technology.mainthread.apps.isitup.IsItUpApp;
import technology.mainthread.apps.isitup.R;
import technology.mainthread.apps.isitup.data.db.FavouritesTable;
import technology.mainthread.apps.isitup.data.db.SyncFavourites;
import technology.mainthread.apps.isitup.data.network.IsItUpRequest;
import technology.mainthread.apps.isitup.data.pref.IsItUpPreferences;
import technology.mainthread.apps.isitup.data.vo.IsItUpInfo;
import technology.mainthread.apps.isitup.data.vo.enumeration.StatusCode;
import timber.log.Timber;

public class CheckerIntentService extends IntentService {

    @Inject
    IsItUpRequest request;
    @Inject
    @SyncFavourites
    FavouritesTable favouritesTable;
    @Inject
    IsItUpPreferences preferences;
    @Inject
    NotificationManagerCompat notificationManager;

    private static final int NOTIFICATION_ID = 100;

    public CheckerIntentService() {
        super(CheckerIntentService.class.getSimpleName());
    }

    public static Intent getCheckerIntentService(Context context) {
        return new Intent(context, CheckerIntentService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IsItUpApp.get(this).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        List<IsItUpInfo> favourites = favouritesTable.getAllFavourites();
        Timber.d("%d favourites to sync", favourites.size());

        boolean success = true;
        for (IsItUpInfo favourite : favourites) {
            Timber.d("Checking favourite %s", favourite.getDomain());
            try {
                IsItUpInfo info = request.checkSite(favourite.getDomain());
                if (info != null) {
                    favouritesTable.update(favourite.getId(), info);
                } else {
                    success = false;
                    Timber.w("Failed to update %s", favourite.getDomain());
                }
            } catch (Exception e) {
                success = false;
                Timber.w("Failed to update %s", favourite.getDomain(), e);
            }
        }
        Timber.d("Checking complete");

        if (success) {
            IsItUpDashClockService.updateDashClock(this);

            if (preferences.showNotifications()) {
                List<IsItUpInfo> infos = favouritesTable.getAllForStatusCode(StatusCode.DOWN);
                Timber.d("showing notifications for %d down sites", infos.size());
                if (infos.isEmpty()) {
                    notificationManager.cancel(NOTIFICATION_ID);
                } else if (infos.size() == 1) {
                    showSingleSiteNotification(infos.get(0).getDomain());
                } else if (infos.size() > 1) {
                    showMultipleSiteNotification(infos.size());
                }
            }
        }
    }

    private void showSingleSiteNotification(String domain) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.intent_uri_check, domain))), 0);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.notification_single_site_down, domain))
                .setContentText(getString(R.string.notification_content))
                .setSmallIcon(R.drawable.ic_stat_down_arrow)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (preferences.makeSoundsAndVibrate()) {
            notifyBuilder.setDefaults(Notification.DEFAULT_ALL);
        }

        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }

    private void showMultipleSiteNotification(int size) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.intent_uri_favorites))), 0);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.notification_multiple_sites_down, size))
                .setContentText(getString(R.string.notification_content))
                .setSmallIcon(R.drawable.ic_stat_down_arrow)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (preferences.makeSoundsAndVibrate()) {
            notifyBuilder.setDefaults(Notification.DEFAULT_ALL);
        }

        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }

}
