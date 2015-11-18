package technology.mainthread.apps.isitup.data.db;

import android.os.AsyncTask;

import java.util.List;

import technology.mainthread.apps.isitup.model.IsItUpInfo;
import technology.mainthread.apps.isitup.model.StatusCode;

public class AsyncFavouritesTable implements FavouritesTable {

    private final FavouritesTable syncFavouritesTable;

    public AsyncFavouritesTable(FavouritesTable syncFavouritesTable) {
        this.syncFavouritesTable = syncFavouritesTable;
    }

    @Override
    public void add(final IsItUpInfo response) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                syncFavouritesTable.add(response);
                return null;
            }
        }.execute();
    }

    @Override
    public IsItUpInfo getFavourite(int id) {
        return syncFavouritesTable.getFavourite(id);
    }

    @Override
    public IsItUpInfo getFavourite(String domain) {
        return syncFavouritesTable.getFavourite(domain);
    }

    @Override
    public boolean hasSite(String site) {
        return syncFavouritesTable.hasSite(site);
    }

    @Override
    public List<IsItUpInfo> getAllFavourites() {
        return syncFavouritesTable.getAllFavourites();
    }

    @Override
    public List<IsItUpInfo> getAllForStatusCode(@StatusCode int code) {
        return syncFavouritesTable.getAllForStatusCode(code);
    }

    @Override
    public void update(final int id, final IsItUpInfo info) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                syncFavouritesTable.update(id, info);
                return null;
            }
        }.execute();
    }

    @Override
    public void update(final IsItUpInfo info) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                syncFavouritesTable.update(info);
                return null;
            }
        }.execute();
    }

    @Override
    public boolean delete(final int id) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                syncFavouritesTable.delete(id);
                return null;
            }
        }.execute();
        return true;
    }

    @Override
    public boolean delete(final String domain) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                syncFavouritesTable.delete(domain);
                return null;
            }
        }.execute();
        return true;
    }

    @Override
    public boolean deleteAll() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                syncFavouritesTable.deleteAll();
                return null;
            }
        }.execute();
        return true;
    }

    @Override
    public boolean hasFavourites() {
        return syncFavouritesTable.hasFavourites();
    }
}
