package technology.mainthread.apps.isitup.ui.fragment;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import technology.mainthread.apps.isitup.R;
import technology.mainthread.apps.isitup.ui.listeners.NavigationDrawerListener;
import technology.mainthread.apps.isitup.ui.listeners.ToolbarProvider;

public class NavigationDrawerFragment extends BaseFragment {


    private NavigationDrawerListener drawerListener = NavigationDrawerListener.NO_OP;
    private ActionBarDrawerToggle mDrawerToggle;
    private ToolbarProvider toolbarProvider;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof NavigationDrawerListener) {
            drawerListener = (NavigationDrawerListener) activity;
        }

        try {
            toolbarProvider = (ToolbarProvider) activity;
        } catch (Exception e) {
            throw new ClassCastException("Activity must implement ToolbarProvider.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mNavigationView = (NavigationView) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        return mNavigationView;
    }

    public void setUp(DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // Set up nav view
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        drawerListener.onNavigationDrawerItemSelected(menuItem);
                        return true;
                    }
                });

        // setup toggle
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                mDrawerLayout,
                toolbarProvider.getToolbar(),
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu();
            }
        };

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        drawerListener = NavigationDrawerListener.NO_OP;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

}
