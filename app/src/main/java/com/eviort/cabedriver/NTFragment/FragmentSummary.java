package com.eviort.cabedriver.NTFragment;


import static com.eviort.cabedriver.NTApplication.trimMessage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.eviort.cabedriver.NTActivity.ActivityBegin;
import com.eviort.cabedriver.NTActivity.MainActivity;
import com.eviort.cabedriver.NTApplication;
import com.eviort.cabedriver.NTCustomView.NTTextView;
import com.eviort.cabedriver.NTHelper.LoadingDialog;
import com.eviort.cabedriver.NTHelper.SharedHelper;
import com.eviort.cabedriver.NTHelper.URLHelper;
import com.eviort.cabedriver.NTUtilites.CountAnimationTextView;
import com.eviort.cabedriver.NTUtilites.Utilities;
import com.eviort.cabedriver.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSummary extends Fragment implements View.OnClickListener {

    ImageView imgBack;
    LinearLayout cardLayout;

    CountAnimationTextView noOfRideTxt;
    CountAnimationTextView scheduleTxt;
    CountAnimationTextView revenueTxt;
    CountAnimationTextView cancelTxt;

    CardView ridesCard;
    CardView cancelCard;
    CardView scheduleCard;
    CardView revenueCard;

    NTTextView currencyTxt;
    Utilities utils = new Utilities();
    ImageView sp_timeperiod_image;

    int rides, schedule, cancel;
    Double doubleRevenue, revenue;
    //Spinner sp_timeperiod;
    View view;
    private String spinnerSelected = "0";
    private int spinnerTouch = 0;
    private Spinner sp_timeperiod;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_summary, container, false);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
                return false;
            }
        });
        findViewsById(view);
        setClickListeners();
        FragmentHome.isRunning = false;
        getProviderSummary(spinnerSelected);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.time_period, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_timeperiod.setAdapter(adapter);

