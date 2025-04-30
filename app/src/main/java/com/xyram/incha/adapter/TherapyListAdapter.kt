package com.xyram.incha.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xyram.incha.databinding.ListItemMedicinesLayoutBinding
import com.xyram.incha.databinding.ListItemTherapyLayoutBinding
import com.xyram.incha.entity.TherapyEntity

class TherapyListAdapter(
    private val context: Context,
    private val onClick: OnClick,
    private val therapyList: MutableList<TherapyEntity>
) : RecyclerView.Adapter<TherapyListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListItemTherapyLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(holder, position)
    }

    override fun getItemCount(): Int = therapyList.size

    inner class ViewHolder(private val binding: ListItemTherapyLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(holder: ViewHolder, position: Int) {
            val therapy = therapyList[position]

            binding.editTherapy.setText(therapy.therapyName)
            binding.editDetails.setText(therapy.details ?: "")

            // --- Save changes when user edits ---
            binding.editTherapy.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    therapy.therapyName = s.toString()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            binding.editDetails.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    therapy.details = s.toString()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            binding.delete.setOnClickListener {
                therapyList.removeAt(position)
                notifyDataSetChanged()
            }

        }

    }


    interface OnClick {

        fun onDeleteTherapy()

    }

}