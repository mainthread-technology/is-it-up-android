package technology.mainthread.apps.isitup.view.listeners;

import android.view.MenuItem;

public interface NavigationDrawerListener {

    /**
     * Called when an item in the navigation drawer is selected.
     *
     * @param menuItem - item that was selected
     */
    void onNavigationDrawerItemSelected(MenuItem menuItem);

    NavigationDrawerListener NO_OP = new NavigationDrawerListener() {
        @Override
        public void onNavigationDrawerItemSelected(MenuItem menuItem) {
        }
    };

}
