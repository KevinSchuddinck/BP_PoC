package com.example.schuddinck.firebasedemo;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.schuddinck.firebasedemo.Activities.AuthActivity;
import com.example.schuddinck.firebasedemo.Activities.DBActivity;
import com.example.schuddinck.firebasedemo.Activities.RemoteConfigActivity;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.Console;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class HomeScreenActivity extends AppCompatActivity
{
    private Unbinder unbinder;
    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        unbinder = ButterKnife.bind(this);

        FirebaseMessaging.getInstance().subscribeToTopic("generalTopic");

        Log.i("HOMESCREEN",FirebaseInstanceId.getInstance().getToken());
    }

    @OnClick(R.id.btnDB)
    public void goToDBActivity(View view)
    {
        firebaseAnalytics.logEvent("user_navigate_DB", Bundle.EMPTY);

        Intent intent = new Intent(this, DBActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btnAuth)
    public void goToAuthActivity(View view)
    {
        firebaseAnalytics.logEvent("user_navigate_auth", Bundle.EMPTY);

        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btnCrashReport)
    public void reportCrash(View view)
    {
        FirebaseCrash.report(new Exception("Crash created by pressing the button on the homescreen."));
    }

    @OnClick(R.id.btnRemoteConfig)
    public void goToRemoteConfigActivity(View view)
    {
        firebaseAnalytics.logEvent("user_navigate_remoteconfig", Bundle.EMPTY);

        Intent intent = new Intent(this, RemoteConfigActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unbinder.unbind();
    }
}
