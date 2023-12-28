package com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.Helper
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
        hostFragment: Fragment
    ) {
        firebaseFireStore.collection("labass-app-borrow-log")
            .document("${borrowModel.modelLRN}-${borrowModel.modelEmail}")
            .set(borrowModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    displayToastMessage("Insert success", hostFragment)
                } else {
                    displayToastMessage("An error occurred", hostFragment)
                }
            }.addOnFailureListener { exception ->
                endTaskNotify(exception, hostFragment)
            }
    }

    // Function to handle the end of tasks and notify the user about errors
    private fun endTaskNotify(exception: Exception, hostFragment: Fragment) {
        // Display an error message, log the exception, and dismiss the loading dialog
        displayToastMessage("Error: ${exception.localizedMessage}", hostFragment)
        Log.e(Constants.TAG, "BorrowReturnDialogViewModel: ${exception.message}")
        Helper.dismissDialog()
    }

    // Display toast message
    private fun displayToastMessage(message: String, hostFragment: Fragment) {
        Toast.makeText(
            hostFragment.requireContext(),
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}