package com.xyram.incha.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.xyram.incha.databinding.ListItemMedicinesLayoutBinding
import com.xyram.incha.entity.MedicationsEntity

class MedicineListAdapter(
    private val context: Context,
    private val onClick: OnClick,
    private val medicineList: MutableList<MedicationsEntity>
) : RecyclerView.Adapter<MedicineListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListItemMedicinesLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(holder, position)
    }

    override fun getItemCount(): Int = medicineList.size

    inner class ViewHolder(private val binding: ListItemMedicinesLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(holder: ViewHolder, position: Int) {
            val medicine = medicineList[position]

            var instructionList = listOf("After Food", "Before Food")

            // EditTexts
            binding.editMedicine.setText(medicine.name)
            binding.editDosage.setText(medicine.dosage)
            binding.editDuration.setText(medicine.duration)
            binding.editInstriction.setText(medicine.instruction)

            // CheckBoxes
            binding.morning.isChecked = medicine.isMorning
            binding.afternoon.isChecked = medicine.isAfternoon
            binding.evening.isChecked = medicine.isEvening

            // Popup for Instruction
            binding.editInstriction.setOnClickListener {
                val popupMenu = PopupMenu(context, binding.editInstriction)
                for (i in instructionList.indices) {
                    popupMenu.menu.add(Menu.NONE, i, i, instructionList[i])
                }
                popupMenu.setOnMenuItemClickListener { item ->
                    binding.editInstriction.setText(item.title)
                    true
                }
                popupMenu.show()
            }

            // --- Update model when typing in EditTexts ---
            binding.editMedicine.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    medicine.name = s.toString()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            binding.editDosage.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    medicine.dosage = s.toString()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            binding.editDuration.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    medicine.duration = s.toString()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            binding.editInstriction.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    medicine.instruction = s.toString()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            // --- Update model when clicking CheckBoxes ---
            binding.morning.setOnCheckedChangeListener { _, isChecked ->
                medicine.isMorning = isChecked
            }
            binding.afternoon.setOnCheckedChangeListener { _, isChecked ->
                medicine.isAfternoon = isChecked
            }
            binding.evening.setOnCheckedChangeListener { _, isChecked ->
                medicine.isEvening = isChecked
            }


            binding.delete.setOnClickListener {
                medicineList.removeAt(position)
                notifyDataSetChanged()
            }

        }

    }


    interface OnClick {

        fun onDeleteMedicine()

    }

}