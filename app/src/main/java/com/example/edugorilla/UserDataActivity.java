package com.example.edugorilla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.edugorilla.Adapters.UserDataAdapter;
import com.example.edugorilla.ModelClasses.UserDataInfo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class UserDataActivity extends AppCompatActivity
{
    private static final String TAG = "UserDataActivity";
    //    User data activity
    MaterialToolbar materialToolbar;

    //adapter
    private UserDataAdapter adapter;

    //a list to store all the prs users name
    public ArrayList<UserDataInfo> userDataInfoArrayList;

    //the recyclerview
    RecyclerView recyclerView;

    private ProgressBar progressBar;

//    firebase
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    //  network
    AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //for network register broadcast
        alertMessage();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        setContentView(R.layout.activity_user_data);

        getSupportActionBar().hide();

//        User data Activity
        materialToolbar = findViewById(R.id.idMaterialToolbarUserDataActivity);

        progressBar = findViewById(R.id.idProgressbarLoadingUserDataActivity);

        userDataInfoArrayList = new ArrayList<>();

        recyclerView = findViewById(R.id.idRecycleViewAllUserList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setNestedScrollingEnabled(false);

        adapter = new UserDataAdapter(userDataInfoArrayList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new UserDataAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(int position)
            {
                Intent intent = new Intent(getApplicationContext(), GraphActivity.class);
                startActivity(intent);
            }
        });

        materialToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                if(item.getItemId() == R.id.menulogout)
                {
                    logOutUser();
                }
                return false;
            }
        });


//        firebase
        firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        authStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if(firebaseUser == null)
                {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

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

//        getUserData();
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        getUserData();

    }

    //    get user data

    private void getUserData()
    {
        Log.d("TAG", "getNews: 111");
        String requestUrl = "https://www.dropbox.com/s/1hh8vh7whv6cjme/list1.json?dl=1";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                requestUrl,
                null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d(TAG, "onResponse: "+response);

                        progressBar.setVisibility(View.GONE);


                        try
                        {
                            JSONArray jsonArray = response.getJSONArray("data");

                            for(int j = 0; j < 10; j++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(j);

                                userDataInfoArrayList.add(new UserDataInfo(
                                        jsonObject.getString("id"),
                                        jsonObject.getString("name"),
                                        jsonObject.getString("email")
                                ));

                            }

                            recyclerView.setAdapter(adapter);

                            progressBar.setVisibility(View.GONE);

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.d(TAG, "onErrorResponse: "+error);
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );

        RequestHandler.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }


    //    User data Activity
    @Override
    public void onStart()
    {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

//    ++network

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
        AlertDialog.Builder builder = new AlertDialog.Builder(UserDataActivity.this);

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


//    logout function
    private void logOutUser()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(UserDataActivity.this);

        builder.setMessage("Are you sure to Sign Out?");

        builder.setTitle("Alert !");

        builder.setCancelable(true);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                GoogleSignIn.getClient(
                        getApplicationContext(),
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                ).signOut();

                firebaseAuth.signOut();
            }
        });


        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    //    handle notification

    private void alertNotificationFCM(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserDataActivity.this);

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