package com.example.license.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.license.R
import java.io.File
import java.io.IOException

private const val REQUEST_CODE = 47
private const val FILE_NAME = "Photo.jpg"
private lateinit var photoFile: File
class SecondFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_second).setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        view.findViewById<Button>(R.id.button_diagnose).setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_diagnosisFragment)
        }


        view.findViewById<Button>(R.id.button_take_photo).setOnClickListener{
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(
                FILE_NAME
            )

            //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile)
            val fileProvider = activity?.let { it1 -> FileProvider.getUriForFile(it1,"com.example.license.fileprovider",
                photoFile
            ) }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if(activity?.packageManager?.let { it1 -> takePictureIntent.resolveActivity(it1) } != null){
                startActivityForResult(takePictureIntent,
                    REQUEST_CODE
                )
            } else {
                Toast.makeText(activity, "Unable to open camera", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
//            val takenImage = data?.extras?.get("data") as Bitmap
            val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
            val imageView = view?.findViewById<ImageView>(R.id.diagnosis_image)
            val finalImage = rotateImage(takenImage)
            imageView?.setImageBitmap(finalImage)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun rotateImage(bitmap: Bitmap): Bitmap? {
        var exifInterface: ExifInterface? = null
        try {
            exifInterface = ExifInterface(photoFile)
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