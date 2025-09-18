package com.eviort.cabedriver.NTActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.eviort.cabedriver.NTApplication;
import com.eviort.cabedriver.NTCustomView.NTButtonBold;
import com.eviort.cabedriver.NTCustomView.NTTextView;
import com.eviort.cabedriver.NTHelper.SharedHelper;
import com.eviort.cabedriver.NTHelper.URLHelper;
import com.eviort.cabedriver.NTUtilites.Utilities;
import com.eviort.cabedriver.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WaitingForApproval extends AppCompatActivity implements View.OnClickListener {
    public Handler ha;
    public Context context = WaitingForApproval.this;
    NTButtonBold logoutBtn;
    NTTextView uploadDocument;
    ImageButton btnVehicleDoc;
    ImageView ivVehicleStatus;
    Utilities utils = new Utilities();
    int VEHICLE_REQUEST_CODE = 1992;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Utilities.setLocale(context, SharedHelper.getKey(context, "lang"));
        setContentView(R.layout.activity_waiting_for_approval);
        token = SharedHelper.getKey(WaitingForApproval.this, "access_token");

        ivVehicleStatus = (ImageView) findViewById(R.id.ivVehicleStatus);
        uploadDocument = (NTTextView) findViewById(R.id.uploadDocument);
        btnVehicleDoc = (ImageButton) findViewById(R.id.btnVehicleDoc);
        logoutBtn = (NTButtonBold) findViewById(R.id.logoutBtn);

        uploadDocument.setOnClickListener(this);
        btnVehicleDoc.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);


        ha = new Handler();
        //check status every 3 sec
        ha.postDelayed(new Runnable() {
            @Override
            public void run() {
                //call function
                UpdateLocationToServer();
                ha.postDelayed(this, 6000);
            }
        }, 6000);
    }


    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    public void onBackPressed() {

    }

    private void UpdateLocationToServer() {
        JSONObject object = new JSONObject();

        try {
            object.put("latitude", "");
            object.put("longitude", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Utilities.PrintAPI_URL(URLHelper.UPDATE_LOCATION_GET_STATUS, object.toString());

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.UPDATE_LOCATION_GET_STATUS, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (response != null) {
                    Utilities.printAPI_Response(response.toString());
                    switch (response.optString("account_status")) {
                        case "approved":

                            if (response.optString("account_status").equals("approved")) {
                                ha.removeCallbacksAndMessages(null);
                                startActivity(new Intent(WaitingForApproval.this, MainActivity.class));
                            }
                            break;


                    }

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {

                    if (error instanceof TimeoutError) {
                        Toast.makeText(context, context.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show();
                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(context, context.getString(R.string.error_no_network), Toast.LENGTH_LONG).show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(context, context.getString(R.string.error_auth_failure), Toast.LENGTH_LONG).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(context, context.getString(R.string.error_server_connection), Toast.LENGTH_LONG).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_LONG).show();
                    } else if (error instanceof ParseError) {
                        // Toast.makeText(context, context.getString(R.string.error_parse), Toast.LENGTH_LONG).show();
                    } else {
                        utils.showCustomAlert(context, Utilities.ALERT_ERROR, getResources().getString(R.string.app_name), error.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();

                }
                Toast.makeText(context, "Error in receiving Driver Status", Toast.LENGTH_SHORT).show();


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-localization", SharedHelper.getKey(getApplicationContext(), "lang"));
                headers.put("Content-Type", "application/json");
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    private void checkStatus() {
        String url = URLHelper.base + "api/provider/trip";
        Utilities.PrintAPI_URL(url, "GET");
        System.out.println("DriverAPI Header: " + "X-Requested-With : XMLHttpRequest " + "Authorization : " + token);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Utilities.printAPI_Response(response.toString());
                //SharedHelper.putKey(context, "currency", response.optString("currency"));

                if (response.optString("account_status").equals("approved")) {
                    ha.removeMessages(0);
                    startActivity(new Intent(WaitingForApproval.this, MainActivity.class));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Error", error.toString());
                try {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        checkStatus();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();

                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-localization", SharedHelper.getKey(getApplicationContext(), "lang"));
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void displayMessage(String toastString) {
        Toast.makeText(WaitingForApproval.this, toastString, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode,
                                    final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VEHICLE_REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {
                ivVehicleStatus.setBackgroundResource(R.drawable.success);
            } else {
                ivVehicleStatus.setBackgroundResource(R.drawable.failure);
            }

        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnVehicleDoc:
                Intent vehicleIntent = new Intent(WaitingForApproval.this, UploadDocument.class);
//                Intent vehicleIntent = new Intent(WaitingForApproval.this, UploadDocument.class);
                startActivity(vehicleIntent);
//                startActivityForResult(vehicleIntent, VEHICLE_REQUEST_CODE);
                break;
            case R.id.uploadDocument:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URLHelper.DRIVER_DOCUMENT_URL));
                startActivity(browserIntent);
                break;
            case R.id.logoutBtn:
                SharedHelper.putKey(WaitingForApproval.this, "loggedIn", getString(R.string.False));
                Intent mainIntent = new Intent(WaitingForApproval.this, ActivityBegin.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                WaitingForApproval.this.finish();
                break;
        }
    }
}
