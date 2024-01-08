package com.example.lab_ass_app.ui.main.student_teacher.profile

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.BorrowModel
import com.example.lab_ass_app.ui.main.student_teacher.profile.rv.ProfileAdapter
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.DataCache
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @Named("FirebaseFireStore.Instance")
    private val fireStore: FirebaseFirestore
) : ViewModel() {
    fun displayRealTimeProfileInfoToRV(
        hostFragment: Fragment,
        rvProfileList: RecyclerView
    ) {
        Log.d(Constants.TAG, "displayRealTimeProfileInfoToRV: Entry")
        fireStore.collection("labass-app-borrow-log")
            .whereGreaterThanOrEqualTo(FieldPath.documentId(), DataCache.filter)
            .whereLessThan(FieldPath.documentId(), DataCache.filter + '\uF7FF')
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val modelBorrowDateTime = document.getString("modelBorrowDateTime")!!
                    val modelBorrowDeadlineDateTime = document.getString("modelBorrowDeadlineDateTime")!!
                    val modelEmail = document.getString("modelEmail")!!
                    val modelItemCategory = document.getString("modelItemCategory")!!
                    val modelItemCode = document.getString("modelItemCode")!!
                    val modelItemName = document.getString("modelItemName")!!
                    val modelItemSize = document.getString("modelItemSize")!!
                    val modelLRN = document.getString("modelLRN")!!
                    val modelUserID = document.getString("modelUserID")!!
                    val modelUserType = document.getString("modelUserType")!!

                    DataCache.rvBorrowInfoProfile.add(
                        BorrowModel(
                            modelEmail,
                            modelLRN,
                            modelUserType,
                            modelUserID,
                            modelItemCode,
                            modelItemName,
                            modelItemCategory,
                            modelItemSize,
                            modelBorrowDateTime,
                            modelBorrowDeadlineDateTime
                        )
                    )
                }
                val adapter = ProfileAdapter(
                    hostFragment.requireContext(),
                    FirebaseStorage.getInstance()
                )

                adapter.setList(DataCache.rvBorrowInfoProfile)
                rvProfileList.adapter = adapter
                Log.d(Constants.TAG, "displayRealTimeProfileInfoToRV: End")
            }.addOnFailureListener { exception ->
                endTaskException(exception, hostFragment)
            }
    }

    private fun endTaskException(exception: Exception, hostFragment: Fragment) {
        Toast.makeText(
            hostFragment.requireContext(),
            "Error: ${exception.localizedMessage}",
            Toast.LENGTH_LONG
        ).show()
        Log.e(Constants.TAG, "endTaskException-profileViewModel: ${exception.message}", )
    }
}