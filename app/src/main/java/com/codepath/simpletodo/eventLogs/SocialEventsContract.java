package com.codepath.simpletodo.eventLogs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tyson Macdonald on 1/15/2016.
 *
 */
public class SocialEventsContract {
    Context mContext;
    // To access your database, instantiate your subclass of SQLiteOpenHelper:
    EventLogDbHandler mDbHelper;

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public SocialEventsContract(Context context) {
        mContext = context;
        mDbHelper = new EventLogDbHandler (mContext);
    }

    public void close(){
        mDbHelper.close();
    }
    /* Inner class that defines the table contents */
    public static abstract class TableEntry implements BaseColumns {
        //By implementing the BaseColumns interface, your inner class can inherit a primary key field called _ID
        public static final String TABLE_NAME = "eventLog";

 /*
+-----------------------+------------+------------------------------+---+--------+--+
| Field Name            |  Field Type                   | Sample                        |
+-----------------------+------------+------------------------------+---+--------+--+
| ID                    |  PRIMARY KEY [Auto Generated] |  1                            |
| Event TIME            |  Long                         | 555555555555555555            |
| Contact Name          |  TEXT                         | Chintan Khetiya               |
| Contact Key           |  TEXT                         | 787                           |
| CONTACT Address       |  TEXT                         | 555*555-5555/ TYSON@GMAIL.COM |
| Class                 |  Int                          |   1
| NOTES/MESSAGE         |  String                       | Hey, sweetie!                 |
| DONE                  |  int                          | 0                             |
| Priority              |  int                          | 3                             |
+-----------------------+------------+------------------------------+---+--------+------+
*/

        //define the table entries, matching EventInfo.class
        public static final String KEY_CONTACT_NAME = "contact_name";
        public static final String KEY_CONTACT_KEY = "contact_key";
        public static final String KEY_EVENT_TIME = "event_time";
        public static final String KEY_CONTACT_ADDRESS = "contact_address";
        public static final String KEY_CLASS = "class";
        public static final String KEY_MESSAGE = "message";
        public static final String KEY_DONE = "done";
        public static final String KEY_PRIORITY = "priority";

        public final static int ROW_ID = 0;
        public final static int EVENT_TIME = 1;
        public final static int CONTACT_NAME = 2;
        public final static int CONTACT_KEY = 3;
        public final static int CONTACT_ADDRESS = 4;
        public final static int CLASS = 5;
        public final static int MESSAGE = 6;
        public final static int DONE = 7;
        public final static int PRIORITY = 8;

        // provide the name of a column in which the framework can insert NULL 
        // in the event that the ContentValues is empty
        public static final String COLUMN_NAME_NULLABLE = null;
    }


    /*
    Note: Because they can be long-running, be sure that
    getWritableDatabase() or getReadableDatabase() are called in a background thread,
    such as with AsyncTask or IntentService.
     */

    public static class EventLogDbHandler extends SQLiteOpenHelper {

        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "EventLog.db";
        private static final String TEXT_TYPE = " TEXT";
        private static final String LONG_TYPE = " LONG";
        private static final String INT_TYPE = " INT";
        private static final String COMMA_SEP = ", ";


        private static final String SQL_CREATE_ENTRIES = //Another example puts this string in the Helper method
                "CREATE TABLE " + TableEntry.TABLE_NAME + " (" +
                        TableEntry._ID + " INTEGER PRIMARY KEY, " +
                        TableEntry.KEY_EVENT_TIME + LONG_TYPE + COMMA_SEP +
                        TableEntry.KEY_CONTACT_NAME + TEXT_TYPE + COMMA_SEP +
                        TableEntry.KEY_CONTACT_KEY + TEXT_TYPE + COMMA_SEP +
                        TableEntry.KEY_CONTACT_ADDRESS + TEXT_TYPE + COMMA_SEP +
                        TableEntry.KEY_CLASS + INT_TYPE + COMMA_SEP +
                        TableEntry.KEY_MESSAGE + TEXT_TYPE + COMMA_SEP +
                        TableEntry.KEY_DONE + INT_TYPE + COMMA_SEP +
                        TableEntry.KEY_PRIORITY + INT_TYPE + //COMMA_SEP +

                        //... // Any other options for the CREATE command
                        " );";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TableEntry.TABLE_NAME;

        public EventLogDbHandler(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // Creating Tables
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        // Upgrading database
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }


