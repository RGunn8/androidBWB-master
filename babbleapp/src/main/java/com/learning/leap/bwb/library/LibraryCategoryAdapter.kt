package com.learning.leap.bwb.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.learning.leap.bwb.R

class LibraryCategoryAdapter(val categories: List<String>) :
    RecyclerView.Adapter<CategoryViewHolder>() {

    var itemOnClick: ((String, Int) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_library_category, parent, false)
        return CategoryViewHolder(v)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
       // holder.itemView.libraryCategoryListItemTextView.text = categories[position]
        holder.itemView.setOnClickListener {
            itemOnClick?.invoke(categories[position], position)
        }
    }
}

class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
}
