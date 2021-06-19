package com.example.license.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlantReportDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPlantReport(plantReport: PlantReport)

    @Query("SELECT * FROM plant_report ORDER BY id ASC")
    fun readAllData(): LiveData<List<PlantReport>>

}