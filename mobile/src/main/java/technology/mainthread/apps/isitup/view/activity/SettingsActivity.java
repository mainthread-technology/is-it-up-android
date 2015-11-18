package technology.mainthread.apps.isitup.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import technology.mainthread.apps.isitup.R;
import technology.mainthread.apps.isitup.view.fragment.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    public static Intent getSettingsActivityIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);

        // set up toolbar
        setSupportActionBar(getToolbar());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getFragmentManager().findFragmentById(R.id.container) == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, SettingsFragment.newInstance())
                    .commit();
        }
    }
}
