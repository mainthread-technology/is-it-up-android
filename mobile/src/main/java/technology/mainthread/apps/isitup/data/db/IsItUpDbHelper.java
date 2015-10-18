package technology.mainthread.apps.isitup.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static technology.mainthread.apps.isitup.background.service.CheckerIntentService.getCheckerIntentService;

public class IsItUpDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "IS_IT_UP_DB";

    private final Context mContext;

    public IsItUpDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SyncFavouritesTable.DATABASE_CREATE);

        db.execSQL(SyncFavouritesTable.INSERT_SAMPLE_1);
        db.execSQL(SyncFavouritesTable.INSERT_SAMPLE_2);
        db.execSQL(SyncFavouritesTable.INSERT_SAMPLE_3);
        db.execSQL(SyncFavouritesTable.INSERT_SAMPLE_4);

        mContext.startService(getCheckerIntentService(mContext));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SyncFavouritesTable.TABLE_FAVOURITES);
        onCreate(db);
    }
}
