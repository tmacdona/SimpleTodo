package com.codepath.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {

    EditText etNewItem;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etNewItem = (EditText) findViewById(R.id.etNewItem);

        if (getIntent().getExtras() != null) {
            // retrieve the arguments from the calling activity
            etNewItem.setText(getIntent().getExtras().getString("todo_text"));

            position = getIntent().getExtras().getInt("position");
        }

        int textLength = etNewItem.getText().length();

        etNewItem.setSelection(textLength, textLength);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResult();
            }
        });
    }

    private void sendResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("position", position);
        returnIntent.putExtra("todo_text", etNewItem.getText().toString());

        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
