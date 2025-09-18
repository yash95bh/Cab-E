package com.eviort.cabedriver.NTFragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.eviort.cabedriver.NTActivity.ActivityBegin
import com.eviort.cabedriver.NTActivity.ActivityForgotPassword
import com.eviort.cabedriver.NTActivity.ActivitySignIn
import com.eviort.cabedriver.NTActivity.MainActivity
import com.eviort.cabedriver.NTApplication
import com.eviort.cabedriver.NTCustomView.*
import com.eviort.cabedriver.NTHelper.AppHelper.getFileDataFromDrawable
import com.eviort.cabedriver.NTHelper.LoadingDialog
import com.eviort.cabedriver.NTHelper.SharedHelper
import com.eviort.cabedriver.NTHelper.SharedHelper.getKey
import com.eviort.cabedriver.NTHelper.SharedHelper.putKey
import com.eviort.cabedriver.NTHelper.URLHelper
import com.eviort.cabedriver.NTHelper.VolleyMultipartRequest
import com.eviort.cabedriver.NTUtilites.Utilities
import com.eviort.cabedriver.R
import com.squareup.picasso.Picasso/*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView*/
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class FragmentPersonalProfile : Fragment(), View.OnClickListener {
    var edt_phone: NTEditText? = null
    var edt_name: NTEditText? = null
    var edt_email: NTEditText? = null
    var edt_name_profile: NTEditText? = null
    var tv_email: NTTextView? = null
    var forgot_password: NTTextView? = null
    var img_edit: ImageView? = null
    var img_edit_password: ImageView? = null
    var img_profile: NTCircularImageView? = null
    var edit_image: ImageView? = null
    var btn_back: ImageView? = null
    var btn_save: NTButton? = null
    var delete_account_btn: NTButton? = null
    var fav_driver_label: NTBoldTextView? = null
    var ll_fav_driver: LinearLayout? = null
    var ll_personal_details: LinearLayout? = null
    var insuranceBitmap: Bitmap? = null
    var uri: Uri? = null
    var isImageChanged = false
    var rootview: View? = null
    var tv_changePassword: NTBoldTextView? = null
    var profile_title: NTBoldTextView? = null
    var loadingDialog: LoadingDialog? = null
    var utilities = Utilities()
    var et_current_password: NTEditText? = null
    var et_change_password: NTEditText? = null
    var et_confirm_password: NTEditText? = null
    var mycontext: Context? = activity
    var name_text =" "

    //profile
    var file: File? = null
    var displayMetrics: DisplayMetrics? = null
    var width = 0
    var height:Int = 0
    var GalIntent:Intent? = null
    var CropIntent :Intent? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_personal_profile, container, false)
        rootview?.setFocusableInTouchMode(true)
        rootview?.requestFocus()
        rootview?.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return@OnKeyListener true
                }
            }
            false
        })
        AssignFindViewById(rootview)
        loadingDialog = LoadingDialog(activity!!)
        profileUpdate
        val args = arguments
        if (args != null) {
            val index = args.getString("edit")
            if (index == "edit") {
                editprofile()
            } else {
            }
        }
        return rootview
    }

    // if (response.getString("success").equals("1")) {
    //                    } else {
