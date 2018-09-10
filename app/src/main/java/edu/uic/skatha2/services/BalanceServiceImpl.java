package edu.uic.skatha2.services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BalanceServiceImpl extends Service {
    private DatabaseOpenHelper mDbHelper;
    public BalanceServiceImpl() {
    }

    @Override
    public void onCreate() {
        mDbHelper = new DatabaseOpenHelper(getApplicationContext());
        mDbHelper.getWritableDatabase();
    }

    private final BalanceService.Stub mBinder = new BalanceService.Stub() {

        @Override
        public boolean createDatabase() {
            synchronized (mDbHelper) {
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                //filing in the database
                if(!mDbHelper.dbCreated(db)) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(getResources()
                            .openRawResource(R.raw.treasury_io_final)));
                    try {
                        String line;
                        while ((line = br.readLine()) != null) {
                            String[] values = line.split(",");
                            ContentValues rowData = new ContentValues();
                            rowData.put(DatabaseOpenHelper.YEAR, values[0]);
                            rowData.put(DatabaseOpenHelper.MONTH, values[1]);
                            rowData.put(DatabaseOpenHelper.DAY, values[2]);
                            rowData.put(DatabaseOpenHelper.DAY_OF_WEEK, values[3]);
                            rowData.put(DatabaseOpenHelper.OPENING_AMOUNT, values[4]);
                            rowData.put(DatabaseOpenHelper.CLOSING_AMOUNT, values[5]);

                            db.insert(DatabaseOpenHelper.TABLE_NAME, null,
                                    rowData);
                        }
                    } catch (IOException ioE) {
                        System.out.println(ioE);
                        return false;
                    } catch (SQLException sqlE) {
                        System.out.println(sqlE);
                        return false;
                    } finally {
                        try {
                            br.close();
                        } catch (IOException ioe) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        @Override
        public DailyCash[] dailyCash(int day, int month, int year, int dayRange) {
            synchronized (mDbHelper) {
                DailyCash[] output = new DailyCash[0];
                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                // if not created , return zero length array
                if(!mDbHelper.dbCreated(db)) {
                    return output;
                }
                // Get the ID for the row and fetch all rows with ID >= that ID
                // LIMIT the rows returned using dayRange
                // doesn't work for end of month coming on weekends
                Cursor result = db.query(
                        DatabaseOpenHelper.TABLE_NAME, DatabaseOpenHelper.columns,
                         DatabaseOpenHelper._ID + " >= ( SELECT MIN(" + DatabaseOpenHelper._ID + ") FROM "
                                 + DatabaseOpenHelper.TABLE_NAME + " WHERE "
                                 + DatabaseOpenHelper.YEAR + " = ? AND " + DatabaseOpenHelper.MONTH + " = ? AND " +
                                DatabaseOpenHelper.DAY + " >= ?)",
                        new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day)},
                        null, null, null, String.valueOf(dayRange)
                );

                if(result == null) {
                    return output;
                }

                // in case the month ends on weekends, the above query will fail to
                // retrieve rows for next month
                // this condition takes care of that
                if(result.getCount() == 0) {
                    // if month is December -> search in January of next month
                    if(month == 12) {
                        year += 1;
                        month = 1;
                    }
                    // otherwise increment month and search in that month
                    else {
                        month += 1;
                    }

                    // queries only on year and month, limiting rows returned using dayRange
                    result = db.query(
                            DatabaseOpenHelper.TABLE_NAME, DatabaseOpenHelper.columns,
                            DatabaseOpenHelper._ID + " >= ( SELECT MIN(" + DatabaseOpenHelper._ID + ") FROM "
                                    + DatabaseOpenHelper.TABLE_NAME + " WHERE "
                                    + DatabaseOpenHelper.YEAR + " = ? AND " + DatabaseOpenHelper.MONTH + " = ?)",
                            new String[]{String.valueOf(year), String.valueOf(month)},
                            null, null, null, String.valueOf(dayRange)
                    );
                }

                if(result == null || result.getCount() == 0) {
                    return output;
                }

                // initialize the size using number of rows returned
                output = new DailyCash[result.getCount()];
                int i = 0;

                // create DailyCash array to be returned
                while (result.moveToNext()) {
                    year = result.getInt(0);
                    month = result.getInt(1);
                    day = result.getInt(2);
                    String dayOfWeek = result.getString(3);
                    int cash = result.getInt(4);
                    output[i++] = new DailyCash(day, month, year, dayOfWeek, cash);
                }
                result.close();
                return output;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mDbHelper.getWritableDatabase().close();
        return true;
    }
}