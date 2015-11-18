package technology.mainthread.apps.isitup.injector.module;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import technology.mainthread.apps.isitup.data.db.AsyncFavourites;
import technology.mainthread.apps.isitup.data.db.AsyncFavouritesTable;
import technology.mainthread.apps.isitup.data.db.FavouritesTable;
import technology.mainthread.apps.isitup.data.db.IsItUpDbHelper;
import technology.mainthread.apps.isitup.data.db.SyncFavourites;
import technology.mainthread.apps.isitup.data.db.SyncFavouritesTable;

@Module
public class DatabaseModule {

    private final SQLiteOpenHelper helper;

    public DatabaseModule(Context context) {
        helper = new IsItUpDbHelper(context);
    }

    @Provides
    @Singleton
    @SyncFavourites
    FavouritesTable syncFavouritesTable() {
        return new SyncFavouritesTable(helper);
    }

    @Provides
    @Singleton
    @AsyncFavourites
    FavouritesTable asyncFavouritesTable(@SyncFavourites FavouritesTable syncFavourites) {
        return new AsyncFavouritesTable(syncFavourites);
    }
}
