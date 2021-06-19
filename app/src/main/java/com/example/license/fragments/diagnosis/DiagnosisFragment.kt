package com.example.license.fragments.diagnosis

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.license.R
import com.example.license.data.PlantReport
import com.example.license.data.PlantReportViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DiagnosisFragment : Fragment() {

    private lateinit var mPlantReportViewModel: PlantReportViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_diagnosis, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPlantReportViewModel = ViewModelProvider(this).get(PlantReportViewModel::class.java)

        view.findViewById<Button>(R.id.button_add_report_result).setOnClickListener {
            insertDataToDatabase()
            findNavController().navigate(R.id.action_diagnosisFragment_to_FirstFragment)
        }

        view.findViewById<Button>(R.id.button_cancel).setOnClickListener {
            findNavController().navigate(R.id.action_diagnosisFragment_to_FirstFragment)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertDataToDatabase() {
        val name = "aaaaaaa"
        val imagePath = "dhasldka"
        val reportDiagnosis = "blight"

        if(inputCheck(name, imagePath, reportDiagnosis)){
            val plantReport = PlantReport(0, name, imagePath, reportDiagnosis,  LocalDateTime.now().format(DateTimeFormatter.ISO_DATE))
            mPlantReportViewModel.addPlantReport(plantReport)
            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "Please fill out all the fields.", Toast.LENGTH_LONG).show()
        }

    }

    private fun inputCheck(name: String,  imagePath: String, reportDiagnosis: String): Boolean {
        return !(TextUtils.isEmpty(name) &&  TextUtils.isEmpty(imagePath) && TextUtils.isEmpty(reportDiagnosis))
    }
}