package com.example.license.fragments

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.license.R
import com.example.license.data.PlantReportViewModel
import com.example.license.entity.diagnosis.Diagnosis
import java.io.IOException


class InspectFragment : Fragment() {

    private val args by navArgs<InspectFragmentArgs>()
    private lateinit var mPlantReportViewModel: PlantReportViewModel
    private lateinit var pathFile : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inspect, container, false)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //pathFile = arguments?.getString("imgPath").toString()
        pathFile = args.currentReport.imagePath
        println("imgPath: " + pathFile )

        //imageFile = getPhotoFile(pathFile)
        val takenImage = BitmapFactory.decodeFile(pathFile)
        if(takenImage != null) {
            val imageView = view?.findViewById<ImageView>(R.id.inspect_image)
            val finalImage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                rotateImage(takenImage)
            } else {
                TODO("VERSION.SDK_INT < Q")
            }
            imageView?.setImageBitmap(finalImage)
        }
        view.findViewById<TextView>(R.id.inspect_name).setText(args.currentReport.name)
        view.findViewById<TextView>(R.id.inspect_disease).setText(args.currentReport.reportDiagnosis)
        view.findViewById<TextView>(R.id.inspect_description).text = Diagnosis().diagnosisMap.get(args.currentReport.reportDiagnosis)

        mPlantReportViewModel = ViewModelProvider(this).get(PlantReportViewModel::class.java)

        view.findViewById<Button>(R.id.button_delete).setOnClickListener {
            deleteUser()
            findNavController().navigate(R.id.action_inspectFragment_to_FirstFragment)
        }

        view.findViewById<Button>(R.id.button_back).setOnClickListener {
            findNavController().navigate(R.id.action_inspectFragment_to_FirstFragment)
        }
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

    private fun deleteUser(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mPlantReportViewModel.deletePlantReport(args.currentReport)
        }
        builder.setNegativeButton("No") { _, _ ->

        }
        builder.setTitle("Delete report")
        builder.setMessage("Are you sure that you want to delete this report?")
        builder.create().show()
    }

}