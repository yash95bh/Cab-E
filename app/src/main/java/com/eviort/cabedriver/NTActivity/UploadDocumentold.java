package com.eviort.cabedriver.NTActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.eviort.cabedriver.NTApplication;
import com.eviort.cabedriver.NTHelper.LoadingDialog;
import com.eviort.cabedriver.NTHelper.SharedHelper;
import com.eviort.cabedriver.NTHelper.URLHelper;
import com.eviort.cabedriver.NTHelper.VolleyMultipartRequest;
import com.eviort.cabedriver.NTUtilites.Utilities;
import com.eviort.cabedriver.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UploadDocumentold extends AppCompatActivity implements View.OnClickListener {

    ImageView btnInsurance;
    ImageView btnRegCertificate;
    ImageView btnLicense, btnSPSL, btnUniqueId, btnPhotograph, btnOwnership, btnRWC;
    ImageView btnDeleteLicense, btnDeleteSPSL, btnDeleteUniqueId, btnDeletePhotograph, btnDeleteOwnership, btnDeleteInsurance, btnDeleteRWC;
    ImageView licenseStatus, spslStatus, insuranceStatus, uniqueidStatus, photographStatus, ownershipStatus, rwcStatus;
    String responsemsg;
    Context context = UploadDocumentold.this;
    Activity activity = UploadDocumentold.this;
    Bitmap insuranceBitmap, licenseBitmap, spslBitmap, uniqueidBitmap, ownershipBitmap, photographBitmap, rwcBitmap;
    Utilities utils = new Utilities();
    LoadingDialog loadingDialog;
    int fileSize;
    Boolean isUploadImage = false;
    int LICENSE_REQUEST_CODE = 199;
    int SPSL_REQUEST_CODE = 200;
    int INSURANCE_REQUEST_CODE = 201;
    int UNIQUEID_REQUEST_CODE = 202;
    int PHOTOGRAPH_REQUEST_CODE = 203;
    int OWNERSHIP_REQUEST_CODE = 204;
    int RWC_REQUEST_CODE = 205;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_document);

        licenseStatus = (ImageView) findViewById(R.id.licenseStatus);
        spslStatus = (ImageView) findViewById(R.id.spslStatus);
        insuranceStatus = (ImageView) findViewById(R.id.insuranceStatus);
        uniqueidStatus = (ImageView) findViewById(R.id.uniqueidStatus);
        photographStatus = (ImageView) findViewById(R.id.photographStatus);
        ownershipStatus = (ImageView) findViewById(R.id.ownershipStatus);
        rwcStatus = (ImageView) findViewById(R.id.rwcStatus);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        btnLicense = (ImageView) findViewById(R.id.btnLicense);
        btnSPSL = (ImageView) findViewById(R.id.btnSPSL);
        btnInsurance = (ImageView) findViewById(R.id.btnInsurance);
        btnUniqueId = (ImageView) findViewById(R.id.btnUniqueId);
        btnPhotograph = (ImageView) findViewById(R.id.btnPhotograph);
        btnOwnership = (ImageView) findViewById(R.id.btnOwnership);
        btnRWC = (ImageView) findViewById(R.id.btnRWC);

        btnDeleteLicense = (ImageView) findViewById(R.id.btnDeleteLicense);
        btnDeleteSPSL = (ImageView) findViewById(R.id.btnDeleteSPSL);
        btnDeleteInsurance = (ImageView) findViewById(R.id.btnDeleteInsurance);
        btnDeleteUniqueId = (ImageView) findViewById(R.id.btnDeleteUniqueId);
        btnDeletePhotograph = (ImageView) findViewById(R.id.btnDeletePhotograph);
        btnDeleteOwnership = (ImageView) findViewById(R.id.btnDeleteOwnership);
        btnDeleteRWC = (ImageView) findViewById(R.id.btnDeleteRWC);


        btnLicense.setOnClickListener(this);
        btnSPSL.setOnClickListener(this);
        btnInsurance.setOnClickListener(this);
        btnUniqueId.setOnClickListener(this);
        btnPhotograph.setOnClickListener(this);
        btnOwnership.setOnClickListener(this);
        btnRWC.setOnClickListener(this);


        btnDeleteLicense.setOnClickListener(this);
        btnDeleteSPSL.setOnClickListener(this);
        btnDeleteInsurance.setOnClickListener(this);
        btnDeleteUniqueId.setOnClickListener(this);
        btnDeletePhotograph.setOnClickListener(this);
        btnDeleteOwnership.setOnClickListener(this);
        btnDeleteRWC.setOnClickListener(this);

        getUploadedDocuments();
