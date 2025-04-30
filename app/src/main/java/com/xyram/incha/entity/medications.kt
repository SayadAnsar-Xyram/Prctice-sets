package com.xyram.incha.entity

data class MedicationsEntity(
    var name: String = "",
    var dosage: String = "",
    var isMorning: Boolean = false,
    var isAfternoon: Boolean = false,
    var isEvening: Boolean = false,
    var duration: String = "",
    var instruction: String = ""

)

data class LabEntity(
    var testName: String = "",
    var instruction: String = ""
)

data class TherapyEntity(
    var therapyName: String = "",
    var details: String = ""
)
