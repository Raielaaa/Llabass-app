package com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.lab_ass_app.R
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.Helper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class BorrowReturnDialogViewModel @Inject constructor(
    @Named("FirebaseFireStore.Instance")
    private val firebaseFireStore: FirebaseFirestore
) : ViewModel() {
    fun insertInfoToFireStore(
        borrowModel: BorrowModel,
        hostFragment: BottomSheetDialogFragment,
        activity: Activity
    ) {
        Log.d(Constants.TAG, "${borrowModel.modelLRN}-${borrowModel.modelEmail}-${borrowModel.modelBorrowDateTime.replace(" ", "").replace("/", "_")}")
        firebaseFireStore.collection("labass-app-borrow-log")
            .document("${borrowModel.modelLRN}-${borrowModel.modelEmail}-${borrowModel.modelBorrowDateTime.replace(" ", "").replace("/", "_")}")
            .set(borrowModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Helper.dismissDialog()
                    Helper.displayCustomDialog(
                        activity,
                        R.layout.custom_dialog_notice,
                        "Borrow notice",
                        "Borrow registration for the item has been successfully completed."
                    )
                } else {
                    Helper.dismissDialog()
                    displayToastMessage("An error occurred", hostFragment)
                }
                hostFragment.dismiss()
            }.addOnFailureListener { exception ->
                endTaskNotify(exception, hostFragment)
            }
    }

    // Function to handle the end of tasks and notify the user about errors
    private fun endTaskNotify(exception: Exception, hostFragment: BottomSheetDialogFragment) {
        // Display an error message, log the exception, and dismiss the loading dialog
        displayToastMessage("Error: ${exception.localizedMessage}", hostFragment)
        Log.e(Constants.TAG, "BorrowReturnDialogViewModel: ${exception.message}")
        Helper.dismissDialog()
        hostFragment.dismiss()
    }

    // Display toast message
    private fun displayToastMessage(message: String, hostFragment: Fragment) {
        Toast.makeText(
            hostFragment.requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}