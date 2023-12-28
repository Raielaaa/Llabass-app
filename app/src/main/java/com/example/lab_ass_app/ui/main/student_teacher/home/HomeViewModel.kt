package com.example.lab_ass_app.ui.main.student_teacher.home

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentHomeBinding
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeAdapter
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeModelLive
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
    private val firebaseFireStore: FirebaseFirestore
) : ViewModel() {
    private val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    fun takeQR(activity: Activity) {
        //  Method for starting the camera
        Helper.takeQR(activity)
    }

    fun initTopBorrowDisplay(binding: FragmentHomeBinding, context: Context, hostFragment: Fragment) {
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
                    displayItemToTopBorrow(tvTitleTop1, tvBorrowCountTop1, topThreeList[0], context, ivTop1)
                    displayItemToTopBorrow(tvTitleTop2, tvBorrowCountTop2, topThreeList[1], context, ivTop2)
                    displayItemToTopBorrow(tvTitleTop3, tvBorrowCountTop3, topThreeList[2], context, ivTop3)
                }
            }.addOnFailureListener { exception ->
                endTaskNotify(exception, hostFragment)
            }
    }

    private fun displayItemToTopBorrow(
        tvTitle: TextView,
        tvBorrowCount: TextView,
        topList: PopularModel,
        context: Context,
        image: ImageView
    ) {
        tvTitle.text = topList.itemName
        tvBorrowCount.text = "${topList.borrowCount} Borrows"
        val gsReference = storage.getReferenceFromUrl("gs://labass-app.appspot.com/${topList.imageLink}.jpg")
        Glide.with(context)
            .load(gsReference)
            .into(image)
    }

    fun initListItemRV(rvListItems: RecyclerView, hostFragment: Fragment) {
        Helper.displayCustomDialog(
            hostFragment.requireActivity(),
            R.layout.custom_dialog_loading
        )

        firebaseFireStore.collection("labass-app-item-description")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result.documents
                    val retrievedData: ArrayList<HomeModelLive> = ArrayList()

                    for (document in documents) {
                        retrievedData.add(
                            HomeModelLive(
                                document.get("modelImageLink").toString(),
                                document.get("modelName").toString(),
                                document.get("modelCode").toString(),
                                document.get("modelBorrowCount").toString(),
                                document.get("modelStatus").toString()
                            )
                        )
                    }
                    val adapter = HomeAdapter(storage, hostFragment.requireContext()) {
                        Helper.displayCustomDialog(
                            hostFragment.requireActivity(),
                            R.layout.selected_item_dialog
                        )
                    }
                    adapter.setItem(retrievedData)
                    rvListItems.adapter = adapter

                    Helper.dismissDialog()
                }
            }.addOnFailureListener { exception ->
                endTaskNotify(exception, hostFragment)
            }
    }

    // Function to handle the end of tasks and notify the user about errors
    private fun endTaskNotify(exception: Exception, hostFragment: Fragment) {
        // Display an error message, log the exception, and dismiss the loading dialog
        displayToastMessage("Error: ${exception.localizedMessage}", hostFragment)
        Log.e(Constants.TAG, "isCredentialsWithUserTypeExist: ${exception.message}")
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