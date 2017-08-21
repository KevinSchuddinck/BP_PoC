package com.example.schuddinck.firebasedemo.Activities;

import android.app.Activity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.schuddinck.firebasedemo.Comparators.DateComparator;
import com.example.schuddinck.firebasedemo.Models.Message;
import com.example.schuddinck.firebasedemo.R;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DBActivity extends Activity
{
    private static String TAG = "DBActivity";

    @BindView(R.id.txfMessage)
    public EditText txfMessage;

    @BindView(R.id.list)
    public ListView list;

    ArrayAdapter<String> adapter;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseMessagesReference = database.getReference();

    private Unbinder unbinder;
    ArrayList<String> adapterMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        unbinder = ButterKnife.bind(this);

        readChangesFromDB();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, adapterMessages);
        list.setAdapter(adapter);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.btnSend)
    public void sendMessage(View view)
    {
        String body = txfMessage.getText().toString().trim();
        if(!body.equals(""))
        {
            Date date = new Date();
            Message toSendMessage = new Message(body, date);
            databaseMessagesReference.child("messages").child(UUID.randomUUID().toString()).setValue(toSendMessage);
            txfMessage.setText("");

            // logged with firebase crash reporting
            FirebaseCrash.log("User sent message at " + date.toString());
        }
    }

    private void readChangesFromDB()
    {
        databaseMessagesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                // logged with firebase events
                FirebaseCrash.log("User received update of new message");

                List<Message> listMessages = new ArrayList<>();
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren())
                {
                    for(DataSnapshot retrievedMessage : messageSnapshot.getChildren())
                    {
                        Message message = retrievedMessage.getValue(Message.class);
                        listMessages.add(message);
                    }
                }

                if(listMessages.size() > 1)
                {
                    Collections.sort(listMessages, new DateComparator());
                }

                adapterMessages.clear();
                for (Message message : listMessages)
                {
                    adapterMessages.add(message.body);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                FirebaseCrash.report(error.toException());
            }
        });
    }
}
