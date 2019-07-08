package com.example.wsins.xmly.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.wsins.xmly.base.BaseApplication;
import com.example.wsins.xmly.utils.Constants;
import com.example.wsins.xmly.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sin on 2019/7/6
 */
public class HistoryDao implements IHistoryDao {

    private static final String TAG = "HistoryDao";
    private final XimalayaDBHelper dbHelper;
    private IHistoryDaoCallback mCallback = null;
    private Object look = new Object();

    public HistoryDao() {
        dbHelper = new XimalayaDBHelper(BaseApplication.getAppContext());
    }

    @Override
    public void setCallback(IHistoryDaoCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void addHistory(Track track) {
        synchronized (look) {
            SQLiteDatabase db = null;
            boolean isAddSuccess = false;
            try {
                db = dbHelper.getWritableDatabase();
                db.beginTransaction();
                ContentValues values = new ContentValues();
                //封装数据
                values.put(Constants.HISTORY_TRACK_ID, track.getDataId());
                values.put(Constants.HISTORY_TITLE, track.getTrackTitle());
                values.put(Constants.HISTORY_PLAY_COUNT, track.getPlayCount());
                values.put(Constants.HISTORY_DURATION, track.getDuration());
                values.put(Constants.HISTORY_UPDATE_TIME, track.getUpdatedAt());
                values.put(Constants.HISTORY_COVER, track.getCoverUrlLarge());
                //插入数据
                db.insert(Constants.HISTORY_TB_NAME, null, values);
                db.setTransactionSuccessful();
                isAddSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
                isAddSuccess = false;
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mCallback != null) {
                    mCallback.onHistoryAdd(isAddSuccess);
                }
            }
        }
    }

    @Override
    public void delHistory(Track track) {
        synchronized (look) {
            SQLiteDatabase db = null;
            boolean isDeleteSuccess = false;
            try {
                db = dbHelper.getWritableDatabase();
                db.beginTransaction();
                int delete = db.delete(Constants.HISTORY_TB_NAME, Constants.HISTORY_TRACK_ID + "=?", new String[]{track.getDataId() + ""});
                LogUtil.d(TAG, "delete --> " + delete);
                db.setTransactionSuccessful();
                isDeleteSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
                isDeleteSuccess = false;
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mCallback != null) {
                    mCallback.onHistoryDel(isDeleteSuccess);
                }
            }
        }
    }

    @Override
    public void cleanHistory() {
        synchronized (look) {
            SQLiteDatabase db = null;
            boolean isCleanSuccess = false;
            try {
                db = dbHelper.getWritableDatabase();
                db.beginTransaction();
                db.delete(Constants.HISTORY_TB_NAME, null, null);
                db.setTransactionSuccessful();
                isCleanSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
                isCleanSuccess = false;
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mCallback != null) {
                    mCallback.onHistoryClean(isCleanSuccess);
                }
            }
        }
    }

    @Override
    public void listHistory() {
        //从数据表中查询历史记录
        synchronized (look) {
            SQLiteDatabase db = null;
            List<Track> result = new ArrayList<>();
            try {
                db = dbHelper.getReadableDatabase();
                db.beginTransaction();
                Cursor query = db.query(Constants.HISTORY_TB_NAME, null, null, null, null, null, "_id desc");
                //封装数据
                while (query.moveToNext()) {
                    Track track = new Track();
                    //
                    int trackId = query.getInt(query.getColumnIndex(Constants.HISTORY_TRACK_ID));
                    track.setDataId(trackId);
                    //
                    String title = query.getString(query.getColumnIndex(Constants.HISTORY_TITLE));
                    track.setTrackTitle(title);
                    //
                    String cover = query.getString(query.getColumnIndex(Constants.HISTORY_COVER));
                    track.setCoverUrlLarge(cover);
                    //
                    int playCount = query.getInt(query.getColumnIndex(Constants.HISTORY_PLAY_COUNT));
                    track.setPlayCount(playCount);
                    //
                    int duration = query.getInt(query.getColumnIndex(Constants.HISTORY_DURATION));
                    track.setDuration(duration);
                    //
                    long updateTime = query.getLong(query.getColumnIndex(Constants.HISTORY_UPDATE_TIME));
                    track.setUpdatedAt(updateTime);
                    result.add(track);
                }
                query.close();
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                //把数据通知出去
                if (mCallback != null) {
                    mCallback.onHistoryLoaded(result);
                }
            }
        }
    }
}
