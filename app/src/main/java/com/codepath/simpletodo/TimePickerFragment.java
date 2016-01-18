package com.codepath.simpletodo;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment  //example is static
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        DateFormat formatTime = new SimpleDateFormat(
                getResources().getString(R.string.twelve_hour_time_format));
        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                false); //is 24 hour time
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        // set the time
        EditItemActivity.setTime(hourOfDay, minute);
    }
}