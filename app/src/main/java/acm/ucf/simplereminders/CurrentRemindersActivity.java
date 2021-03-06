package acm.ucf.simplereminders;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.database.DatabaseUtils;
import java.util.ArrayList;
import java.util.List;

import savedreminderssql.SavedRemindersContract;

public class CurrentRemindersActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_reminders);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    // Refresh the listview, put under onStart so when the user presses back it will automatically update
    @Override
    protected void onStart(){

        super.onStart();

        String defaultString = "";
        List<String> reminders = new ArrayList<String>();

        SavedRemindersContract.SimpleRemindersDbHelper mDbHelper = new SavedRemindersContract.SimpleRemindersDbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // This projection defines which columns from the table we will use
        String[] projection = {
                SavedRemindersContract.ReminderEntry.COLUMN_NAME_MONTH,
                SavedRemindersContract.ReminderEntry.COLUMN_NAME_DAY,
                SavedRemindersContract.ReminderEntry.COLUMN_NAME_YEAR,
                SavedRemindersContract.ReminderEntry.COLUMN_NAME_HOUR,
                SavedRemindersContract.ReminderEntry.COLUMN_NAME_MINUTE,
                SavedRemindersContract.ReminderEntry.COLUMN_NAME_DESCRIPTION
        };

        Cursor cursor = db.query(SavedRemindersContract.ReminderEntry.TABLE_NAME, projection, null, null, null, null, null);
        cursor.moveToFirst();
        int month = cursor.getInt(cursor.getColumnIndexOrThrow(SavedRemindersContract.ReminderEntry.COLUMN_NAME_MONTH));
        String test = DatabaseUtils.dumpCursorToString(cursor);


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, reminders);
        ListView eventsListView = (ListView) findViewById(R.id.currentRemindersListView);
        eventsListView.setAdapter(arrayAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.current_reminders, menu);
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

    public void startAddNewReminderActivity(View view){
        Intent intent = new Intent(this, AddReminderActivity.class);
        startActivity(intent);
    }

}
