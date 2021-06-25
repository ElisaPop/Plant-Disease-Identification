package com.example.license.fragments.diagnosis

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.license.R
import com.example.license.entity.PlantReport
import com.example.license.data.PlantReportViewModel
import com.example.license.entity.diagnosis.Diagnosis
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DiagnosisFragment : Fragment() {

    private lateinit var mPlantReportViewModel: PlantReportViewModel
    private lateinit var pathFile : String
    private lateinit var diagnosis : String

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

        pathFile = arguments?.getString("imgPath").toString()
        println("imgPath: " + pathFile )

        diagnosis =  arguments?.getString("diagnosis").toString()

        //imageFile = getPhotoFile(pathFile)
        val takenImage = BitmapFactory.decodeFile(pathFile)
        val imageView = view?.findViewById<ImageView>(R.id.diagnosis_image)
        val diagnosisView = view?.findViewById<TextView>(R.id.diagnosis_disease)
        val diagnosisDescription = view?.findViewById<TextView>(R.id.diagnosis_description)
        val finalImage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            rotateImage(takenImage)
        } else {
            TODO("VERSION.SDK_INT < Q")
        }
        imageView?.setImageBitmap(finalImage)
        diagnosisView.text = diagnosis
        diagnosisDescription.text = Diagnosis().diagnosisMap[diagnosis]

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
        val name = view?.findViewById<EditText>(R.id.diagnosis_name)?.text.toString()
        val reportDiagnosis = view?.findViewById<TextView>(R.id.diagnosis_disease)?.text.toString()

        if(inputCheck(name, pathFile, reportDiagnosis)){
            val plantReport = PlantReport(
                    0,
                    name,
                    pathFile,
                    reportDiagnosis,
                    LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)
            )
            mPlantReportViewModel.addPlantReport(plantReport)
            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "Please fill out all the fields.", Toast.LENGTH_LONG).show()
        }

    }

    private fun inputCheck(name: String,  imagePath: String, reportDiagnosis: String): Boolean {
        return !(TextUtils.isEmpty(name) &&  TextUtils.isEmpty(imagePath) && TextUtils.isEmpty(reportDiagnosis))
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun rotateImage(bitmap: Bitmap): Bitmap? {
        var exifInterface: ExifInterface? = null
        try {
            exifInterface = ExifInterface(pathFile)
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
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}