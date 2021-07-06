package com.example.license.model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.lang.Float
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class Classifier {
    private val imgSize: Int = 224
    var tflite: Interpreter
    var labelList: List<String>
    private val IMAGE_MEAN = 0
    private val IMAGE_STD = 255.0f


    constructor(modelPath: String, labelPath: String, context: Context){
        tflite = Interpreter(loadModelFile(modelPath,context))
        labelList = context.assets.open(labelPath).bufferedReader().useLines { it.toList() }

    }

    fun recognizeImage(bitmap: Bitmap): String {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, imgSize, imgSize, false)
        val result = Array(1) { FloatArray(labelList.size) }
        tflite.run(convertBitmapToByteBuffer(scaledBitmap), result)

        var cnt = 0
        var maxCnt = 0
        var max = 0.0f

        for(i in result){
            for (j in i){
                cnt++
                if(j > max){
                    max = j
                    maxCnt = cnt
                }
            }
        }

        if(max > 0.4f){
            val s = labelList[maxCnt-1]
            val newvalue = s.substring(s.indexOf(" ") + 1)
            return newvalue
        } else return "Unknown"
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

    private fun loadModelFile(modelPath: String, context:Context): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

}