package com.example.edugorilla;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.edugorilla.ModelClasses.CustomResponseList;
import com.example.edugorilla.ModelClasses.MyGson;
import com.example.edugorilla.ModelClasses.XYValues;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GraphActivity extends AppCompatActivity
{

    private static final String TAG = "GraphActivity";

    GraphView graphView;
    private ProgressBar progressBar;

    private TextView textViewXaxis;
    private TextView textViewYaxis;
    private MaterialButton materialButtonLine;
    private MaterialButton materialButtonPoint;
    private MaterialButton materialButtonBoth;
    private MaterialButton materialButtonNext;


//    Graph
    PointsGraphSeries<DataPoint> xySeries;
    LineGraphSeries<DataPoint> xySeriesLine;



//    arraylist xy object
    ArrayList<XYValues> xyValuesArrayList;


    //  network
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //for network register broadcast
        alertMessage();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        setContentView(R.layout.activity_graph);

        getSupportActionBar().hide();

        progressBar = findViewById(R.id.idProgressbarLoadingGraphActivity);

//        graph
        graphView = findViewById(R.id.graphview);
        textViewXaxis = findViewById(R.id.idTextViewXAxisGraphActivity);
        textViewYaxis = findViewById(R.id.idTextViewYAxisGraphActivity);
        materialButtonLine = findViewById(R.id.idButtonLineSeriesGraphActivity);
        materialButtonPoint = findViewById(R.id.idButtonPointSeriesGraphActivity);
        materialButtonBoth = findViewById(R.id.idButtonBothGraphGraphActivity);
        materialButtonNext = findViewById(R.id.idButtonActivity4GraphActivity);

        xyValuesArrayList = new ArrayList<>();

        materialButtonBoth.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                graphView.addSeries(xySeries);
                graphView.addSeries(xySeriesLine);

                materialButtonBoth.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                materialButtonBoth.setTextColor(getResources().getColor(R.color.whiteColor));

                materialButtonLine.setBackgroundColor(getResources().getColor(R.color.whiteColor));
                materialButtonLine.setTextColor(getResources().getColor(R.color.colorPrimary));

                materialButtonPoint.setBackgroundColor(getResources().getColor(R.color.whiteColor));
                materialButtonPoint.setTextColor(getResources().getColor(R.color.colorPrimary));

            }
        });

        materialButtonLine.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                graphView.removeAllSeries();
                graphView.addSeries(xySeriesLine);

                materialButtonBoth.setBackgroundColor(getResources().getColor(R.color.whiteColor));
                materialButtonBoth.setTextColor(getResources().getColor(R.color.colorPrimary));

                materialButtonLine.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                materialButtonLine.setTextColor(getResources().getColor(R.color.whiteColor));

                materialButtonPoint.setBackgroundColor(getResources().getColor(R.color.whiteColor));
                materialButtonPoint.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });

        materialButtonPoint.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                graphView.removeAllSeries();
                graphView.addSeries(xySeries);

                materialButtonBoth.setBackgroundColor(getResources().getColor(R.color.whiteColor));
                materialButtonBoth.setTextColor(getResources().getColor(R.color.colorPrimary));

                materialButtonLine.setBackgroundColor(getResources().getColor(R.color.whiteColor));
                materialButtonLine.setTextColor(getResources().getColor(R.color.colorPrimary));

                materialButtonPoint.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                materialButtonPoint.setTextColor(getResources().getColor(R.color.whiteColor));
            }
        });

        materialButtonNext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), WebViewActivity.class));
            }
        });
    }

