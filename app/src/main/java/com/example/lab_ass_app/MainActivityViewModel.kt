package com.example.lab_ass_app

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.Helper
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    @Named("BarcodeScannerOptionsBuilder.Instance")
    private val options: BarcodeScannerOptions
) : ViewModel() {
    fun getInfoFromQR(
        mainActivity: Activity,
        imageBitmap: Bitmap?
    ) {
        displayLoadingDialog(mainActivity)

        if (imageBitmap != null) {
            //  Creates an InputImage object from the bitmap
            val image = InputImage.fromBitmap(imageBitmap, 0)
            //  Creates a BarcodeScanner client with options
            val scanner = BarcodeScanning.getClient(options)

            //  Processes the image for barcodes
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.toString() != "[]") {
                        //  Loop through each barcode found in the image
                        for (barcode in barcodes) {
                            //  If the barcode is of type text, extract the book name and author
                            when (barcode.valueType) {
                                Barcode.TYPE_TEXT -> {
                                    Log.d(Constants.TAG, "getInfoFromQR: ${barcode.rawValue}")
                                }
                            }
                        }
                    } else {
                        //  If no barcode is found
                        displayToastMessage("Nothing to scan. Please try again.", mainActivity)
                    }
                }.addOnFailureListener { exception ->
                    endTaskNotify(exception, mainActivity)
                }
        } else {
            displayToastMessage("QR code not found", mainActivity)
        }
    }

    private fun displayLoadingDialog(mainActivity: Activity) {
        Helper.displayCustomDialog(
            mainActivity,
            R.layout.custom_dialog_loading
        )
    }

    //  Function to handle the end of tasks and notify the user about errors
    private fun endTaskNotify(exception: Exception, activity: Activity) {
        //  Display an error message, log the exception, and dismiss the loading dialog
        displayToastMessage("Error: ${exception.localizedMessage}", activity)
        Log.e(Constants.TAG, "MainActivityViewModel: ${exception.message}")
        Helper.dismissDialog()
    }

    //  Function to display toast messages
    private fun displayToastMessage(message: String, activity: Activity) {
        Toast.makeText(
            activity,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}