//        sp_statistics.getBackground().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
        sp_timeperiod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                if (++spinnerTouch > 1) {

                    switch (i) {
                        case 0:

                            spinnerSelected = "0";

//                            gettodayDate();

                            break;
                        case 1:
                            spinnerSelected = "1";

//                            getStartEndOfWeek();

                            break;
                        case 2:
                            spinnerSelected = "2";
                            //                            getStartEndOfMonth();

                            break;
                        case 3:
                            spinnerSelected = "3";

//                            getStartEndOfYear();

                            break;
                        case 4:
                            spinnerSelected = "4";
                            //                            startDate = "";
//                            endDate="";

                            break;
                        default:
                            spinnerSelected = "0";
//                            gettodayDate();

                            break;
                    }
                    getProviderSummary(spinnerSelected);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return view;

    }

    private void setClickListeners() {
        imgBack.setOnClickListener(this);
        revenueCard.setOnClickListener(this);
        ridesCard.setOnClickListener(this);
        cancelCard.setOnClickListener(this);
        scheduleCard.setOnClickListener(this);
        sp_timeperiod_image.setOnClickListener(this);
    }

    private void findViewsById(View view) {
        imgBack = (ImageView) view.findViewById(R.id.backArrow);
        cardLayout = (LinearLayout) view.findViewById(R.id.card_layout);
        noOfRideTxt = (CountAnimationTextView) view.findViewById(R.id.no_of_rides_txt);
        scheduleTxt = (CountAnimationTextView) view.findViewById(R.id.schedule_txt);
        cancelTxt = (CountAnimationTextView) view.findViewById(R.id.cancel_txt);
        revenueTxt = (CountAnimationTextView) view.findViewById(R.id.revenue_txt);
        sp_timeperiod_image = (ImageView) view.findViewById(R.id.sp_timeperiod_image);
        // currencyTxt = (NTTextView) view.findViewById(R.id.currency_txt);
        revenueCard = (CardView) view.findViewById(R.id.revenue_card);
        scheduleCard = (CardView) view.findViewById(R.id.schedule_card);
        ridesCard = (CardView) view.findViewById(R.id.rides_card);
        cancelCard = (CardView) view.findViewById(R.id.cancel_card);
        sp_timeperiod = (Spinner) view.findViewById(R.id.sp_timeperiod);
    }

    @Override
    public void onClick(View v) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = new Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("toolbar", "true");
        switch (v.getId()) {
            case R.id.backArrow:
                pop();

                break;
            case R.id.sp_timeperiod_image:
                ((Spinner) view.findViewById(R.id.sp_timeperiod)).performClick();
                break;
            case R.id.rides_card:
               /* fragment = new PastTrips(); ;f'9
                fragment.setArguments(bundle);
                transaction.add(R.id.content, fragment);
                transaction.hide(this);
                transaction.commit();*/
                break;
            case R.id.schedule_card:
               /* fragment = new OnGoingTrips();
                transaction.add(R.id.content, fragment);
                fragment.setArguments(bundle);
                transaction.hide(this);
                transaction.commit();*/
                break;
            case R.id.revenue_card:
           /*     fragment = new EarningsFragment();
                transaction.add(R.id.content, fragment);
                transaction.hide(this);
                transaction.commit();*/
                break;
            case R.id.cancel_card:
              /*  fragment = new PastTrips();
                transaction.add(R.id.content, fragment);
                fragment.setArguments(bundle);
                transaction.hide(this);
                transaction.commit();*/
                break;
        }
    }

    private void setDetails() {
        Animation txtAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.txt_size);
        if (schedule > 0) {
            scheduleTxt.setAnimationDuration(500)
                    .countAnimation(0, schedule);
        } else {
            scheduleTxt.setText("0");
        }
        if (revenue > 0) {
            revenueTxt.setText(revenue.toString());
        } else {
            revenueTxt.setText("0");
        }
        if (rides > 0) {
            noOfRideTxt.setAnimationDuration(500)
                    .countAnimation(0, rides);

        } else {
            noOfRideTxt.setText("0");
        }
        if (cancel > 0) {
            cancelTxt.setAnimationDuration(500)
                    .countAnimation(0, cancel);
        } else {
            cancelTxt.setText("0");
        }
        scheduleTxt.startAnimation(txtAnim);
        revenueTxt.startAnimation(txtAnim);
        noOfRideTxt.startAnimation(txtAnim);
        cancelTxt.startAnimation(txtAnim);
        //currencyTxt.setText(SharedHelper.getKey(getContext(), "currency"));
    }

    public void getProviderSummary(final String timeperiod) {
        {
            final LoadingDialog customDialog = new LoadingDialog(getActivity());
            //   customDialog.(false);
            customDialog.showDialog();
            JSONObject object = new JSONObject();

            Utilities.print(URLHelper.SUMMARY + "?type=" + timeperiod, object.toString());

            System.out.println("DriverAPI Header: " + "X-Requested-With : XMLHttpRequest " + "Authorization : " + SharedHelper.getKey(getContext(), "access_token"));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.SUMMARY + "?type=" + timeperiod, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Utilities.printAPI_Response(response.toString());
                    customDialog.hideDialog();
                    cardLayout.setVisibility(View.VISIBLE);
                   /* Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
                    cardLayout.startAnimation(slideUp);*/
                    rides = Integer.parseInt(response.optString("rides"));
                    schedule = Integer.parseInt(response.optString("scheduled_rides"));
                    cancel = Integer.parseInt(response.optString("cancel_rides"));
                    doubleRevenue = Double.parseDouble(response.optString("revenue"));
//                    revenue = doubleRevenue;
                    revenue = doubleRevenue;
                    //revenue = Integer.parseInt(response.optString("revenue"));
                    setDetails();
                   /* slideUp.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            setDetails();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });*/


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.hideDialog();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {

                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.optString("message"));
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                    e.printStackTrace();
                                }
                            } else if (response.statusCode == 401) {
                                GoToBeginActivity();
                            } else if (response.statusCode == 422) {


                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    displayMessage(json);
                                } else {
                                    displayMessage(getString(R.string.please_try_again));
                                }
                            } else if (response.statusCode == 503) {
                                displayMessage(getString(R.string.server_down));
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } catch (Exception e) {
                            displayMessage(getString(R.string.something_went_wrong));
                            e.printStackTrace();
                        }

                    } else {
                        if (error instanceof NoConnectionError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof NetworkError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof TimeoutError) {
                            getProviderSummary(timeperiod);
                        }
                    }
                }
            }) {
                @Override
                public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-localization", SharedHelper.getKey(getActivity(), "lang"));
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "Bearer " + SharedHelper.getKey(getContext(), "access_token"));
                    Log.e("", "Access_Token" + SharedHelper.getKey(getContext(), "access_token"));
                    return headers;
                }
            };

            NTApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        }
    }

    public void displayMessage(String toastString) {
        Snackbar.make(getView(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public void GoToBeginActivity() {
        SharedHelper.putKey(getContext(), "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(getContext(), ActivityBegin.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        getActivity().finish();
    }

    public void pop() {
       /* FragmentManager fm = getActivity().getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        for (int i = 0; i <= count; ++i) {
            fm.popBackStackImmediate();
        }*/
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish();
    }

}
