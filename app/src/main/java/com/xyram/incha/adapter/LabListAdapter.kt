package com.xyram.incha.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xyram.incha.databinding.ListItemLabsLayoutBinding
import com.xyram.incha.databinding.ListItemMedicinesLayoutBinding
import com.xyram.incha.databinding.ListItemTherapyLayoutBinding
import com.xyram.incha.entity.LabEntity

class LabListAdapter(
    private val context: Context,
    private val onClick: OnClick,
    private val labList: MutableList<LabEntity>
) : RecyclerView.Adapter<LabListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemLabsLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(holder, position)
    }

    override fun getItemCount(): Int = labList.size

    inner class ViewHolder(private val binding: ListItemLabsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(holder: ViewHolder, position: Int) {
            val lab = labList[position]

            binding.editMedicine.setText(lab.testName)
            binding.editInstruction.setText(lab.instruction ?: "") // If you add instruction field later

            // --- Save changes when user edits ---
            binding.editMedicine.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    lab.testName = s.toString()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            binding.editInstruction.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    lab.instruction = s.toString()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            binding.delete.setOnClickListener {
                labList.removeAt(position)
                notifyDataSetChanged()
            }

        }

    }


    interface OnClick {
        fun onDeleteLab()

    }

}