package com.example.license.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlantReportViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<PlantReport>>
    private val repository: PlantReportRepository

    init{
        val plantReportDao = PlantReportDatabase.getDatabase(application).plantReportDao()
        repository = PlantReportRepository(plantReportDao)
        readAllData = repository.readAllData
    }

    fun addPlantReport(plantReport: PlantReport){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addPlantReport(plantReport)
        }
    }

}