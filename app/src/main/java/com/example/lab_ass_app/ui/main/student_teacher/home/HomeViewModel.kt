package com.example.lab_ass_app.ui.main.student_teacher.home

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.example.lab_ass_app.databinding.FragmentHomeBinding
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.Helper
import com.example.lab_ass_app.utils.PopularModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HomeViewModel @Inject constructor(
    @Named("FirebaseFireStore.Instance")
    private val firebaseFireStore: FirebaseFirestore,
    @Named("FirebaseStorage.Instance")
    private val firebaseStorage: StorageReference
) : ViewModel() {
    private val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    fun takeQR(activity: Activity) {
        //  Method for starting the camera
        Helper.takeQR(activity)
    }

    fun initTopBorrowDisplay(binding: FragmentHomeBinding, context: Context) {
        firebaseFireStore.collection("labass-app-item-description")
            .orderBy("modelBorrowCount", Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val topThreeList: MutableList<PopularModel> = mutableListOf()

                querySnapshot.forEachIndexed { _, queryDocumentSnapshot ->
                    topThreeList.add(
                        PopularModel(
                            queryDocumentSnapshot.get("modelImageLink").toString(),
                            queryDocumentSnapshot.get("modelName").toString(),
                            queryDocumentSnapshot.get("modelBorrowCount").toString()
                        )
                    )
                }

                binding.apply {
                    tvTitleTop1.text = topThreeList[0].itemName
                    tvBorrowCountTop1.text = "${topThreeList[0].borrowCount} Borrows"
                    val gsReference1 = storage.getReferenceFromUrl("gs://labass-app.appspot.com/${topThreeList[0].imageLink}.jpg")
                    Glide.with(context)
                        .load(gsReference1)
                        .into(ivTop1)

                    tvTitleTop2.text = topThreeList[1].itemName
                    tvBorrowCountTop2.text = "${topThreeList[1].borrowCount} Borrows"
                    val gsReference2 = storage.getReferenceFromUrl("gs://labass-app.appspot.com/${topThreeList[1].imageLink}.jpg")
                    Glide.with(context)
                        .load(gsReference2)
                        .into(ivTop2)

                    tvTitleTop3.text = topThreeList[2].itemName
                    tvBorrowCountTop3.text = "${topThreeList[2].borrowCount} Borrows"
                    val gsReference3 = storage.getReferenceFromUrl("gs://labass-app.appspot.com/${topThreeList[2].imageLink}.jpg")
                    Glide.with(context)
                        .load(gsReference3)
                        .into(ivTop3)
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