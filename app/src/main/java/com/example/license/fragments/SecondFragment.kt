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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.license.R
import com.example.license.model.Recognition
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Float
import java.net.URI
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

private const val REQUEST_CODE = 47
private const val FILE_NAME = "Photo"
private lateinit var photoFile: File

private const val IMAGE_PICK_CODE = 1000;
private const val PERMISSION_CODE = 1001;

class SecondFragment : Fragment() {

    private val imgSize: Int = 224
    lateinit var tflite: Interpreter
    lateinit var labelList: List<String>
    lateinit var imgBitmap: Bitmap
    private val IMAGE_MEAN = 0
    private val IMAGE_STD = 255.0f

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        try{
            tflite = Interpreter(loadModelFile("plant_disease_model.tflite"))
        } catch(ex: Exception){
            ex.printStackTrace()
        }

        labelList = requireContext().assets.open("labels.txt").bufferedReader().useLines { it.toList() }

        return inflater.inflate(R.layout.fragment_second, container, false)

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
                    val result = recognizeImage(imgBitmap)

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
            println("photofile: " + photoFile)

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
            println("photofile: " + photoFile)

            //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile)
            val fileProvider = activity?.let { it1 -> FileProvider.getUriForFile(it1,"com.example.license.fileprovider",
                    photoFile
            ) }

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if(activity?.packageManager?.let { it1 -> intent.resolveActivity(it1) } != null){
                hideButtons(view)
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
            }
            imageView?.setImageBitmap(finalImage)
        }
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK){
            val selectedImageUri: Uri? = data!!.data
            //val s: String? = selectedImageUri?.path
            //val file = selectedImageUri?.toFile()


            var inputStream: InputStream
            var s: String
            if (selectedImageUri != null) {
                inputStream = this.requireContext().contentResolver?.openInputStream(selectedImageUri)!!
                s = inputStream.toString()
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

    private fun loadModelFile(modelPath: String): MappedByteBuffer {
        val fileDescriptor = requireContext().assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun recognizeImage(bitmap: Bitmap): String {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, imgSize, imgSize, false)
        val result = Array(1) { FloatArray(labelList.size) }
        tflite.run(convertBitmapToByteBuffer(scaledBitmap), result)
        return getSortedResult(result)
    }

    private fun getSortedResult(labelProbArray: Array<FloatArray>): String {
        Log.d("Classifier", "List Size:(%d, %d, %d)".format(labelProbArray.size,labelProbArray[0].size,labelList.size))

        val pq = PriorityQueue(
                3,
                Comparator<Recognition> {
                    (_, confidence1), (_, confidence2)
                    -> Float.compare(confidence1, confidence2) * -1
                })

        for (i in labelList.indices) {
            val confidence = labelProbArray[0][i]
            if (confidence >= 0.4f) {
                pq.add(Recognition(if (labelList.size > i) labelList[i] else "Unknown", confidence)
                )
            }
        }
        Log.d("Classifier", "pqsize:(%d)".format(pq.size))

        val recognitions = ArrayList<Recognition>()
        val recognitionsSize = pq.size
        for (i in 0 until recognitionsSize) {
            recognitions.add(pq.poll())
        }

        if(recognitions.size > 0) {
            return recognitions[0].title
        } else return "Healthy"

    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * imgSize * imgSize * 3) // 3 is pixel size
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(imgSize * imgSize)

        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0 until imgSize) {
            for (j in 0 until imgSize) {
                val `val` = intValues[pixel++]

                byteBuffer.putFloat((((`val`.shr(16)  and 0xFF) - IMAGE_MEAN) / IMAGE_STD))
                byteBuffer.putFloat((((`val`.shr(8) and 0xFF) - IMAGE_MEAN) / IMAGE_STD))
                byteBuffer.putFloat((((`val` and 0xFF) - IMAGE_MEAN) / IMAGE_STD))
            }
        }
        return byteBuffer
    }

}