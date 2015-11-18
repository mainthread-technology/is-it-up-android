package technology.mainthread.apps.isitup.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import technology.mainthread.apps.isitup.model.IsItUpInfo;
import technology.mainthread.apps.isitup.model.StatusCode;

public class SyncFavouritesTable implements FavouritesTable {

    public static final String TABLE_FAVOURITES = "TABLE_FAVOURITES";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DOMAIN = "domain";
    private static final String COLUMN_PORT = "port";
    private static final String COLUMN_STATUS_CODE = "statusCode";
    private static final String COLUMN_RESPONSE_IP = "responseIp";
    private static final String COLUMN_RESPONSE_CODE = "responseCode";
    private static final String COLUMN_RESPONSE_TIME = "responseTime";
    private static final String COLUMN_LAST_CHECKED = "lastChecked";

    private static final String[] PROJECTION = new String[]{
            COLUMN_ID,
            COLUMN_DOMAIN,
            COLUMN_PORT,
            COLUMN_STATUS_CODE,
            COLUMN_RESPONSE_IP,
            COLUMN_RESPONSE_CODE,
            COLUMN_RESPONSE_TIME,
            COLUMN_LAST_CHECKED
    };

    static final String DATABASE_CREATE =
            "CREATE TABLE " + TABLE_FAVOURITES + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY, "
                    + COLUMN_DOMAIN + " TEXT UNIQUE NOT NULL, "
                    + COLUMN_PORT + " INTEGER, "
                    + COLUMN_STATUS_CODE + " INTEGER, "
                    + COLUMN_RESPONSE_IP + " TEXT, "
                    + COLUMN_RESPONSE_CODE + " INTEGER, "
                    + COLUMN_RESPONSE_TIME + " REAL, "
                    + COLUMN_LAST_CHECKED + " INTEGER "
                    + ");";

    // insert sample
    public static final String INSERT_SAMPLE_1 = "INSERT INTO " + TABLE_FAVOURITES
            + " (" + COLUMN_DOMAIN + ") VALUES ('www.google.com')";
    public static final String INSERT_SAMPLE_2 = "INSERT INTO " + TABLE_FAVOURITES
            + " (" + COLUMN_DOMAIN + ") VALUES ('duckduckgo.com')";
    public static final String INSERT_SAMPLE_3 = "INSERT INTO " + TABLE_FAVOURITES
            + " (" + COLUMN_DOMAIN + ") VALUES ('isitup.org')";
    public static final String INSERT_SAMPLE_4 = "INSERT INTO " + TABLE_FAVOURITES
            + " (" + COLUMN_DOMAIN + ") VALUES ('twitter.com')";

    private final SQLiteOpenHelper dbHelper;

    public SyncFavouritesTable(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    private Cursor get(String whereClause, String[] params) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query(TABLE_FAVOURITES, PROJECTION, whereClause, params, null, null, COLUMN_ID + " ASC");
    }

    @Override
    public void add(IsItUpInfo response) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DOMAIN, response.getDomain());
        values.put(COLUMN_PORT, response.getPort());
        values.put(COLUMN_STATUS_CODE, response.getStatusCodeInteger());
        values.put(COLUMN_RESPONSE_IP, response.getResponseIp());
        values.put(COLUMN_RESPONSE_CODE, response.getResponseCode());
        values.put(COLUMN_RESPONSE_TIME, response.getResponseTime());
        values.put(COLUMN_LAST_CHECKED, System.currentTimeMillis());

        db.insertWithOnConflict(TABLE_FAVOURITES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    @Override
    public IsItUpInfo getFavourite(int id) {
        Cursor cursor = get(COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        IsItUpInfo result = null;
        if (cursor.moveToFirst()) {
            result = fromCursor(cursor);
        }
        cursor.close();

        return result;
    }

    @Override
    public IsItUpInfo getFavourite(String domain) {
        Cursor cursor = get(COLUMN_DOMAIN + "=?", new String[]{domain});
        IsItUpInfo result = null;
        if (cursor.moveToFirst()) {
            result = fromCursor(cursor);
        }
        cursor.close();

        return result;
    }

    @Override
    public boolean hasSite(String domain) {
        Cursor cursor = get(COLUMN_DOMAIN + "=?", new String[]{domain});
        boolean result = cursor.moveToFirst();
        cursor.close();

        return result;
    }

    @Override
    public List<IsItUpInfo> getAllFavourites() {
        List<IsItUpInfo> responseList = new ArrayList<>();
        Cursor cursor = get(null, null);

        if (cursor.moveToFirst()) {
            do {
                responseList.add(fromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return responseList;
    }

    @Override
    public List<IsItUpInfo> getAllForStatusCode(StatusCode code) {
        List<IsItUpInfo> responseList = new ArrayList<>();
        Cursor cursor = get(COLUMN_STATUS_CODE + "=?", new String[]{String.valueOf(code.ordinal() + 1)});

        if (cursor.moveToFirst()) {
            do {
                responseList.add(fromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return responseList;
    }

    @Override
    public void update(int id, IsItUpInfo info) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DOMAIN, info.getDomain());
        values.put(COLUMN_PORT, info.getPort());
        values.put(COLUMN_STATUS_CODE, info.getStatusCodeInteger());
        values.put(COLUMN_RESPONSE_IP, info.getResponseIp());
        values.put(COLUMN_RESPONSE_CODE, info.getResponseCode());
        values.put(COLUMN_RESPONSE_TIME, info.getResponseTime());
        values.put(COLUMN_LAST_CHECKED, System.currentTimeMillis());

        db.update(TABLE_FAVOURITES, values, COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        db.close();
    }

    @Override
    public void update(IsItUpInfo info) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DOMAIN, info.getDomain());
        values.put(COLUMN_PORT, info.getPort());
        values.put(COLUMN_STATUS_CODE, info.getStatusCodeInteger());
        values.put(COLUMN_RESPONSE_IP, info.getResponseIp());
        values.put(COLUMN_RESPONSE_CODE, info.getResponseCode());
        values.put(COLUMN_RESPONSE_TIME, info.getResponseTime());
        values.put(COLUMN_LAST_CHECKED, System.currentTimeMillis());

        db.update(TABLE_FAVOURITES, values, COLUMN_DOMAIN + "=?", new String[]{info.getDomain()});
        db.close();
    }

    @Override
    public boolean delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean result = db.delete(TABLE_FAVOURITES, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}) > 0;
        db.close();
        return result;
    }

    @Override
    public boolean delete(String domain) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean result = db.delete(TABLE_FAVOURITES, COLUMN_DOMAIN + "=?",
                new String[]{domain}) > 0;
        db.close();
        return result;
    }

    @Override
    public boolean deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean result = db.delete(TABLE_FAVOURITES, null, null) > 0;
        db.close();
        return result;
    }

    @Override
    public boolean hasFavourites() {
        Cursor cursor = get(null, null);
        return cursor.getCount() != 0;
    }

    private IsItUpInfo fromCursor(Cursor cursor) {
        return IsItUpInfo.builder()
                .id(cursor.getInt(0))
                .domain(cursor.getString(1))
                .port(cursor.getInt(2))
                .statusCode(cursor.getInt(3))
                .responseIp(cursor.getString(4))
                .responseCode(cursor.getInt(5))
                .responseTime(cursor.getDouble(6))
                .lastChecked(cursor.getLong(7))
                .build();
    }
}
