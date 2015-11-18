package technology.mainthread.apps.isitup.injection.module;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.support.v4.app.NotificationManagerCompat;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.analytics.Tracker;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import technology.mainthread.apps.isitup.IsItUpApp;

@Module
public class IsItUpAppModule {

    private final IsItUpApp application;

    public IsItUpAppModule(IsItUpApp application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    Resources provideApplicationResources() {
        return application.getResources();
    }

    @Provides
    @Singleton
    AlarmManager provideAlarmManager() {
        return (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
    }

    @Provides
    @Singleton
    ConnectivityManager provideConnectivityManager() {
        return (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Provides
    @Singleton
    InputMethodManager inputMethodManager() {
        return (InputMethodManager) application.getSystemService(Service.INPUT_METHOD_SERVICE);
    }

    @Provides
    @Singleton
    NotificationManagerCompat notificationManager() {
        return NotificationManagerCompat.from(application);
    }

    @Provides
    Tracker provideTracker() {
        return application.getTracker();
    }

}
