package com.learning.leap.bwb.settings

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.learning.leap.bwb.R


class AgeRangeAdapter(private val ageRanges: List<String>, val context: Context) :
    RecyclerView.Adapter<AgeRangeViewHolder>() {
    var itemOnClick: ((Int) -> Unit)? = null
    var selectPosition = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgeRangeViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_age_range, parent, false)
        return AgeRangeViewHolder(v)
    }

    override fun getItemCount(): Int {
        return ageRanges.size
    }

    override fun onBindViewHolder(holder: AgeRangeViewHolder, position: Int) {
        holder.itemView.setBackgroundColor(
            if (selectPosition == position) ContextCompat.getColor(
                context,
                R.color.dark_green
            ) else Color.WHITE
        )
//        holder.itemView.ageRangeTextView.setTextColor(if (selectPosition == position) Color.WHITE else Color.BLACK)
//        holder.itemView.ageRangeTextView.text = ageRanges[position]
        holder.itemView.setOnClickListener {
            itemOnClick?.invoke(position)
        }
    }
}

class AgeRangeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}
