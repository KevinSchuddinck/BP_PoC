package com.example.schuddinck.firebasedemo.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.schuddinck.firebasedemo.R;
import com.example.schuddinck.firebasedemo.Utilities.Tools;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AuthActivity extends AppCompatActivity
{
    @BindView(R.id.txfEmailR)
    public EditText txfEmailR;

    @BindView(R.id.txfPasswordR)
    public EditText txfPasswordR;

    @BindView(R.id.txfEmail)
    public EditText txfEmail;

    @BindView(R.id.txfPassword)
    public EditText txfPassword;

    @BindView(R.id.txtLoggedIn)
    public TextView txtLoggedIn;

    @BindView(R.id.btnFBLogin)
    LoginButton btnFBLogin;

    private FirebaseAuth mAuth;

    private Unbinder unbinder;

    private CallbackManager mCallbackManager;

    private AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        unbinder = ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        btnFBLogin.setReadPermissions("email", "public_profile");
        btnFBLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                FirebaseCrash.log("User logged in with Facebook.");

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel()
            {
                FirebaseCrash.log("User cancelled request to log in with Facebook.");
            }

            @Override
            public void onError(FacebookException error) {
                FirebaseCrash.report(error);
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken)
            {
                if (currentAccessToken == null)
                {
                    FirebaseCrash.log("User logged out with Facebook.");

                    updateUI("You are logged out.");
                }
            }
        };
    }

    private void handleFacebookAccessToken(AccessToken token)
    {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            FirebaseCrash.log("Firebase login with Facebook completed.");

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            FirebaseCrash.report(new Exception("Firebase could not login with Facebook."));
                            updateUI("Something went wrong while logging in. Try again.");
                        }
                    }
                });
    }

    @OnClick(R.id.btnLogin)
    public void logIn(View view)
    {
        mAuth.signInWithEmailAndPassword(txfEmail.getText().toString(), txfPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseCrash.log("Firebase login with email successful.");

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else
                        {

                            FirebaseCrash.report(new Exception("Firebase login with email unsuccessful."));

                            updateUI("Something went wrong while logging in. Try again.");
                        }
                    }
                });
    }

    @OnClick(R.id.btnLogout)
    public void logOut(View view)
    {
        FirebaseCrash.log("User logged out.");

        mAuth.signOut();
        updateUI("You are logged out.");
    }

    @OnClick(R.id.btnRegister)
    public void register(View view)
    {
        mAuth.createUserWithEmailAndPassword(txfEmailR.getText().toString(), txfPasswordR.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            FirebaseCrash.log("User registered with email");

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                        }
                        else
                        {
                            // If sign in fails, display a message to the user.

                            FirebaseCrash.report(new Exception("User registration with email failed."));

                            updateUI("Something went wrong while registering your account. Try again.");
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user)
    {
        txtLoggedIn.setText("You are logged in as " + user.getEmail());
        txtLoggedIn.setVisibility(View.VISIBLE);
        Tools.makeTextFieldsEmpty(txfEmail,txfEmailR,txfPassword,txfPasswordR);
    }

    private void updateUI(String message)
    {
        txtLoggedIn.setText(message);
        txtLoggedIn.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            updateUI(currentUser);
        }
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
}
