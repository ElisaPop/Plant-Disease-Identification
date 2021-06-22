package com.example.license.data

import androidx.lifecycle.LiveData
import com.example.license.entity.PlantReport
import com.example.license.entity.PlantReportDao

class PlantReportRepository(private val plantReportDao: PlantReportDao) {

    val readAllData: LiveData<List<PlantReport>> = plantReportDao.readAllData()

    suspend fun addPlantReport(plantReport: PlantReport){
        plantReportDao.addPlantReport(plantReport)
    }

    suspend fun deletePlantReport(plantReport: PlantReport){
        plantReportDao.deletePlantReport(plantReport)
    }
}