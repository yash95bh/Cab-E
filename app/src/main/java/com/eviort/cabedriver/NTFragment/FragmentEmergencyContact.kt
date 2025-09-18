package com.eviort.cabedriver.NTFragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.eviort.cabedriver.NTAdapter.ContactAdapter
import com.eviort.cabedriver.NTApplication
import com.eviort.cabedriver.NTCustomView.NTBoldTextView
import com.eviort.cabedriver.NTCustomView.NTTextView
import com.eviort.cabedriver.NTHelper.LoadingDialog
import com.eviort.cabedriver.NTHelper.RecyclerItemTouchHelper
import com.eviort.cabedriver.NTHelper.RecyclerItemTouchHelper.RecyclerItemTouchHelperListener
import com.eviort.cabedriver.NTHelper.SharedHelper.getKey
import com.eviort.cabedriver.NTHelper.URLHelper
import com.eviort.cabedriver.NTModel.EmergencyContactData
import com.eviort.cabedriver.NTUtilites.Utilities
import com.eviort.cabedriver.R
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class FragmentEmergencyContact : Fragment(), RecyclerItemTouchHelperListener {
    private val contactList: MutableList<EmergencyContactData> = ArrayList()
    private var recyclerView: RecyclerView? = null
    private var mAdapter: ContactAdapter? = null
    var addContactsBtn: ImageView? = null
    var RESULT_PICK_CONTACT = 2017
    var em_name_delete: String? = null
    var em_phone_delete: String? = null
    var em_deletedIndex = 0
    var mycontext: Context? = null
    var activity: Activity? = null
    var utils = Utilities()
    var imgBack: ImageView? = null
    var loadingDialog: LoadingDialog? = null
    var rootview: View? = null
    var ll_mainLayout: LinearLayout? = null
    var ll_errorLayout: LinearLayout? = null
    var btnAddContact_el: ImageView? = null
    var error_title: NTBoldTextView? = null
    var error_description: NTTextView? = null
    var i = 0
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_emergency_contact, container, false)
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
        mycontext = getActivity()
        activity = getActivity()
        ll_mainLayout = rootview?.findViewById<View>(R.id.ll_mainLayout) as LinearLayout
        ll_errorLayout = rootview?.findViewById<View>(R.id.ll_errorLayout) as LinearLayout
        recyclerView = rootview?.findViewById<View>(R.id.ec_recyclerview) as RecyclerView
        addContactsBtn = rootview?.findViewById<View>(R.id.addContactsBtn) as ImageView
        btnAddContact_el = rootview?.findViewById<View>(R.id.btnAddContact_el) as ImageView
        error_title = rootview?.findViewById<View>(R.id.error_title) as NTBoldTextView
        error_description = rootview?.findViewById<View>(R.id.error_description) as NTTextView
        imgBack = rootview?.findViewById<View>(R.id.backArrow) as ImageView
        loadingDialog = LoadingDialog(activity!!)
        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback = RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
        GetEmergencyContactList()


