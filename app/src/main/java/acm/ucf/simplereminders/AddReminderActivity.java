package acm.ucf.simplereminders;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.view.View;
import android.widget.Toast;
import acm.ucf.simplereminders.alarmreceiver.AlarmReceiver;
import java.util.Calendar;
import java.util.GregorianCalendar;


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
            EditText description = (EditText) findViewById(R.id.editText);
            EditText reminderTime = (EditText) findViewById(R.id.editText2);

            // Get the date and time
            int month = datePicker.getMonth();
            int day = datePicker.getDayOfMonth();
            int year = datePicker.getYear();

            // Hour is given in military time, so we mod by 12 to get regular time. We need to make sure to do it only if it's bigger
            // than 12, because otherwise, 12 mod 12 = 0 and we don't want that
            Integer raw_hour = timePicker.getCurrentHour(); // Hour directly taken from the time picker, we don't want to modify this because we will use it for the calendar to make
                                                            // a notification at a specified time

            Integer hour = raw_hour;

            if (raw_hour > 12) {
                hour = raw_hour % 12;
            }

            Integer raw_minute = timePicker.getCurrentMinute();
            String minute = raw_minute.toString();
            if (raw_minute < 10) {
                minute = "0" + raw_minute;              // Prefix a 0 if the minute is less than 10
            }

            String reminder = month + 1 + "/" + day + "/" + year + " at " + hour + ":" + minute;    // Finally, build the string



            int beforeTime = Integer.parseInt(reminderTime.getText().toString());       // Get the minutes ahead of time a user wants to be reminded, and parse it to an int

            Context context = view.getContext();
            SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.acm_ucf_simplereminders_reminderdata), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("reminderEvent", reminder);
            editor.apply();

            // Create a calendar and set all the fields to make a reminder at a specific date and time
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.HOUR, raw_hour);
            cal.set(Calendar.MINUTE, raw_minute);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            long millisAtReminder = cal.getTimeInMillis();
            System.out.println(millisAtReminder);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.setAction("android.intent.action.NOTIFY");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            String reminderDescription = description.getText().toString();
            System.out.println(reminderDescription);
            intent.putExtra("Description", reminderDescription);

            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), millisAtReminder, pendingIntent);

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
