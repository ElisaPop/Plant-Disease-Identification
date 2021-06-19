package com.example.license.data

import androidx.lifecycle.LiveData

class PlantReportRepository(private val plantReportDao: PlantReportDao) {

    val readAllData: LiveData<List<PlantReport>> = plantReportDao.readAllData()

    suspend fun addPlantReport(plantReport: PlantReport){
        plantReportDao.addPlantReport(plantReport)
    }
}