/*        i= 0 ;

        ll_errorLayout.setVisibility(View.GONE);
        ll_mainLayout.setVisibility(View.VISIBLE);

        if (i == 0){

            ll_errorLayout.setVisibility(View.VISIBLE);
            ll_mainLayout.setVisibility(View.GONE);

            btnAddContact_el.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    if (contactList.size() < 5) {
                        pickContact(v);

                    } else {
                        Toast.makeText(context, "you can add maximum of 5 contacts", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            error_title.setVisibility(View.VISIBLE);
            error_description.setVisibility(View.VISIBLE);

            error_title.setText(getResources().getString(R.string.contact_title));
            error_description.setText(getResources().getString(R.string.contact_desc));

        }*/addContactsBtn!!.setOnClickListener { v ->
            if (contactList.size < 5) {
                pickContact(v)
            } else {
                Toast.makeText(mycontext, "you can add maximum of 5 contacts", Toast.LENGTH_SHORT).show()
            }
        }
        imgBack!!.setOnClickListener { pop() }
        return rootview
    }

    private fun GetEmergencyContactList() {
        /*  loadingDialog.showDialog();*/
        val `object` = JSONObject()
        Utilities.PrintAPI_URL(URLHelper.EMERGENCY_CONTACT_LIST, "GET")
        val jsonArrayRequest: JsonArrayRequest = object : JsonArrayRequest(URLHelper.EMERGENCY_CONTACT_LIST, Response.Listener { response ->
            Utilities.printAPI_Response(response.toString())
            /*  loadingDialog.hideDialog();*/
            var phoneNo: String? = null
            var name: String? = null
            try {
                contactList.clear()
                if (response != null && response.length() > 0) {
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        name = jsonObject.optString("contact_name")
                        phoneNo = jsonObject.optString("contact_number")
                        contactList.add(EmergencyContactData(name, phoneNo))

                        // mAdapter.notifyDataSetChanged();
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(getActivity()!!.applicationContext)
            recyclerView!!.layoutManager = mLayoutManager
            recyclerView!!.isNestedScrollingEnabled = false
            mAdapter = ContactAdapter(contactList)
            recyclerView!!.adapter = mAdapter
            mAdapter!!.notifyDataSetChanged()
        }, Response.ErrorListener { error ->
            if (error is TimeoutError) {
                Toast.makeText(getActivity(), getActivity()!!.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
            } else if (error is NoConnectionError) {
                Toast.makeText(getActivity(), getActivity()!!.getString(R.string.error_no_network), Toast.LENGTH_LONG).show()
            } else if (error is AuthFailureError) {
                Toast.makeText(getActivity(), getActivity()!!.getString(R.string.error_auth_failure), Toast.LENGTH_LONG).show()
            } else if (error is ServerError) {
                Toast.makeText(getActivity(), getActivity()!!.getString(R.string.error_server_connection), Toast.LENGTH_LONG).show()
            } else if (error is NetworkError) {
                Toast.makeText(getActivity(), getActivity()!!.getString(R.string.error_network), Toast.LENGTH_LONG).show()
            } else if (error is ParseError) {
                //  Toast.makeText(getActivity(), getActivity()!!.getString(R.string.error_parse), Toast.LENGTH_LONG).show()
            } else {
            }
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = getKey(mycontext!!, "lang")!!
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "Bearer " + getKey(mycontext!!, "access_token")
                Log.e("", "Access_Token" + getKey(mycontext!!, "access_token"))
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonArrayRequest)
    }

    private fun pickContact(v: View) {
        val contactPickerIntent = Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // check whether the result is ok
        // Check for the request code, we might be usign multiple startActivityForReslut
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESULT_PICK_CONTACT) {
                contactPicked(data!!)
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact")
        }
    }

    private fun contactPicked(data: Intent) {
        var cursor: Cursor? = null
        try {
            var phoneNo: String? = null
            var name: String? = null
            // getData() method will have the Content Uri of the selected contact
            val uri = data.data
            //Query the content uri
            cursor = mycontext!!.contentResolver.query(uri!!, null, null, null, null)
            cursor!!.moveToFirst()
            // column index of the phone number
            val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            // column index of the contact name
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            phoneNo = cursor.getString(phoneIndex)
            name = cursor.getString(nameIndex)
            try {
                val j = JSONObject()
                j.put("contact_name", name)
                j.put("contact_number", phoneNo)
                AddEmergencyContact(j)
            } catch (e: Exception) {
                // TODO: handle exception
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun AddEmergencyContact(`object`: JSONObject) {
        Utilities.PrintAPI_URL(URLHelper.EMERGENCY_CONTACT_ADD, `object`.toString())
        println("PassengerAPI Header : " + "Authorization :" + getKey(mycontext!!, "token_type") + " " + getKey(mycontext!!, "access_token"))
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URLHelper.EMERGENCY_CONTACT_ADD, `object`, Response.Listener { response ->
            Utilities.printAPI_Response(response.toString())
            if (response != null) {
                Utilities.print("SendRequestResponse", response.toString())
                /*   if ((loadingDialog != null) && (loadingDialog.isShowing()))
                          loadingDialog.hideDialog();*/
                utils.showAlert(mycontext, response.optString("contact_name") + " : " + resources.getString(R.string.added_succ))
                GetEmergencyContactList()
                /* if (response.optString("status").equals("200")) {
                          contactList.add(new EmergencyContactData(response.optString("contact_name"), response.optString("contact_number")));
  
  
                      }*/
            }
        }, Response.ErrorListener { error -> /* if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.hideDialog();*/
            try {
                if (error is TimeoutError) {
                    Toast.makeText(getActivity(), getActivity()!!.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                } else if (error is NoConnectionError) {
                    Toast.makeText(getActivity(), getActivity()!!.getString(R.string.error_no_network), Toast.LENGTH_LONG).show()
                } else if (error is AuthFailureError) {
                    Toast.makeText(getActivity(), getActivity()!!.getString(R.string.error_auth_failure), Toast.LENGTH_LONG).show()
                } else if (error is ServerError) {
                    Toast.makeText(getActivity(), getActivity()!!.getString(R.string.error_server_connection), Toast.LENGTH_LONG).show()
                } else if (error is NetworkError) {
                    Toast.makeText(getActivity(), getActivity()!!.getString(R.string.error_network), Toast.LENGTH_LONG).show()
                } else if (error is ParseError) {
                    //  Toast.makeText(getActivity(), getActivity()!!.getString(R.string.error_parse), Toast.LENGTH_LONG).show()
                } else {
                    val json: String? = null
                    var Message: String
                    val response = error.networkResponse
                    if (response != null && response.data != null) {
                        utils.showAlert(mycontext, mycontext!!.getString(R.string.something_went_wrong))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = getKey(mycontext!!, "lang")!!
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "" + getKey(mycontext!!, "token_type") + " " + getKey(mycontext!!, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun DeleteEmergencyContact(`object`: JSONObject) {
        Utilities.PrintAPI_URL(URLHelper.EMERGENCY_CONTACT_DELETE, `object`.toString())
        println("PassengerAPI Header : " + "Authorization :" + getKey(mycontext!!, "token_type") + " " + getKey(mycontext!!, "access_token"))
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URLHelper.EMERGENCY_CONTACT_DELETE, `object`, Response.Listener { response ->
            Utilities.printAPI_Response(response.toString())
            if (response != null) {
                Utilities.print("SendRequestResponse", response.toString())
                /* if ((loadingDialog != null) && (loadingDialog.isShowing()))
                          loadingDialog.hideDialog();*/removeItemAt(em_deletedIndex)
                utils.showAlert(mycontext, resources.getString(R.string.delete_succ))
            }
        }, Response.ErrorListener { error ->
            /*  if ((loadingDialog != null) && (loadingDialog.isShowing()))
                      loadingDialog.hideDialog();*/
            try {
                if (error is TimeoutError) {
                    Toast.makeText(getActivity(), getActivity()!!.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                } else if (error is NoConnectionError) {
                    Toast.makeText(getActivity(), getActivity()!!.getString(R.string.error_no_network), Toast.LENGTH_LONG).show()
                } else if (error is AuthFailureError) {
                    Toast.makeText(getActivity(), getActivity()!!.getString(R.string.error_auth_failure), Toast.LENGTH_LONG).show()
                } else if (error is ServerError) {
                    Toast.makeText(getActivity(), getActivity()!!.getString(R.string.error_server_connection), Toast.LENGTH_LONG).show()
                } else if (error is NetworkError) {
                    Toast.makeText(getActivity(), getActivity()!!.getString(R.string.error_network), Toast.LENGTH_LONG).show()
                } else if (error is ParseError) {
                    //   Toast.makeText(getActivity(), getActivity()!!.getString(R.string.error_parse), Toast.LENGTH_LONG).show()
                } else {
                    var json: String? = null
                    var Message: String
                    val response = error.networkResponse
                    if (response != null && response.data != null) {
                        try {
                            val errorObj = JSONObject(String(response.data))
                            println("PassengerAPI response error : " + error + " " + error.networkResponse + " " + response.statusCode + " " + errorObj.optString("error"))
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    utils.showAlert(mycontext, errorObj.optString("message"))
                                } catch (e: Exception) {
                                    utils.showAlert(mycontext, getString(R.string.something_went_wrong))
                                }
                            } else if (response.statusCode == 401) {
//                            refreshAccessToken("PAYMENT_LIST");
                            } else if (response.statusCode == 422) {
                                json = NTApplication.trimMessage(String(response.data))
                                if (json !== "" && json != null) {
                                    utils.showAlert(mycontext, json)
                                } else {
                                    utils.showAlert(mycontext, getString(R.string.please_try_again))
                                }
                            } else if (response.statusCode == 503) {
                                utils.showAlert(mycontext, getString(R.string.server_down))
                            } else {
                                utils.showAlert(mycontext, getString(R.string.please_try_again))
                            }
                        } catch (e: Exception) {
                            utils.showAlert(mycontext, getString(R.string.something_went_wrong))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = getKey(mycontext!!, "lang")!!
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "" + getKey(mycontext!!, "token_type") + " " + getKey(mycontext!!, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    /* public void displayMessage(String toastString) {
        Utilities.commonAlert(mycontext,toastString);
//        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
//                .setAction("Action", null).show();
    }
*/
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (viewHolder is ContactAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            em_name_delete = contactList[viewHolder.getAdapterPosition()].name
            em_phone_delete = contactList[viewHolder.getAdapterPosition()].number

            // backup of removed item for undo purpose
            val deletedItem = contactList[viewHolder.getAdapterPosition()]
            //            final int deletedIndex = viewHolder.getAdapterPosition();
            em_deletedIndex = viewHolder.getAdapterPosition()
            try {
                val j = JSONObject()
                j.put("contact_number", em_phone_delete)
                DeleteEmergencyContact(j)
            } catch (e: Exception) {
                // TODO: handle exception
                e.printStackTrace()
            }
        }
    }

    fun removeItemAt(position: Int) {
        contactList.removeAt(position)
        mAdapter!!.notifyItemRemoved(position)
        mAdapter!!.notifyItemRangeChanged(position, contactList.size)
    }

    fun showAlert(context: Context?, message: String?) {
        try {
            val builder = AlertDialog.Builder(context!!)
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            builder.setMessage(message)
                    .setTitle(context.getString(R.string.app_name))
                    .setCancelable(true)
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton(context.getString(R.string.ok)) { dialog, id ->
                        GetEmergencyContactList()
                        dialog.dismiss()
                    }
            val alert = builder.create()
            alert.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pop() {
        val fm = getActivity()!!.supportFragmentManager
        val count = fm.backStackEntryCount
        for (i in 0..count) {
            fm.popBackStackImmediate()
        }
    }
}