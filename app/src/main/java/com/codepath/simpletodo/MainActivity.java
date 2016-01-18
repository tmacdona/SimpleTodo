package com.codepath.simpletodo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.simpletodo.eventLogs.EventInfo;
import com.codepath.simpletodo.eventLogs.SocialEventsContentProvider;
import com.codepath.simpletodo.eventLogs.SocialEventsContract;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = "Main Activity";

    private ToDoItemAdapter itemsAdapter;
    private ListView lvItems;

    private TextView mEmpyList;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lvItems = (ListView) findViewById(R.id.lvItems);
        readItems();
        itemsAdapter = new ToDoItemAdapter(this);
        lvItems.setAdapter(itemsAdapter);

        setupListViewListener();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        mEmpyList = (TextView) findViewById(R.id.empty_list_textView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onAddItem(View v) {
        // start the new task activity with a blank form
        startTaskEditActivity(-1);
    }

    private void setupListViewListener() {
/*        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                //items.remove(position);
                itemsAdapter.notifyDataSetChanged();
                //writeItems();
                return true;
            }
        });*/

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "Item Clicked " + position);

                final Cursor data = itemsAdapter.getCursor();

                // Moves to the Cursor row corresponding to the ListView item that was clicked
                data.moveToPosition(position);

                startTaskEditActivity(data.getLong(SocialEventsContract.TableEntry.ROW_ID));
            }
        });
    }

    private void startTaskEditActivity(long dataBaseId) {
        Intent intent = new Intent(this, EditItemActivity.class);
        intent.putExtra("todo_id", dataBaseId);

        startActivityForResult(intent, 111);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        if (requestCode == 111) {
            if (resultCode == Activity.RESULT_OK ) {
                readItems();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.codepath.simpletodo/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onResume(){
        super.onResume();

        readItems();
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.codepath.simpletodo/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    private void readItems() {
        getLoaderManager().restartLoader(ToDoItemQuery.QUERY_ID, null, this);
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String where;

        switch (id) {

            case ToDoItemQuery.QUERY_ID:
                // This query loads data from ToDoProvider.

                //prepare the where and args clause for the lookup
                where = SocialEventsContract.TableEntry.KEY_DONE + " = ? ";

                // by default...
                // find only items that are not done.
                String[] whereArgs = {Integer.toString(EventInfo.DONE.NO)};

                return new android.content.CursorLoader(this,
                        SocialEventsContentProvider.SOCIAL_EVENTS_URI,
                        null,
                        null, null,
                        SocialEventsContract.TableEntry.KEY_EVENT_TIME + " ASC");
        }
        return null;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
            case ToDoItemQuery.QUERY_ID:

                // Moves to the first row in the Cursor
                if (data != null && data.moveToFirst()) {
                    mEmpyList.setVisibility(View.GONE);
                    itemsAdapter.swapCursor(data);
                }else {
                    mEmpyList.setVisibility(View.VISIBLE);
                    itemsAdapter.swapCursor(null);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        // Nothing to do here. The Cursor does not need to be released as it was never directly
        // bound to anything (like an adapter).
        itemsAdapter.swapCursor(null);
    }


    // for getting the list of ToDo items
    public interface ToDoItemQuery{
        int QUERY_ID = 1;
    }

    /**
     * Implementation of a ListAdapter
     */
    private class ToDoItemAdapter extends CursorAdapter {
        private LayoutInflater mInflater; // Stores the layout inflater

        /**
         * Instantiates a new Contacts Adapter.
         *
         * @param context A context that has access to the app's layout.
         */
        public ToDoItemAdapter(Context context) {
            super(context, null, 0);

            // Stores inflater for use later
            mInflater = LayoutInflater.from(context);
        }


        /**
         * Overrides newView() to inflate the list item views.
         */
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            // Inflates the list item layout.
            final View itemLayout =
                    mInflater.inflate(R.layout.to_do_item, viewGroup, false);

            // Creates a new ViewHolder in which to store handles to each view resource. This
            // allows bindView() to retrieve stored references instead of calling findViewById for
            // each instance of the layout.
            final ViewHolder holder = new ViewHolder();
            holder.text1 = (TextView) itemLayout.findViewById(R.id.to_textView);
            holder.text2 = (TextView) itemLayout.findViewById(R.id.date_textView);
            holder.text3 = (TextView) itemLayout.findViewById(R.id.message_textView);
            holder.checkbox = (CheckBox) itemLayout.findViewById(R.id.done_checkBox);
            holder.layout = (RelativeLayout) itemLayout.findViewById(R.id.item_layout);


            // Stores the resourceHolder instance in itemLayout. This makes resourceHolder
            // available to bindView and other methods that receive a handle to the item view.
            itemLayout.setTag(holder);

            // Returns the item layout view
            return itemLayout;
        }

        /**
         * Binds data from the Cursor to the provided view.
         */
        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            // Gets handles to individual view resources
            final ViewHolder holder = (ViewHolder) view.getTag();

            holder.text1.setText(cursor.getString(SocialEventsContract.TableEntry.CONTACT_NAME));

            Date date = new Date();
            date.setTime(cursor.getLong(SocialEventsContract.TableEntry.EVENT_TIME));

            DateFormat formatDate = new SimpleDateFormat("MM-dd-yyyy");
            String dateString = formatDate.format(date);

            holder.text2.setText(dateString);
            holder.text3.setText(cursor.getString(SocialEventsContract.TableEntry.MESSAGE));

            Boolean checked = cursor.getInt(SocialEventsContract.TableEntry.DONE) != 0;
            holder.checkbox.setChecked(checked);

            switch (cursor.getInt(SocialEventsContract.TableEntry.PRIORITY)) {

                default:
                case EventInfo.PRIORITY.HI:
                    holder.text2.setTextColor(getColor(R.color.priority_hi));
                    break;

                case EventInfo.PRIORITY.MED:
                    holder.text2.setTextColor(getColor(R.color.priority_medium));
                    break;

                case EventInfo.PRIORITY.LOW:
                    holder.text2.setTextColor(getColor(R.color.priority_low));
                    break;

                // not currently used
                case EventInfo.PRIORITY.AUTO:
                    holder.text2.setTextColor(Color.BLUE);
                    break;
            }

            holder.id = cursor.getLong(SocialEventsContract.TableEntry.ROW_ID);


            holder.checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateItemAsDone(holder.id, holder.checkbox.isChecked());
                }
            });

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Item Clicked ");

                    startTaskEditActivity(holder.id);
                }
            });

            holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //remove item from database
                    deleteItem(holder.id);

                    return true;
                }
            });

        }

        /**
         * Overrides swapCursor to move the new Cursor into the AlphabetIndex as well as the
         * CursorAdapter.
         */
        @Override
        public Cursor swapCursor(Cursor newCursor) {
            // Update the AlphabetIndexer with new cursor as well
            return super.swapCursor(newCursor);
        }

        /**
         * An override of getCount that simplifies accessing the Cursor. If the Cursor is null,
         * getCount returns zero. As a result, no test for Cursor == null is needed.
         */
        @Override
        public int getCount() {
            if (getCursor() == null) {
                return 0;
            }
            return super.getCount();
        }


        /**
         * A class that defines fields for each resource ID in the list item layout. This allows
         * ContactsAdapter.newView() to store the IDs once, when it inflates the layout, instead of
         * calling findViewById in each iteration of bindView.
         */
        private class ViewHolder {
            TextView text1;
            TextView text2;
            TextView text3;
            CheckBox checkbox;
            Long id;
            RelativeLayout layout;

        }
    }

    private void deleteItem(final Long id) {
        final Context context = this;
        // delete event
        new Thread(new Runnable() {

            @Override
            public void run() {
                //grab contact relevant event data from db
                SocialEventsContract db = new SocialEventsContract(context);

                db.deleteEvent(id);
                db.close();

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //refresh list
                        readItems();
                    }
                });

            }
        }).start();

    }


    private void updateItemAsDone(final long id, final boolean checked) {
        final Context context = this;
        // update old event
        new Thread(new Runnable() {

            @Override
            public void run() {
                //grab contact relevant event data from db
                SocialEventsContract db = new SocialEventsContract(context);

                EventInfo event = db.getEvent(id);
                event.done = checked ? EventInfo.DONE.YES : EventInfo.DONE.NO;
                final int count = db.updateEvent(event);
                db.close();

/*                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if(count > 0){
                            // return to main activity
                            readItems();
                        }
                    }
                });*/

            }
        }).start();
    }

}
