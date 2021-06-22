package com.example.license.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "plant_report")
data class PlantReport(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val imagePath: String,
    val reportDiagnosis: String,
    val dateCreated: String
) : Parcelable