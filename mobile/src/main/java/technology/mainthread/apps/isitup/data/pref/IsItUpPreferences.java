package technology.mainthread.apps.isitup.data.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import javax.inject.Inject;

import technology.mainthread.apps.isitup.R;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class IsItUpPreferences {

    private final SharedPreferences sharedPreferences;
    private final Resources resources;

    @Inject
    public IsItUpPreferences(Context context, Resources resources) {
        this.sharedPreferences = getDefaultSharedPreferences(context);
        this.resources = resources;
    }

    private int refreshFrequency() {
        String frequencyString = sharedPreferences.getString(resources.getString(R.string.key_checker_frequency),
                resources.getString(R.string.default_checker_frequency));
        return Integer.parseInt(frequencyString);
    }

    public long refreshFrequencyInMillis() {
        int frequency = refreshFrequency();
        return frequency * 60 * 60 * 1000;
    }

    public boolean showNotifications() {
        return sharedPreferences.getBoolean(resources.getString(R.string.key_notifications), true);
    }

    public boolean makeSoundsAndVibrate() {
        return sharedPreferences.getBoolean(resources.getString(R.string.key_sound_vibrate), false);
    }
}