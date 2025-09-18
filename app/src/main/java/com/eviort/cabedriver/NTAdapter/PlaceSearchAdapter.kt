package com.eviort.cabedriver.NTAdapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.eviort.cabedriver.NTCustomView.NTTextView
import com.eviort.cabedriver.NTHelper.LoadingDialog
import com.eviort.cabedriver.R
import org.json.JSONArray


class PlaceSearchAdapter(placeArray: JSONArray, desc: String?,
                         alertDialog: AlertDialog?, activity: Activity?, statuss: String, addressID: String?, mplaceListener: PlacesClickListener) : RecyclerView.Adapter<PlaceSearchAdapter.MyViewHolder>() {
    var jsonArray: JSONArray? = placeArray
    var desc: String? = desc
    var activity = activity
    var statuss = statuss
    var addressID = addressID
    var listener = mplaceListener
    var alertDialog: AlertDialog? = alertDialog
    var loadingDialog: LoadingDialog? = LoadingDialog(activity!!)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.taxi_search_list_item,
                        parent,
                        false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        try {
            holder.taxi__search_listitem_textview_subtitle?.setText(jsonArray!!.optJSONObject(position).getJSONObject("structured_formatting").optString("secondary_text"))

            holder.taxi__search_listitem_textview_title?.setText(jsonArray!!.optJSONObject(position).getJSONObject("structured_formatting").optString("main_text"))


            holder.search_card_view?.setOnClickListener(View.OnClickListener {
                try {

                    if (listener != null) listener.onBtnClick(jsonArray!!.optJSONObject(position).optString("place_id"), desc, jsonArray!!.optJSONObject(position).getJSONObject("structured_formatting").optString("main_text") + jsonArray!!.optJSONObject(position).getJSONObject("structured_formatting").optString("secondary_text"))
                    alertDialog!!.dismiss()

//                    jsonArray!!.optJSONObject(position).optString("place_id")
                    /* alertDialog?.let { it1 ->
                         getPlaces(jsonArray!!.optJSONObject(position).optString("place_id"), desc, it1, jsonArray!!.optJSONObject(position).getJSONObject("structured_formatting").optString("main_text") + jsonArray!!.optJSONObject(position).getJSONObject("structured_formatting").optString("secondary_text")) }
                    */
                    // mClickListener.onBtnClick(s_d_address, holder.address!!.getText().toString(), jsonArray?.optJSONObject(position)?.getJSONObject("position")?.optString("lat"), jsonArray?.optJSONObject(position)?.getJSONObject("position")?.optString("lon"))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return jsonArray!!.length()
    }


    class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var search_card_view = itemView?.findViewById(R.id.search_card_view) as? LinearLayout
        var taxi__search_listitem_textview_subtitle = itemView?.findViewById(R.id.taxi__search_listitem_textview_subtitle) as? NTTextView
        var taxi__search_listitem_textview_title = itemView?.findViewById(R.id.taxi__search_listitem_textview_title) as? NTTextView
    }


    interface PlacesClickListener {
        fun onBtnClick(PLACE_ID: String, desc: String?, address: String)
    }

}