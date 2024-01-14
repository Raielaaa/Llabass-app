package com.example.lab_ass_app.ui.main.admin.list;

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView
import com.example.lab_ass_app.R
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeAdapter
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeModelDisplay
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeModelLive
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.`object`.DataCache
import com.example.lab_ass_app.utils.`object`.Helper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AdminListViewModel @Inject constructor(
    @Named("FirebaseFireStore.Instance")
    private val firebaseFireStore: FirebaseFirestore
) : ViewModel() {
    private var listToBeDisplayedToRV: ArrayList<HomeModelDisplay> = ArrayList()
    private var firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()

    fun initSearchFunction(
        inputtedText: String,
        isToolsSelected: Boolean
    ) {
        if (isToolsSelected) {
            val listToBeFiltered = DataCache.rvItemsForTools
            filterItems(listToBeFiltered, inputtedText, true)
        } else {
            val listToBeFiltered = DataCache.rvItemsForChemicals
            filterItems(listToBeFiltered, inputtedText, false)
        }
    }

    private fun filterItems(
        listToBeFiltered: ArrayList<HomeModelDisplay>,
        inputtedText: String,
        isToolsSelected: Boolean
    ) {
        listToBeDisplayedToRV.clear()
//        for (item in listToBeFiltered) {
//            if (item.itemNameModel.lowercase().contains(inputtedText.lowercase()) ||
//                item.itemCodeModel.lowercase().contains(inputtedText.lowercase())
//                ) {
//                listToBeDisplayedToRV.add(item)
//            }
//        }

        DataCache.adapter.setItem(listToBeDisplayedToRV)

        if (inputtedText.isEmpty()) {
            if (isToolsSelected) {
                DataCache.adapter.setItem(DataCache.rvItemsForTools)
            } else {
                DataCache.adapter.setItem(DataCache.rvItemsForChemicals)
            }
        }
    }

    fun refreshRV(
        toolsSelected: Boolean,
        hostFragment: AdminListFragment,
        rvListItem: RecyclerView
    ) {
        Helper.displayCustomDialog(
            hostFragment.requireActivity(),
            R.layout.custom_dialog_loading
        )

        if (toolsSelected) {
            displayInfoToRV("Tools", hostFragment, rvListItem)
        } else {
            displayInfoToRV("Chemicals", hostFragment, rvListItem)
        }
    }

    private fun displayInfoToRV(selectedCategory: String, hostFragment: AdminListFragment, rvListItem: RecyclerView) {
        firebaseFireStore.collection("labass-app-item-description")
            .whereEqualTo("modelCategory", selectedCategory)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val retrievedInfo: ArrayList<HomeModelLive> = ArrayList()

                for (document in querySnapshot) {
                    val modelBorrowCount = document.get("modelBorrowCount").toString()
                    val modelCode = document.get("modelCode").toString()
                    val modelImageLink = document.get("modelImageLink").toString()
                    val modelName = document.get("modelName").toString()
                    val modelStatus = document.get("modelStatus").toString()

                    retrievedInfo.add(
                        HomeModelLive(
                            modelImageLink,
                            modelName,
                            modelCode,
                            modelBorrowCount,
                            modelStatus
                        )
                    )
                }

                com.example.lab_ass_app.ui.main.admin.util.DataCache.toolsList = retrievedInfo

                val adapter = HomeAdapter(
                    hostFragment.requireActivity(),
                    firebaseFireStore,
                    firebaseStorage,
                    hostFragment.requireContext()
                ) {
                    Helper.displayCustomDialog(
                        hostFragment.requireActivity(),
                        R.layout.custom_dialog_loading
                    )
                }
//                adapter.setItem(retrievedInfo)
//                rvListItem.adapter = adapter
//
//                if (selectedCategory == "Tools") {
//                    DataCache.rvItemsForTools = retrievedInfo
//                } else {
//                    DataCache.rvItemsForChemicals = retrievedInfo
//                }
                Helper.dismissDialog()
            }.addOnFailureListener { exception ->
                Log.e(Constants.TAG, "refreshRV: ", exception)
                Toast.makeText(
                    hostFragment.requireContext(),
                    "Error: ${exception.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()

                Helper.dismissDialog()
            }
    }
}