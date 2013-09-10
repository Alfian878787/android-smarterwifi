package net.kismetwireless.android.smarterwifimanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/**
 * Created by dragorn on 8/30/13.
 */
public class SmarterDBSource {
    private SQLiteDatabase dataBase;
    private SmarterWifiDBHelper dataBaseHelper;

    public SmarterDBSource(Context c) throws SQLiteException {
        dataBaseHelper = new SmarterWifiDBHelper(c);

        dataBase = dataBaseHelper.getWritableDatabase();
    }

    public long getTowerDbId(long towerid) {
        long id = -1;

        final String[] idcol = {SmarterWifiDBHelper.COL_CELL_ID};

        String compare = SmarterWifiDBHelper.COL_CELL_CELLID + " = " + towerid;

        Cursor c = dataBase.query(SmarterWifiDBHelper.TABLE_CELL, idcol, compare, null, null, null, null);

        if (c.getCount() <= 0) {
            c.close();
            return -1;
        }

        c.moveToFirst();

        id = c.getLong(0);

        c.close();

        return id;
    }

    // Is this tower associated with any known SSID?
    public boolean queryTowerMapped(long towerid) {
        long tid = getTowerDbId(towerid);

        // If we don't know the tower...
        if (tid < 0)
            return false;

        // Do we have a SSID that uses this?
        final String[] idcol = {SmarterWifiDBHelper.COL_SCMAP_SSIDID};

        String compare = SmarterWifiDBHelper.COL_SCMAP_CELLID + " = " + tid;

        Cursor c = dataBase.query(SmarterWifiDBHelper.TABLE_SSID_CELL_MAP, idcol, compare, null, null, null, null);

        if (c.getCount() <= 0) {
            c.close();
            return false;
        }

        c.close();

        // We map to a ssid
        return true;
    }

    // Update time or create new tower
    public long updateTower(long towerid, long tid) {
        if (tid < 0)
            tid = getTowerDbId(towerid);

        ContentValues cv = new ContentValues();

        if (tid < 0) {
            cv.put(SmarterWifiDBHelper.COL_CELL_CELLID, towerid);
        }

        cv.put(SmarterWifiDBHelper.COL_CELL_TIME_S, System.currentTimeMillis() / 1000);

        String compare = SmarterWifiDBHelper.COL_CELL_ID + " = ?";
        String args[] = {Long.toString(towerid)};

        if (tid < 0)
            dataBase.insert(SmarterWifiDBHelper.TABLE_CELL, null, cv);
        else
            dataBase.update(SmarterWifiDBHelper.TABLE_CELL, cv, compare, args);

        return tid;
    }

    public long getSsidDbId(String ssid) {
        long id = -1;

        final String[] idcol = {SmarterWifiDBHelper.COL_SSID_ID};

        String compare = SmarterWifiDBHelper.COL_SSID_SSID + "=?";
        String[] args = {ssid};

        Cursor c = dataBase.query(SmarterWifiDBHelper.TABLE_SSID, idcol, compare, args, null, null, null);

        if (c.getCount() <= 0) {
            c.close();
            return -1;
        }

        c.moveToFirst();

        id = c.getLong(0);

        c.close();

        return id;
    }

    // Update time or create new ssid
    public long updateSsid(String ssid, long sid) {
        if (sid < 0)
            sid = getSsidDbId(ssid);

        ContentValues cv = new ContentValues();

        if (sid < 0) {
            cv.put(SmarterWifiDBHelper.COL_SSID_SSID, ssid);
        }

        cv.put(SmarterWifiDBHelper.COL_SSID_TIME_S, System.currentTimeMillis() / 1000);

        String compare = SmarterWifiDBHelper.COL_SSID_ID + "=?";
        String args[] = {Long.toString(sid)};

        if (sid < 0)
            dataBase.insert(SmarterWifiDBHelper.TABLE_SSID, null, cv);
        else
            dataBase.update(SmarterWifiDBHelper.TABLE_SSID, cv, compare, args);

        return sid;
    }

    public long getMapId(long sid, long tid) {
        long id = -1;

        final String[] idcol = {SmarterWifiDBHelper.COL_SCMAP_ID};

        String compare = SmarterWifiDBHelper.COL_SCMAP_SSIDID + "=? AND " + SmarterWifiDBHelper.COL_SCMAP_CELLID + "=?";
        String[] args = {Long.toString(sid), Long.toString(tid)};

        Cursor c = dataBase.query(SmarterWifiDBHelper.TABLE_SSID_CELL_MAP, idcol, compare, args, null, null, null);

        if (c.getCount() <= 0) {
            c.close();
            return -1;
        }

        c.moveToFirst();

        id = c.getLong(0);

        c.close();

        return id;
    }

    public void mapTower(String ssid, long towerid) {
        long sid = getSsidDbId(ssid);
        long tid = getTowerDbId(towerid);

        sid = updateSsid(ssid, sid);
        tid = updateTower(towerid, tid);

        long mid = getMapId(sid, tid);

        ContentValues cv = new ContentValues();

        if (mid < 0) {
            cv.put(SmarterWifiDBHelper.COL_SCMAP_CELLID, tid);
            cv.put(SmarterWifiDBHelper.COL_SCMAP_SSIDID, sid);
        }

        cv.put(SmarterWifiDBHelper.COL_SCMAP_TIME_S, System.currentTimeMillis() / 1000);

        String compare = SmarterWifiDBHelper.COL_SCMAP_SSIDID + "=? AND " + SmarterWifiDBHelper.COL_SCMAP_CELLID + "=?";
        String[] args = {Long.toString(sid), Long.toString(tid)};

        if (mid < 0) {
            Log.d("smarter", "Update tower/ssid map for " + towerid + " / " + ssid);
            dataBase.insert(SmarterWifiDBHelper.TABLE_SSID_CELL_MAP, null, cv);
        } else {
            Log.d("smarter", "Mapping tower " + towerid + " to ssid " + ssid);
            dataBase.update(SmarterWifiDBHelper.TABLE_SSID_CELL_MAP, cv, compare, args);
        }

    }

}