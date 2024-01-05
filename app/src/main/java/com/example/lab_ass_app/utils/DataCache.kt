package com.example.lab_ass_app.utils

import android.annotation.SuppressLint
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.lab_ass_app.R
import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.BorrowModel
import com.example.lab_ass_app.ui.main.student_teacher.home.HomeViewModel
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeAdapter
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeModelLive
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object DataCache {
    val rvItemsForTools: ArrayList<HomeModelLive> = ArrayList()
    val rvItemsForChemicals: ArrayList<HomeModelLive> = ArrayList()
    @SuppressLint("StaticFieldLeak")
    lateinit var adapter: HomeAdapter

    var borrowedItemsInfo: ArrayList<BorrowModel> = ArrayList()

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
}