package com.samuelsihotang.android.qr.ui

import android.annotation.SuppressLint
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

typealias BarcodeListener = (barcode: String) -> Unit

@OptIn(ExperimentalGetImage::class)
@androidx.annotation.OptIn(ExperimentalGetImage::class)
class BarcodeAnalyzer(private val barcodeListener: (barcode: String) -> Unit) : ImageAnalysis.Analyzer{
    private val scanner = BarcodeScanning.getClient()

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if(mediaImage != null){
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image).addOnSuccessListener { barcodes ->
                for(barcode in barcodes){
                    barcodeListener(barcode.rawValue ?: "")
                }
            }
                .addOnFailureListener{

                }
                .addOnCompleteListener{
                    imageProxy.close()
                }
        }
    }
}