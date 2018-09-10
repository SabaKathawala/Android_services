package edu.uic.skatha2.services;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TextView;

import java.time.DayOfWeek;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
	
	final static String TABLE_NAME = "treasury_db";
	final static String YEAR = "year";
	final static String MONTH = "month";
	final static String DAY = "day";
	final static String DAY_OF_WEEK = "day_of_week";
	final static String OPENING_AMOUNT = "opening_amount";
	final static String CLOSING_AMOUNT = "closing_amount";
	final static String _ID = "_id";
	final static String[] columns = { YEAR, MONTH, DAY, DAY_OF_WEEK, CLOSING_AMOUNT };

	final private static String CREATE_CMD =

			"CREATE TABLE " + TABLE_NAME + "(" + _ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ YEAR + " INTEGER NOT NULL,"
					+ MONTH + " INTEGER NOT NULL,"
					+ DAY + " INTEGER NOT NULL,"
					+ DAY_OF_WEEK + " TEXT NOT NULL,"
					+ OPENING_AMOUNT + " INTEGER NOT NULL,"
					+ CLOSING_AMOUNT + " INTEGER NOT NULL)";

	final private static String NAME = "treasury";
	final private static Integer VERSION = 1;
	public static final String SELECT_CMD =
			"SELECT " + DatabaseOpenHelper._ID + " FROM " + TABLE_NAME ;

	final private Context mContext;

	public DatabaseOpenHelper(Context context) {
		// Always call superclass's constructor
		super(context, NAME, null, VERSION);

		// Save the context that created DB in order to make calls on that context,
		// e.g., deleteDatabase() below.
		this.mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CMD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// N/A
	}

	// Calls ContextWrapper.deleteDatabase() by way of inheritance
	void deleteDatabase() {
		mContext.deleteDatabase(NAME);
	}

	// check if db exists
	public boolean dbCreated(SQLiteDatabase db) {
		Cursor result = db.rawQuery(SELECT_CMD, new String[0]);
		return result != null && result.getCount() > 0;
	}
}
