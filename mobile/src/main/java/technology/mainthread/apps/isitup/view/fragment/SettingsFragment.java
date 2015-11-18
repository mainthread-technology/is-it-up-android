package technology.mainthread.apps.isitup.view.fragment;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import javax.inject.Inject;

import de.psdev.licensesdialog.LicensesDialog;
import technology.mainthread.apps.isitup.IsItUpApp;
import technology.mainthread.apps.isitup.R;
import technology.mainthread.apps.isitup.background.receiver.CheckerAlarmManager;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    @Inject
    CheckerAlarmManager checkerManager;

    public static Fragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IsItUpApp.get(getActivity()).inject(this);
        addPreferencesFromResource(R.xml.preferences);

        initializeCheckerFrequency();
        setupDashClockClickEvent();
        setOSLicencesClickEvent();
        setVersionName();
    }

    private void initializeCheckerFrequency() {
        String checkerFrequencyKey = getString(R.string.key_checker_frequency);
        Preference checkerFrequency = findPreference(checkerFrequencyKey);
        String checkerFrequencyValue = getPreferenceManager().getSharedPreferences().getString(checkerFrequencyKey, "");
        onPreferenceChange(checkerFrequency, checkerFrequencyValue);
        checkerFrequency.setOnPreferenceChangeListener(this);
    }

    private void setupDashClockClickEvent() {
        findPreference(getString(R.string.key_dashclock)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(getString(R.string.dash_clock_package_name), getString(R.string.dash_clock_config_activity)));
                    startActivity(intent);
                    return true;
                } catch (Exception exception) {
                    return false;
                }
            }
        });
    }

    private void setOSLicencesClickEvent() {
        findPreference(getString(R.string.key_os_licences)).setOnPreferenceClickListener(this);
    }

    private void setVersionName() {
        Preference buildNumber = findPreference(getResources().getString(R.string.key_version));
        try {
            PackageInfo info = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            buildNumber.setSummary(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            buildNumber.setSummary(getResources().getString(R.string.version_name_error));
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(getString(R.string.key_checker_frequency))) {
            boolean enabled = !"0".equals(newValue);
            findPreference(getString(R.string.key_notifications)).setEnabled(enabled);
        }
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.key_os_licences))) {
            showLicencesDialog();
        }
        return true;
    }

    private void showLicencesDialog() {
        new LicensesDialog.Builder(getActivity()).setNotices(R.raw.notices).setIncludeOwnLicense(true).build().show();
    }
}
