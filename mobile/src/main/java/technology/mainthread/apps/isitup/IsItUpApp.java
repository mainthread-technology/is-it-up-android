package technology.mainthread.apps.isitup;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.util.HashMap;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import technology.mainthread.apps.isitup.background.receiver.CheckerAlarmManager;
import technology.mainthread.apps.isitup.data.CrashlyticsTree;
import technology.mainthread.apps.isitup.data.StethoUtil;
import timber.log.Timber;

public class IsItUpApp extends Application {

    @Inject
    CheckerAlarmManager checkerAlarmManager;

    public enum TrackerName {
        APP_TRACKER
    }

    private HashMap<TrackerName, Tracker> mTrackers = new HashMap<>();

    private IsItUpComponent component;
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        component = IsItUpComponent.Initializer.init(this);
        component.inject(this);

        refWatcher = LeakCanary.install(this);
        StethoUtil.setupStetho(this); // enabled on debug variants

        // Schedule repeating checker service
        checkerAlarmManager.startOrUpdateAlarm();

        initializeAnalyticsAndSettings();
    }

    private void initializeAnalyticsAndSettings() {
        // setup fabric
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        // setup timber logging
        Timber.plant(BuildConfig.DEBUG ? new Timber.DebugTree() : new CrashlyticsTree());

        // setup google analytics
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        analytics.setDryRun(BuildConfig.DEBUG);
        analytics.getLogger().setLogLevel(Logger.LogLevel.ERROR);

        SharedPreferences userPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        analytics.setAppOptOut(!userPrefs.getBoolean(getString(R.string.key_analytics), true));
        userPrefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(getString(R.string.key_analytics))) {
                    GoogleAnalytics.getInstance(getApplicationContext()).setAppOptOut(!sharedPreferences.getBoolean(key, true));
                } else if (key.equals(getString(R.string.key_checker_frequency))) {
                    checkerAlarmManager.startOrUpdateAlarm();
                }
            }
        });
    }

    public synchronized Tracker getTracker() {
        if (!mTrackers.containsKey(TrackerName.APP_TRACKER)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);

            // setup app tracker
            Tracker t = analytics.newTracker(R.xml.app_tracker);
            t.enableAdvertisingIdCollection(true);

            mTrackers.put(TrackerName.APP_TRACKER, t);
        }
        return mTrackers.get(TrackerName.APP_TRACKER);
    }

    public static IsItUpComponent get(Context context) {
        return ((IsItUpApp) context.getApplicationContext()).component;
    }

    public static RefWatcher getRefWatcher(Context context) {
        IsItUpApp application = (IsItUpApp) context.getApplicationContext();
        return application.refWatcher;
    }

}
