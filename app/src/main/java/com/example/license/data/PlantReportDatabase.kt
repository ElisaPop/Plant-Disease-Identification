package com.example.license.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.license.entity.PlantReport
import com.example.license.entity.PlantReportDao

@Database(entities = [PlantReport::class], version = 1, exportSchema = false)
abstract class PlantReportDatabase: RoomDatabase() {

    abstract fun plantReportDao(): PlantReportDao

    companion object{
        @Volatile
        private var INSTANCE: PlantReportDatabase? = null

        fun getDatabase(context: Context): PlantReportDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlantReportDatabase::class.java,
                    "plant_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}