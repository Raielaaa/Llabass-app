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
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.core.FieldFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class BorrowReturnDialogViewModel @Inject constructor(
    @Named("FirebaseFireStore.Instance")
    private val firebaseFireStore: FirebaseFirestore
) : ViewModel() {
    private fun insertInfoToFireStore(
        borrowModel: BorrowModel,
        hostFragment: BottomSheetDialogFragment,
        activity: Activity
    ) {
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

    fun checksTheNumberOfBorrows(activity: Activity, filter: String, hostFragment: BottomSheetDialogFragment, vararg userInfo: String) {
        firebaseFireStore.collection("labass-app-borrow-log")
            .whereGreaterThanOrEqualTo(FieldPath.documentId(), filter)
            .whereLessThan(FieldPath.documentId(), filter + '\uF7FF')
            .get()
            .addOnSuccessListener { querySnapshot ->
                val count = querySnapshot.size()

                if (count < 3) {
                    for (document in querySnapshot.documents) {
                        val itemCode = document.get("modelItemCode").toString()
                        val itemCodeToBeBorrowed = userInfo[4]

                        if (itemCode != itemCodeToBeBorrowed) {
                            insertInfoToFireStore(
                                BorrowModel(
                                    userInfo[0],
                                    userInfo[1],
                                    userInfo[2],
                                    userInfo[3],
                                    userInfo[4],
                                    userInfo[5],
                                    userInfo[6],
                                    userInfo[7],
                                    userInfo[8],
                                    userInfo[9]
                                ),
                                hostFragment,
                                hostFragment.requireActivity()
                            )
                        } else {
                            Helper.dismissDialog()
                            Helper.displayCustomDialog(
                                activity,
                                R.layout.custom_dialog_notice,
                                "Borrow notice",
                                "Borrow Unsuccessful: The scanned item is already present in your active borrowing list."
                            )
                        }
                    }
                } else {
                    Helper.dismissDialog()
                    Helper.displayCustomDialog(
                        activity,
                        R.layout.custom_dialog_notice,
                        "Borrow notice",
                        "Borrow limit reached. Only 3 borrows are allowed at a time."
                    )
                }
            }.addOnFailureListener { exception ->
                Log.e(Constants.TAG, "checksTheNumberOfBorrows: ${exception.message}")
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