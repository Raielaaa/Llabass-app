package com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.lab_ass_app.R
import com.example.lab_ass_app.ui.main.student_teacher.home.HomeViewModel
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.Helper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
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
        activity: Activity,
        homeViewModel: HomeViewModel
    ) {
        firebaseFireStore.collection("labass-app-borrow-log")
            .document("${borrowModel.modelLRN}-${borrowModel.modelEmail}-${borrowModel.modelBorrowDateTime.replace(" ", "").replace("/", "_")}")
            .set(borrowModel)
            .addOnCompleteListener { task ->
                Log.d(Constants.TAG, "checkBorrowAvailability: cp-4")
                if (task.isSuccessful) {
                    firebaseFireStore.collection("labass-app-item-description")
                        .document(borrowModel.modelItemCode)
                        .update("modelStatus", "Unavailable")
                        .addOnSuccessListener {
                            homeViewModel.retrieveBorrowedItemInfoFromDB(
                                hostFragment,
                                Helper.homeBinding!!
                            )

                            Helper.dismissDialog()
                            Helper.displayCustomDialog(
                                activity,
                                R.layout.custom_dialog_notice,
                                "Borrow notice",
                                "Borrow registration for the item has been successfully completed."
                            )
                        }.addOnFailureListener { exception ->
                            endTaskNotify(exception, hostFragment)
                        }
                } else {
                    Helper.dismissDialog()
                    displayToastMessage("An error occurred", hostFragment)
                }
                hostFragment.dismiss()
            }.addOnFailureListener { exception ->
                endTaskNotify(exception, hostFragment)
            }
    }

    fun checkBorrowAvailability(
        homeViewModel: HomeViewModel,
        activity: Activity,
        filter: String,
        hostFragment: BottomSheetDialogFragment,
        vararg userInfo: String
    ) {
        firebaseFireStore.collection("labass-app-item-description")
            .document(userInfo[4])
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val availability = documentSnapshot.get("modelStatus")

                if (availability == "Available") {
                    firebaseFireStore.collection("labass-app-borrow-log")
                        .whereGreaterThanOrEqualTo(FieldPath.documentId(), filter)
                        .whereLessThan(FieldPath.documentId(), filter + '\uF7FF')
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            val count = querySnapshot.size()

                            if (count < 3) {
                                if (querySnapshot.documents.isEmpty()) {
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
                                        hostFragment.requireActivity(),
                                        homeViewModel
                                    )
                                } else {
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
                                                hostFragment.requireActivity(),
                                                homeViewModel
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
                } else if (availability == "Unavailable") {
                    Helper.dismissDialog()
                    Helper.displayCustomDialog(
                        activity,
                        R.layout.custom_dialog_notice,
                        "Borrow notice",
                        "Borrow unsuccessful. The item is either already reserved or currently unavailable."
                    )
                }
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