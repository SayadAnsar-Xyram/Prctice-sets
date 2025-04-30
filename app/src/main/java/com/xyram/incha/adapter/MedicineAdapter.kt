package com.xyram.incha.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xyram.incha.R
import com.xyram.incha.databinding.ListItemMedicinesLayoutBinding

class MedicineAdapter(
    context: Context,
    private val originalList: List<String>,
    private val onAddClick: (String) -> Unit
) : ArrayAdapter<String>(context, R.layout.item_drop_down_layout, R.id.text, originalList) {

    private var filteredList = ArrayList(originalList)
    private var currentQuery: String = ""

    override fun getCount(): Int {
        return filteredList.size
    }

    override fun getItem(position: Int): String? {
        return filteredList[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)

        val textView = view.findViewById<TextView>(R.id.text)

        val itemText = filteredList[position]
        textView.text = getHighlightedText(itemText, currentQuery)

        textView.setOnClickListener {
            onAddClick(filteredList[position])
        }


        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                currentQuery = constraint?.toString() ?: ""

                val results = FilterResults()
                val suggestions = if (currentQuery.isEmpty()) {
                    originalList
                } else {
                    originalList.filter {
                        it.contains(currentQuery, ignoreCase = true)
                    }
                }
                results.values = suggestions
                results.count = suggestions.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = ArrayList(results?.values as List<String>)
                notifyDataSetChanged()
            }
        }
    }

    private fun getHighlightedText(fullText: String, query: String): SpannableString {
        val spannable = SpannableString(fullText)
        if (query.isEmpty()) return spannable

        val startIndex = fullText.lowercase().indexOf(query.lowercase())
        if (startIndex >= 0) {
            val endIndex = startIndex + query.length
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                ForegroundColorSpan(Color.BLUE),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return spannable
    }
}