package net.kismetwireless.android.smarterwifimanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dragorn on 8/30/13.
 */
public class SmarterWifiDBHelper extends SQLiteOpenHelper {
    public static final String TABLE_SSID = "ssid";
    public static final String COL_SSID_ID = "_id";
    public static final String COL_SSID_SSID = "ssid";
    public static final String COL_SSID_TIME_S = "timesec";

    public static final String TABLE_CELL = "cell";
    public static final String COL_CELL_ID = "_id";
    public static final String COL_CELL_CELLID = "cellid";
    public static final String COL_CELL_TIME_S = "timesec";

    public static final String TABLE_SSID_CELL_MAP = "ssidcellmap";
    public static final String COL_SCMAP_ID = "_id";
    public static final String COL_SCMAP_SSIDID = "ssidid";
    public static final String COL_SCMAP_CELLID = "cellid";
    public static final String COL_SCMAP_TIME_S = "timesec";

    public static final String TABLE_SSID_BLACKLIST = "ssidblacklist";
    public static final String COL_SSIDBL_ID = "_id";
    public static final String COL_SSIDBL_SSID = "ssid";
    public static final String COL_SSIDBL_BLACKLIST = "blacklist";

    public static final String TABLE_BT_BLACKLIST = "btblacklist";
    public static final String COL_BTBL_ID = "_id";
    public static final String COL_BTBL_MAC = "btmac";
    public static final String COL_BTBL_NAME = "btname";
    public static final String COL_BTBL_BLACKLIST = "blacklist";
    public static final String COL_BTBL_ENABLE = "enable";

    public static final String TABLE_TIMEFRAME= "timeframe";

    public static final String CREATE_SSID_TABLE =
            "CREATE TABLE " + TABLE_SSID + " (" +
                    COL_SSID_ID + " integer primary key autoincrement, " +
                    COL_SSID_SSID + " text, " +
                    COL_SSID_TIME_S + " int " +
                    ");";

    public static final String CREATE_CELL_TABLE =
            "CREATE TABLE " + TABLE_CELL + " (" +
                    COL_CELL_ID + " integer primary key autoincrement, " +
                    COL_CELL_CELLID + " int, " +
                    COL_CELL_TIME_S + " int " +
                    ");";

    public static final String CREATE_SSID_CELL_MAP_TABLE =
            "CREATE TABLE " + TABLE_SSID_CELL_MAP + " (" +
                    COL_SCMAP_ID + " integer primary key autoincrement, " +
                    COL_SCMAP_SSIDID + " int, " +
                    COL_SCMAP_CELLID + " int, " +
                    COL_SCMAP_TIME_S + " int" +
                    ");";

    public static final String CREATE_SSID_BLACKLIST_TABLE =
            "CREATE TABLE " + TABLE_SSID_BLACKLIST + " (" +
                    COL_SSIDBL_ID + " integer primary key autoincrement, " +
                    COL_SSIDBL_SSID + " text," +
                    COL_SSIDBL_BLACKLIST +  " int" +
                    ");";

    public static final String CREATE_BLUETOOTH_BLACKLIST_TABLE =
            "CREATE TABLE " + TABLE_BT_BLACKLIST + " (" +
                    COL_BTBL_ID + " integer primary key autoincrement, " +
                    COL_BTBL_MAC + " text, " +
                    COL_BTBL_NAME + " text, " +
                    COL_BTBL_BLACKLIST + " int," +
                    COL_BTBL_ENABLE + " int" +
                    ");";

    public static final String DATABASE_NAME = "smartermap.db";
    private static final int DATABASE_VERSION = 8;

    public SmarterWifiDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_SSID_TABLE);
        database.execSQL(CREATE_CELL_TABLE);
        database.execSQL(CREATE_SSID_CELL_MAP_TABLE);
        database.execSQL(CREATE_SSID_BLACKLIST_TABLE);
        database.execSQL(CREATE_BLUETOOTH_BLACKLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            db.execSQL("DROP TABLE " + TABLE_SSID_BLACKLIST);
            db.execSQL(CREATE_SSID_BLACKLIST_TABLE);
        }

        if (oldVersion < 7) {
            try {
                db.execSQL("DROP TABLE " + TABLE_BT_BLACKLIST);
            } catch (SQLiteException e) {
                Log.e("smarter", "failed to drop old table, soldiering on: " + e);
            }

            db.execSQL(CREATE_BLUETOOTH_BLACKLIST_TABLE);
        }

        if (oldVersion < 8) {
            Log.d("smarter", "Purging old cell tower format");
            db.execSQL("DELETE FROM " + TABLE_SSID_CELL_MAP);
            db.execSQL("DELETE FROM " + TABLE_CELL);
        }

    }

}
