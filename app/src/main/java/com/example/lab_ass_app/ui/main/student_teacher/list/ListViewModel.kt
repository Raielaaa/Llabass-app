package com.example.lab_ass_app.ui.main.student_teacher.list

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentHomeBinding
import com.example.lab_ass_app.databinding.FragmentListBinding
import com.example.lab_ass_app.ui.main.student_teacher.home.HomeViewModel
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeAdapter
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeModelLive
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.`object`.DataCache
import com.example.lab_ass_app.utils.`object`.Helper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ListViewModel : ViewModel() {
    private var listToBeDisplayedToRV: ArrayList<HomeModelLive> = ArrayList()

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
        listToBeFiltered: ArrayList<HomeModelLive>,
        inputtedText: String,
        isToolsSelected: Boolean
    ) {
        listToBeDisplayedToRV.clear()
        for (item in listToBeFiltered) {
            if (item.itemNameModel.lowercase().contains(inputtedText.lowercase())) {
                listToBeDisplayedToRV.add(item)
            }
        }

        DataCache.adapter.setItem(listToBeDisplayedToRV)

        if (inputtedText.isEmpty()) {
            if (isToolsSelected) {
                DataCache.adapter.setItem(DataCache.rvItemsForTools)
            } else {
                DataCache.adapter.setItem(DataCache.rvItemsForChemicals)
            }
        }
    }

    fun initRefreshButtonAndTV(
        activity: Activity,
        context: Context,
        tvCurrentDate: TextView,
        btnListRefresh: Button,
        listFragment: Fragment,
        homeViewModel: HomeViewModel,
        binding: FragmentListBinding,
        fireStore: FirebaseFirestore
    ) {
        //  Init date for TextView
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(Calendar.getInstance().time)
        val dateToBeDisplayed = "Updated as of $formattedDate"
        tvCurrentDate.text = dateToBeDisplayed

        //  Init refresh button
        btnListRefresh.setOnClickListener {
            refreshList(
                activity,
                context,
                listFragment,
                fireStore,
                binding
            )
        }
    }

    fun refreshList(
        activity: Activity,
        context: Context,
        listFragment: Fragment,
        fireStore: FirebaseFirestore,
        fragmentListBinding: FragmentListBinding? = null,
        fragmentHomeBinding: FragmentHomeBinding? = null
    ) {
        //  Display loading dialog
        Helper.dismissDialog()
        Helper.displayCustomDialog(
            activity,
            R.layout.custom_dialog_loading
        )

        fireStore.collection("labass-app-item-description")
            .whereEqualTo("modelCategory", "Tools")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val retrievedToolsInfo: ArrayList<HomeModelLive> = ArrayList()

                for (document in querySnapshot) {
                    val modelBorrowCount = document.get("modelBorrowCount").toString()
                    val modelCode = document.get("modelCode").toString()
                    val modelImageLink = document.get("modelImageLink").toString()
                    val modelName = document.get("modelName").toString()
                    val modelStatus = document.get("modelStatus").toString()

                    retrievedToolsInfo.add(
                        HomeModelLive(
                            modelImageLink,
                            modelName,
                            modelCode,
                            modelBorrowCount,
                            modelStatus
                        )
                    )
                }
                DataCache.rvItemsForTools = retrievedToolsInfo

                fireStore.collection("labass-app-item-description")
                    .whereEqualTo("modelCategory", "Chemicals")
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val retrievedChemicalsInfo: ArrayList<HomeModelLive> = ArrayList()

                        for (document in querySnapshot) {
                            val modelImageLink = document.getString("modelImageLink")!!
                            val modelName = document.getString("modelName")!!
                            val modelCode = document.getString("modelCode")!!
                            val modelBorrowCount = document.getString("modelBorrowCount")!!
                            val modelStatus = document.getString("modelStatus")!!

                            retrievedChemicalsInfo.add(
                                HomeModelLive(
                                    modelImageLink,
                                    modelName,
                                    modelCode,
                                    modelBorrowCount,
                                    modelStatus
                                )
                            )
                        }
                        DataCache.rvItemsForChemicals = retrievedChemicalsInfo

                        updateListForListFragment(activity, listFragment, fireStore, fragmentListBinding)
                        updateListForHomeFragment(activity, listFragment, fireStore, fragmentHomeBinding)

                        Helper.dismissDialog()
                    }.addOnFailureListener { exception ->
                        Helper.dismissDialog()
                        Log.e(Constants.TAG, "initRefreshButtonAndTV: ${exception.message}")
                        Toast.makeText(
                            context,
                            "An error occurred: ${exception.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }.addOnFailureListener { exception ->
                Helper.dismissDialog()
                Log.e(Constants.TAG, "initRefreshButtonAndTV: ${exception.message}")
                Toast.makeText(
                    context,
                    "An error occurred: ${exception.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        Toast.makeText(
            listFragment.requireContext(),
            "List items are updated.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateListForHomeFragment(
        activity: Activity,
        hostFragment: Fragment,
        fireStore: FirebaseFirestore,
        fragmentHomeBinding: FragmentHomeBinding?
    ) {
        if (DataCache.isToolSelected) {
            val firebaseStorage = FirebaseStorage.getInstance()
            val toolsAdapter = HomeAdapter(
                activity,
                fireStore,
                firebaseStorage,
                hostFragment.requireContext()
            ) {
                Helper.displayCustomDialog(
                    activity,
                    R.layout.custom_dialog_loading
                )
            }

            fragmentHomeBinding?.rvListItems?.adapter = toolsAdapter
            toolsAdapter.setItem(DataCache.rvItemsForTools)
        } else {
            val firebaseStorage = FirebaseStorage.getInstance()
            val toolsAdapter = HomeAdapter(
                activity,
                fireStore,
                firebaseStorage,
                hostFragment.requireContext()
            ) {
                Helper.displayCustomDialog(
                    activity,
                    R.layout.custom_dialog_loading
                )
            }

            fragmentHomeBinding?.rvListItems?.adapter = toolsAdapter
            toolsAdapter.setItem(DataCache.rvItemsForChemicals)
        }
    }

    private fun updateListForListFragment(
        activity: Activity,
        hostFragment: Fragment,
        fireStore: FirebaseFirestore,
        fragmentListBinding: FragmentListBinding?
    ) {
        if (DataCache.isToolSelected) {
            val firebaseStorage = FirebaseStorage.getInstance()
            val toolsAdapter = HomeAdapter(
                activity,
                fireStore,
                firebaseStorage,
                hostFragment.requireContext()
            ) {
                Helper.displayCustomDialog(
                    activity,
                    R.layout.custom_dialog_loading
                )
            }

            fragmentListBinding?.rvListListItem?.adapter = toolsAdapter
            toolsAdapter.setItem(DataCache.rvItemsForTools)
        } else {
            val firebaseStorage = FirebaseStorage.getInstance()
            val toolsAdapter = HomeAdapter(
                activity,
                fireStore,
                firebaseStorage,
                hostFragment.requireContext()
            ) {
                Helper.displayCustomDialog(
                    activity,
                    R.layout.custom_dialog_loading
                )
            }

            fragmentListBinding?.rvListListItem?.adapter = toolsAdapter
            toolsAdapter.setItem(DataCache.rvItemsForChemicals)
        }
    }
}