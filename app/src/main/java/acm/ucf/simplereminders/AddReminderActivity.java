package acm.ucf.simplereminders;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.view.View;
import android.widget.Toast;
import acm.ucf.simplereminders.alarmreceiver.AlarmReceiver;
import java.util.Calendar;
import savedreminderssql.SavedRemindersContract;

import static savedreminderssql.SavedRemindersContract.*;

public class AddReminderActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setReminder(View view){

        try {
            // Instantiate all our required objects
            DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
            TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
            EditText reminderDescriptionEditText = (EditText) findViewById(R.id.editText);
            EditText reminderTimeEditText = (EditText) findViewById(R.id.editText2);


            // Get the date and time picked by the user
            int month = datePicker.getMonth();
            int day = datePicker.getDayOfMonth();
            int year = datePicker.getYear();

            Integer raw_hour = timePicker.getCurrentHour();     // Hour directly taken from the time picker
            Integer raw_minute = timePicker.getCurrentMinute(); // Minute directly taken from the time picker

            // Create a calendar and set all the fields to make a reminder at a specific date and time
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.HOUR_OF_DAY, raw_hour);
            cal.set(Calendar.MINUTE, raw_minute);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            long millisAtReminder = cal.getTimeInMillis();
            long millisNow = System.currentTimeMillis();
            long millisToDate = millisAtReminder - millisNow;
            long millisToAlarm = millisToDate + SystemClock.elapsedRealtime();

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.setAction("android.intent.action.NOTIFY");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, millisToAlarm, pendingIntent);

            // Hour is given in military time, so we mod by 12 to get regular time. We need to make sure to do it only if it's bigger
            // than 12, because otherwise, 12 mod 12 = 0 and we don't want that
            Integer hour = raw_hour;

            if (raw_hour > 12) {
                hour = raw_hour % 12;
            }

            String minute = raw_minute.toString();
            if (raw_minute < 10) {
                minute = "0" + raw_minute;              // Prefix a 0 if the minute is less than 10
            }

            String reminder = month + 1 + "/" + day + "/" + year + " at " + hour + ":" + minute;    // Finally, build the string

            SavedRemindersContract.SimpleRemindersDbHelper mDbHelper = new SavedRemindersContract.SimpleRemindersDbHelper(getApplicationContext());
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            // Get the description from the user
            String reminderDescription = reminderDescriptionEditText.getText().toString();

            // Make a ContentValues object and store all the information for a given row
            ContentValues values = new ContentValues();
            values.put(ReminderEntry.COLUMN_NAME_MONTH, month);
            values.put(ReminderEntry.COLUMN_NAME_DAY, day);
            values.put(ReminderEntry.COLUMN_NAME_YEAR, year);
            values.put(ReminderEntry.COLUMN_NAME_HOUR, hour);
            values.put(ReminderEntry.COLUMN_NAME_MINUTE, minute);
            values.put(ReminderEntry.COLUMN_NAME_DESCRIPTION, reminderDescription);

            // Insert the value in the database, it will return the new row ID
            long newRowId = db.insert(ReminderEntry.TABLE_NAME, null, values);

        }catch(NumberFormatException e) {

            System.err.println("NOPE");
            Context context = getApplicationContext();
            CharSequence text = "Please enter a reminder time";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }

        Toast toast = Toast.makeText(getApplicationContext(), "Reminder set!", Toast.LENGTH_SHORT);
        toast.show();
        finish();
    }
}
