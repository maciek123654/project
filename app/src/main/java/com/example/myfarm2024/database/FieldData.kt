package com.example.myfarm2024.database

data class FieldData(
    val parcelId: String,
    val parcelNumber: String,
    val surfaceArea: Double,
    val province: String?,
    val county: String?,
    val commune: String?,
    val town: String?,
    val landClass: String
)