//    graph
    private void createScatterPlot()
    {
        //x and y values must be ascending order otherwise app will crash

        //sort the array of xy values
//        xyValuesArrayList = sortArray(xyValuesArrayList);

        for (int i = 0; i < xyValuesArrayList.size(); i++)
        {
            try
            {
                xySeries.appendData(
                        new DataPoint(xyValuesArrayList.get(i).getX(), xyValuesArrayList.get(i).getY()),
                        true,
                        100
                );

                xySeriesLine.appendData(
                        new DataPoint(xyValuesArrayList.get(i).getX(), xyValuesArrayList.get(i).getY()),
                        true,
                        100
                );
            }
            catch (IllegalArgumentException e)
            {
                Log.d(TAG, "createScatterPlot: "+e);
            }
        }

        //properties for seriew
        xySeries.setColor(Color.BLUE);
        xySeries.setSize(12f);

        xySeriesLine.setAnimated(true);
        xySeriesLine.setDataPointsRadius(2f);
        xySeriesLine.setBackgroundColor(Color.RED);

        //setScrollable and scalable
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScrollableY(true);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScrollableY(true);

        //set manul x bounds
        graphView.getViewport().setMaxY(500);
        graphView.getViewport().setMinY(200);

        //set manual y bounds
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMaxX(35);
        graphView.getViewport().setMinX(1);

        graphView.setTitle("Graph (date vs amount)");
        graphView.setTitleColor(getResources().getColor(R.color.colorPrimary));
        graphView.setTitleTextSize(30f);

        graphView.addSeries(xySeries);
        graphView.addSeries(xySeriesLine);

    }

    /**
     * Sorts an ArrayList<XYValue> with respect to the x values.
     * @param array
     * @return
     */
    private ArrayList<XYValues> sortArray(ArrayList<XYValues> array)
    {
        /*
        //Sorts the xyValues in Ascending order to prepare them for the PointsGraphSeries<DataSet>
         */
        int factor = Integer.parseInt(String.valueOf(Math.round(Math.pow(array.size(),2))));
        int m = array.size() - 1;
        int count = 0;
        Log.d(TAG, "sortArray: Sorting the XYArray.");


        while (true) {
            m--;
            if (m <= 0) {
                m = array.size() - 1;
            }
            Log.d(TAG, "sortArray: m = " + m);
            try {
                //print out the y entrys so we know what the order looks like
                //Log.d(TAG, "sortArray: Order:");
                //for(int n = 0;n < array.size();n++){
                //Log.d(TAG, "sortArray: " + array.get(n).getY());
                //}
                double tempY = array.get(m - 1).getY();
                double tempX = array.get(m - 1).getX();
                if (tempX > array.get(m).getX()) {
                    array.get(m - 1).setY(array.get(m).getY());
                    array.get(m).setY(tempY);
                    array.get(m - 1).setX(array.get(m).getX());
                    array.get(m).setX(tempX);
                } else if (tempX == array.get(m).getX()) {
                    count++;
                    Log.d(TAG, "sortArray: count = " + count);
                } else if (array.get(m).getX() > array.get(m - 1).getX()) {
                    count++;
                    Log.d(TAG, "sortArray: count = " + count);
                }
                //break when factorial is done
                if (count == factor) {
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e(TAG, "sortArray: ArrayIndexOutOfBoundsException. Need more than 1 data point to create Plot." +
                        e.getMessage());
                break;
            }
        }
        return array;
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        getGraphData();

    }

    //    graph data
    private void getGraphData()
    {
        final String requestUrl = "https://www.dropbox.com/s/n4i57r22rdx89cw/list2.json?dl=1";

        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                requestUrl,
                null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response)
                    {
                        xySeries = new PointsGraphSeries<>();
                        xySeriesLine = new LineGraphSeries<>();

                        try
                        {
                            int length = 0;
                            double sum = 0;
                            int [] daySum = new int[31];
                            int [] dayLength = new int[31];

                            for (int n = 0; n < 31; n++)
                            {
                                dayLength[n]  = 1;
                            }


                            for(int j = 0; j < response.length(); j++)
                            {
                                JSONObject jsonObject = response.getJSONObject(j);

                                String temp = jsonObject.getString("date_created");

                                String [] split = temp.split("-");

                                String julyMonth = split[1];

                                if(julyMonth.equals("07"))
                                {
                                    if(split[2].equals("01"))
                                    {
                                        dayLength[0]++;
                                        daySum[0] = daySum[0] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("02"))
                                    {
                                        dayLength[1]++;
                                        daySum[1] = daySum[1] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("03"))
                                    {
                                        dayLength[2]++;
                                        daySum[2] = daySum[2] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("04"))
                                    {
                                        dayLength[3]++;
                                        daySum[3] = daySum[3] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("05"))
                                    {
                                        dayLength[4]++;
                                        daySum[4] = daySum[4] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("06"))
                                    {
                                        dayLength[5]++;
                                        daySum[5] = daySum[5] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("07"))
                                    {
                                        dayLength[6]++;
                                        daySum[6] = daySum[6] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("08"))
                                    {
                                        dayLength[7]++;
                                        daySum[7] = daySum[7] + Integer.parseInt(jsonObject.getString("amount"));
                                    }


                                    if(split[2].equals("09"))
                                    {
                                        dayLength[8]++;
                                        daySum[8] = daySum[8] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("10"))
                                    {
                                        dayLength[9]++;
                                        daySum[9] = daySum[9] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("11"))
                                    {
                                        dayLength[10]++;
                                        daySum[10] = daySum[10] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("12"))
                                    {
                                        dayLength[11]++;
                                        daySum[11] = daySum[11] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("13"))
                                    {
                                        dayLength[12]++;
                                        daySum[12] = daySum[12] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("14"))
                                    {
                                        dayLength[13]++;
                                        daySum[13] = daySum[13] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("15"))
                                    {
                                        dayLength[14]++;
                                        daySum[14] = daySum[14] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("16"))
                                    {
                                        dayLength[15]++;
                                        daySum[15] = daySum[15] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    //after 16 days

                                    if(split[2].equals("17"))
                                    {
                                        dayLength[16]++;
                                        daySum[16] = daySum[16] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("18"))
                                    {
                                        dayLength[17]++;
                                        daySum[17] = daySum[17] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("19"))
                                    {
                                        dayLength[18]++;
                                        daySum[18] = daySum[18] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("20"))
                                    {
                                        dayLength[19]++;
                                        daySum[19] = daySum[19] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("21"))
                                    {
                                        dayLength[20]++;
                                        daySum[20] = daySum[20] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("22"))
                                    {
                                        dayLength[21]++;
                                        daySum[21] = daySum[21] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("23"))
                                    {
                                        dayLength[22]++;
                                        daySum[22] = daySum[22] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("24"))
                                    {
                                        dayLength[23]++;
                                        daySum[23] = daySum[23] + Integer.parseInt(jsonObject.getString("amount"));
                                    }


                                    if(split[2].equals("25"))
                                    {
                                        dayLength[24]++;
                                        daySum[24] = daySum[24] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("26"))
                                    {
                                        dayLength[25]++;
                                        daySum[25] = daySum[25] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("27"))
                                    {
                                        dayLength[26]++;
                                        daySum[26] = daySum[26] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("28"))
                                    {
                                        dayLength[27]++;
                                        daySum[27] = daySum[27] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("29"))
                                    {
                                        dayLength[28]++;
                                        daySum[28] = daySum[28] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("30"))
                                    {
                                        dayLength[29]++;
                                        daySum[29] = daySum[29] + Integer.parseInt(jsonObject.getString("amount"));
                                    }

                                    if(split[2].equals("31"))
                                    {
                                        dayLength[30]++;
                                        daySum[30] = daySum[30] + Integer.parseInt(jsonObject.getString("amount"));
                                    }
                                }
                            }

                            for(int k = 0; k < 31; k++)
                            {
                                int x = k+1;
                                int y = daySum[k]/dayLength[k];

                                Log.d(TAG, "onResponse: x = "+x);
                                Log.d(TAG, "onResponse: y = "+y);
                                xyValuesArrayList.add(new XYValues(x, y));

                                createScatterPlot();
                            }

                            progressBar.setVisibility(View.GONE);

                            graphView.setVisibility(View.VISIBLE);
                            textViewXaxis.setVisibility(View.VISIBLE);
                            textViewYaxis.setVisibility(View.VISIBLE);
                            materialButtonLine.setVisibility(View.VISIBLE);
                            materialButtonPoint.setVisibility(View.VISIBLE);
                            materialButtonBoth.setVisibility(View.VISIBLE);


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
                        progressBar.setVisibility(View.GONE);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(GraphActivity.this);

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
}