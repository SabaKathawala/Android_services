// TreasuryService.aidl

package edu.uic.skatha2.services;
import edu.uic.skatha2.services.DailyCash;
// AIDL for bound service "BalanceServiceImpl" that manages the database
// as well as database queries

interface BalanceService {

    /*
     * This zero-argument method reads the text file and creates the SQLite
     * database used for subsequent querying. It returns true or false depending on whether the database was
     * successfully created or not
     */
    boolean createDatabase();

    /*
     * This method takes as input 4 integers: a day, a month, a year, and a number of working days.
     * The first 3 integers denote a date in years 2017 or 2018.
     * The last integer denotes a number of working days between 1 and 30.
     * If successful, this method returns an array of DailyCash instances containing the requested data.
     * Otherwise the method returns a length-zero array. If the database had not been created before this
     * method is called, the method returns a zero-length array
     */
    DailyCash[] dailyCash(int day, int month, int year, int dayRange);
}
