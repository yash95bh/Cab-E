package com.eviort.cabedriver.NTUtilites;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.Ringtone;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.eviort.cabedriver.NTCustomView.NTBoldTextView;
import com.eviort.cabedriver.NTCustomView.NTTextView;
import com.eviort.cabedriver.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {


    public static final int ENABLE_NETWORK = 1;
    public static final int ENABLE_LOCATION = 2;
    public static final int ALERT_SUCCESS = 0;
    public static final int ALERT_ERROR = 1;
    public static final int ALERT_WARNING = 2;
    static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates";
    public static boolean showLog = true;
    public static AlertDialog alertmDialog;
    public static int LAYOUT_VALUE = 0;
    public static boolean clearSound;
    public static Ringtone r;
    static boolean isDebugmode = true;

    public static String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Log.e("Utilis", "My Current: " + addresses);
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                if (returnedAddress.getMaxAddressLineIndex() > 0) {

                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(", ");
                    }
                } else {
                    strReturnedAddress.append(returnedAddress.getAddressLine(0)).append("");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("Utilis My Current", "" + strReturnedAddress);
            } else {
                Log.w("Utilis My Current", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("Utilis My Current", "Canont get Address!");
        }
        return strAdd;
    }

    public static void showToast(Context context, String toastString) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = null;

        //Getting the View object as defined in the customtoast.xml file
        layout = layoutInflater.inflate(R.layout.layout_custom_toast, null);
        NTTextView txtToast = (NTTextView) layout.findViewById(R.id.txt_toast);
        txtToast.setText(toastString);
        //Creating the Toast object
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);//setting the view of custom toast layout
        toast.show();


    }


    public static String parseDateMonth(String time) {
        String inputPattern = "yyyy-MM-dd'T'hh:mm:ss.SSSSSS'Z'";
        String outputPattern = "E,MMM dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseDateToddMMyyyyT(String time) {
        String inputPattern = "yyyy-MM-dd'T'hh:mm:ss.SSSSSS'Z'";
        String outputPattern = "dd-MM-yyyy hh:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseDate(String time) {
        String inputPattern = "yyyy-MM-dd'T'hh:mm:ss.SSSSSS'Z'";
        String outputPattern = "dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static void print(String tag, String message) {
        if (showLog) {
            Log.v(tag, message);
        }
    }

    public static void displayNetworkerror(final Context context, LinearLayout layout, int errorStatus) {

        Log.d("location error status", "" + errorStatus);

        GPSTracker appLocationService = new GPSTracker(context);
        LayoutInflater layoutInflater;
        View child = null;
        NTBoldTextView tvTitle;
        NTTextView tvDescription;
        NTTextView btnTxt, btnNetwork;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        child = layoutInflater.inflate(R.layout.layout_error, layout, false);
        layout.addView(child);

        tvTitle = (NTBoldTextView) child.findViewById(R.id.error_title);
        tvDescription = (NTTextView) child.findViewById(R.id.error_description);
        btnTxt = (NTTextView) child.findViewById(R.id.tap_to_location_enable);
        btnNetwork = (NTTextView) child.findViewById(R.id.tap_to_network_enable);
        switch (errorStatus) {
            case ENABLE_NETWORK:
                child.findViewById(R.id.error_image).setBackgroundResource(R.drawable.no_internet);
                tvTitle.setText(context.getResources().getString(R.string.no_internet));
                tvDescription.setText(context.getResources().getString(R.string.no_internet_desc));
                btnTxt.setVisibility(View.GONE);
                btnNetwork.setVisibility(View.VISIBLE);

                btnNetwork.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        context.startActivity(i);
                    }
                });

                break;

            case ENABLE_LOCATION:

                child.findViewById(R.id.error_image).setBackgroundResource(R.drawable.no_internet);

                tvTitle.setText(context.getResources().getString(R.string.no_loc_service));
                tvDescription.setText(context.getResources().getString(R.string.no_loc_service_desc));
                btnTxt.setVisibility(View.VISIBLE);
                btnNetwork.setVisibility(View.GONE);

                btnTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    }
                });
                break;
        }
    }

    public static void displayNetworkerrorFragment(final Context context, int errorStatus) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
// Get the layout inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.layout_custom_alert, null);
        builder.setCancelable(false);
        builder.setView(layout);
        builder.create();

        final AlertDialog alertDialog = builder.create();
        NTBoldTextView alert_title = (NTBoldTextView) layout.findViewById(R.id.alert_title);
        NTTextView alert_desc = (NTTextView) layout.findViewById(R.id.alert_desc);
        NTBoldTextView btnOk = (NTBoldTextView) layout.findViewById(R.id.btnOk);
        Drawable res = null;

        switch (errorStatus) {
            case ENABLE_NETWORK:
                // child.findViewById(R.id.error_image).setBackgroundResource(R.drawable.no_internet);
                alert_title.setText(context.getResources().getString(R.string.no_internet));
                alert_desc.setText(context.getResources().getString(R.string.no_internet_desc));


                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        context.startActivity(i);
                        if (alertDialog != null && alertDialog.isShowing())
                            alertDialog.dismiss();
                    }
                });

                break;

            case ENABLE_LOCATION:


                alert_title.setText(context.getResources().getString(R.string.no_loc_service));
                alert_desc.setText(context.getResources().getString(R.string.no_loc_service_desc));

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                        if (alertDialog != null && alertDialog.isShowing())
                            alertDialog.dismiss();
                    }
                });
                break;
        }


        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
