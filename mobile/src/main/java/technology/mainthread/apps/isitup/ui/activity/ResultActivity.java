package technology.mainthread.apps.isitup.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import technology.mainthread.apps.isitup.R;
import technology.mainthread.apps.isitup.ui.fragment.ResultFragment;

public class ResultActivity extends BaseActivity {

    private static final String PARAM_DOMAIN = "param_domain";

    public static Intent getResultActivityIntent(Context context, String domain) {
        Intent intent = new Intent(context, ResultActivity.class);
        intent.putExtra(PARAM_DOMAIN, domain);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);

        // set up toolbar
        Toolbar toolbar = getToolbar();
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getFragmentManager().findFragmentById(R.id.container) == null) {
            String domain = getIntent().getStringExtra(PARAM_DOMAIN);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, ResultFragment.newInstance(domain))
                    .commit();
        }
    }

}
