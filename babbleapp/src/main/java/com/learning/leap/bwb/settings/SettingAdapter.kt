package com.learning.leap.bwb.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.learning.leap.bwb.R
import com.learning.leap.bwb.library.CategoryViewHolder
import kotlinx.android.synthetic.main.list_item_library_category.view.*
import kotlinx.android.synthetic.main.list_item_setting.view.*

class SettingAdapter(val titleStrings: List<String>, val detailStrings: List<String>) :
    RecyclerView.Adapter<SettingViewHolder>() {
    var itemOnClick: ((Int) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_setting, parent, false)
        return SettingViewHolder(v)
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        holder.itemView.settingListItemTitleTextView.text = titleStrings[position]
        holder.itemView.settingListItemDetailTextview.text = detailStrings[position]
        holder.itemView.setOnClickListener {
            itemOnClick?.invoke(position)
        }
    }
}

class SettingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}