// alertDialog.getWindow().addFlags(WindowManager.LayoutParams.DIM_AMOUNT_CHANGED);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();

    }

    public static void PrintAPI_URL(String url, String params) {

        if (isDebugmode) {
            Log.v("DriverAPI", "URL: " + url + " Params: " + params);
        }
    }

    public static void printAPI_Response(String response) {

        if (isDebugmode) {
            Log.v("DriverAPI", "Response: " + response);
        }
    }

    public static void dispalyDialog(final Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The {@link Context}.
     */
    static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }

    /**
     * Stores the location updates state in SharedPreferences.
     *
     * @param requestingLocationUpdates The location updates state.
     */
    public static void setRequestingLocationUpdates(Context context, boolean requestingLocationUpdates) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
                .apply();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isAfterToday(int year, int month, int day) {
        Calendar today = Calendar.getInstance();
        Calendar myDate = Calendar.getInstance();

        myDate.set(year, month, day);

        return !myDate.before(today);
    }

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void setLocale(Context context, String language) {

        Locale locale = new Locale(language);
        Resources res = context.getResources();

        Configuration config = new Configuration(res.getConfiguration());

        Locale.setDefault(locale);
        config.locale = locale;
        //        context.getResources().updateConfiguration(config, null);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

//        if (Build.VERSION.SDK_INT >= 17) {
//            config.setLocale(locale);
//            context = context.createConfigurationContext(config);
//        } else {
//            config.locale = locale;
//            res.updateConfiguration(config, res.getDisplayMetrics());
//        }

    }

    public String parseDateMonth2(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MMM dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);


        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String parseDateMonth1(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MMM dd yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);


        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public void displayMessage(View view, String toastString) {

      /*  Snackbar snackbar;
        snackbar =  Snackbar.make(view,toastString, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(view.getResources().getColor(R.color.colorPrimaryDark));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(view.getResources().getColor(R.color.white));
        snackbar.show();*/
//        Snackbar.make(view, toastString, Snackbar.LENGTH_SHORT)
//                .setAction("Action", null).show();
    }

    public void showCustomAlert(Context context, int msg_type, String title, String desc) {
// public void showCustomAlert(Context context,String msg_type,String msg,String desc){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
// Get the layout inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.layout_custom_alert, null);
        builder.setCancelable(false);
        builder.setView(layout);
        builder.create();

        final AlertDialog alertDialog = builder.create();
        NTBoldTextView alert_title = (NTBoldTextView) layout.findViewById(R.id.alert_title);
        NTTextView alert_desc = (NTTextView) layout.findViewById(R.id.alert_desc);
        NTBoldTextView btnOk = (NTBoldTextView) layout.findViewById(R.id.btnOk);
        Drawable res = null;

        switch (msg_type) {
            case ALERT_ERROR:
                alert_title.setTextColor(context.getResources().getColor(R.color.black));
                break;

            case ALERT_WARNING:
                alert_title.setTextColor(context.getResources().getColor(R.color.black));
                break;

            case ALERT_SUCCESS:
                alert_title.setTextColor(context.getResources().getColor(R.color.black));

                break;
        }


        alert_title.setText(title);
        alert_desc.setText(desc);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alertDialog != null && alertDialog.isShowing())
                    alertDialog.dismiss();
            }
        });


        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
// alertDialog.getWindow().addFlags(WindowManager.LayoutParams.DIM_AMOUNT_CHANGED);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();

    }

    public boolean checktimings(String time) {

        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            String currentTime = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(currentTime);

            return date1.after(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MM-yyyy hh:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }

    public String getMonthSch(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSSSS'Z'", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }

    public String getDate(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSSSS'Z'", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String dateName = new SimpleDateFormat("dd").format(cal.getTime());
        return dateName;
    }

    public String getYear(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSSSS'Z'", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String yearName = new SimpleDateFormat("yyyy").format(cal.getTime());
        return yearName;
    }

    public String getTime(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSSSS'Z'", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String timeName = new SimpleDateFormat("hh:mm a").format(cal.getTime());
        return timeName;
    }

    public void hideKeypad(Context context, View view) {
        // Check if no view has focus:
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showAlert(Context context, String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            builder.setMessage(message)
                    .setTitle(context.getString(R.string.app_name))
                    .setCancelable(true)
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void NTToast(Context context, String msg, int duration) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = null;

        //Getting the View object as defined in the customtoast.xml file
        layout = layoutInflater.inflate(R.layout.layout_custom_toast, null);
        NTTextView txtToast = (NTTextView) layout.findViewById(R.id.txt_toast);
        txtToast.setText(msg);
        //Creating the Toast object
        Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setView(layout);//setting the view of custom toast layout
        toast.show();
    }


}
