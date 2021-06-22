package com.example.license.entity

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.license.entity.PlantReport

@Dao
interface PlantReportDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPlantReport(plantReport: PlantReport)

    @Query("SELECT * FROM plant_report ORDER BY id ASC")
    fun readAllData(): LiveData<List<PlantReport>>

    @Delete
    suspend fun deletePlantReport(plantReport: PlantReport)

}