package com.example.lab_ass_app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.Helper
import com.example.lab_ass_app.utils.models.ItemInfoModel
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
        Log.d(Constants.TAG, "camera-path: 5")
        displayLoadingDialog(mainActivity)

        if (imageBitmap != null) {
            Log.d(Constants.TAG, "camera-path: 6")

            //  Creates an InputImage object from the bitmap
            val image = InputImage.fromBitmap(imageBitmap, 0)
            //  Creates a BarcodeScanner client with options
            val scanner = BarcodeScanning.getClient(options)

            //  Processes the image for barcodes
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    Log.d(Constants.TAG, "camera-path: 7")
                    if (barcodes.toString() != "[]") {
                        Log.d(Constants.TAG, "camera-path: 8")
                        //  Loop through each barcode found in the image
                        for (barcode in barcodes) {
                            Log.d(Constants.TAG, "getInfoFromQR: ${barcode.rawValue}")
                            if (barcode.rawValue.toString().isEmpty()) {
                                Helper.dismissDialog()
                                Helper.displayCustomDialog(
                                    mainActivity,
                                    R.layout.custom_dialog_notice,
                                    "QR-scan notice",
                                    "Nothing to scan. Please try again."
                                )
                            } else {
                                //  If the barcode is of type text, extract the book name and author
                                when (barcode.valueType) {
                                    Barcode.TYPE_TEXT -> {
                                        //  Getting item code
                                        val itemInfo = barcode.rawValue.toString().split(":")
                                        retrieveItemDescription(itemInfo, mainActivity, imageBitmap)
                                    }
                                }
                            }
                        }
                    } else {
                        //  If no barcode is found
                        Helper.dismissDialog()
                        Helper.displayCustomDialog(
                            mainActivity,
                            R.layout.custom_dialog_notice,
                            "QR-scan notice",
                            "Nothing to scan. Please try again."
                        )
                    }
                }.addOnFailureListener { exception ->
                    endTaskNotify(exception, mainActivity)
                }
        } else {
            Log.e(Constants.TAG, "getInfoFromQR: QR code not found", )
            Helper.dismissDialog()
            Helper.displayCustomDialog(
                mainActivity,
                R.layout.custom_dialog_notice,
                "QR-scan notice",
                "QR-code not found."
            )
        }
    }

    private fun retrieveItemDescription(
        itemInfo: List<String>,
        mainActivity: MainActivity,
        imageBitmap: Bitmap?
    ) {
        Log.d(Constants.TAG, "camera-path: 9")
        fireStore.collection("labass-app-item-description")
            .document(itemInfo[0])
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(Constants.TAG, "camera-path: 10")
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
        val userID = firebaseAuth.currentUser!!.uid

        fireStore.collection("labass-app-user-account-initial")
            .document(firebaseAuth.currentUser!!.uid)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val retrievedData = task.result.data

                    val currentUserLRN = retrievedData?.get("userLRNModel").toString()
                    val currentUserEmail = retrievedData?.get("userEmailModel").toString()
                    val currentUserType = retrievedData?.get("userTypeModel").toString()

                    displayBorrowReturnDialog(mainActivity, imageBitmap, completeItemInfo, currentUserLRN, currentUserEmail, currentUserType, userID)
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
        currentUserEmail: String,
        currentUserType: String,
        userID: String
    ) {
        Helper.dismissDialog()

        // Display QR code dialog
        Helper.displayBorrowReturnDialog(
            mainActivity.supportFragmentManager,
            imageBitmap,
            mainActivity,
            itemInfoModel,
            currentUserLRN,
            currentUserEmail,
            currentUserType,
            userID
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

    fun checkNetworkAvailability(mainActivity: MainActivity): Boolean {
        val connectivityManager =
            mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

            networkCapabilities != null &&
                    (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }
}