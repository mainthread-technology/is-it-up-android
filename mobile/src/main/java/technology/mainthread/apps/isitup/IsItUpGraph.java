package technology.mainthread.apps.isitup;

import technology.mainthread.apps.isitup.background.receiver.AlarmReceiver;
import technology.mainthread.apps.isitup.background.receiver.BootReceiver;
import technology.mainthread.apps.isitup.background.service.CheckerIntentService;
import technology.mainthread.apps.isitup.background.service.IsItUpDashClockService;
import technology.mainthread.apps.isitup.ui.activity.MainActivity;
import technology.mainthread.apps.isitup.ui.fragment.BaseFragment;
import technology.mainthread.apps.isitup.ui.fragment.FavouritesFragment;
import technology.mainthread.apps.isitup.ui.fragment.ResultFragment;
import technology.mainthread.apps.isitup.ui.fragment.SettingsFragment;

public interface IsItUpGraph {

    // MainApp
    void inject(IsItUpApp isItUpApp);

    // Background
    void inject(AlarmReceiver alarmReceiver);

    void inject(BootReceiver bootReceiver);

    void inject(CheckerIntentService checkerIntentService);

    void inject(IsItUpDashClockService isItUpDashClockService);

    // UI
    void inject(MainActivity mainActivity);

    void inject(BaseFragment baseFragment);

    void inject(ResultFragment resultFragment);

    void inject(FavouritesFragment favouritesFragment);

    void inject(SettingsFragment settingsFragment);
}
