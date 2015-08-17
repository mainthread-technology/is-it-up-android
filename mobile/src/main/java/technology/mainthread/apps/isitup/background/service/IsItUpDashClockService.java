package technology.mainthread.apps.isitup.background.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import java.util.List;

import javax.inject.Inject;

import technology.mainthread.apps.isitup.IsItUpApp;
import technology.mainthread.apps.isitup.R;
import technology.mainthread.apps.isitup.data.db.AsyncFavourites;
import technology.mainthread.apps.isitup.data.db.FavouritesTable;
import technology.mainthread.apps.isitup.data.vo.IsItUpInfo;
import technology.mainthread.apps.isitup.data.vo.enumeration.StatusCode;
import timber.log.Timber;

public class IsItUpDashClockService extends DashClockExtension {

    @Inject
    @AsyncFavourites
    FavouritesTable favouritesTable;

    private static final String ARS_CHANGED_INTENT = "technology.mainthread.apps.isitup.dashclock.refresh";

    public static void updateDashClock(Context context) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ARS_CHANGED_INTENT));
    }

    private SRChangeReceiver srChangeReceiver;

    private class SRChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.d("IsItUp favourites refreshed broadcast");
            onUpdateData(UPDATE_REASON_CONTENT_CHANGED);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IsItUpApp.get(this).inject(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (srChangeReceiver != null)
            try {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(srChangeReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @Override
    protected void onInitialize(boolean isReconnect) {
        super.onInitialize(isReconnect);
        setUpdateWhenScreenOn(true);
        if (srChangeReceiver != null) {
            try {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(srChangeReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        IntentFilter intentFilter = new IntentFilter(ARS_CHANGED_INTENT);
        srChangeReceiver = new SRChangeReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(srChangeReceiver, intentFilter);
    }

    @Override
    protected void onUpdateData(int reason) {
        List<IsItUpInfo> downSites = favouritesTable.getAllForStatusCode(StatusCode.DOWN);
        if (downSites.isEmpty()) {
            // hide if no sites are down
            publishUpdate(new ExtensionData().visible(false));
        } else if (downSites.size() == 1) {
            //show single down site
            String domain = downSites.get(0).getDomain();
            publishUpdate(new ExtensionData()
                    .visible(true)
                    .icon(R.drawable.ic_stat_down_arrow)
                    .status(getString(R.string.dashclock_status_down))
                    .expandedTitle(getString(R.string.notification_single_site_down, domain))
                    .clickIntent(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.intent_uri_check, domain)))));
        } else if (downSites.size() > 1) {
            // show multiple down sites
            publishUpdate(new ExtensionData()
                    .visible(true)
                    .icon(R.drawable.ic_stat_down_arrow)
                    .status(getString(R.string.dashclock_status_down))
                    .expandedTitle(getString(R.string.notification_multiple_sites_down, downSites.size()))
                    .expandedBody(getExpandedBodyText(downSites))
                    .clickIntent(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.intent_uri_favorites)))));
        }
    }


    private String getExpandedBodyText(List<IsItUpInfo> isItUpInfos) {
        StringBuilder out = new StringBuilder();
        out.append(getString(R.string.dashclock_expanded_text));
        for (IsItUpInfo isItUpInfo : isItUpInfos) {
            out.append(isItUpInfo.getDomain()).append("\n");
        }
        return out.toString();
    }
}
