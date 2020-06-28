package com.example.edugorilla;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WebViewActivity extends AppCompatActivity
{
    //Webview activity
    private static final String TAG = "WebViewActivity";

    private ProgressBar progressBar;
    private WebView webView;
    private TextView textViewTopicName;
    private MaterialButton materialButtonPrevious;
    private MaterialButton materialButtonNext;

    //  network
    AlertDialog alertDialog;

//    Json array;
    public JSONArray jsonArray;
    int webViewCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //for network register broadcast
        alertMessage();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        setContentView(R.layout.activity_web_view);

        getSupportActionBar().hide();

        progressBar = findViewById(R.id.idProgressbarLoadingWebViewActivity);
        webView = findViewById(R.id.idWebViewWebViewActivity);
        textViewTopicName = findViewById(R.id.idTextViewTopicNameWebViewActivity);
        materialButtonPrevious = findViewById(R.id.idButtonPreviousWebViewActivity);
        materialButtonNext = findViewById(R.id.idButtonNextWebViewActivity);

        webView.clearCache(false);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);

        materialButtonPrevious.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                progressBar.setVisibility(View.VISIBLE);

                if(webViewCount != 0)
                {
                    webViewCount--;
                }
                else
                {
                    webViewCount = jsonArray.length() - 1;
                }

                try
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(webViewCount);

                    textViewTopicName.setText(jsonObject.getString("title"));

                    webView.loadData(jsonObject.getString("content"), "text/html", "UTF-8");

                    webView.setVisibility(View.VISIBLE);

                    progressBar.setVisibility(View.GONE);
                }
                catch (JSONException e)
                {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }

            }
        });
        materialButtonNext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                progressBar.setVisibility(View.VISIBLE);

                if(webViewCount != jsonArray.length() - 1)
                {
                    webViewCount++;
                }
                else
                {
                    webViewCount = 0;
                }

                try
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(webViewCount);

                    textViewTopicName.setText(jsonObject.getString("title"));

                    webView.loadData(jsonObject.getString("content"), "text/html", "UTF-8");

                    webView.setVisibility(View.VISIBLE);

                    progressBar.setVisibility(View.GONE);
                }
                catch (JSONException e)
                {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        });

    }

    //WebviewActivity


    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        getWebData();

    }

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
        AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);

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

    // network request data webview
    private void getWebData()
    {
        final String requestUrl = "https://www.dropbox.com/s/ep7v5yex3fjs3s1/webview.json?dl=1";


        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                requestUrl,
                null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response)
                    {
                        Log.d(TAG, "onResponse: "+response.length());

                        jsonArray = response;
                        try
                        {

                            JSONObject jsonObject = response.getJSONObject(webViewCount);

                            textViewTopicName.setText(jsonObject.getString("title"));

                            webView.loadData(jsonObject.getString("content"), "text/html", "UTF-8");
                            webView.setVisibility(View.VISIBLE);
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
//                        progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "onErrorResponse: "+error);
                    }
                }
        );

        RequestHandler.getInstance(this).addToRequestQueue(jsonArrayRequest);

//        GsonRequest<MyGson> gsonGsonRequest = new GsonRequest<MyGson>(
//                requestUrl,
//                MyGson.class,
//                null,
//                new Response.Listener<MyGson>() {
//                    @Override
//                    public void onResponse(MyGson response) {
//                        progressBar.setVisibility(View.GONE);
//                        Log.d(TAG, "onResponse: "+response);
//                        Toast.makeText(getApplicationContext(), "res "+response, Toast.LENGTH_SHORT).show();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        progressBar.setVisibility(View.GONE);
//                        Log.d(TAG, "onErrorResponse: "+error);
//                        Toast.makeText(getApplicationContext(), "er "+error, Toast.LENGTH_SHORT).show();
//                    }
//                }
//        );

//        GsonRequest<CustomResponseList> gsonRequest = new GsonRequest(
//                requestUrl,
//                CustomResponseList.class,
//                null,
//                new Response.Listener<CustomResponseList>() {
//                    @Override
//                    public void onResponse(CustomResponseList response) {
//                        progressBar.setVisibility(View.GONE);
//                        Log.d(TAG, "onResponse: "+response);
//                        Toast.makeText(getApplicationContext(), "res "+response, Toast.LENGTH_SHORT).show();
//
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        progressBar.setVisibility(View.GONE);
//                        Log.d(TAG, "onErrorResponse: "+error);
//                        Toast.makeText(getApplicationContext(), "er "+error, Toast.LENGTH_SHORT).show();
//
//                    }
//                }
//        );

//        GsonRequest<CustomResponseList> gsonRequest = new GsonRequest(
//                requestUrl,
//                CustomResponseList[].class,
//                null,
//                new Response.Listener() {
//                    @Override
//                    public void onResponse(Object response)
//                    {
//                        progressBar.setVisibility(View.GONE);
//                        Log.d(TAG, "onResponse: "+response);
//                        Toast.makeText(getApplicationContext(), "res "+response, Toast.LENGTH_SHORT).show();
//
////                        List<CustomResponseList> lists = response.
////                        CustomResponseList customResponseList = new CustomResponseList();
////                        customResponseList.setCustomResponseList((List<MyGson>) response);
////
////                        Log.d(TAG, "onResponse: "+customResponseList.getCustomResponseList());
////                        JsonArray jsonArray =
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        progressBar.setVisibility(View.GONE);
//                        Log.d(TAG, "onErrorResponse: "+error);
//                        Toast.makeText(getApplicationContext(), "er "+error, Toast.LENGTH_SHORT).show();
//
//                    }
//                }
//        );

//        GsonRequest gsonGsonRequest = new GsonRequest(
//                requestUrl,
//                CustomResponseList.class,
//                null,
//                new Response.Listener<CustomResponseList>() {
//                    @Override
//                    public void onResponse(CustomResponseList response) {
//                        progressBar.setVisibility(View.GONE);
//                        Log.d(TAG, "onResponse: "+response);
//                        Toast.makeText(getApplicationContext(), "res "+response, Toast.LENGTH_SHORT).show();
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        progressBar.setVisibility(View.GONE);
//                        Log.d(TAG, "onErrorResponse: "+error);
//                        Toast.makeText(getApplicationContext(), "er "+error, Toast.LENGTH_SHORT).show();
//
//
//                    }
//                }
//                new Response.Listener<MyGson>()
//                {
//                    @Override
//                    public void onResponse(MyGson response)
//                    {
//                        progressBar.setVisibility(View.GONE);
//                        Log.d(TAG, "onResponse: "+response);
//                        Toast.makeText(getApplicationContext(), "res "+response, Toast.LENGTH_SHORT).show();
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error)
//                    {
//                        progressBar.setVisibility(View.GONE);
//                        Log.d(TAG, "onErrorResponse: "+error);
//                        Toast.makeText(getApplicationContext(), "er "+error, Toast.LENGTH_SHORT).show();
//
//
//                    }
//                }
//        );

//        RequestHandler.getInstance(this).addToRequestQueue(gsonGsonRequest);
    }

}