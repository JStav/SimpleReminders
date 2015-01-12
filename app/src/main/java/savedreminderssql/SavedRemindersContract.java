/* Defines the schema for the SQL database that will store the reminders.
* */

package savedreminderssql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class SavedRemindersContract {

    // Empty constructor to prevent the class from being accidentally instantiated.
    public SavedRemindersContract(){}


    // Table contents class
    public static abstract class ReminderEntry implements BaseColumns{

        public static final String TABLE_NAME = "reminder";
        public static final String COLUMN_NAME_MONTH = "month";
        public static final String COLUMN_NAME_DAY = "day";
        public static final String COLUMN_NAME_YEAR = "year";
        public static final String COLUMN_NAME_HOUR = "hour";
        public static final String COLUMN_NAME_MINUTE = "minute";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
    }


    // SQL methods
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    /* Building the SQL command to create a table in the format:
     *
     *      CREATE TABLE "tablename"
     *      ("column1" "data type",
     *      "column2" "data type",
     *      "column3" "data type");
     */
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ReminderEntry.TABLE_NAME + " (" +
            ReminderEntry.COLUMN_NAME_MONTH         + INT_TYPE + COMMA_SEP +
            ReminderEntry.COLUMN_NAME_DAY           + INT_TYPE + COMMA_SEP +
            ReminderEntry.COLUMN_NAME_YEAR          + INT_TYPE + COMMA_SEP +
            ReminderEntry.COLUMN_NAME_HOUR          + INT_TYPE + COMMA_SEP +
            ReminderEntry.COLUMN_NAME_MINUTE        + INT_TYPE + COMMA_SEP +
            ReminderEntry.COLUMN_NAME_DESCRIPTION   + TEXT_TYPE  +
            " )";

    // Delete the table
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ReminderEntry.TABLE_NAME;


    // SQLiteHelper implementation, must implement onCreate and onUpgrade. onDowngrade is optional.
    public static class SimpleRemindersDbHelper extends SQLiteOpenHelper{

        // When changing the schema of the database, we must increment DATABASE_VERSION
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "SimpleReminders.db";

        public SimpleRemindersDbHelper (Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        // Whenever the db version is changed, onUpgrade is called.
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
            onUpgrade(db, oldVersion, newVersion);
        }
    }

}