//        getDownloadableDocuments();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {

            case R.id.btnLicense:

                selectImage(LICENSE_REQUEST_CODE);

//                Intent iLicense = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(iLicense, LICENSE_REQUEST_CODE);
                break;

            case R.id.btnSPSL:
                selectImage(SPSL_REQUEST_CODE);
//                Intent iJoining = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(iJoining, SPSL_REQUEST_CODE);
                break;

            case R.id.btnInsurance:
                selectImage(INSURANCE_REQUEST_CODE);
//                Intent iDeposit = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(iDeposit, INSURANCE_REQUEST_CODE);
                break;

            case R.id.btnUniqueId:
                selectImage(UNIQUEID_REQUEST_CODE);
//                Intent iInsurance = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(iInsurance, UNIQUEID_REQUEST_CODE);
                break;

            case R.id.btnPhotograph:
                selectImage(PHOTOGRAPH_REQUEST_CODE);
//                Intent iPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(iPhoto, PHOTOGRAPH_REQUEST_CODE);
                break;

            case R.id.btnOwnership:
                selectImage(OWNERSHIP_REQUEST_CODE);
//                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(i, OWNERSHIP_REQUEST_CODE);
                break;

            case R.id.btnRWC:
                selectImage(RWC_REQUEST_CODE);
