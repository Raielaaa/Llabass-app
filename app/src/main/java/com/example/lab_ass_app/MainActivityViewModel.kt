package com.example.lab_ass_app

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.Helper
import com.example.lab_ass_app.utils.ItemInfoModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    private val options: BarcodeScannerOptions,
    @Named("FirebaseFireStore.Instance")
    private val fireStore: FirebaseFirestore,
    @Named("FirebaseAuth.Instance")
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    fun getInfoFromQR(
        mainActivity: MainActivity,
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
                                    //  Getting item code
                                    val itemInfo = barcode.rawValue.toString().split(":")
                                    retrieveItemDescription(itemInfo, mainActivity, imageBitmap)
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

    private fun retrieveItemDescription(
        itemInfo: List<String>,
        mainActivity: MainActivity,
        imageBitmap: Bitmap?
    ) {
        fireStore.collection("labass-app-item-description")
            .document(itemInfo[0])
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val retrievedData = task.result.data

                    val completeItemInfo: ItemInfoModel = ItemInfoModel(
                        modelCode = itemInfo[0],
                        modelName = retrievedData?.get("modelName").toString(),
                        modelCategory = retrievedData?.get("modelCategory").toString(),
                        modelStatus = retrievedData?.get("modelStatus").toString(),
                        modelSize = retrievedData?.get("modelSize").toString(),
                        modelDescription = retrievedData?.get("modelDescription").toString()
                    )

                    retrieveCurrentUserLRN(mainActivity, imageBitmap, completeItemInfo)
                } else {
                    displayToastMessage("Item description not found", mainActivity)
                }
            }.addOnFailureListener { exception ->
                endTaskNotify(exception, mainActivity)
            }
    }

    private fun retrieveCurrentUserLRN(
        mainActivity: MainActivity,
        imageBitmap: Bitmap?,
        completeItemInfo: ItemInfoModel
    ) {
        fireStore.collection("labass-app-user-account-initial")
            .document(firebaseAuth.currentUser!!.uid)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val retrievedData = task.result.data

                    val currentUserLRN = retrievedData?.get("userLRNModel").toString()
                    val currentUserEmail = retrievedData?.get("userEmailModel").toString()

                    displayBorrowReturnDialog(mainActivity, imageBitmap, completeItemInfo, currentUserLRN, currentUserEmail)
                } else {
                    displayToastMessage("LRN not found", mainActivity)
                }
            }.addOnFailureListener { exception ->
                endTaskNotify(exception, mainActivity)
            }
    }

    private fun displayBorrowReturnDialog(
        mainActivity: MainActivity,
        imageBitmap: Bitmap?,
        itemInfoModel: ItemInfoModel,
        currentUserLRN: String,
        currentUserEmail: String
    ) {
        Helper.dismissDialog()

        // Display QR code dialog
        Helper.displayBorrowReturnDialog(
            mainActivity.supportFragmentManager,
            imageBitmap,
            mainActivity,
            itemInfoModel,
            currentUserLRN,
            currentUserEmail
        )
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
        Helper.dismissDialog()
        Toast.makeText(
            activity,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}