package com.example.edugorilla;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.edugorilla.ModelClasses.CustomResponseList;
import com.example.edugorilla.ModelClasses.MyGson;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GsonRequest<T> extends Request<T>
{
    private static final String TAG = "GsonRequest";
    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Response.Listener<T> listener;

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(
            String url,
            Class<T> clazz,
            Map<String, String> headers,
            Response.Listener<T> listener,
            Response.ErrorListener errorListener)
    {
        super(Method.GET, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
    }

    /**
     * Returns a list of extra HTTP headers to go along with this request. Can throw {@link
     * AuthFailureError} as authentication may be required to provide these values.
     *
     * @throws AuthFailureError In the event of auth failure
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError
    {
        return headers != null ? headers : super.getHeaders();
    }

    /**
     * Subclasses must implement this to parse the raw network response and return an appropriate
     * response type. This method will be called from a worker thread. The response will not be
     * delivered if you return null.
     *
     * @param response Response from the network
     * @return The parsed response, or null in the case of an error
     */
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response)
    {
      try
      {
          String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
          Log.d(TAG, "parseNetworkResponse: "+json);


//          Type collectionType = new TypeToken<ArrayList<MyGson>>(){}.getType();
//
//          List<MyGson> list = gson.fromJson(json, collectionType);

          return Response.success(gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));

      }
      catch (UnsupportedEncodingException e)
      {
          return Response.error(new ParseError(e));
      }
      catch (JsonSyntaxException e)
      {
          return  Response.error(new ParseError(e));
      }
    }

    /**
     * Subclasses must implement this to perform delivery of the parsed response to their listeners.
     * The given response is guaranteed to be non-null; responses that fail to parse are not
     * delivered.
     *
     * @param response The parsed response returned by {@link
     *                 #parseNetworkResponse(NetworkResponse)}
     */
    @Override
    protected void deliverResponse(T response)
    {
        listener.onResponse(response);
    }
}
