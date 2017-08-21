package com.example.schuddinck.firebasedemo.Activities;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.schuddinck.firebasedemo.BuildConfig;
import com.example.schuddinck.firebasedemo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RemoteConfigActivity extends AppCompatActivity
{
    @BindView(R.id.LLRemoteConfig)
    LinearLayout LLRemoteConfig;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_config);

        unbinder = ButterKnife.bind(this);

        // Get Remote Config instance.
        // [START get_remote_config_instance]
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        // [END get_remote_config_instance]

        // Create a Remote Config Setting to enable developer mode, which you can use to increase
        // the number of fetches available per hour during development. See Best Practices in the
        // README for more information.
        // [START enable_dev_mode]
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        // [END enable_dev_mode]

        // Set default Remote Config parameter values. An app uses the in-app default values, and
        // when you need to adjust those defaults, you set an updated value for only the values you
        // want to change in the Firebase console. See Best Practices in the README for more
        // information.
        // [START set_default_values]
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        // [END set_default_values]

        fetchDataRemoteConfig();
    }

    private void fetchDataRemoteConfig()
    {
        String bgColor = mFirebaseRemoteConfig.getString("Demo_background_color");

        LLRemoteConfig.setBackgroundColor(Color.parseColor(bgColor));

        long cacheExpiration = 3600; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseCrash.log("Data fetch from Remote Config succeeded.");

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            FirebaseCrash.report(new Exception("Fetching data from remote config failed."));
                        }
                        displayDataRemoteConfig();
                    }
                });
    }

    private void displayDataRemoteConfig()
    {
        String welcomeColor = mFirebaseRemoteConfig.getString("Demo_background_color");

        LLRemoteConfig.setBackgroundColor(Color.parseColor(welcomeColor));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unbinder.unbind();
    }
}
