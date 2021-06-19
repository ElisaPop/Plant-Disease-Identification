package com.example.license.fragments.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.license.R
import com.example.license.data.PlantReport
import java.util.*

class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var plantReportList = emptyList<PlantReport>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var reportRef: TextView =itemView.findViewById(R.id.recycler_report_name)
        private var dateRef: TextView =itemView.findViewById(R.id.recycler_date_created)
        private var diseaseRef: TextView =itemView.findViewById(R.id.recycler_disease_name)
        private var imageRef: ImageView = itemView.findViewById(R.id.recycler_image)

        lateinit var recipe: PlantReport

        fun bindData(data: PlantReport){
            reportRef.text = data.name
            dateRef.text = data.dateCreated
            diseaseRef.text = data.reportDiagnosis

            recipe = data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.report_row, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(plantReportList.elementAt(position))
    }

    override fun getItemCount(): Int {
        return plantReportList.size
    }

    fun setData(plantReport: List<PlantReport>){
        this.plantReportList = plantReport
        notifyDataSetChanged()
    }

}