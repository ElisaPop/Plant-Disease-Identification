package com.example.license.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.license.R
import com.example.license.model.Classifier
import java.io.File
import java.io.IOException
import java.io.InputStream

private const val REQUEST_CODE = 47
private const val FILE_NAME = "Photo"
private lateinit var photoFile: File
lateinit var imgBitmap: Bitmap
lateinit var classifier: Classifier

private const val IMAGE_PICK_CODE = 1000;
private const val PERMISSION_CODE = 1001;

class SecondFragment : Fragment() {


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        classifier = Classifier("plant_disease_model.tflite","labels.txt", requireContext())
        return inflater.inflate(R.layout.fragment_chooseimage, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_second).setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
            showButtons(view)
        }

        view.findViewById<Button>(R.id.button_diagnose).setOnClickListener {

            if(::photoFile.isInitialized){
                if (::imgBitmap.isInitialized) {
                    val result = classifier.recognizeImage(imgBitmap)

                    val bundle = bundleOf("imgPath" to photoFile.toString(),
                            "diagnosis" to result)

                    findNavController().navigate(R.id.action_SecondFragment_to_diagnosisFragment, bundle)
                    showButtons(view)
                }


            } else {
                Toast.makeText(activity, "Please take a picture", Toast.LENGTH_SHORT).show()
            }
            //findNavController().navigate(R.id.action_SecondFragment_to_diagnosisFragment)
        }

        view.findViewById<Button>(R.id.button_take_photo).setOnClickListener{
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(
                FILE_NAME
            )
            println("photofile: $photoFile")

            //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile)
            val fileProvider = activity?.let { it1 -> FileProvider.getUriForFile(it1,"com.example.license.fileprovider",
                photoFile
            ) }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if(activity?.packageManager?.let { it1 -> takePictureIntent.resolveActivity(it1) } != null){
                hideButtons(view)
                startActivityForResult(takePictureIntent,REQUEST_CODE)
            } else {
                Toast.makeText(activity, "Unable to open camera", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.button_upload).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"

            photoFile = getPhotoFile(
                    FILE_NAME
            )
            println("photofile: $photoFile")

            //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile)
            val fileProvider = activity?.let { it1 -> FileProvider.getUriForFile(it1,"com.example.license.fileprovider",
                    photoFile
            ) }

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if(activity?.packageManager?.let { it1 -> intent.resolveActivity(it1) } != null){
                startActivityForResult(intent,IMAGE_PICK_CODE)
            } else {
                Toast.makeText(activity, "Unable to open gallery", Toast.LENGTH_SHORT).show()
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
            if (finalImage != null) {
                imgBitmap = finalImage
                this.view?.let { hideButtons(it) }
            }
            imageView?.setImageBitmap(finalImage)
        }
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK){
            val selectedImageUri: Uri? = data!!.data
            //val s: String? = selectedImageUri?.path
            //val file = selectedImageUri?.toFile()

            var inputStream: InputStream

            if (selectedImageUri != null) {
                inputStream = this.requireContext().contentResolver?.openInputStream(selectedImageUri)!!
                photoFile.copyInputStreamToFile(inputStream)
            }

            val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
            //photoFile = selectedImageUri?.toFile()!!
            //val takenImage = data?.extras?.get("data") as Bitmap
            //val takenImage = MediaStore.Images.Media.getBitmap(this.context?.contentResolver,selectedImageUri)
            val imageView = view?.findViewById<ImageView>(R.id.diagnosis_image)
            val finalImage = rotateImage(takenImage)
            if (finalImage != null) {
                imgBitmap = finalImage
                this.view?.let { hideButtons(it) }
            }
            imageView?.setImageBitmap(finalImage)
        }
    }

    fun File.copyInputStreamToFile(inputStream: InputStream) {
        this.outputStream().use { fileOut ->
            inputStream.copyTo(fileOut)
        }
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

    private fun hideButtons(view: View){
        view.findViewById<Button>(R.id.button_upload).visibility = View.GONE
        view.findViewById<Button>(R.id.button_take_photo).visibility = View.GONE
        view.findViewById<Button>(R.id.button_take_video).visibility = View.GONE
    }

    private fun showButtons(view: View){
        view.findViewById<Button>(R.id.button_upload).visibility = View.VISIBLE
        view.findViewById<Button>(R.id.button_take_photo).visibility = View.VISIBLE
        view.findViewById<Button>(R.id.button_take_video).visibility = View.VISIBLE
    }





}