package com.codepath.simpletodo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.codepath.simpletodo.eventLogs.EventInfo;
import com.codepath.simpletodo.eventLogs.SocialEventsContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditItemActivity extends AppCompatActivity {

    EditText etNewItem;
    EditText mAddressEditText;
    Spinner mPrioritySelector;
    int mPriority = 0;
    static Button mDateViewButton;
    static Button mTimeViewButton;
    private CheckBox mDoneCheckBox;
    private static Long mEventDate;

    private long mItemId = -1;

    private boolean mNewEvent = true;




    public static final String EXTRA_CONTACT_URI =
            "com.example.android.contactslist.ui.EXTRA_CONTACT_URI";

    private final String BUNDLE_CONTACT_ADDRESS =
            "com.example.android.contactslist.ui.BUNDLE_CONTACT_ADDRESS";
    private final String BUNDLE_EVENT_DATE_TIME =
            "com.example.android.contactslist.ui.BUNDLE_EVENT_DATE_TIME";
    private final String BUNDLE_MESSAGE =
            "com.example.android.contactslist.ui.BUNDLE_EVENT_NOTES";
    private final String BUNDLE_EVENT_DONE =
            "com.example.android.contactslist.ui.BUNDLE_EVENT_DONE";
    private final String BUNDLE_PRIORITY =
            "com.example.android.contactslist.ui.BUNDLE_EVENT_PRIORITY";
    private final String BUNDLE_EVENT_ID =
            "com.example.android.contactslist.ui.BUNDLE_EVENT_ID";
    private final String BUNDLE_EVENT_NEW =
            "com.example.android.contactslist.ui.BUNDLE_EVENT_NEW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etNewItem = (EditText) findViewById(R.id.etNewItem);

        mAddressEditText = (EditText) findViewById(R.id.address_editText);

        mPrioritySelector = (Spinner) findViewById(R.id.spinner);

        mDoneCheckBox = (CheckBox) findViewById(R.id.checkBox);

        //set the adapter to the string-array in the strings resource
        ArrayAdapter<String> selectionAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.array_of_priorities));

        mPrioritySelector.setAdapter(selectionAdapter);

        mPrioritySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPriority = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //nothing
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEvent();
            }
        });


        mDateViewButton = (Button) findViewById(R.id.edit_date_button);
        mTimeViewButton = (Button) findViewById((R.id.edit_time_button));


        if (getIntent().getExtras() != null) {
            // retrieve the arguments from the calling activity

            mItemId = getIntent().getExtras().getLong("todo_id");
        }

        // If being created from a previous state
        if (savedInstanceState != null) {
            // If being recreated from a saved state, sets the contact from the incoming
            // savedInstanceState Bundle

            //setContact((Uri) savedInstanceState.getParcelable(EXTRA_CONTACT_URI));

            //retrieve the saved contact address
            mAddressEditText.setText(savedInstanceState.getString(BUNDLE_CONTACT_ADDRESS));

            //retrieve the saved event notes
            etNewItem.setText(savedInstanceState.getString(BUNDLE_MESSAGE));

            //retrieve the saved date
            mEventDate = savedInstanceState.getLong(BUNDLE_EVENT_DATE_TIME);
            // set the display to the event date and time
            setDateAndTimeViews(mEventDate);

            mPriority = savedInstanceState.getInt(BUNDLE_PRIORITY);

            mDoneCheckBox.setChecked(
                    savedInstanceState.getInt(BUNDLE_EVENT_DONE) == EventInfo.DONE.YES);

            mItemId = savedInstanceState.getLong(BUNDLE_EVENT_ID);

            mNewEvent = savedInstanceState.getBoolean(BUNDLE_EVENT_NEW);

            mPrioritySelector.setSelection(mPriority);


        }else {

            // TODO get item from database

            if(mItemId == -1){
                //Display the current date
                setDateAndTimeViews((long) 0);
            }else {
                loadEventFromDataBase(mItemId);
            }
        }


        // place cursor at very end of text
        int textLength = etNewItem.getText().length();
        etNewItem.setSelection(textLength, textLength);
    }


    private void loadEventFromDataBase(final long id){

        final Context context = this;

        new Thread(new Runnable() {

            @Override
            public void run() {
                //grab contact relevant event data from db
                SocialEventsContract db = new SocialEventsContract(context);
                final EventInfo event = db.getEvent(id);
                db.close();

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // show event
                        displayEvent(event);
                    }
                });

            }
        }).start();
    }

    private void displayEvent(EventInfo eventInfo){
        //retrieve the saved contact address
        mAddressEditText.setText(eventInfo.getAddress());

        //retrieve the saved event notes
        etNewItem.setText(eventInfo.getEventMessage());

        //retrieve the saved date
        mEventDate = eventInfo.getDate();
        // set the display to the event date and time
        setDateAndTimeViews(mEventDate);

        mPriority = eventInfo.getPriority();

        mDoneCheckBox.setChecked(eventInfo.done == EventInfo.DONE.YES);

        mItemId = eventInfo.rowId;

        // label as an existing event
        mNewEvent = false;


        // place cursor at very end of text
        int textLength = etNewItem.getText().length();
        etNewItem.setSelection(textLength, textLength);

        mPrioritySelector.setSelection(mPriority);
    }


    private void setDateAndTimeViews(Long timeInMills) {

        // set the default time to now
        Date date = new Date();

        // if the time is not 0, then we should set the date to it
        if(timeInMills != 0){
            date.setTime(timeInMills);
        }

        mEventDate = date.getTime();

        DateFormat formatDate = new SimpleDateFormat(getResources().getString(R.string.date_format));
        String formattedDate = formatDate.format(date);
        mDateViewButton.setText(formattedDate);

        DateFormat formatTime =
                new SimpleDateFormat(getResources().getString(R.string.twelve_hour_time_format));
        String formattedTime = formatTime.format(date);
        mTimeViewButton.setText(formattedTime);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Saves the contact Uri
        //outState.putParcelable(EXTRA_CONTACT_URI, mContactUri);

        //save the displayed address
        outState.putString(BUNDLE_CONTACT_ADDRESS, mAddressEditText.getText().toString());

        //save the event notes
        outState.putString(BUNDLE_MESSAGE, etNewItem.getText().toString());

        //save the event date/time
        outState.putLong(BUNDLE_EVENT_DATE_TIME, mEventDate);

        //save the event priority
        outState.putInt(BUNDLE_PRIORITY, mPriority);

        outState.putInt(BUNDLE_EVENT_DONE,
                mDoneCheckBox.isChecked() ? EventInfo.DONE.YES : EventInfo.DONE.NO);

        outState.putLong(BUNDLE_EVENT_ID, mItemId);

        outState.putBoolean(BUNDLE_EVENT_NEW, mNewEvent);
    }

    /**
     Sets the time of day of the event, preserving the calendar date that was previously set
     And displays that time.
     */
    static void setTime(int hourOfDay, int minute) { //TODO Can this be private?

        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(mEventDate);
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);

        // set the seconds and milliseconds to 0
        // as there is little use for them in human time setting
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        Date date = c.getTime();

        DateFormat formatTime = new SimpleDateFormat("HH:mm a");
        String formattedDate = formatTime.format(date);
        mTimeViewButton.setText(formattedDate);
        mEventDate = date.getTime();

    }

    /*
    Sets the calendar date of the event, preserving the time that was previously set
    And displays that date.
     */
    static void setDate(int year, int monthOfYear, int dayOfMonth){  //TODO Can this be private?
        //Display the current date

        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(mEventDate);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        // set the seconds and milliseconds to 0
        // as there is little use for them in human time setting
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        Date date = c.getTime();

        DateFormat formatDate = new SimpleDateFormat("MM-dd-yyyy");
        String formattedDate = formatDate.format(date);
        mDateViewButton.setText(formattedDate);
        mEventDate = date.getTime();

    }

    private void saveEvent(){

        final Context context = this;

        final EventInfo event = new EventInfo("","",
                mAddressEditText.getText().toString(),
                mEventDate, etNewItem.getText().toString(),
                mDoneCheckBox.isChecked() ? EventInfo.DONE.YES : EventInfo.DONE.NO,
                mPriority);

        event.setRowId(mItemId);

        if(mNewEvent) {

            new Thread(new Runnable() {

                @Override
                public void run() {
                    //grab contact relevant event data from db
                    SocialEventsContract db = new SocialEventsContract(context);
                    final Long result = db.addIfNewEvent(event);
                    db.close();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if(result != -1){
                                // return to main activity
                                sendResult();

                            }else {
                                //display error
                            }
                        }
                    });

                }
            }).start();
        }else {

            // update old event
            new Thread(new Runnable() {

                @Override
                public void run() {
                    //grab contact relevant event data from db
                    SocialEventsContract db = new SocialEventsContract(context);
                    final int count = db.updateEvent(event);
                    db.close();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if(count > 0){
                                // return to main activity
                                sendResult();

                            }else {
                                //display error
                            }
                        }
                    });

                }
            }).start();

        }
    }

    private void sendResult() {
        Intent returnIntent = new Intent();

        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
