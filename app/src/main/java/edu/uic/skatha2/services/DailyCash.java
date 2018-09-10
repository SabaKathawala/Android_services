package edu.uic.skatha2.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ugobuy on 4/25/18.
 */

public class DailyCash implements Parcelable {
    int mDay = 25 ;
    int mMonth = 4 ;
    int mYear = 2018 ;
    int mCash = 8988 ;
    String mDayOfWeek = "Wednesday" ;

    public DailyCash(int mDay, int mMonth, int mYear, String mDayOfWeek, int mCash) {
        this.mDay = mDay;
        this.mMonth = mMonth;
        this.mYear = mYear;
        this.mDayOfWeek = mDayOfWeek;
        this.mCash = mCash;
    }

    public DailyCash() {

    }

    public DailyCash(Parcel in) {
        mDay = in.readInt() ;
        mMonth = in.readInt() ;
        mYear = in.readInt() ;
        mCash = in.readInt() ;
        mDayOfWeek = in.readString() ;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mDay);
        out.writeInt(mMonth) ;
        out.writeInt(mYear) ;
        out.writeInt(mCash) ;
        out.writeString(mDayOfWeek) ;
    }

    public static final Parcelable.Creator<DailyCash> CREATOR
            = new Parcelable.Creator<DailyCash>() {

        public DailyCash createFromParcel(Parcel in) {
            return new DailyCash(in) ;
        }

        public DailyCash[] newArray(int size) {
            return new DailyCash[size];
        }
    };

    public int describeContents()  {
        return 0 ;
    }
}