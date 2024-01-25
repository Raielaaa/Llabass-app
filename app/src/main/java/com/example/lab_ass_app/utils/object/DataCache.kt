package com.example.lab_ass_app.utils.`object`

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.lab_ass_app.R
import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.BorrowModel
import com.example.lab_ass_app.ui.main.student_teacher.home.HomeViewModel
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeAdapter
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeModelDisplay
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeModelLive
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.models.ItemFullInfoModel
import com.example.lab_ass_app.utils.models.PopularModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DataCache {
    var seeAllData: ArrayList<HomeModelLive> = ArrayList()
    var rvItemsForTools: ArrayList<HomeModelDisplay> = ArrayList()
    var rvItemsForChemicals: ArrayList<HomeModelDisplay> = ArrayList()
    @SuppressLint("StaticFieldLeak")
    lateinit var adapter: HomeAdapter

    var borrowedItemsInfo: ArrayList<BorrowModel> = ArrayList()

    var topThreeList: MutableList<PopularModel> = mutableListOf()
    var topThreeListFullInfo: MutableList<ItemFullInfoModel> = mutableListOf()
    var isToolSelected: Boolean = true

    var filter: String = ""
    var rvBorrowInfoProfile: ArrayList<BorrowModel> = ArrayList()

    fun cacheDataForCategory(
        category: String,
        homeViewModel: HomeViewModel,
        rvListItems: RecyclerView,
        hostFragment: Fragment,
        fireStore: FirebaseFirestore
    ) {
        val isToolsCategory = (category == "Tools")
        val cachedItems = if (isToolsCategory) rvItemsForTools else rvItemsForChemicals

        if (cachedItems.isEmpty()) {
            homeViewModel.initListItemRV(rvListItems, hostFragment, category)
        } else {
            val firebaseStorage = FirebaseStorage.getInstance()

            adapter = HomeAdapter(
                hostFragment.requireActivity(),
                fireStore,
                firebaseStorage,
                hostFragment.requireContext()
            ) {
                Helper.displayCustomDialog(
                    hostFragment.requireActivity(),
                    R.layout.custom_dialog_loading
                )
            }

            adapter.setItem(cachedItems)
            rvListItems.adapter = adapter
        }
    }

    suspend fun cacheDataForCategories(
        rvListItems: RecyclerView,
        hostFragment: Fragment,
        fireStore: FirebaseFirestore,
        storage: FirebaseStorage
    ) {
        Log.d("MyTag", "cacheDataForCategories: check-1")
        Helper.displayCustomDialog(
            hostFragment.requireActivity(),
            R.layout.custom_dialog_loading
        )

        val cachedItems = rvItemsForTools

        if (cachedItems.isEmpty()) {
            fireStore.collection("labass-app-item-description")
                .whereEqualTo("modelCategory", "Chemicals")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result.documents
                        val mergedItemInfoList = arrayListOf<HomeModelDisplay>()


                        // Filter out items with "Unavailable" status
                        val availableItems = documents.filter {
                            it.get("modelStatus").toString() == "Available"
                        }

                        val itemCounts = availableItems.groupBy { it.get("modelName").toString() }
                            .mapValues { it.value.size }

                        val itemCountsWithUnavailable = documents.groupBy { it.get("modelName").toString() }
                            .mapValues { it.value.size }

                        for (itemName in itemCounts.keys.union(itemCountsWithUnavailable.keys)) {
                            val availableCount = itemCounts[itemName] ?: 0
                            val unavailableCount = itemCountsWithUnavailable[itemName] ?: 0

                            val imageLink = documents.find { it.get("modelName").toString() == itemName }
                                ?.get("modelImageLink").toString()

                            val mergedItemInfo = HomeModelDisplay(
                                itemName,
                                availableCount,
                                unavailableCount,
                                imageLink
                            )
                            mergedItemInfoList.add(mergedItemInfo)
                        }


                        rvItemsForChemicals.clear()
                        rvItemsForChemicals.addAll(mergedItemInfoList)

                        Log.d("MyTag", "cacheDataForCategories: check-2")
                        Helper.displayCustomDialog(
                            hostFragment.requireActivity(),
                            R.layout.custom_dialog_loading
                        )
                        fireStore.collection("labass-app-item-description")
                            .whereEqualTo("modelCategory", "Tools")
                            .get()
                            .addOnCompleteListener { taskTools ->
                                if (taskTools.isSuccessful) {
                                    val documentsTools = taskTools.result.documents
                                    val mergedItemInfoListTools = arrayListOf<HomeModelDisplay>()


                                    // Filter out items with "Unavailable" status
                                    val availableItemsTools = documentsTools.filter {
                                        it.get("modelStatus").toString() == "Available"
                                    }

                                    val itemCountsTools = availableItemsTools.groupBy { it.get("modelName").toString() }
                                        .mapValues { it.value.size }

                                    val itemCountsWithUnavailableTools = documentsTools.groupBy { it.get("modelName").toString() }
                                        .mapValues { it.value.size }

                                    for (itemName in itemCountsTools.keys.union(itemCountsWithUnavailableTools.keys)) {
                                        val availableCount = itemCountsTools[itemName] ?: 0
                                        val unavailableCount = itemCountsWithUnavailableTools[itemName] ?: 0

                                        val imageLink = documentsTools.find { it.get("modelName").toString() == itemName }
                                            ?.get("modelImageLink").toString()

                                        val mergedItemInfo = HomeModelDisplay(
                                            itemName,
                                            availableCount,
                                            unavailableCount,
                                            imageLink
                                        )
                                        mergedItemInfoListTools.add(mergedItemInfo)
                                    }

                                    rvItemsForTools.clear()
                                    rvItemsForTools.addAll(mergedItemInfoListTools)

                                    val adapter = HomeAdapter(hostFragment.requireActivity(), fireStore, storage, hostFragment.requireContext()) {
                                        Helper.displayCustomDialog(
                                            hostFragment.requireActivity(),
                                            R.layout.custom_dialog_loading
                                        )
                                    }
                                    adapter.setItem(mergedItemInfoListTools)
                                    rvListItems.adapter = adapter

                                    Log.d("MyTag", "cacheDataForCategories: check-3")
                                    Helper.dismissDialog()
                                }
                            }.addOnFailureListener { exception ->
                                endTaskNotify(exception, hostFragment)
                            }
                    }
                }.addOnFailureListener { exception ->
                    endTaskNotify(exception, hostFragment)
                }
        } else {
            val firebaseStorage = FirebaseStorage.getInstance()

            adapter = HomeAdapter(
                hostFragment.requireActivity(),
                fireStore,
                firebaseStorage,
                hostFragment.requireContext()
            ) {
                Helper.displayCustomDialog(
                    hostFragment.requireActivity(),
                    R.layout.custom_dialog_loading
                )
            }

            adapter.setItem(cachedItems)
            rvListItems.adapter = adapter
        }
    }

    // Function to handle the end of tasks and notify the user about errors
    private fun endTaskNotify(exception: Exception, hostFragment: Fragment) {
        // Display an error message, log the exception, and dismiss the loading dialog
        displayToastMessage("Error: ${exception.localizedMessage}", hostFragment)
        Log.e(Constants.TAG, "isCredentialsWithUserTypeExist: ${exception.message}")
    }

    private fun displayToastMessage(message: String, hostFragment: Fragment) {
        Toast.makeText(
            hostFragment.requireContext(),
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}