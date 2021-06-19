package com.example.license.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plant_report")
data class PlantReport(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val imagePath: String,
    val reportDiagnosis: String,
    val dateCreated: String
)