//
//                        utilities.showCustomAlert(getActivity(), Utilities.ALERT_ERROR, getResources().getString(R.string.app_name), response.getString("message"));
//                    }
    private val profileUpdate: Unit
        private get() {
            if (loadingDialog != null) loadingDialog!!.showDialog()
            Utilities.PrintAPI_URL(URLHelper.GET_PROFILE_DETAILS, "GET")
            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.GET, URLHelper.GET_PROFILE_DETAILS, null, Response.Listener { response ->
                try {
                    Log.d("TAG", "onResponse: ===================>>" + response.toString())

                    if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                    // if (response.getString("success").equals("1")) {
                    if (response.toString() != null) {
                        edt_name!!.setText(response.optString("name"))
                        edt_name_profile!!.setText(response.optString("name"))
                        edt_phone!!.setText(response.optString("mobile"))
                        edt_email!!.setText(response.optString("email"))
                        Picasso.with(activity).load(response.optString("avatar"))
                                .placeholder(R.drawable.ic_driver_black).error(R.drawable.ic_driver_black).into(img_profile)
                    }
                    //                    } else {
//
//                        utilities.showCustomAlert(getActivity(), Utilities.ALERT_ERROR, getResources().getString(R.string.app_name), response.getString("message"));
//                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                try {
                    if (error is TimeoutError) {
                        Toast.makeText(activity, resources.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                    } else if (error is NoConnectionError) {
                        Toast.makeText(activity, resources.getString(R.string.error_no_network), Toast.LENGTH_LONG).show()
                    } else if (error is AuthFailureError) {
                        Toast.makeText(activity, resources.getString(R.string.error_auth_failure), Toast.LENGTH_LONG).show()
                    } else if (error is ServerError) {
                        Toast.makeText(activity, resources.getString(R.string.error_server_connection), Toast.LENGTH_LONG).show()
                    } else if (error is NetworkError) {
                        Toast.makeText(activity, resources.getString(R.string.error_network), Toast.LENGTH_LONG).show()
                    } else if (error is ParseError) {
                        //  Toast.makeText(activity, resources.getString(R.string.error_parse), Toast.LENGTH_LONG).show()
                    } else {
                        utilities.showCustomAlert(activity, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), error.message)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(activity, resources.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
                }
            }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["X-localization"] = "en"
                    headers["Content-Type"] = "application/json"
                    headers["X-Requested-With"] = "XMLHttpRequest"
                    headers["Authorization"] = "" + getKey(activity!!, "token_type") + " " + getKey(activity!!, "access_token")
                    return headers
                }
            }
            NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
        }

    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.img_edit -> {
                name_text=edt_name!!.text.toString()

                profile_title!!.text = resources.getString(R.string.Edit_Account)
                img_profile!!.visibility = View.VISIBLE
                edit_image!!.visibility = View.VISIBLE
                img_edit!!.visibility = View.GONE
                ll_fav_driver!!.visibility = View.GONE
                fav_driver_label!!.visibility = View.GONE
                edt_name_profile!!.visibility = View.GONE
                img_edit_password!!.visibility = View.VISIBLE
                btn_save!!.visibility = View.VISIBLE
                forgot_password!!.visibility = View.GONE

                edt_name!!.requestFocus()
                edt_name!!.isFocusable = true
                edt_name!!.isFocusableInTouchMode = true
                edt_email!!.isFocusable = true
                edt_email!!.isFocusableInTouchMode = true

                img_profile!!.setOnClickListener(this)

            }
            R.id.img_profile -> selectImage(100)
            R.id.backArrow -> if (profile_title!!.text.toString() == resources.getString(R.string.Edit_Account)) {
                hidekeyboard()
                ll_personal_details!!.visibility = View.VISIBLE
                profile_title!!.text = resources.getString(R.string.profile)
                img_profile!!.visibility = View.VISIBLE
                edit_image!!.visibility = View.GONE
                img_edit!!.visibility = View.VISIBLE
                ll_fav_driver!!.visibility = View.GONE
                fav_driver_label!!.visibility = View.GONE
                edt_name_profile!!.visibility = View.VISIBLE
                img_edit_password!!.visibility = View.GONE
                forgot_password!!.visibility = View.GONE

                edt_name!!.setText(name_text.toString())

                btn_save!!.visibility = View.GONE
                edt_name!!.requestFocus()
                edt_name!!.isFocusable = false
                edt_name!!.isFocusableInTouchMode = false
                edt_email!!.isFocusable = false
                edt_email!!.isFocusableInTouchMode = false
                img_profile!!.setOnClickListener(null)
            } else {
                activity!!.onBackPressed()
            }
            R.id.forgot_password -> {
                // ll_personal_details.setVisibility(View.GONE);
                //  loadFragment(new FragmentForgotPassword(),"Forgot password");
                val intent = Intent(activity, ActivityForgotPassword::class.java)
                intent.putExtra("isfromprofile", true)
                startActivity(intent)
            }
            R.id.img_edit_password ->                 //ll_personal_details.setVisibility(View.GONE);
                loadFragment(FragmentChangePassword(), "Change password")
            R.id.btn_save -> if (edt_name!!.text.toString().trim { it <= ' ' } != null && edt_name!!.text.toString().trim { it <= ' ' }.equals("", ignoreCase = true)) {
                utilities.NTToast(activity, resources.getString(R.string.pp_name), Toast.LENGTH_SHORT)
            } else if (edt_phone!!.text.toString() == null || edt_phone!!.text.toString().equals("", ignoreCase = true)) {
                utilities.NTToast(activity, resources.getString(R.string.signup_mobile_no), Toast.LENGTH_SHORT)
            } else {
                updateProfile()
            }
            R.id.tv_changePassword -> {
                val builder = AlertDialog.Builder(activity)
                val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val layout = inflater.inflate(R.layout.fragment_change_password, null)
                builder.setCancelable(false)
                builder.setView(layout)
                builder.create()
                val alertDialog = builder.create()
                val img_exit = layout.findViewById<View>(R.id.img_exit) as ImageView
                et_current_password = layout.findViewById<View>(R.id.et_current_password) as NTEditText
                et_change_password = layout.findViewById<View>(R.id.et_change_password) as NTEditText
                et_confirm_password = layout.findViewById<View>(R.id.et_confirm_password) as NTEditText
                val btn_save = layout.findViewById<View>(R.id.btn_save) as NTButton
                img_exit.setOnClickListener { v ->
                    utilities.hideKeypad(activity, v)
                    alertDialog.dismiss()
                }
                btn_save.setOnClickListener { v ->
                    utilities.hideKeypad(activity, v)
                    if (et_current_password!!.text.toString() != null && et_current_password!!.text.toString().equals("", ignoreCase = true)) {
                        utilities.NTToast(activity, resources.getString(R.string.pp_crnt_password), Toast.LENGTH_SHORT)
                    } else if (et_change_password!!.text.toString() == null || et_change_password!!.text.toString().equals("", ignoreCase = true)) {
                        utilities.NTToast(activity, resources.getString(R.string.pp_chng_password), Toast.LENGTH_SHORT)
                    } else if (et_change_password!!.text.toString().length <= 5) {
                        utilities.NTToast(activity, resources.getString(R.string.pp_password_minimum), Toast.LENGTH_SHORT)
                    } else if (et_confirm_password!!.text.toString() == null || et_confirm_password!!.text.toString().equals("", ignoreCase = true)) {
                        utilities.NTToast(activity, resources.getString(R.string.pp_confm_password), Toast.LENGTH_SHORT)
                    } else if (et_confirm_password!!.text.toString().length <= 5) {
                        utilities.NTToast(activity, resources.getString(R.string.pp_cnfrm_password_minimum), Toast.LENGTH_SHORT)
                    } /*else if (!et_change_password!!.text.toString().matches(et_confirm_password!!.text.toString())) {
                        utilities.NTToast(activity, resources.getString(R.string.pp_password_not_match), Toast.LENGTH_SHORT)
                    }*/ else {
                        getPasswordAPI(alertDialog)
                    }
                }
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                alertDialog.window!!.attributes.windowAnimations = R.style.dialog_animation
                alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                alertDialog.show()
            }
            R.id.delete_account_btn ->{
                showLogoutDialog()
            }
        }
    }

    fun showLogoutDialog() {
        val builder3 = AlertDialog.Builder(requireContext())
        val inflater3 = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout3 = inflater3.inflate(R.layout.layout_alert, null)
        builder3.setCancelable(false)
        builder3.setView(layout3)
        builder3.create()
        val alertDialog3 = builder3.create()
        val tv_alert_title = layout3.findViewById<View>(R.id.tv_alert_title) as NTTextView
        val tv_alert_desc = layout3.findViewById<View>(R.id.tv_alert_desc) as NTTextView
        val tv_alert_okBtn = layout3.findViewById<View>(R.id.tv_alert_okBtn) as NTTextView
        val tv_alert_noBtn = layout3.findViewById<View>(R.id.tv_alert_noBtn) as NTTextView
        tv_alert_title.text = "Delete account"
        tv_alert_desc.text = "Are you sure you want to delete your account?"
        tv_alert_okBtn.text = resources.getString(R.string.ok)
        tv_alert_noBtn.text = resources.getString(R.string.cancel)
        tv_alert_okBtn.setOnClickListener {
            DeleteAccount()
            alertDialog3.dismiss()
        }
        tv_alert_noBtn.setOnClickListener { alertDialog3.dismiss() }
        alertDialog3.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog3.window!!.attributes.windowAnimations = R.style.dialog_animation
        alertDialog3.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog3.show()
    }

    private fun DeleteAccount() {
        if (loadingDialog != null) loadingDialog!!.showDialog()
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URLHelper.DELETE_ACCOUNT, null, Response.Listener { response ->
            if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
            try {
                Log.v("DELETE_ACCOUNT_RES",URLHelper.DELETE_ACCOUNT+" --- "+response.toString())
                if (response!=null){
                    Toast.makeText(context,"Cuenta borrada.",Toast.LENGTH_SHORT).show()
                    val otpPage = Intent(context, ActivitySignIn::class.java)
                    startActivity(otpPage)
                    putKey(requireContext(), "loggedIn", "false")
                    putKey(requireContext(), "access_token", "")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { error ->
            if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
            try {
                if (error is TimeoutError) {
                    Toast.makeText(context, requireContext().getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                } else if (error is NoConnectionError) {
                    Toast.makeText(context, requireContext().getString(R.string.error_no_network), Toast.LENGTH_LONG).show()
                } else if (error is AuthFailureError) {
                    Toast.makeText(context, requireContext().getString(R.string.error_auth_failure), Toast.LENGTH_LONG).show()
                } else if (error is ServerError) {
                    Toast.makeText(context, requireContext().getString(R.string.error_server_connection), Toast.LENGTH_LONG).show()
                } else if (error is NetworkError) {
                    Toast.makeText(context, requireContext().getString(R.string.error_network), Toast.LENGTH_LONG).show()
                } else if (error is ParseError) {
                    Toast.makeText(context, requireContext().getString(R.string.error_parse), Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, resources.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
            }
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "" + SharedHelper.getKey(context!!, "token_type") + " " + SharedHelper.getKey(context!!, "access_token")
                Log.v("DELETE_ACCOUNT_HEADERS",headers.toString())
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun selectImage(REQUEST_CODE: Int) {
        val options = arrayOf<CharSequence>(getString(R.string.gal_txt), getString(R.string.cancel))
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.add_photo))
        builder.setItems(options) { dialog, item ->

            if (options[item] == getString(R.string.gal_txt)) {
                GalIntent = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                startActivityForResult(Intent.createChooser(GalIntent, getString(R.string.gal_txt)), 2)
            } else if (options[item] == getString(R.string.cancel)) {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 0) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (intent.resolveActivity(activity!!.packageManager) != null) {
                    startActivityForResult(intent, requestCode)
                }
            }
        }
    }

    fun updateProfile() {
        val text=edt_name!!.text.toString()
        edt_name_profile!!.setText(text)
        name_text=text.toString()
        if (isImageChanged) {
            updateProfileWithImage()
        } else {
            updateProfileWithoutImage()
        }
    }

    private fun updateProfileWithImage() {
        loadingDialog = LoadingDialog(activity!!)
        if (loadingDialog != null) loadingDialog!!.showDialog()
        val volleyMultipartRequest: VolleyMultipartRequest = object : VolleyMultipartRequest(Method.POST, URLHelper.PROFILE_UPDATE, Response.Listener { response ->
            Utilities.printAPI_Response(response.toString())
            if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
            val res = String(response.data)
            Utilities.print("ProfileUpdateRes", "" + res)
            try {
                val jsonObject = JSONObject(res)
                putKey(activity!!, "id", jsonObject.optString("id"))
                putKey(activity!!, "first_name", jsonObject.optString("name"))
                putKey(activity!!, "last_name", jsonObject.optString("last_name"))
                putKey(activity!!, "sos", jsonObject.optString("sos"))
                putKey(activity!!, "email", jsonObject.optString("email"))
                if (jsonObject.optString("avatar") == "" || jsonObject.optString("avatar") == null) {
                    putKey(activity!!, "picture", "")
                } else {
                    if (jsonObject.optString("avatar").startsWith("http")) putKey(activity!!, "picture", jsonObject.optString("avatar")) else putKey(activity!!, "picture", URLHelper.base + "storage/" + jsonObject.optString("avatar"))
                }
                putKey(activity!!, "gender", jsonObject.optString("gender"))
                putKey(activity!!, "mobile", jsonObject.optString("mobile"))
                //                        SharedHelper.putKey(mycontext, "wallet_balance", jsonObject.optString("wallet_balance"));
//                        SharedHelper.putKey(mycontext, "payment_mode", jsonObject.optString("payment_mode"));
               // GoToMainActivity()
                if (profile_title!!.text.toString() == "Edit Account") {
                    ll_personal_details!!.visibility = View.VISIBLE
                    profile_title!!.text = "Profile"
                    img_profile!!.visibility = View.VISIBLE
                    img_edit!!.visibility = View.VISIBLE
                    edit_image!!.visibility = View.GONE
                    ll_fav_driver!!.visibility = View.GONE
                    fav_driver_label!!.visibility = View.GONE
                    edt_name_profile!!.visibility = View.VISIBLE
                    img_edit_password!!.visibility = View.GONE
                    forgot_password!!.visibility = View.GONE
                    btn_save!!.visibility = View.GONE
                    edt_name!!.requestFocus()
                    edt_name!!.isFocusable = false
                    edt_name!!.isFocusableInTouchMode = false
                    edt_email!!.isFocusable = false
                    edt_email!!.isFocusableInTouchMode = false
                    img_profile!!.setOnClickListener(null)
                }

                Toast.makeText(activity, getString(R.string.update_success), Toast.LENGTH_SHORT).show()
                //displayMessage(getString(R.string.update_success));
            } catch (e: JSONException) {
                e.printStackTrace()
                displayMessage(getString(R.string.something_went_wrong))
            }
        }, Response.ErrorListener { error ->
            try {
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                var json: String? = null
                var Message: String
                val response = error.networkResponse
                if (response != null && response.data != null) {
                    try {
                        val errorObj = JSONObject(String(response.data))
                        if (response.javaClass == TimeoutError::class.java) {
                            updateProfileWithoutImage()
                            return@ErrorListener
                        }
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"))
                            } catch (e: Exception) {
                                displayMessage(getString(R.string.something_went_wrong))
                            }
                        } else if (response.statusCode == 401) {
                            GoToBeginActivity()
                        } else if (response.statusCode == 422) {
                            json = NTApplication.trimMessage(String(response.data))
                            if (json !== "" && json != null) {
                                displayMessage(json)
                            } else {
                                displayMessage(getString(R.string.please_try_again))
                            }
                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down))
                        } else {
                            displayMessage(getString(R.string.please_try_again))
                        }
                    } catch (e: Exception) {
                        displayMessage(getString(R.string.something_went_wrong))
                    }
                } else {
                    if (error is NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet))
                    } else if (error is NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet))
                    } else if (error is TimeoutError) {
                        updateProfileWithoutImage()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                displayMessage(getString(R.string.something_went_wrong))
            }
        }) {
            @Throws(AuthFailureError::class)
            public override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                //params["dial_code"] = "+677"
                params["first_name"] = edt_name!!.text.toString()
                params["email"] = edt_email!!.text.toString()
                params["mobile"] = edt_phone!!.text.toString()
                Utilities.PrintAPI_URL(URLHelper.PROFILE_UPDATE, params.toString())
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = getKey(activity!!, "lang")!!
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "Bearer " + getKey(activity!!, "access_token")
                return headers
            }

            @Throws(AuthFailureError::class)
            override fun getByteData(): Map<String, DataPart> {
                val params: MutableMap<String, DataPart> = HashMap()
                params["avatar"] = DataPart("userImage.jpg", getFileDataFromDrawable(img_profile!!.drawable), "image/jpeg")
                return params
            }
        }
        NTApplication.getInstance().addToRequestQueue(volleyMultipartRequest)
    }

    private fun GoToMainActivity() {
        val mainIntent = Intent(activity, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(mainIntent)
        activity!!.finish()
    }

    private fun displayMessage(string: String) {
        Toast.makeText(activity, string, Toast.LENGTH_SHORT).show()
    }

    private fun updateProfileWithoutImage() {
        if (loadingDialog != null) loadingDialog!!.showDialog()
        val `object` = JSONObject()
        try {
            `object`.put("name", edt_name!!.text.toString())
            `object`.put("email", edt_email!!.text.toString())
            //`object`.put("dial_code", "+677")
            `object`.put("mobile", edt_phone!!.text.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utilities.PrintAPI_URL(URLHelper.PROFILE_UPDATE, `object`.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URLHelper.PROFILE_UPDATE, `object`, Response.Listener { response ->
            if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
            Utilities.PrintAPI_URL(URLHelper.PROFILE_UPDATE, response.toString())
            utilities.NTToast(activity, resources.getString(R.string.save_success), Toast.LENGTH_SHORT)
            edt_name!!.isFocusable = false
            edt_name!!.isFocusableInTouchMode = false
            edt_email!!.isFocusable = false
            edt_email!!.isFocusableInTouchMode = false
            edt_phone!!.isFocusable = false
            edt_phone!!.isFocusableInTouchMode = false

            if (profile_title!!.text.toString() == "Edit Account") {
                ll_personal_details!!.visibility = View.VISIBLE
                profile_title!!.text = "Profile"
                img_profile!!.visibility = View.VISIBLE
                img_edit!!.visibility = View.VISIBLE
                edit_image!!.visibility = View.GONE
                ll_fav_driver!!.visibility = View.GONE
                fav_driver_label!!.visibility = View.GONE
                edt_name_profile!!.visibility = View.VISIBLE
                img_edit_password!!.visibility = View.GONE
                forgot_password!!.visibility = View.GONE
                btn_save!!.visibility = View.GONE
                edt_name!!.requestFocus()
                edt_name!!.isFocusable = false
                edt_name!!.isFocusableInTouchMode = false
                edt_email!!.isFocusable = false
                edt_email!!.isFocusableInTouchMode = false
                img_profile!!.setOnClickListener(null)
            }
        }, Response.ErrorListener { if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog() }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "" + getKey(activity!!, "token_type") + " " + getKey(activity!!, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun getPasswordAPI(alertDialog: AlertDialog) {
        if (loadingDialog != null) loadingDialog!!.showDialog()
        val `object` = JSONObject()
        try {
            `object`.put("password", et_change_password!!.text.toString())
            `object`.put("password_confirmation", et_confirm_password!!.text.toString())
            `object`.put("password_old", et_current_password!!.text.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utilities.PrintAPI_URL(URLHelper.PROFILE_CHANGE_PASSWORD, `object`.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URLHelper.PROFILE_CHANGE_PASSWORD, `object`, Response.Listener { response ->
            if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
            try {
                if (response.getString("success") == "1") {
                    utilities.showCustomAlert(activity, Utilities.ALERT_SUCCESS, resources.getString(R.string.app_name), response.getString("message"))
                    alertDialog.dismiss()
                } else {
                    utilities.showCustomAlert(activity, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), response.getString("message"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Utilities.PrintAPI_URL(URLHelper.PROFILE_CHANGE_PASSWORD, response.toString())
        }, Response.ErrorListener { error ->
            if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
            try {
                var json: String? = null
                var Message: String
                val response = error.networkResponse
                if (response != null && response.data != null) {
                    try {
                        val errorObj = JSONObject(String(response.data))
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                utilities.showCustomAlert(mycontext, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), errorObj.optString("error"))
                            } catch (e: Exception) {
                                utilities.showCustomAlert(mycontext, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), getString(R.string.something_went_wrong))
                            }
                        } else if (response.statusCode == 401) {
                            putKey(mycontext!!, "loggedIn", getString(R.string.False))
                        } else if (response.statusCode == 422) {
                            json = NTApplication.trimMessage(String(response.data))
                            if (json !== "" && json != null) {
                                utilities.showCustomAlert(mycontext, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), json)
                            } else {
                                utilities.showCustomAlert(mycontext, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), getString(R.string.please_try_again))
                            }
                        } else if (response.statusCode == 503) {
                            utilities.showCustomAlert(mycontext, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), getString(R.string.server_down))
                        }
                    } catch (e: Exception) {
                        utilities.showCustomAlert(mycontext, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), getString(R.string.something_went_wrong))
                    }
                } else {
                    if (error is NoConnectionError) {
                        utilities.showCustomAlert(mycontext, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), getString(R.string.oops_connect_your_internet))
                    } else if (error is NetworkError) {
                        utilities.showCustomAlert(mycontext, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), getString(R.string.oops_connect_your_internet))
                    } else if (error is TimeoutError) {
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(mycontext, mycontext!!.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
            }
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "" + getKey(activity!!, "token_type") + " " + getKey(activity!!, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun AssignFindViewById(rootview: View?) {
        img_edit = rootview!!.findViewById<View>(R.id.img_edit) as ImageView
        forgot_password = rootview.findViewById<View>(R.id.forgot_password) as NTTextView
        img_edit_password = rootview.findViewById<View>(R.id.img_edit_password) as ImageView
        img_profile = rootview.findViewById<View>(R.id.img_profile) as NTCircularImageView
        edit_image = rootview.findViewById<View>(R.id.edit_image) as ImageView
        edt_name = rootview.findViewById<View>(R.id.edt_name) as NTEditText
        edt_phone = rootview.findViewById<View>(R.id.edt_phone) as NTEditText
        edt_email = rootview.findViewById<View>(R.id.edt_email) as NTEditText
        ll_personal_details = rootview.findViewById<View>(R.id.ll_personal_details) as LinearLayout
        //edt_email = (NTTextView) rootview.findViewById(R.id.edt_mail);
        profile_title = rootview.findViewById<View>(R.id.profile_title) as NTBoldTextView
        tv_changePassword = rootview.findViewById<View>(R.id.tv_changePassword) as NTBoldTextView
        fav_driver_label = rootview.findViewById<View>(R.id.fav_driver_label) as NTBoldTextView
        ll_fav_driver = rootview.findViewById<View>(R.id.ll_fav_driver) as LinearLayout
        edt_name_profile = rootview.findViewById<View>(R.id.edt_name_profile) as NTEditText
        btn_back = rootview.findViewById<View>(R.id.backArrow) as ImageView
        btn_save = rootview.findViewById<View>(R.id.btn_save) as NTButton
        delete_account_btn = rootview.findViewById<View>(R.id.delete_account_btn) as NTButton
        tv_changePassword!!.setOnClickListener(this)
        img_edit_password!!.setOnClickListener(this)
        img_edit!!.setOnClickListener(this)
        btn_back!!.setOnClickListener(this)
        img_profile!!.setOnClickListener(null)
        btn_save!!.setOnClickListener(this)
        delete_account_btn!!.setOnClickListener(this)
        forgot_password!!.setOnClickListener(null)
        edt_name!!.isFocusable = false
        edt_name!!.isFocusableInTouchMode = false
        edt_email!!.isFocusable = false
        edt_email!!.isFocusableInTouchMode = false
        edt_phone!!.isFocusable = false
        edt_phone!!.isFocusableInTouchMode = false
        edt_name_profile!!.isFocusable = false
        edt_name_profile!!.isFocusableInTouchMode = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
               // ImageCropFunction()
//                performCrop(uri!!)
            } else if (requestCode == 2) {
                if (data != null) {
                    uri = data.data
                    isImageChanged = true
                    img_profile!!.setImageURI(uri)

                   // ImageCropFunction()
//                    performCrop(uri!!)
                }
            } /*else if (requestCode == 1) {
                if (data != null) {
                //result for imagcrop function
                    isImageChanged = true
                    val bundle = data.extras
                    val bitmap = bundle!!.getParcelable<Bitmap>("data")
                    img_profile!!.setImageBitmap(bitmap)
                }else{
                    isImageChanged = false
                }
            }*/

            /*if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    isImageChanged = true
                    val resultUri = result.uri
                    img_profile!!.setImageURI(resultUri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                    isImageChanged = false
                }
            }*/
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /*private fun performCrop(imageUri: Uri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(1, 1)
                .setMultiTouchEnabled(true)
                .start(requireContext(),this)
    }*/

    fun ImageCropFunction() {
        // Image Crop Code
        try {
            CropIntent = Intent("com.android.camera.action.CROP")
            CropIntent!!.setDataAndType(uri, "image/*")
            CropIntent!!.putExtra("crop", "true")
            CropIntent!!.putExtra("outputX", 180)
            CropIntent!!.putExtra("outputY", 180)
            CropIntent!!.putExtra("aspectX", 3)
            CropIntent!!.putExtra("aspectY", 4)
            CropIntent!!.putExtra("scaleUpIfNeeded", true)
            CropIntent!!.putExtra("return-data", true)
            startActivityForResult(CropIntent, 1)
        } catch (e: ActivityNotFoundException) {
        }
    }

    private fun GoToBeginActivity() {
        val mainIntent = Intent(activity, ActivityBegin::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(mainIntent)
        activity!!.finish()
    }

    fun loadFragment(fragment: Fragment?, aBackstack: String?) {
        // load fragment
        val transaction = activity!!.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment!!)
        transaction.addToBackStack(aBackstack)
        transaction.commit()
    }

    fun editprofile() {
        profile_title!!.text = "Edit Profile"
        img_profile!!.visibility = View.VISIBLE
        img_edit!!.visibility = View.GONE
        edit_image!!.visibility = View.VISIBLE
        ll_fav_driver!!.visibility = View.GONE
        fav_driver_label!!.visibility = View.GONE
        edt_name_profile!!.visibility = View.GONE
        img_edit_password!!.visibility = View.VISIBLE
        forgot_password!!.visibility = View.GONE

        // edt_name.requestFocus();
        edt_name!!.isFocusable = true
        edt_name!!.isFocusableInTouchMode = true
        img_profile!!.setOnClickListener(this)
    }

    companion object {
        private const val SELECT_PHOTO = 100
        var deviceHeight = 0
        var deviceWidth = 0
        private const val TAG = "FragmentPersonalProfile"

        //    @Override
        //    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //        super.onActivityResult(requestCode, resultCode, data);
        //        if (requestCode == SELECT_PHOTO && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
        //
        //            uri = data.getData();
        //            try {
        //                isImageChanged = true;
        //                //bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
        //                Bitmap resizeImg = getBitmapFromUri(getActivity(), uri);
        //                if (resizeImg != null) {
        ////                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg, AppHelper.getPath(getActivity(), uri));
        //                    img_profile.setImageBitmap(resizeImg);
        //                }
        //            } catch (IOException e) {
        //                e.printStackTrace();
        //            }
        //        }
        //    }
        @Throws(IOException::class)
        private fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
            Log.e(TAG, "getBitmapFromUri: Resize uri$uri")
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")!!
            val fileDescriptor = parcelFileDescriptor.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            Log.e(TAG, "getBitmapFromUri: Height" + deviceHeight)
            Log.e(TAG, "getBitmapFromUri: width" + deviceWidth)
            val maxSize = Math.min(deviceHeight, deviceWidth)
            return if (image != null) {
                Log.e(TAG, "getBitmapFromUri: Width" + image.width)
                Log.e(TAG, "getBitmapFromUri: Height" + image.height)
                val inWidth = image.width
                val inHeight = image.height
                val outWidth: Int
                val outHeight: Int
                if (inWidth > inHeight) {
                    outWidth = maxSize
                    outHeight = inHeight * maxSize / inWidth
                } else {
                    outHeight = maxSize
                    outWidth = inWidth * maxSize / inHeight
                }
                Bitmap.createScaledBitmap(image, outWidth, outHeight, false)
            } else {
                Toast.makeText(context, context.getString(R.string.valid_image), Toast.LENGTH_SHORT).show()
                null
            }
        }
    }

    fun hidekeyboard() {
        // Check if no view has focus:
        val view = activity!!.currentFocus
        if (view != null) {
            val inputManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
        /*  InputMethodManager in = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(activity.getCurrentFocus().getApplicationWindowToken(), 0);*/
    }

}