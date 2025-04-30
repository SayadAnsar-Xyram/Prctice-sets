package com.xyram.incha

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.xyram.incha.adapter.LabListAdapter
import com.xyram.incha.adapter.MedicineAdapter
import com.xyram.incha.adapter.MedicineListAdapter
import com.xyram.incha.adapter.TherapyListAdapter
import com.xyram.incha.databinding.ActivityMainBinding
import com.xyram.incha.entity.LabEntity
import com.xyram.incha.entity.MedicationsEntity
import com.xyram.incha.entity.TherapyEntity
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity(), MedicineListAdapter.OnClick, LabListAdapter.OnClick,
    TherapyListAdapter.OnClick {
    private lateinit var binding: ActivityMainBinding

    // list for spinner
    var typeList = mutableListOf<String>()

    // list for auto complete text view
    var medicines = mutableListOf<String>()
    var labs = mutableListOf<String>()
    var therapies = mutableListOf<String>()

    // list for recycler view
    var medicineList: MutableList<MedicationsEntity> = mutableListOf()
    var therapyList: MutableList<TherapyEntity> = mutableListOf()
    var labList: MutableList<LabEntity> = mutableListOf()

    // adapters
    lateinit var searchAdapter: MedicineAdapter
    lateinit var medicineListAdapter: MedicineListAdapter
    lateinit var labListAdapter: LabListAdapter
    lateinit var therapyListAdapter: TherapyListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initView()
    }

    private fun initView() {
        typeList.add("Medicine")
        typeList.add("Lab Test")
        typeList.add("Therapy")
        binding.back.setOnClickListener {
            finish()
        }
        binding.type.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(this, binding.type)
            for (i in typeList.indices) {
                popupMenu.menu.add(Menu.NONE, i, i, typeList[i])
            }

            popupMenu.setOnMenuItemClickListener { item ->
                binding.type.setText(item.title)
                if (binding.type.text.toString().equals("Medicine", true)) {
                    binding.list.setHint("Select Medicine")
                }
                if (binding.type.text.toString().equals("Lab Test", true)) {
                    binding.list.setHint("Select Lab Test")
                }
                if (binding.type.text.toString().equals("Therapy", true)) {
                    binding.list.setHint("Select Therapy")
                }
                true
            }

            popupMenu.show()
        }

        //dummy list data
        medicines.add("Dolo")
        medicines.add("Paracetamol")
        medicines.add("Motrin")
        medicines.add("Aspirin")
        medicines.add("Ibuprofen")
        medicines.add("Acetaminophen")
        medicines.add("Amoxicillin")
        medicines.add("Omeprazole")
        medicines.add("Simvastatin")
        medicines.add("Metformin")
        medicines.add("Lisinopril")
        medicines.add("Albuterol")



        labs.add("Blood Test")
        labs.add("X-ray")
        labs.add("MRI")
        labs.add("CT Scan")
        labs.add("Ultrasound")

        therapies.add("Physical Therapy")
        therapies.add("Cognitive Therapy")
        therapies.add("Behavioral Therapy")
        therapies.add("Spiritual Therapy")
        therapies.add("Occupational Therapy")
        therapies.add("Neurological Therapy")



        binding.list.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (binding.type.text.toString().equals("Medicine", true)) {
                        searchAdapter(medicines)
                    }
                    if (binding.type.text.toString().equals("Lab Test", true)) {
                        searchAdapter(labs)
                    }
                    if (binding.type.text.toString().equals("Therapy", true)) {
                        searchAdapter(therapies)
                    }
                    searchAdapter.filter.filter(s)
                    binding.list.showDropDown()

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        searchAdapter(medicines)
        setMedicineListAdapter()
        setLabListAdapter()
        setTherapyListAdapter()

        binding.save.setOnClickListener{

            val finalJson = JSONObject()

            // Medicines
            val medicinesArray = JSONArray()
            for (medicine in medicineList) {
                val medicineJson = JSONObject()
                medicineJson.put("name", medicine.name)
                medicineJson.put("dosage", medicine.dosage)
                medicineJson.put("duration", medicine.duration)
                medicineJson.put("instruction", medicine.instruction)
                medicineJson.put("isMorning", medicine.isMorning)
                medicineJson.put("isAfternoon", medicine.isAfternoon)
                medicineJson.put("isEvening", medicine.isEvening)
                medicinesArray.put(medicineJson)
            }
            finalJson.put("medicines", medicinesArray)

            // Labs
            val labsArray = JSONArray()
            for (lab in labList) {
                val labJson = JSONObject()
                labJson.put("testName", lab.testName)
                labJson.put("instruction", lab.instruction)
                labsArray.put(labJson)
            }
            finalJson.put("labs", labsArray)

            // Therapies
            val therapiesArray = JSONArray()
            for (therapy in therapyList) {
                val therapyJson = JSONObject()
                therapyJson.put("therapyName", therapy.therapyName)
                therapyJson.put("details", therapy.details)
                therapiesArray.put(therapyJson)
            }
            finalJson.put("therapies", therapiesArray)

            // Print the final JSON
            Log.d("SaveJson", finalJson.toString(4))  // Pretty printed

            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun searchAdapter(list: MutableList<String>) {
        searchAdapter = MedicineAdapter(this@MainActivity, list) { selectedMedicine ->

            if (binding.type.text.toString().equals("Medicine", true)) {
                val selectedMedicineList= MedicationsEntity()
                selectedMedicineList.name = selectedMedicine
                medicineList.add(selectedMedicineList)
                medicineListAdapter.notifyDataSetChanged()
                binding.list.setText("")
            }
            if (binding.type.text.toString().equals("Lab Test", true)) {
                val selectedLabList= LabEntity()
                selectedLabList.testName = selectedMedicine
                labList.add(selectedLabList)
                labListAdapter.notifyDataSetChanged()
                binding.list.setText("")
            }
            if (binding.type.text.toString().equals("Therapy", true)) {
                val selectedTherapy= TherapyEntity()
                selectedTherapy.therapyName = selectedMedicine
                therapyList.add(selectedTherapy)
                therapyListAdapter.notifyDataSetChanged()
                binding.list.setText("")
            }
            Toast.makeText(this@MainActivity, "Selected: $selectedMedicine", Toast.LENGTH_SHORT)
                .show()
        }

        binding.list.threshold = 1
        binding.list.setAdapter(searchAdapter)

    }

    private fun setTherapyListAdapter() {

        therapyListAdapter = TherapyListAdapter(this, this, therapyList)
        binding.rvTherapyList.adapter = therapyListAdapter
        binding.rvTherapyList.layoutManager = LinearLayoutManager(this)

    }

    private fun setLabListAdapter() {

        labListAdapter = LabListAdapter(this, this, labList)
        binding.rvLabList.adapter = labListAdapter
        binding.rvLabList.layoutManager = LinearLayoutManager(this)

    }

    private fun setMedicineListAdapter() {

        medicineListAdapter = MedicineListAdapter(this, this, medicineList)
        binding.rvMedicineList.adapter = medicineListAdapter
        binding.rvMedicineList.layoutManager = LinearLayoutManager(this)

    }

    override fun onDeleteMedicine() {
    }

    override fun onDeleteLab() {
    }

    override fun onDeleteTherapy() {
    }

}