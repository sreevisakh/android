package com.sv.cc;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
import android.widget.Toast;
public class CurrencyDbAdapter {

	    public static final String KEY_COUNTRY = "country";
	    public static final String KEY_RATE = "rate";
	    public static final String KEY_ROWID = "_id";

	    private static final String TAG = "CurrencyDbAdapter";
	    private DatabaseHelper mDbHelper;
	    private SQLiteDatabase mDb;

	    /**
	     * Database creation sql statement
	     */
	    private static final String DATABASE_CREATE =
	        "create table rates (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
	        + "country TEXT not null, rate NUMERIC not null);";

	    private static final String DATABASE_NAME = "currency";
	    private static final String DATABASE_TABLE = "rates";
	    private static final int DATABASE_VERSION = 2;

	    private final Context mCtx;

	    private static class DatabaseHelper extends SQLiteOpenHelper {

	        DatabaseHelper(Context context) {
	            super(context, DATABASE_NAME, null, DATABASE_VERSION);
	        }

	        @Override
	        public void onCreate(SQLiteDatabase db) {

	            db.execSQL(DATABASE_CREATE);
	        }

	        @Override
	        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
	                    + newVersion + ", which will destroy all old data");
	            db.execSQL("DROP TABLE IF EXISTS rates");
	            onCreate(db);
	        }
	    }

	    public CurrencyDbAdapter(Context ctx) {
	        this.mCtx = ctx;
	    }

	    public CurrencyDbAdapter open() throws SQLException {
	        mDbHelper = new DatabaseHelper(mCtx);
	        mDb = mDbHelper.getWritableDatabase();
	        return this;
	    }

	    public void close() {
	        mDbHelper.close();
	    }


	    /**
	     * Create a new note using the title and body provided. If the note is
	     * successfully created return the new rowId for that note, otherwise return
	     * a -1 to indicate failure.
	     * 
	     * @param title the title of the note
	     * @param body the body of the note
	     * @return rowId or -1 if failed
	     */
	    public long createRate(String country, double rate) {
	        ContentValues initialValues = new ContentValues();
	        initialValues.put(KEY_COUNTRY, country);
	        initialValues.put(KEY_RATE, rate);

	        return mDb.insert(DATABASE_TABLE, null, initialValues);
	        
	    }

	    /**
	     * Delete the note with the given rowId
	     * 
	     * @param rowId id of note to delete
	     * @return true if deleted, false otherwise
	     */
	    public boolean deleteRate(long rowId) {

	        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	    }
	    public boolean deleteAllRate() {

	        return mDb.delete(DATABASE_TABLE, null, null) > 0;
	    }
	    /**
	     * Return a Cursor over the list of all notes in the database
	     * 
	     * @return Cursor over all notes
	     */
	    public Cursor fetchAllRates() {

	        return mDb.query(true,DATABASE_TABLE, new String[] {KEY_ROWID,KEY_COUNTRY}, null, null, null, null, KEY_COUNTRY+" ASC", null);
	        
	    }

	    /**
	     * Return a Cursor positioned at the note that matches the given rowId
	     * 
	     * @param rowId id of note to retrieve
	     * @return Cursor positioned to matching note, if found
	     * @throws SQLException if note could not be found/retrieved
	     */
	    public Cursor fetchRate(long rowId) throws SQLException {

	        Cursor mCursor =

	            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
	            		KEY_COUNTRY, KEY_RATE}, KEY_ROWID + "=" + rowId, null,
	                    null, null, null, null);
	        if (mCursor != null) {
	            mCursor.moveToFirst();
	        }
	        return mCursor;

	    }
	    
	    public double fetchRate(String country) throws SQLException {

	        Cursor mCursor =

	            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
	            		KEY_COUNTRY, KEY_RATE}, KEY_COUNTRY + "='" + country+"'", null,
	                    null, null, null, null);
	        if (mCursor != null) {
	            mCursor.moveToFirst();
	            return mCursor.getFloat(2);
	        }
	        return 0;

	    }

	    /**
	     * Update the note using the details provided. The note to be updated is
	     * specified using the rowId, and it is altered to use the title and body
	     * values passed in
	     * 
	     * @param rowId id of note to update
	     * @param title value to set note title to
	     * @param body value to set note body to
	     * @return true if the note was successfully updated, false otherwise
	     */
	    public boolean updateRate(long rowId,Double rate) {
	        ContentValues args = new ContentValues();
	        args.put(KEY_RATE, rate);

	        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	    }
	    
	    public int containRate(String country) {
	    	 
	    	
		    try {
				Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,KEY_COUNTRY,KEY_RATE}, KEY_COUNTRY + "='" + country + "'", null,null, null, null, null);
				
				if (mCursor != null) {
					if (mCursor.getCount() > 0) {
						
						mCursor.moveToFirst();
						
						Log.i("TEST",String.valueOf(mCursor.getInt(0)));
						return mCursor.getInt(0);

					}
				}
			} catch (Exception e) {
				return -1;
			}
				return -1;
	    }

	    
	}


	
	
