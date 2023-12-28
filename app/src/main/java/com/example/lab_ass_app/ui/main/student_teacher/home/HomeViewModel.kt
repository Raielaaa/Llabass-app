package com.example.lab_ass_app.ui.main.student_teacher.home

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.lab_ass_app.databinding.FragmentHomeBinding
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.Helper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HomeViewModel @Inject constructor(
    @Named("FirebaseFireStore.Instance")
    private val firebaseFireStore: FirebaseFirestore
) : ViewModel() {
    fun takeQR(activity: Activity) {
        //  Method for starting the camera
        Helper.takeQR(activity)
    }

    fun initTopBorrowDisplay(binding: FragmentHomeBinding) {
        firebaseFireStore.collection("labass-app-item-description")
            .orderBy("modelBorrowCount", Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val topThreeList: MutableList<Pair<String, MutableList<String>>> = mutableListOf()

                querySnapshot.forEachIndexed { index, queryDocumentSnapshot ->
                    val itemName = queryDocumentSnapshot.get("modelName")
                    val borrowCount = queryDocumentSnapshot.get("modelBorrowCount")

                    topThreeList.add(Pair("Top${index + 1}", mutableListOf(itemName.toString(), borrowCount.toString())))
                }

                binding.apply {
                    tvTitleTop1.text = topThreeList[0].second[0]
                    tvBorrowCountTop1.text = "${topThreeList[0].second[1]} Borrows"

                    tvTitleTop2.text = topThreeList[1].second[0]
                    tvBorrowCountTop2.text = "${topThreeList[1].second[1]} Borrows"

                    tvTitleTop3.text = topThreeList[2].second[0]
                    tvBorrowCountTop3.text = "${topThreeList[2].second[1]} Borrows"
                }

                Helper.dismissDialog()
            }.addOnFailureListener { exception ->
                Log.e(Constants.TAG, "Error getting top items: ", exception)
            }
    }

    //  Used for displaying toast messages
    private fun displayToastMessage(activity: Activity, message: String?) {
        Toast.makeText(
            activity,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}