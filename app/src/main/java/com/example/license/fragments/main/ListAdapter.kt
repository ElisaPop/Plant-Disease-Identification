package com.example.license.fragments.main

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.license.R
import com.example.license.entity.PlantReport
import java.io.IOException

class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var plantReportList = emptyList<PlantReport>()


    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var reportRef: TextView =itemView.findViewById(R.id.recycler_report_name)
        private var dateRef: TextView =itemView.findViewById(R.id.recycler_date_created)
        private var diseaseRef: TextView =itemView.findViewById(R.id.recycler_disease_name)
        private var imageRef: ImageView = itemView.findViewById(R.id.recycler_image)
        private lateinit var imgPath : String

        lateinit var recipe: PlantReport

        @RequiresApi(Build.VERSION_CODES.Q)
        fun bindData(data: PlantReport){
            reportRef.text = data.name
            dateRef.text = data.dateCreated
            diseaseRef.text = data.reportDiagnosis

            val takenImage = BitmapFactory.decodeFile(data.imagePath)
            if(takenImage != null){
                imgPath = data.imagePath
                println("diseaseImg: " + data.imagePath)
                val finalImage = rotateImage(takenImage)
                imageRef.setImageBitmap(finalImage)
            }


            recipe = data
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        private fun rotateImage(bitmap: Bitmap): Bitmap? {
            var exifInterface: ExifInterface? = null
            try {
                exifInterface = ExifInterface(imgPath)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val orientation = exifInterface!!.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90F)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180F)
                else -> {
                }
            }
            return Bitmap.createBitmap(bitmap, bitmap.width/3, bitmap.width/3, bitmap.width/4, bitmap.width/4, matrix, true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.report_row, parent, false))
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentReport = plantReportList.elementAt(position)
        holder.bindData(currentReport)

        holder.itemView.findViewById<ConstraintLayout>(R.id.rowReport).setOnClickListener{
            val action = FirstFragmentDirections.actionFirstFragmentToInspectFragment(currentReport)
            holder.itemView.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return plantReportList.size
    }

    fun setData(plantReport: List<PlantReport>){
        this.plantReportList = plantReport
        notifyDataSetChanged()
    }



}