    public interface EventQuery{
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                TableEntry._ID,
                TableEntry.KEY_EVENT_TIME,
                TableEntry.KEY_CONTACT_NAME,
                TableEntry.KEY_CONTACT_KEY,
                TableEntry.KEY_CONTACT_ADDRESS,
                TableEntry.KEY_CLASS,
                TableEntry.KEY_MESSAGE,
                TableEntry.KEY_DONE,
                TableEntry.KEY_PRIORITY
                //...
        };
    }


    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    public long addEvent(EventInfo event){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

    // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        // populate the values table
        values = setValuesFromEvent(values, event);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                TableEntry.TABLE_NAME,
                TableEntry.COLUMN_NAME_NULLABLE, //provides the name of a column in which the framework can insert NULL in the event that the ContentValues is empty
                values);

        db.close();
        return newRowId;
    }


    /*
    helper function to set values with all the table entries from the event
     */
    private ContentValues setValuesFromEvent(ContentValues values, EventInfo event) {
        values.put(TableEntry.KEY_EVENT_TIME, event.getDate());
        values.put(TableEntry.KEY_CONTACT_NAME, event.getContactName());
        values.put(TableEntry.KEY_CONTACT_KEY, event.getContactKey());
        values.put(TableEntry.KEY_CONTACT_ADDRESS, event.getAddress());
        values.put(TableEntry.KEY_CLASS, event.getEventClass());
        values.put(TableEntry.KEY_MESSAGE, event.getEventMessage());
        values.put(TableEntry.KEY_DONE, event.getCompletness());
        values.put(TableEntry.KEY_PRIORITY, event.getPriority());

        return values;
    }

    public long checkEventExists(EventInfo event){

        String contactName = event.getContactName();
        String cursorContactName = null;
        long rowId = -1;

        if (contactName == null){
            Log.d(" Check Event Exists", "Name is null");
        }

        SQLiteDatabase db = mDbHelper.getReadableDatabase();


        //What best to search for?
        String selection = TableEntry.KEY_EVENT_TIME;
        String selection_arg = Long.toString(event.getDate());

        //format selection and search
        selection = selection + " =? ";
        String[] selectionArgs = {selection_arg};

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                TableEntry.KEY_EVENT_TIME + " DESC";

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                TableEntry._ID,
                TableEntry.KEY_CONTACT_NAME,
                TableEntry.KEY_EVENT_TIME
        };

        Cursor cursor = db.query(
                TableEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        try {
            if (cursor.moveToFirst()) {
                do {
                    cursorContactName = cursor.getString(1);
                    if ((contactName == null && cursorContactName == null)
                            || contactName.equals(cursorContactName)
                        //|| (event.getDate() == cursor.getLong(TableEntry.EVENT_TIME))
                            ) {
                        rowId = cursor.getLong(TableEntry.ROW_ID);
                        break; //return the id of the clashing event
                    }
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.e(" Check Event Exists", "Dead Cursor");
            cursor.close();
            db.close();
            return 0;  // There is an error
        }
       cursor.close();
        db.close();
        return rowId;  // -1 means there is no record of this event, so it's probably new
    }

    public long addIfNewEvent(EventInfo event){
        long id = -1;
        if(checkEventExists(event) == -1) { // if event is likely new
            id = addEvent(event);
        }
        return id;  //return -1 for events
    }


    public EventInfo getEvent(long rowId){

        // Define 'where' part of query.
        String selection = TableEntry._ID + " =?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(rowId) };
        // Issue SQL statement.
        final EventInfo value = getEvent(selection, selectionArgs);

        return value;    }

    public EventInfo getEvent(String selection,   String selectionArgs[] ){
    // selection needs to be a string from TableEntry.class
    // selection_arg needs to be the string value of the item filtering for.  String.valueOf(long_number)

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        EventInfo event = null;

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                TableEntry.KEY_CONTACT_NAME + " DESC";


        Cursor cursor = db.query(
                TableEntry.TABLE_NAME,  // The table to query
                EventQuery.projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );


        // organize the event info and pass it back
        if (cursor.moveToFirst()) {

            //populate the event from the cursor
            event = setEventInfoFromCursor(event, cursor);

        }

        db.close();
        cursor.close();
        return event;
    }

    /*
    load the cursor information into the eventInfo
     */
    public EventInfo setEventInfoFromCursor(EventInfo event, Cursor cursor) {
        event = new EventInfo(cursor.getString(TableEntry.CONTACT_NAME),
                cursor.getString(TableEntry.CONTACT_KEY),
                cursor.getString(TableEntry.CONTACT_ADDRESS),
                cursor.getLong(TableEntry.EVENT_TIME),
                cursor.getString(TableEntry.MESSAGE),
                cursor.getInt(TableEntry.DONE),
                cursor.getInt(TableEntry.PRIORITY)
        );
        event.setEventClass(cursor.getInt(TableEntry.CLASS));
        event.setRowId(cursor.getLong(TableEntry.ROW_ID));

        return event;
    }

    public int updateEvent(EventInfo event){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Since this is an update, we should already have a rowId of _ID
        long rowId = event.getRowId();

        ContentValues values = new ContentValues();

        // populate the values table
        values = setValuesFromEvent(values, event);

// Which row to update, based on the ID
        String selection = TableEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(rowId) }; //TODO: Review what is needed

        int count = db.update(
                TableEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        db.close();
        return count;
    }


    public int deleteEvent(long rowId){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = TableEntry._ID + " =?";
// Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(rowId) };
// Issue SQL statement.
        final int value = db.delete(TableEntry.TABLE_NAME, selection, selectionArgs);

        db.close();

        return value;    }


    public int deleteEvent(EventInfo event){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = TableEntry._ID + " =?";
// Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(event.getRowId()) };
// Issue SQL statement.
        final int value = db.delete(TableEntry.TABLE_NAME, selection, selectionArgs);

        db.close();

        return value;
    }

    public void deleteAllEvents(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.execSQL("delete from "+ TableEntry.TABLE_NAME);
    }

    // Getting All Events -- based on http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
    public List<EventInfo> getAllEvents() {
        List<EventInfo> eventList = new ArrayList<EventInfo>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TableEntry.TABLE_NAME;

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        EventInfo event = null;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //populate the event from the cursor
                event = setEventInfoFromCursor(event, cursor);

                eventList.add(event);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        // return contact list
        return eventList;
    }


    // Getting contacts Count
    public int getEventCount() {
        int count;
        String countQuery = "SELECT  * FROM " + TableEntry.TABLE_NAME;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
       cursor.close();
        db.close();
        // return count
        return count;
    }



    public List<EventInfo> getEventsInDateRange(String contactLookupKey, int dataFeedClass, long startDate, long endDate){

        // TODO set size of eventlist at time of initialization according to number of cursor entries
        List<EventInfo> eventList = new ArrayList<EventInfo>();

        // Select All Query
        String where = TableEntry.KEY_CONTACT_KEY + " = ? AND "
                + TableEntry.KEY_CLASS + " = ? AND "
                + TableEntry.KEY_EVENT_TIME + " BETWEEN ? AND ? ";
        //String where = TableEntry.KEY_EVENT_TIME + " >= ? AND "
         //       + TableEntry.KEY_EVENT_TIME + " < ?";

        String[] whereArgs = {contactLookupKey,
                Integer.toString(dataFeedClass),
                Long.toString(startDate), Long.toString(endDate)};

        if(dataFeedClass == EventInfo.ALL_CLASS){
            // return data from all classes (sources)
            where = TableEntry.KEY_CONTACT_KEY + " = ? AND "
                    + TableEntry.KEY_EVENT_TIME + " BETWEEN ? AND ? ";

            String[] Args = {contactLookupKey,
                    Long.toString(startDate), Long.toString(endDate)};

            whereArgs = Args;
        }

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                TableEntry.KEY_EVENT_TIME + " ASC";


        SQLiteDatabase db = mDbHelper.getWritableDatabase();


        Cursor cursor = db.query(
                TableEntry.TABLE_NAME,  // The table to query
                EventQuery.projection,                               // The columns to return
                where,                                // The columns for the WHERE clause
                whereArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        EventInfo event = null;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //populate the event from the cursor
                event = setEventInfoFromCursor(event, cursor);

                // Adding contact to list
                eventList.add(event);

               // Log.d("getEventsInDateRange ", "col_date: " + event.getDate());
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        // return contact list
        return eventList;

    }


    public List<EventInfo> getEventsForContact(String contactLookupKey, int dataFeedClass){
        List<EventInfo> eventList = new ArrayList<EventInfo>();

        // Select All Query
        String where = TableEntry.KEY_CONTACT_KEY + " = ? AND "
                + TableEntry.KEY_CLASS + " = ? ";

        String[] whereArgs = {contactLookupKey,
                Integer.toString(dataFeedClass)};

        if(dataFeedClass == EventInfo.ALL_CLASS){
            // return data from all classes (sources)
            where = TableEntry.KEY_CONTACT_KEY + " = ? ";

            String[] Args = {contactLookupKey};

            whereArgs = Args;
        }

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                TableEntry.KEY_EVENT_TIME + " ASC";
        //some classes (such as UpdateStats) depend on ascending sort order on time


        SQLiteDatabase db = mDbHelper.getWritableDatabase();


        Cursor cursor = db.query(
                TableEntry.TABLE_NAME,  // The table to query
                EventQuery.projection,                               // The columns to return
                where,                                // The columns for the WHERE clause
                whereArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        EventInfo event = null;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //populate the event from the cursor
                event = setEventInfoFromCursor(event, cursor);

                // Adding contact to list
                eventList.add(event);

                // Log.d("getEventsInDateRange ", "col_date: " + event.getDate());
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        // return contact list
        return eventList;

    }

}
