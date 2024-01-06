package com.example.lab_ass_app.ui.main.student_teacher.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentHomeBinding
import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.BorrowModel
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeAdapter
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeModelLive
import com.example.lab_ass_app.ui.main.student_teacher.home.see_all.SeeAllDialog
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.DataCache
import com.example.lab_ass_app.utils.Helper
import com.example.lab_ass_app.utils.models.ItemFullInfoModel
import com.example.lab_ass_app.utils.models.PopularModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@Suppress("NAME_SHADOWING")
@HiltViewModel
class HomeViewModel @Inject constructor(
    @Named("FirebaseFireStore.Instance")
    private val firebaseFireStore: FirebaseFirestore,
    @Named("FirebaseAuth.Instance")
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    private val fullInfoForTopItems: ArrayList<ItemFullInfoModel> = ArrayList()
    private var topThreeList: MutableList<PopularModel> = mutableListOf()

    fun takeQR(activity: Activity) {
        //  Method for starting the camera
        Helper.takeQR(activity)
    }

    fun initTopBorrowDisplay(binding: FragmentHomeBinding, context: Context, hostFragment: Fragment, btnSeeAll: Button? = null) {
        Log.d(Constants.TAG, "initTopBorrowDisplay-out: ${DataCache.topThreeList}")

        if (DataCache.topThreeList.isNotEmpty()) {
            binding.apply {
                displayItemToTopBorrow(tvTitleTop1, tvBorrowCountTop1, DataCache.topThreeList[0], context, ivTop1)
                displayItemToTopBorrow(tvTitleTop2, tvBorrowCountTop2, DataCache.topThreeList[1], context, ivTop2)
                displayItemToTopBorrow(tvTitleTop3, tvBorrowCountTop3, DataCache.topThreeList[2], context, ivTop3)
            }
        } else {
            firebaseFireStore.collection("labass-app-item-description")
                .orderBy("modelBorrowCount", Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    querySnapshot.forEachIndexed { _, queryDocumentSnapshot ->
                        val imageLink = queryDocumentSnapshot.get("modelImageLink").toString()
                        val itemName = queryDocumentSnapshot.get("modelName").toString()
                        val itemBorrowCount = queryDocumentSnapshot.get("modelBorrowCount").toString()
                        val itemCode = queryDocumentSnapshot.get("modelCode").toString()
                        val itemCategory = queryDocumentSnapshot.get("modelCategory").toString()
                        val itemDescription = queryDocumentSnapshot.get("modelDescription").toString()
                        val itemSize = queryDocumentSnapshot.get("modelSize").toString()
                        val itemStatus = queryDocumentSnapshot.get("modelStatus").toString()

                        val dataToBeAdded = PopularModel(
                            imageLink,
                            itemName,
                            itemBorrowCount,
                            itemCode,
                            itemStatus
                        )

                        Log.d(Constants.TAG, "initTopBorrowDisplay-in: $dataToBeAdded")

                        topThreeList.add(dataToBeAdded)
                        DataCache.topThreeList.add(dataToBeAdded)

                        val fullInfoToBeAdded = ItemFullInfoModel(
                            imageLink,
                            itemName,
                            itemSize,
                            itemCategory,
                            itemStatus,
                            itemDescription,
                            itemBorrowCount,
                            itemCode
                        )
                        fullInfoForTopItems.add(fullInfoToBeAdded)
                        DataCache.topThreeListFullInfo.add(fullInfoToBeAdded)
                    }

                    initSeeAllButtonRV(hostFragment, btnSeeAll)

                    binding.apply {
                        displayItemToTopBorrow(tvTitleTop1, tvBorrowCountTop1, topThreeList[0], context, ivTop1)
                        displayItemToTopBorrow(tvTitleTop2, tvBorrowCountTop2, topThreeList[1], context, ivTop2)
                        displayItemToTopBorrow(tvTitleTop3, tvBorrowCountTop3, topThreeList[2], context, ivTop3)
                    }
                }.addOnFailureListener { exception ->
                    endTaskNotify(exception, hostFragment)
                }
        }
    }

    fun initTopBorrowDisplaySelected(binding: FragmentHomeBinding, activity: Activity) {
        binding.apply {
            cvTop1.setOnClickListener {
                displaySelectedItemCustomDialog(DataCache.topThreeListFullInfo[0], activity)
            }
            cvTop2.setOnClickListener {
                displaySelectedItemCustomDialog(DataCache.topThreeListFullInfo[1], activity)
            }
            cvTop3.setOnClickListener {
                displaySelectedItemCustomDialog(DataCache.topThreeListFullInfo[2], activity)
            }
        }
    }

    private fun displaySelectedItemCustomDialog(fullInfoModel: ItemFullInfoModel, activity: Activity) {
        Helper.displayCustomDialog(
            activity,
            R.layout.selected_item_dialog,
            fullInfoModel,
            storage
        )
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

    fun initListItemRV(rvListItems: RecyclerView, hostFragment: Fragment, category: String) {
        Helper.displayCustomDialog(
            hostFragment.requireActivity(),
            R.layout.custom_dialog_loading
        )

        firebaseFireStore.collection("labass-app-item-description")
            .whereEqualTo("modelCategory", category)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result.documents
                    val retrievedData: ArrayList<HomeModelLive> = ArrayList()

                    for (document in documents) {
                        val imageLink = document.get("modelImageLink").toString()
                        val itemName = document.get("modelName").toString()
                        val itemCode = document.get("modelCode").toString()
                        val itemBorrowCount = document.get("modelBorrowCount").toString()
                        val itemStatus = document.get("modelStatus").toString()

                        retrievedData.add(
                            HomeModelLive(
                                imageLink,
                                itemName,
                                itemCode,
                                itemBorrowCount,
                                itemStatus
                            )
                        )
                    }

                    if (category == "Tools") {
                        DataCache.rvItemsForTools.clear()
                        DataCache.rvItemsForTools.addAll(retrievedData)
                    } else if (category == "Chemicals") {
                        DataCache.rvItemsForChemicals.clear()
                        DataCache.rvItemsForChemicals.addAll(retrievedData)
                    }

                    val adapter = HomeAdapter(hostFragment.requireActivity(), firebaseFireStore, storage, hostFragment.requireContext()) {
                        Helper.displayCustomDialog(
                            hostFragment.requireActivity(),
                            R.layout.custom_dialog_loading
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

    private fun initSeeAllButtonRV(hostFragment: Fragment, seeAllButton: Button?) {
        val storage = FirebaseStorage.getInstance()
        val seeAllAdapter = HomeAdapter(
            hostFragment.requireActivity(),
            firebaseFireStore,
            storage,
            hostFragment.requireContext()
        ) {
            Helper.displayCustomDialog(
                hostFragment.requireActivity(),
                R.layout.selected_item_dialog
            )
        }

        seeAllAdapter.setItem(
            arrayListOf(
                HomeModelLive(
                    topThreeList[0].imageLink,
                    topThreeList[0].itemName,
                    topThreeList[0].itemCode,
                    topThreeList[0].borrowCount,
                    topThreeList[0].itemStatus
                ),
                HomeModelLive(
                    topThreeList[1].imageLink,
                    topThreeList[1].itemName,
                    topThreeList[1].itemCode,
                    topThreeList[1].borrowCount,
                    topThreeList[1].itemStatus
                ),
                HomeModelLive(
                    topThreeList[2].imageLink,
                    topThreeList[2].itemName,
                    topThreeList[2].itemCode,
                    topThreeList[2].borrowCount,
                    topThreeList[2].itemStatus
                ),
            )
        )

        seeAllButton?.setOnClickListener {
            SeeAllDialog(seeAllAdapter).show(hostFragment.parentFragmentManager, "SeeAllDialog")
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

    fun retrieveBorrowedItemInfoFromDB(
        hostFragment: Fragment,
        binding: FragmentHomeBinding
    ) {
        val userID = firebaseAuth.currentUser?.uid

        firebaseFireStore.collection("labass-app-user-account-initial")
            .document(userID.toString())
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val userLRN = documentSnapshot.get("userLRNModel")
                val userEmail = documentSnapshot.get("userEmailModel")
                val filter = "$userLRN-$userEmail"

                firebaseFireStore.collection("labass-app-borrow-log")
                    .whereGreaterThanOrEqualTo(FieldPath.documentId(), filter)
                    .whereLessThan(FieldPath.documentId(), filter + '\uF7FF')
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        val retrievedData: ArrayList<BorrowModel> = ArrayList()

                        for (document in documentSnapshot.documents) {
                            retrievedData.add(
                                BorrowModel(
                                    modelEmail = document.get("modelEmail").toString(),
                                    modelLRN = document.get("modelLRN").toString(),
                                    modelUserType = document.get("modelUserType").toString(),
                                    modelUserID = document.get("modelUserID").toString(),
                                    modelItemCode = document.get("modelItemCode").toString(),
                                    modelItemName = document.get("modelItemName").toString(),
                                    modelItemCategory = document.get("modelItemCategory").toString(),
                                    modelItemSize = document.get("modelItemSize").toString(),
                                    modelBorrowDateTime = document.get("modelBorrowDateTime").toString(),
                                    modelBorrowDeadlineDateTime = document.get("modelModelDeadlineDateTime").toString()
                                )
                            )
                        }

                        DataCache.borrowedItemsInfo = retrievedData
                        initHomeUserStatusUI(binding, hostFragment)
                    }.addOnFailureListener { exception ->
                        endTaskNotify(exception, hostFragment)
                    }
            }.addOnFailureListener { exception ->
                endTaskNotify(exception, hostFragment)
            }
    }

    @SuppressLint("SetTextI18n")
    private fun initHomeUserStatusUI(binding: FragmentHomeBinding, hostFragment: Fragment) {
        val borrowCount = DataCache.borrowedItemsInfo.size

        binding.apply {
            when (borrowCount) {
                3 -> {
                    tvHomePending.text = "$borrowCount/3"
                    tvHomeStatus.apply {
                        text = "Limit reached!"
                        setTextColor(ContextCompat.getColor(hostFragment.requireContext(), R.color.Theme_red))
                    }
                    cvHomeStatus.setCardBackgroundColor(ContextCompat.getColor(hostFragment.requireContext(), R.color.Theme_green))
                }
                in 1..2 -> {
                    tvHomePending.text = "$borrowCount/3"
                    tvHomeStatus.apply {
                        text = "On-borrow"
                        setTextColor(ContextCompat.getColor(hostFragment.requireContext(), R.color.Theme_green))
                    }
                    cvHomeStatus.setCardBackgroundColor(ContextCompat.getColor(hostFragment.requireContext(), R.color.Theme_green))
                }
                else -> {
                    tvHomePending.text = "$borrowCount/3"
                    tvHomeStatus.apply {
                        text = "Available"
                        setTextColor(ContextCompat.getColor(hostFragment.requireContext(), R.color.Theme_light))
                    }
                    cvHomeStatus.setCardBackgroundColor(ContextCompat.getColor(hostFragment.requireContext(), R.color.Theme_light))
                }
            }
        }
    }
}