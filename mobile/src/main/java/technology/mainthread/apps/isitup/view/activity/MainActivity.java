package technology.mainthread.apps.isitup.view.activity;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import technology.mainthread.apps.isitup.IsItUpApp;
import technology.mainthread.apps.isitup.R;
import technology.mainthread.apps.isitup.view.fragment.FavouritesFragment;
import technology.mainthread.apps.isitup.view.fragment.HomeFragment;
import technology.mainthread.apps.isitup.view.fragment.NavigationDrawerFragment;
import technology.mainthread.apps.isitup.view.listeners.KeyboardManager;
import technology.mainthread.apps.isitup.view.listeners.NavigationDrawerListener;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements NavigationDrawerListener, KeyboardManager {

    @Inject
    InputMethodManager inputMethodManager;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IsItUpApp.get(this).inject(this);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (!canHandleDeepLink()) {
            showFragment(HomeFragment.newInstance());
        }

        // Set up the drawer.
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        navigationDrawerFragment.setUp(mDrawerLayout);
    }

    private boolean canHandleDeepLink() {
        boolean handled = false;
        if (getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            Uri uri = getIntent().getData();

            String domain = null;
            if (uri != null) {
                if ("http".equals(uri.getScheme())) {
                    List<String> pathSegments = uri.getPathSegments();
                    if (pathSegments != null && !pathSegments.isEmpty()) {
                        domain = pathSegments.get(0);
                    }
                } else if ("isitup".equals(uri.getScheme())) {
                    if ("check".equals(uri.getHost())) {
                        List<String> pathSegments = uri.getPathSegments();
                        if (pathSegments != null && !pathSegments.isEmpty()) {
                            domain = pathSegments.get(0);
                        }
                    } else if ("favourites".equals(uri.getHost())) {
                        showFragment(FavouritesFragment.newInstance());
                        handled = true;
                    }
                }
            }
            getIntent().setAction(Intent.ACTION_MAIN);

            if (domain != null && !domain.isEmpty()) {
                // add home fragment before going to check activity
                showFragment(HomeFragment.newInstance());
                startActivity(ResultActivity.getResultActivityIntent(this, domain));
                handled = true;
            }
        }
        return handled;
    }

    @Override
    public void onNavigationDrawerItemSelected(MenuItem menuItem) {
        Timber.d("menu item: %s", menuItem.getTitle());
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                showFragment(HomeFragment.newInstance());
                break;
            case R.id.nav_favourites:
                showFragment(FavouritesFragment.newInstance());
                break;
            case R.id.nav_settings:
                startActivity(SettingsActivity.getSettingsActivityIntent(this));
                break;
            default:
                break;
        }
    }

    private void showFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    public void showKeyboard(View view) {
        inputMethodManager.showSoftInput(view, 0);
    }

    @Override
    public void hideKeyboard() {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }
}