//                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(i, OWNERSHIP_REQUEST_CODE);
                break;


            case R.id.btnDeleteLicense:
                deleteDocument("license");
                break;
            case R.id.btnDeleteSPSL:
                deleteDocument("vehicle_certificate");
                break;
            case R.id.btnDeleteInsurance:
                deleteDocument("insurance");
                break;
            case R.id.btnDeleteUniqueId:
                deleteDocument("driver_certificate");
                break;
            case R.id.btnDeletePhotograph:
                deleteDocument("vehicle_photo");
                break;
            case R.id.btnDeleteOwnership:
                deleteDocument("reg_certificate");
                break;
            case R.id.btnDeleteRWC:
                deleteDocument("rwc_certificate");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK && data != null) {
                isUploadImage = true;
                Bitmap bitmap;

                if (data.getData() != null) {
                    //getting the image Uri
                    Uri imageUri = data.getData();
                    fileSize = getImageSize(imageUri);
                    //getting bitmap object from uri
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } else {
                    bitmap = (Bitmap) data.getExtras().get("data");
                }


                if (requestCode == INSURANCE_REQUEST_CODE) {
                    btnInsurance.setImageBitmap(bitmap);
                    insuranceBitmap = bitmap;

                    setUploadStatus("insurance", View.VISIBLE, "waiting");
                    //uploadImage("insurance");
                } else if (requestCode == LICENSE_REQUEST_CODE) {
                    btnLicense.setImageBitmap(bitmap);
                    licenseBitmap = bitmap;
                    setUploadStatus("license", View.VISIBLE, "waiting");

                    new UploadFileToServer().execute("license");
                    // uploadImage("license");
                } else if (requestCode == SPSL_REQUEST_CODE) {
                    btnSPSL.setImageBitmap(bitmap);
                    spslBitmap = bitmap;
                    setUploadStatus("vehicle_certificate", View.VISIBLE, "waiting");
                    //  uploadImage("vehicle_certificate");
                } else if (requestCode == UNIQUEID_REQUEST_CODE) {
                    btnUniqueId.setImageBitmap(bitmap);
                    uniqueidBitmap = bitmap;
                    setUploadStatus("driver_certificate", View.VISIBLE, "waiting");
                    //  uploadImage("driver_certificate");
                } else if (requestCode == PHOTOGRAPH_REQUEST_CODE) {
                    btnPhotograph.setImageBitmap(bitmap);
                    photographBitmap = bitmap;
                    setUploadStatus("vehicle_photo", View.VISIBLE, "waiting");
                    //  uploadImage("vehicle_photo");
                } else if (requestCode == OWNERSHIP_REQUEST_CODE) {
                    btnOwnership.setImageBitmap(bitmap);
                    ownershipBitmap = bitmap;
                    setUploadStatus("reg_certificate", View.VISIBLE, "waiting");
                    // uploadImage("reg_certificate");
                } else if (requestCode == RWC_REQUEST_CODE) {
                    btnRWC.setImageBitmap(bitmap);
                    rwcBitmap = bitmap;
                    setUploadStatus("rwc_certificate", View.VISIBLE, "waiting");
                    //  uploadImage("rwc_certificate");
                }

            } else {
                isUploadImage = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void getDownloadableDocuments() {
        Utilities.PrintAPI_URL(URLHelper.DOWNLOAD_DOCUMENT, "GET");
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.DOWNLOAD_DOCUMENT, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    utils.showCustomAlert(context, Utilities.ALERT_SUCCESS, "Document Upload", response.toString(1));
                } catch (JSONException e) {
                    e.printStackTrace();
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
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-localization", SharedHelper.getKey(context, "lang"));
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    private void getUploadedDocuments() {
        Utilities.PrintAPI_URL(URLHelper.GET_DOCUMENT, "GET");
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.GET_DOCUMENT, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                if (response.optString("license") != null && !response.optString("license").isEmpty()) {
                    Picasso.with(context).load(response.optString("license")).error(R.drawable.placeholder).into(btnLicense);
                }
                if (response.optString("vehicle_certificate") != null && !response.optString("vehicle_certificate").isEmpty()) {
                    Picasso.with(context).load(response.optString("vehicle_certificate")).error(R.drawable.placeholder).into(btnSPSL);
                }

                if (response.optString("insurance") != null && !response.optString("insurance").isEmpty()) {
                    Picasso.with(context).load(response.optString("insurance")).error(R.drawable.placeholder).into(btnInsurance);
                }

                if (response.optString("driver_certificate") != null && !response.optString("driver_certificate").isEmpty()) {
                    Picasso.with(context).load(response.optString("driver_certificate")).error(R.drawable.placeholder).into(btnUniqueId);
                }
                if (response.optString("vehicle_photo") != null && !response.optString("vehicle_photo").isEmpty()) {
                    Picasso.with(context).load(response.optString("vehicle_photo")).error(R.drawable.placeholder).into(btnPhotograph);
                }
                if (response.optString("reg_certificate") != null && !response.optString("reg_certificate").isEmpty()) {
                    Picasso.with(context).load(response.optString("reg_certificate")).error(R.drawable.placeholder).into(btnOwnership);
                }

                if (response.optString("rwc_certificate") != null && !response.optString("rwc_certificate").isEmpty()) {
                    Picasso.with(context).load(response.optString("rwc_certificate")).error(R.drawable.placeholder).into(btnRWC);
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
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-localization", SharedHelper.getKey(context, "lang"));
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    private void selectImage(final int REQUEST_CODE) {


        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};


        AlertDialog.Builder builder = new AlertDialog.Builder(UploadDocumentold.this);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {

                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
                    } else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intent, REQUEST_CODE);
                        }
                    }


                } else if (options[item].equals("Choose from Gallery")) {

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(intent, REQUEST_CODE);


                } else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, requestCode);
                }
            }
        }
    }

    private void setUploadStatus(String documentName, int visibility, String status) {
        Drawable drawable;
        if (status.equalsIgnoreCase("success")) {
            drawable = context.getResources().getDrawable(R.drawable.success);
        } else if (status.equalsIgnoreCase("failure")) {
            drawable = context.getResources().getDrawable(R.drawable.failure);
        } else {
            drawable = context.getResources().getDrawable(R.drawable.success_grey);
        }
        switch (documentName) {
            case "license":
                licenseStatus.setVisibility(visibility);
                licenseStatus.setImageDrawable(drawable);

                break;
            case "vehicle_certificate":
                spslStatus.setVisibility(visibility);
                spslStatus.setImageDrawable(drawable);
                break;
            case "insurance":
                insuranceStatus.setVisibility(visibility);
                insuranceStatus.setImageDrawable(drawable);
                break;
            case "driver_certificate":
                uniqueidStatus.setVisibility(visibility);
                uniqueidStatus.setImageDrawable(drawable);
                break;
            case "vehicle_photo":
                photographStatus.setVisibility(visibility);
                photographStatus.setImageDrawable(drawable);
                break;
            case "reg_certificate":
                ownershipStatus.setVisibility(visibility);
                ownershipStatus.setImageDrawable(drawable);
                break;
            case "rwc_certificate":
                rwcStatus.setVisibility(visibility);
                rwcStatus.setImageDrawable(drawable);
                break;
        }
    }

    private String uploadImage(final String documentName) {


        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLHelper.UPLOAD_DOCUMENT, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                try {
                    JSONObject errorObj = new JSONObject(new String(response.data));
                    // utils.showCustomAlert(context,utils.ALERT_WARNING,"Document Upload",errorObj.optString("message"));
                    responsemsg = errorObj.optString("message");
                    setUploadStatus(documentName, View.VISIBLE, "success");

                } catch (Exception e) {
                    // utils.showCustomAlert(context,utils.ALERT_ERROR,"Document Upload", getString(R.string.something_went_wrong));
                    setUploadStatus(documentName, View.VISIBLE, "failure");
                    responsemsg = getString(R.string.something_went_wrong);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    Utilities.print("MyTest", "" + error);
                    Utilities.print("MyTestError", "" + error.networkResponse);
                    Utilities.print("MyTestError1", "" + response.statusCode);
                    setUploadStatus(documentName, View.VISIBLE, "failure");
                    if (response != null && response.data != null) {
                        try {


                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
//                            JSONObject errorObj = new JSONObject(new String(response.data));
//                            try{
//                                utils.commonAlert(context,errorObj.optString("error"));
//                            }catch (Exception e){
////                                utils.commonAlert(context,getString(R.string.something_went_wrong));
//                                utils.commonAlert(context,errorObj.optString("message"));
//
//                            }
                                utils.showCustomAlert(context, Utilities.ALERT_ERROR, "Document Upload", response.data.toString());
                            } else if (response.statusCode == 401) {
                                GoToBeginActivity();
                            } else if (response.statusCode == 422) {
                                json = NTApplication.trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    utils.showCustomAlert(context, Utilities.ALERT_ERROR, "Document Upload", json);
                                } else {
                                    utils.showCustomAlert(context, Utilities.ALERT_ERROR, "Document Upload", getString(R.string.please_try_again));
                                }
                            } else {
                                utils.showCustomAlert(context, Utilities.ALERT_ERROR, "Document Upload", getString(R.string.please_try_again));
                            }

                        } catch (Exception e) {
                            utils.showCustomAlert(context, Utilities.ALERT_ERROR, "Document Upload", getString(R.string.something_went_wrong));
                        }


                    } else {
                        if (error instanceof NoConnectionError) {
                            utils.showCustomAlert(context, Utilities.ALERT_ERROR, "Document Upload", getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof NetworkError) {
                            utils.showCustomAlert(context, Utilities.ALERT_ERROR, "Document Upload", getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof TimeoutError) {
                            uploadImage(documentName);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();

                }

            }
        }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("document_id", documentName);
                Utilities.PrintAPI_URL(URLHelper.UPLOAD_DOCUMENT, params.toString());
                return params;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-localization", SharedHelper.getKey(context, "lang"));
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> object = new HashMap<>();

                switch (documentName) {
                    case "license":
                        if (licenseBitmap != null) {

                            object.put("image", new DataPart("documentName" + "jpg", getFileDataFromDrawable(licenseBitmap)));
                        }
                        break;
                    case "vehicle_certificate":
                        if (spslBitmap != null) {
                            object.put("image", new DataPart("documentName" + "jpg", getFileDataFromDrawable(spslBitmap)));
                        }
                        break;
                    case "insurance":
                        if (insuranceBitmap != null) {
                            object.put("image", new DataPart("documentName" + "jpg", getFileDataFromDrawable(insuranceBitmap)));
                        }
                        break;
                    case "driver_certificate":
                        if (uniqueidBitmap != null) {
                            object.put("image", new DataPart("documentName" + "jpg", getFileDataFromDrawable(uniqueidBitmap)));
                        }
                        break;
                    case "vehicle_photo":
                        if (photographBitmap != null) {
                            object.put("image", new DataPart("documentName" + "jpg", getFileDataFromDrawable(photographBitmap)));
                        }
                        break;
                    case "reg_certificate":
                        if (ownershipBitmap != null) {
                            object.put("image", new DataPart("documentName" + "jpg", getFileDataFromDrawable(ownershipBitmap)));
                        }
                        break;
                    case "rwc_certificate":
                        if (rwcBitmap != null) {
                            object.put("image", new DataPart("documentName" + "jpg", getFileDataFromDrawable(rwcBitmap)));
                        }
                        break;
                }

                Utilities.PrintAPI_URL(URLHelper.UPLOAD_DOCUMENT, object.toString());
                return object;
            }
        };
        NTApplication.getInstance().addToRequestQueue(volleyMultipartRequest);

        return responsemsg;
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void deleteDocument(final String documentName) {
        loadingDialog = new LoadingDialog(this);
        if (loadingDialog != null)
            loadingDialog.showDialog();
        JSONObject object = new JSONObject();
        try {
            object.put("document_id", documentName);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Utilities.PrintAPI_URL(URLHelper.DELETE_DOCUMENT, object.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.DELETE_DOCUMENT, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Utilities.printAPI_Response(response.toString());
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.hideDialog();
                utils.showCustomAlert(context, Utilities.ALERT_SUCCESS, "Document Upload", response.optString("message"));
                setUploadStatus(documentName, View.GONE, "waiting");
                switch (documentName) {
                    case "license":
                        btnLicense.setImageDrawable(getResources().getDrawable(R.drawable.upload_placeholder));
                        break;
                    case "vehicle_certificate":
                        btnSPSL.setImageDrawable(getResources().getDrawable(R.drawable.upload_placeholder));
                        break;
                    case "insurance":
                        btnInsurance.setImageDrawable(getResources().getDrawable(R.drawable.upload_placeholder));
                        break;
                    case "driver_certificate":
                        btnUniqueId.setImageDrawable(getResources().getDrawable(R.drawable.upload_placeholder));
                        break;
                    case "vehicle_photo":
                        btnPhotograph.setImageDrawable(getResources().getDrawable(R.drawable.upload_placeholder));
                        break;
                    case "reg_certificate":
                        btnOwnership.setImageDrawable(getResources().getDrawable(R.drawable.upload_placeholder));
                        break;
                    case "rwc_certificate":
                        btnRWC.setImageDrawable(getResources().getDrawable(R.drawable.upload_placeholder));
                        break;
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    if ((loadingDialog != null) && (loadingDialog.isShowing()))
                        loadingDialog.hideDialog();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    Utilities.print("MyTest", "" + error);
                    Utilities.print("MyTestError", "" + error.networkResponse);
                    Utilities.print("MyTestError1", "" + response.statusCode);
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));
                            Utilities.print("ErrorChangePasswordAPI", "" + errorObj);

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    utils.showCustomAlert(context, Utilities.ALERT_ERROR, "Document Upload", errorObj.optString("error"));
                                } catch (Exception e) {
                                    utils.showCustomAlert(context, Utilities.ALERT_ERROR, "Document Upload", getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                GoToBeginActivity();
                            } else if (response.statusCode == 422) {
                                json = NTApplication.trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    utils.showCustomAlert(context, Utilities.ALERT_ERROR, "Document Upload", json);
                                } else {
                                    utils.showCustomAlert(context, Utilities.ALERT_ERROR, "Document Upload", getString(R.string.please_try_again));
                                }
                            } else {
                                utils.showCustomAlert(context, Utilities.ALERT_ERROR, "Document Upload", getString(R.string.please_try_again));
                            }

                        } catch (Exception e) {
                            utils.showCustomAlert(context, Utilities.ALERT_ERROR, "Document Upload", getString(R.string.something_went_wrong));
                        }


                    } else {
                        if (error instanceof NoConnectionError) {
                            utils.showCustomAlert(context, Utilities.ALERT_ERROR, "Document Upload", getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof NetworkError) {
                            utils.showCustomAlert(context, Utilities.ALERT_ERROR, "Document Upload", getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof TimeoutError) {
                            deleteDocument(documentName);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();

                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-localization", SharedHelper.getKey(context, "lang"));
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public void GoToBeginActivity() {
        SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(context, ActivityBegin.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public int getImageSize(Uri choosen) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), choosen);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        long lengthbmp = imageInByte.length;

        Toast.makeText(getApplicationContext(), Long.toString(lengthbmp), Toast.LENGTH_SHORT).show();

        return (int) lengthbmp;
    }

    private class UploadFileToServer extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);
        }

        @Override
        protected String doInBackground(String... params) {
            for (int i = 0; i < fileSize; i++) {
                publishProgress(i);
                try {
                    Thread.sleep(88);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return uploadImage(params[0]);

        }


        @Override
        protected void onPostExecute(String result) {
            // Log.e(TAG, "Response from server: " + result);


            utils.showCustomAlert(context, Utilities.ALERT_WARNING, "Document Upload", result);
            super.onPostExecute(result);
            //setUploadStatus(doc,View.VISIBLE,"success");
        }

    }
}


