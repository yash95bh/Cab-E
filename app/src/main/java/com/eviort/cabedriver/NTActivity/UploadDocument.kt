package com.eviort.cabedriver.NTActivity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.eviort.cabedriver.NTApplication
import com.eviort.cabedriver.NTCustomView.NTTextView
import com.eviort.cabedriver.NTHelper.LoadingDialog
import com.eviort.cabedriver.NTHelper.SharedHelper
import com.eviort.cabedriver.NTHelper.SharedHelper.getKey
import com.eviort.cabedriver.NTHelper.SharedHelper.putKey
import com.eviort.cabedriver.NTHelper.URLHelper
import com.eviort.cabedriver.NTHelper.VolleyMultipartRequest
import com.eviort.cabedriver.NTUtilites.Utilities
import com.eviort.cabedriver.R
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class UploadDocument : AppCompatActivity(), View.OnClickListener {


    var responsemsg: String? = null
    var documentListAdapter: DocumentListAdapter? = null
    var context: Context = this@UploadDocument
    var activity: Activity = this@UploadDocument
    var licenseBitmap: Bitmap? = null
    var utils = Utilities()
    var loadingDialog: LoadingDialog? = null
    var fileSize = 0
    var recyclerView: RecyclerView? = null
    var isUploadImage = false
    var LICENSE_REQUEST_CODE = 199
    var SPSL_REQUEST_CODE = 200
    var INSURANCE_REQUEST_CODE = 201
    var UNIQUEID_REQUEST_CODE = 202
    var PHOTOGRAPH_REQUEST_CODE = 203
    var OWNERSHIP_REQUEST_CODE = 204
    var RWC_REQUEST_CODE = 205
    var mmprogressBar: ProgressBar? = null
    var ll_errorLayout: LinearLayout? = null
    var backArrow: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_document)
        recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        ll_errorLayout = findViewById<View>(R.id.ll_errorLayout) as LinearLayout
        backArrow = findViewById<View>(R.id.backArrow) as ImageView
        backArrow!!.setOnClickListener(View.OnClickListener { onBackPressed() })
        getDocsList()
        Utilities.setLocale(context!!, SharedHelper.getKey(context!!, "lang"))

        //        getDownloadableDocuments();
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onClick(view: View) {
        val id = view.id
        when (id) {
            /*     R.id.btnLicense -> selectImage(LICENSE_REQUEST_CODE)
                 R.id.btnSPSL -> selectImage(SPSL_REQUEST_CODE)
                 R.id.btnInsurance -> selectImage(INSURANCE_REQUEST_CODE)
                 R.id.btnUniqueId -> selectImage(UNIQUEID_REQUEST_CODE)
                 R.id.btnPhotograph -> selectImage(PHOTOGRAPH_REQUEST_CODE)
                 R.id.btnOwnership -> selectImage(OWNERSHIP_REQUEST_CODE)
                 R.id.btnRWC -> selectImage(RWC_REQUEST_CODE)
                 R.id.btnDeleteLicense -> deleteDocument("license")
                 R.id.btnDeleteSPSL -> deleteDocument("vehicle_certificate")
                 R.id.btnDeleteInsurance -> deleteDocument("insurance")
                 R.id.btnDeleteUniqueId -> deleteDocument("driver_certificate")
                 R.id.btnDeletePhotograph -> deleteDocument("vehicle_photo")
                 R.id.btnDeleteOwnership -> deleteDocument("reg_certificate")
                 R.id.btnDeleteRWC -> deleteDocument("rwc_certificate")*/
        }
    }


    private val downloadableDocuments: Unit
        private get() {
            Utilities.PrintAPI_URL(URLHelper.DOWNLOAD_DOCUMENT, "GET")
            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
                    Method.GET,
                    URLHelper.DOWNLOAD_DOCUMENT,
                    null,
                    Response.Listener { response ->
                        try {
                            utils.showCustomAlert(
                                    context,
                                    Utilities.ALERT_SUCCESS,
                                    "Document Upload",
                                    response.toString(1)
                            )
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener { error ->
                        try {
                            if (error is TimeoutError) {
                                Toast.makeText(
                                        context,
                                        context.getString(R.string.error_network_timeout),
                                        Toast.LENGTH_LONG
                                ).show()
                            } else if (error is NoConnectionError) {
                                Toast.makeText(
                                        context,
                                        context.getString(R.string.error_no_network),
                                        Toast.LENGTH_LONG
                                ).show()
                            } else if (error is AuthFailureError) {
                                Toast.makeText(
                                        context,
                                        context.getString(R.string.error_auth_failure),
                                        Toast.LENGTH_LONG
                                ).show()
                            } else if (error is ServerError) {
                                Toast.makeText(
                                        context,
                                        context.getString(R.string.error_server_connection),
                                        Toast.LENGTH_LONG
                                ).show()
                            } else if (error is NetworkError) {
                                Toast.makeText(
                                        context,
                                        context.getString(R.string.error_network),
                                        Toast.LENGTH_LONG
                                ).show()
                            } else if (error is ParseError) {

                            } else {
                                utils.showCustomAlert(
                                        context,
                                        Utilities.ALERT_ERROR,
                                        resources.getString(R.string.app_name),
                                        error.message
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(
                                    context,
                                    context.getString(R.string.something_went_wrong),
                                    Toast.LENGTH_LONG
                            ).show()
                        }
                    }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["X-localization"] = getKey(context, "lang")!!
                    headers["X-Requested-With"] = "XMLHttpRequest"
                    headers["Authorization"] = "Bearer " + getKey(context, "access_token")
                    return headers
                }
            }
            NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
        }


    private fun getDocsList() {

        Utilities.PrintAPI_URL(URLHelper.GET_DOCUMENT, "GET")
        println("PassengerAPI Header: " + "X-Requested-With : XMLHttpRequest " + "Authorization : " + getKey(context, "token_type") + " " + getKey(context, "access_token"))
        val jsonArrayRequest: JsonArrayRequest = object : JsonArrayRequest(URLHelper.GET_DOCUMENT, Response.Listener { response ->
            Utilities.printAPI_Response(response.toString())
            if (response != null) {
                Log.d("TAG", "getDocsList------------: " + response.toString())
                documentListAdapter = activity.let { DocumentListAdapter(it, response) }
                //  recyclerView.setHasFixedSize(true);
                val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity.applicationContext)
                recyclerView!!.layoutManager = mLayoutManager
                recyclerView!!.itemAnimator = DefaultItemAnimator()
                if (documentListAdapter != null && documentListAdapter!!.itemCount > 0) {
                    ll_errorLayout!!.visibility = View.GONE
                    recyclerView!!.adapter = documentListAdapter
                } else {
                    ll_errorLayout!!.visibility = View.VISIBLE
                }
            } else {
                ll_errorLayout!!.visibility = View.VISIBLE
            }
            if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
        }, Response.ErrorListener { error ->
            if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
            try {
                var json: String? = null
                var Message: String
                val response = error.networkResponse
                if (response != null && response.data != null) {
                    try {
                        val errorObj = JSONObject(String(response.data))
                        println("PassengerAPI response error : " + error + " " + error.networkResponse + " " + response.statusCode + " " + errorObj.optString("error"))
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                /* utils.displayMessage(
                                     context,errorObj.optString("message"))*/
                            } catch (e: Exception) {
                                /* utils.displayMessage(
                                     context,getString(R.string.something_went_wrong))*/
                            }
                        } else if (response.statusCode == 401) {
//                            refreshAccessToken("PAST_TRIPS");
                        } else if (response.statusCode == 422) {
                            json = NTApplication.trimMessage(String(response.data))
                            if (json !== "" && json != null) {
                                /*utils.displayMessage(
                                    context,json)*/
                            } else {
                                /*utils.showCustomAlert(
                                    context,getString(R.string.please_try_again))*/
                            }
                        } else if (response.statusCode == 503) {
                            /* utils.showCustomAlert(
                                 context,getString(R.string.server_down))*/
                        } else {
                            /*utils.showCustomAlert(
                                context,getString(R.string.please_try_again))*/
                        }
                    } catch (e: Exception) {
                        /*  displayMessage(getString(R.string.something_went_wrong))*/
                    }
                } else {
                    if (error is NoConnectionError) {
                        // displayMessage(getString(R.string.oops_connect_your_internet))
                    } else if (error is NetworkError) {
                        //  displayMessage(getString(R.string.oops_connect_your_internet))
                    } else if (error is TimeoutError) {
                        getDocsList()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(activity, resources.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
            }
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = getKey(context, "lang")!!
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "" + getKey(context, "token_type") + " " + getKey(context, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonArrayRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (resultCode == RESULT_OK && data != null) {


                isUploadImage = true
                val bitmap: Bitmap?
                if (data.data != null) {
                    //getting the image Uri
                    val imageUri = data.data
                    fileSize = getImageSize(imageUri)
                    //getting bitmap object from uri
                    bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, imageUri)
                } else {
                    bitmap = data.extras!!["data"] as Bitmap?
                }


                if (requestCode == INSURANCE_REQUEST_CODE) {

                    setUploadStatus("insurance", View.VISIBLE, "waiting")
                    //uploadImage("insurance");
                } else if (requestCode == LICENSE_REQUEST_CODE) {

                    licenseBitmap = bitmap
                    setUploadStatus("license", View.VISIBLE, "waiting")
                    // UploadFileToServer().execute(SharedHelper.getKey(context,"doc_id"))
                    uploadImage(SharedHelper.getKey(context, "doc_id").toString());
                } else if (requestCode == SPSL_REQUEST_CODE) {

                    setUploadStatus("vehicle_certificate", View.VISIBLE, "waiting")
                    //  uploadImage("vehicle_certificate");
                } else if (requestCode == UNIQUEID_REQUEST_CODE) {

                    setUploadStatus("driver_certificate", View.VISIBLE, "waiting")
                    //  uploadImage("driver_certificate");
                } else if (requestCode == PHOTOGRAPH_REQUEST_CODE) {

                    setUploadStatus("vehicle_photo", View.VISIBLE, "waiting")
                    //  uploadImage("vehicle_photo");
                } else if (requestCode == OWNERSHIP_REQUEST_CODE) {

                    setUploadStatus("reg_certificate", View.VISIBLE, "waiting")
                    // uploadImage("reg_certificate");
                } else if (requestCode == RWC_REQUEST_CODE) {

                    setUploadStatus("rwc_certificate", View.VISIBLE, "waiting")
                    //  uploadImage("rwc_certificate");
                }

            } else {
                isUploadImage = false
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    inner class DocumentListAdapter(private val activity: Activity, var jsonArray: JSONArray) : RecyclerView.Adapter<DocumentListAdapter.MyViewHolder>() {
        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var btnLicense: ImageView
            var progressBar: ProgressBar
            var btnDeleteLicense: ImageView
            var licenseStatus: ImageView
            var txt_document: NTTextView

            init {
                licenseStatus = itemView.findViewById<View>(R.id.licenseStatus) as ImageView

                progressBar = itemView.findViewById<View>(R.id.progress_bar) as ProgressBar
                btnLicense = itemView.findViewById<View>(R.id.btnLicense) as ImageView
                btnDeleteLicense = itemView.findViewById<View>(R.id.btnDeleteLicense) as ImageView
                txt_document = itemView.findViewById<View>(R.id.txt_document) as NTTextView

            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
            val itemView = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.layout_document_list, viewGroup, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            try {
                holder.btnLicense!!.setOnClickListener {
                    SharedHelper.putKey(context, "doc_id", jsonArray.optJSONObject(position).optString("id"))
                    selectImage(LICENSE_REQUEST_CODE, holder.progressBar)
                    holder.progressBar.visibility = View.VISIBLE
                    holder.progressBar.setProgress(2)
                }
                holder.btnDeleteLicense!!.setOnClickListener {
                    val builder3 = AlertDialog.Builder(this@UploadDocument)
                    val inflater3 = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val layout3 = inflater3.inflate(R.layout.layout_alert, null)
                    builder3.setCancelable(false)
                    builder3.setView(layout3)
                    builder3.create()
                    val alertDialog3 = builder3.create()
                    val tv_alert_title = layout3.findViewById<View>(R.id.tv_alert_title) as NTTextView
                    val tv_alert_desc = layout3.findViewById<View>(R.id.tv_alert_desc) as NTTextView
                    val tv_alert_okBtn = layout3.findViewById<View>(R.id.tv_alert_okBtn) as NTTextView
                    val tv_alert_noBtn = layout3.findViewById<View>(R.id.tv_alert_noBtn) as NTTextView
                    tv_alert_title.text = resources.getString(R.string.alert_delete_doc_title)
                    tv_alert_desc.text = resources.getString(R.string.delete_doc_alert)
                    tv_alert_okBtn.text = resources.getString(R.string.ok)
                    tv_alert_noBtn.text = resources.getString(R.string.cancel)
                    tv_alert_okBtn.setOnClickListener {
                        deleteDocument(jsonArray.optJSONObject(position).optString("id"))
                        alertDialog3.dismiss()
                    }
                    tv_alert_noBtn.setOnClickListener { alertDialog3.dismiss() }
                    alertDialog3.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    alertDialog3.window!!.attributes.windowAnimations = R.style.dialog_animation
                    alertDialog3.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                    alertDialog3.show()


                }
                holder.txt_document!!.text = jsonArray.optJSONObject(position).optString("doc_name")

                if (jsonArray.optJSONObject(position).optString("url") !== "") {
                    Picasso.with(context)
                            .load(jsonArray.optJSONObject(position).optString("url"))
                            .placeholder(R.drawable.upload_placeholder) // optional
                            .error(R.drawable.upload_placeholder) // optional
                            .into(holder.btnLicense)
                }
                if (!jsonArray.optJSONObject(position).optString("url").equals("")) {
                    Picasso.with(activity).load(jsonArray.optJSONObject(position).optString("url")).placeholder(R.drawable.upload_placeholder).error(R.drawable.upload_placeholder).into(holder.btnLicense)
                    holder.progressBar.visibility = View.VISIBLE
                    holder.progressBar.setProgress(100)
                } else {
                    Picasso.with(activity).load(R.drawable.upload_placeholder).placeholder(R.drawable.upload_placeholder).error(R.drawable.upload_placeholder).into(holder.btnLicense)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        override fun getItemCount(): Int {
            return jsonArray.length()
        }


        private fun deleteDocument(documentName: String) {

            val `object` = JSONObject()
            try {
                `object`.put("document_id", documentName)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            Utilities.PrintAPI_URL(URLHelper.DELETE_DOCUMENT, `object`.toString())
            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
                    Method.POST,
                    URLHelper.DELETE_DOCUMENT,
                    `object`,
                    Response.Listener { response ->
                        Utilities.printAPI_Response(response.toString())
                        utils.showCustomAlert(
                                context,
                                Utilities.ALERT_SUCCESS,
                                "Document Upload",
                                getString(R.string.uploaddocumentfail)
                        )
                        setUploadStatus(documentName, View.GONE, "waiting")
                        getDocsList()
                        /*  when (documentName) {
                              "license" -> btnLicense!!.setImageDrawable(resources.getDrawable(R.drawable.upload_placeholder))
                              "vehicle_certificate" -> btnSPSL!!.setImageDrawable(resources.getDrawable(R.drawable.upload_placeholder))
                              "insurance" -> btnInsurance!!.setImageDrawable(resources.getDrawable(R.drawable.upload_placeholder))
                              "driver_certificate" -> btnUniqueId!!.setImageDrawable(resources.getDrawable(R.drawable.upload_placeholder))
                              "vehicle_photo" -> btnPhotograph!!.setImageDrawable(resources.getDrawable(R.drawable.upload_placeholder))
                              "reg_certificate" -> btnOwnership!!.setImageDrawable(resources.getDrawable(R.drawable.upload_placeholder))
                              "rwc_certificate" -> btnRWC!!.setImageDrawable(resources.getDrawable(R.drawable.upload_placeholder))
                          }*/
                    },
                    Response.ErrorListener { error ->
                        try {

                            var json: String? = null
                            var Message: String
                            val response = error.networkResponse
                            Utilities.print("MyTest", "" + error)
                            Utilities.print("MyTestError", "" + error.networkResponse)
                            Utilities.print("MyTestError1", "" + response!!.statusCode)
                            if (response != null && response.data != null) {
                                try {
                                    val errorObj = JSONObject(String(response.data))
                                    Utilities.print("ErrorChangePasswordAPI", "" + errorObj.toString())
                                    if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                        try {
                                            utils.showCustomAlert(
                                                    context,
                                                    Utilities.ALERT_ERROR,
                                                    "Document Upload",
                                                    errorObj.optString("error")
                                            )
                                        } catch (e: Exception) {
                                            utils.showCustomAlert(
                                                    context,
                                                    Utilities.ALERT_ERROR,
                                                    "Document Upload",
                                                    getString(R.string.something_went_wrong)
                                            )
                                        }
                                    } else if (response.statusCode == 401) {
                                        GoToBeginActivity()
                                    } else if (response.statusCode == 422) {
                                        json = NTApplication.trimMessage(String(response.data))
                                        if (json !== "" && json != null) {
                                            utils.showCustomAlert(
                                                    context,
                                                    Utilities.ALERT_ERROR,
                                                    "Document Upload",
                                                    json
                                            )
                                        } else {
                                            utils.showCustomAlert(
                                                    context,
                                                    Utilities.ALERT_ERROR,
                                                    "Document Upload",
                                                    getString(R.string.please_try_again)
                                            )
                                        }
                                    } else {
                                        utils.showCustomAlert(
                                                context,
                                                Utilities.ALERT_ERROR,
                                                "Document Upload",
                                                getString(R.string.please_try_again)
                                        )
                                    }
                                } catch (e: Exception) {
                                    utils.showCustomAlert(
                                            context,
                                            Utilities.ALERT_ERROR,
                                            "Document Upload",
                                            getString(R.string.something_went_wrong)
                                    )
                                }
                            } else {
                                if (error is NoConnectionError) {
                                    utils.showCustomAlert(
                                            context,
                                            Utilities.ALERT_ERROR,
                                            "Document Upload",
                                            getString(R.string.oops_connect_your_internet)
                                    )
                                } else if (error is NetworkError) {
                                    utils.showCustomAlert(
                                            context,
                                            Utilities.ALERT_ERROR,
                                            "Document Upload",
                                            getString(R.string.oops_connect_your_internet)
                                    )
                                } else if (error is TimeoutError) {
                                    deleteDocument(documentName)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(
                                    context,
                                    context.getString(R.string.something_went_wrong),
                                    Toast.LENGTH_LONG
                            ).show()
                        }
                    }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["X-localization"] = getKey(context, "lang")!!
                    headers["X-Requested-With"] = "XMLHttpRequest"
                    headers["Authorization"] = "Bearer " + getKey(context, "access_token")
                    return headers
                }
            }
            NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
        }

        fun onRequestPermissionsResult(
                requestCode: Int,
                permissions: Array<String>,
                grantResults: IntArray
        ) {
            if (requestCode == 0) {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivityForResult(intent, requestCode)
                    }
                }
            }
        }

    }


    private fun selectImage(REQUEST_CODE: Int, mProgress: ProgressBar) {
        mmprogressBar = mProgress
        val options = arrayOf<CharSequence>(getString(R.string.take_photo), getString(R.string.gal_txt), getString(R.string.cancel))
        val builder = AlertDialog.Builder(this@UploadDocument)
        builder.setTitle(getString(R.string.add_photo))
        builder.setItems(options) { dialog, item ->
            if (options[item] == getString(R.string.take_photo)) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            REQUEST_CODE
                    )
                } else {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivityForResult(intent, REQUEST_CODE)
                    }
                }
            } else if (options[item] == getString(R.string.gal_txt)) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, REQUEST_CODE)

            } else if (options[item] == getString(R.string.cancel)) {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun uploadImage(documentName: String) {
        val volleyMultipartRequest: VolleyMultipartRequest = object : VolleyMultipartRequest(
                Method.POST, URLHelper.UPLOAD_DOCUMENT, Response.Listener { response ->
            try {
                val errorObj = JSONObject(String(response.data))
                utils.showCustomAlert(context, Utilities.ALERT_SUCCESS, "Document upload", getString(R.string.uploaddocumentsucc));
                SharedHelper.putKey(context, "doc_id", "");
                getDocsList()
                //responsemsg = errorObj.optString("message")
                setUploadStatus(documentName, View.VISIBLE, "success")
                mmprogressBar!!.setProgress(50)
            } catch (e: Exception) {
                SharedHelper.putKey(context, "doc_id", "");
                utils.showCustomAlert(context, Utilities.ALERT_ERROR, "Document upload", getString(R.string.something_went_wrong));
                setUploadStatus(documentName, View.VISIBLE, "failure")
                // responsemsg = getString(R.string.something_went_wrong)
            }
        }, Response.ErrorListener { error ->
            try {
                var json: String? = null
                var Message: String
                val response = error.networkResponse
                Utilities.print("MyTest", "" + error)
                Utilities.print("MyTestError", "" + error.networkResponse)
                Utilities.print("MyTestError1", "" + response!!.statusCode)
                setUploadStatus(documentName, View.VISIBLE, "failure")
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
                            utils.showCustomAlert(
                                    context,
                                    Utilities.ALERT_ERROR,
                                    "Document Upload",
                                    response.data.toString()
                            )
                        } else if (response.statusCode == 401) {
                            GoToBeginActivity()
                        } else if (response.statusCode == 422) {
                            json = NTApplication.trimMessage(String(response.data))
                            if (json !== "" && json != null) {
                                utils.showCustomAlert(
                                        context,
                                        Utilities.ALERT_ERROR,
                                        "Document Upload",
                                        json
                                )
                            } else {
                                utils.showCustomAlert(
                                        context,
                                        Utilities.ALERT_ERROR,
                                        "Document Upload",
                                        getString(R.string.please_try_again)
                                )
                            }
                        } else {
                            utils.showCustomAlert(
                                    context,
                                    Utilities.ALERT_ERROR,
                                    "Document Upload",
                                    getString(R.string.please_try_again)
                            )
                        }
                    } catch (e: Exception) {
                        utils.showCustomAlert(
                                context,
                                Utilities.ALERT_ERROR,
                                "Document Upload",
                                getString(R.string.something_went_wrong)
                        )
                    }
                } else {
                    if (error is NoConnectionError) {
                        utils.showCustomAlert(
                                context,
                                Utilities.ALERT_ERROR,
                                "Document Upload",
                                getString(R.string.oops_connect_your_internet)
                        )
                    } else if (error is NetworkError) {
                        utils.showCustomAlert(
                                context,
                                Utilities.ALERT_ERROR,
                                "Document Upload",
                                getString(R.string.oops_connect_your_internet)
                        )
                    } else if (error is TimeoutError) {
                        // uploadImage(documentName)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                        context,
                        context.getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG
                ).show()
            }
        }) {
            @Throws(AuthFailureError::class)
            public override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["document_id"] = documentName
                Utilities.PrintAPI_URL(URLHelper.UPLOAD_DOCUMENT, params.toString())
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = getKey(context, "lang")!!
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "Bearer " + getKey(context, "access_token")
                return headers
            }

            @Throws(AuthFailureError::class)
            override fun getByteData(): Map<String, DataPart> {
                val `object`: MutableMap<String, DataPart> = HashMap()
                if (licenseBitmap != null) {
                    `object`["image"] = DataPart(
                            "documentName" + ".jpg", getFileDataFromDrawable(
                            licenseBitmap!!
                    )
                    )
                }

                Utilities.PrintAPI_URL(URLHelper.UPLOAD_DOCUMENT, `object`.toString())
                return `object`
            }
        }
        NTApplication.getInstance().addToRequestQueue(volleyMultipartRequest)
        // return responsemsg;
    }

    private fun uploadImageProgress(documentName: String): String? {
        val volleyMultipartRequest: VolleyMultipartRequest = object : VolleyMultipartRequest(
                Method.POST, URLHelper.UPLOAD_DOCUMENT, Response.Listener { response ->
            try {
                val errorObj = JSONObject(String(response.data))
                utils.showCustomAlert(context, Utilities.ALERT_SUCCESS, "Document Upload", errorObj.optString("message"));
                SharedHelper.putKey(context, "doc_id", "");
                getDocsList()
                responsemsg = errorObj.optString("message")
                setUploadStatus(documentName, View.VISIBLE, "success")
            } catch (e: Exception) {
                SharedHelper.putKey(context, "doc_id", "");
                utils.showCustomAlert(context, Utilities.ALERT_ERROR, "Document Upload", getString(R.string.something_went_wrong));
                setUploadStatus(documentName, View.VISIBLE, "failure")
                responsemsg = getString(R.string.something_went_wrong)
            }
        }, Response.ErrorListener { error ->
            try {
                var json: String? = null
                var Message: String
                val response = error.networkResponse
                Utilities.print("MyTest", "" + error)
                Utilities.print("MyTestError", "" + error.networkResponse)
                Utilities.print("MyTestError1", "" + response!!.statusCode)
                setUploadStatus(documentName, View.VISIBLE, "failure")
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
                            utils.showCustomAlert(
                                    context,
                                    Utilities.ALERT_ERROR,
                                    "Document Upload",
                                    response.data.toString()
                            )
                        } else if (response.statusCode == 401) {
                            GoToBeginActivity()
                        } else if (response.statusCode == 422) {
                            json = NTApplication.trimMessage(String(response.data))
                            if (json !== "" && json != null) {
                                utils.showCustomAlert(
                                        context,
                                        Utilities.ALERT_ERROR,
                                        "Document Upload",
                                        json
                                )
                            } else {
                                utils.showCustomAlert(
                                        context,
                                        Utilities.ALERT_ERROR,
                                        "Document Upload",
                                        getString(R.string.please_try_again)
                                )
                            }
                        } else {
                            utils.showCustomAlert(
                                    context,
                                    Utilities.ALERT_ERROR,
                                    "Document Upload",
                                    getString(R.string.please_try_again)
                            )
                        }
                    } catch (e: Exception) {
                        utils.showCustomAlert(
                                context,
                                Utilities.ALERT_ERROR,
                                "Document Upload",
                                getString(R.string.something_went_wrong)
                        )
                    }
                } else {
                    if (error is NoConnectionError) {
                        utils.showCustomAlert(
                                context,
                                Utilities.ALERT_ERROR,
                                "Document Upload",
                                getString(R.string.oops_connect_your_internet)
                        )
                    } else if (error is NetworkError) {
                        utils.showCustomAlert(
                                context,
                                Utilities.ALERT_ERROR,
                                "Document Upload",
                                getString(R.string.oops_connect_your_internet)
                        )
                    } else if (error is TimeoutError) {
                        // uploadImage(documentName)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                        context,
                        context.getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG
                ).show()
            }
        }) {
            @Throws(AuthFailureError::class)
            public override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["document_id"] = documentName
                Utilities.PrintAPI_URL(URLHelper.UPLOAD_DOCUMENT, params.toString())
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = getKey(context, "lang")!!
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "Bearer " + getKey(context, "access_token")
                return headers
            }

            @Throws(AuthFailureError::class)
            override fun getByteData(): Map<String, DataPart> {
                val `object`: MutableMap<String, DataPart> = HashMap()
                if (licenseBitmap != null) {
                    `object`["image"] = DataPart(
                            "documentName" + ".jpg", getFileDataFromDrawable(
                            licenseBitmap!!
                    )
                    )
                }

                Utilities.PrintAPI_URL(URLHelper.UPLOAD_DOCUMENT, `object`.toString())
                return `object`
            }
        }
        NTApplication.getInstance().addToRequestQueue(volleyMultipartRequest)
        return responsemsg;
    }

    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    @Throws(IOException::class)
    fun getImageSize(choosen: Uri?): Int {
        val bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, choosen)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val imageInByte = stream.toByteArray()
        val lengthbmp = imageInByte.size.toLong()
        //  Toast.makeText(applicationContext, java.lang.Long.toString(lengthbmp), Toast.LENGTH_SHORT).show()
        return lengthbmp.toInt()
    }

    private fun setUploadStatus(documentName: String, visibility: Int, status: String) {
        val drawable: Drawable
        drawable = if (status.equals("success", ignoreCase = true)) {
            context.resources.getDrawable(R.drawable.success)
        } else if (status.equals("failure", ignoreCase = true)) {
            context.resources.getDrawable(R.drawable.failure)
        } else {
            context.resources.getDrawable(R.drawable.success_grey)
        }
        /*      holder.licenseStatus!!.visibility = visibility
              holder.licenseStatus!!.setImageDrawable(drawable)*/

    }

    fun GoToBeginActivity() {
        putKey(context, "loggedIn", getString(R.string.False))
        val mainIntent = Intent(context, ActivityBegin::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(mainIntent)
        activity.finish()
    }


}

