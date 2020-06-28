package com.example.edugorilla;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity
{
    //   MainActivity
    static Activity fa;

    private static final String TAG = "MainActivity";

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private MaterialButton materialButtonLogin;
    private MaterialButton materialButtonGoogle;
    private TextView textViewCreateAnAccount;

    private ProgressDialog progressDialog;

    //    firebase
    FirebaseAuth firebaseAuth;

//  network
    AlertDialog alertDialog;

    //    sign in with google
    GoogleSignInClient googleSignInClient;
    private static final int RESULT_CODE_SINGIN = 121;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        fa = this;

        //for network register broadcast
        alertMessage();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null)
        {
            Intent intent = new Intent(getApplicationContext(), UserDataActivity.class);

            String s = getIntent().getStringExtra("fromNotification");
            String title = getIntent().getStringExtra("title");
            String message = getIntent().getStringExtra("message");

            if(s != null)
            {
                intent.putExtra("fromNotification","yes");
                intent.putExtra("title", title);
                intent.putExtra("message", message);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            }

            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();


//        MainActivity
        firebaseAuth = FirebaseAuth.getInstance();

        textInputLayoutEmail = findViewById(R.id.idTextInputLayoutEmailLoginActivity);
        textInputLayoutPassword = findViewById(R.id.idTextInputLayoutPasswordLoginActivity);
        materialButtonLogin = findViewById(R.id.idButtonLoginLoginActivity);
        materialButtonGoogle = findViewById(R.id.idButtonGmailLoginActivity);
        textViewCreateAnAccount = findViewById(R.id.idTextViewCreateAnAccountLoginActivity);

        progressDialog = new ProgressDialog(this);

        textInputLayoutEmail.getEditText().setOnTouchListener(new View.OnTouchListener()
        {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                textInputLayoutEmail.setErrorEnabled(false);
                return false;
            }
        });

        textInputLayoutPassword.getEditText().setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                textInputLayoutPassword.setErrorEnabled(false);
                return false;
            }
        });

        materialButtonLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                userLogin();
            }
        });

        textViewCreateAnAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.textview_animation);
                animation.reset();

                textViewCreateAnAccount.clearAnimation();
                textViewCreateAnAccount.startAnimation(animation);

                startActivity(new Intent(getApplicationContext(), NewAccountActivity.class));
            }
        });

        //        sign in with google

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        materialButtonGoogle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                signInWithGoogle();
            }
        });

//        handle fcm notifications
        String s = getIntent().getStringExtra("fromNotification");
        String title = getIntent().getStringExtra("title");
        String message = getIntent().getStringExtra("message");

        if(s != null)
        {
            // show your dialog here
            alertNotificationFCM(title, message);
            Log.d(TAG, "onCreate: "+"Gautam");

        }
    }

    //Mainactivity

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int network = NetworkUtil.getConnectivityStatus(context);

            if(network == 0)
            {
                alertDialog.show();
            }
            else
            {
                alertDialog.dismiss();
            }
        }
    };



    public void alertMessage()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage("Please connect with to working internet connection");

        builder.setTitle("Network Error!");

        builder.setCancelable(false);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
                startActivity(getIntent());
            }
        });

        alertDialog = builder.create();

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        //network
        alertDialog.dismiss();

        if(broadcastReceiver != null)
        {
            unregisterReceiver(broadcastReceiver);
        }
    }

    //    login with email and password
    private void userLogin()
    {
        textInputLayoutEmail.setErrorEnabled(false);
        textInputLayoutPassword.setErrorEnabled(false);

        final String email = textInputLayoutEmail.getEditText().getText().toString().trim();
        final String password = textInputLayoutPassword.getEditText().getText().toString().trim();

        Log.d("TAG", "userLogin: "+email.length());

        if(email.length() == 0)
        {
            textInputLayoutEmail.setError("Email ID not blank");
            textInputLayoutEmail.requestFocus();
            return;
        }

        if(password.length() == 0)
        {
            textInputLayoutPassword.setError("Password not blank");
            textInputLayoutPassword.requestFocus();
            return;
        }

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        progressDialog.dismiss();

                        if(task.isSuccessful())
                        {
                            Intent intent = new Intent(getApplicationContext(), UserDataActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Invalid Email id or Password",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                });
    }


    //    sign in with google

    private void signInWithGoogle()
    {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RESULT_CODE_SINGIN);
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

//        for google sign in
        if(requestCode == RESULT_CODE_SINGIN)
        {
            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            Log.d(TAG, "onActivityResult: "+data);
            progressDialog.setMessage("Verifying...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            handleSignInResult(googleSignInAccountTask);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> googleSignInAccountTask)
    {
        try
        {
            GoogleSignInAccount googleSignInAccount = googleSignInAccountTask.getResult(ApiException.class);

            assert googleSignInAccount != null;
            fireBaseGoogleAuth(googleSignInAccount);
        }
        catch (ApiException e)
        {
            progressDialog.dismiss();
            fireBaseGoogleAuth(null);
        }
    }

    private void fireBaseGoogleAuth(GoogleSignInAccount googleSignInAccount)
    {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);

        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            updateUI(firebaseUser);
                        }
                        else
                        {
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser firebaseUser)
    {
        progressDialog.dismiss();

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        if(googleSignInAccount != null)
        {
            Intent intent = new Intent(getApplicationContext(), UserDataActivity.class);
            startActivity(intent);
            finish();
        }
    }


//    handle notification

    private void alertNotificationFCM(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage("Title : "+title+"\n\nMessage : "+message);

        builder.setTitle("Notification!");

        builder.setCancelable(false);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });

        AlertDialog alertDialog1 = builder.create();
        alertDialog1.show();
    }
}