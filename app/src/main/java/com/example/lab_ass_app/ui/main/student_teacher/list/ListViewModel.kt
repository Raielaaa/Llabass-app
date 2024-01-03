package com.example.lab_ass_app.ui.main.student_teacher.list

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.lab_ass_app.ui.main.student_teacher.home.HomeViewModel
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeModelLive
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.DataCache
import com.google.firebase.firestore.FirebaseFirestore
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
        tvCurrentDate: TextView,
        btnListRefresh: Button,
        listFragment: Fragment,
        homeViewModel: HomeViewModel,
        rvListListItem: RecyclerView,
        fireStore: FirebaseFirestore
    ) {
        //  Init date for TextView
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(Calendar.getInstance().time)
        val dateToBeDisplayed = "Updated as of $formattedDate"

        tvCurrentDate.text = dateToBeDisplayed

        //  Init refresh button
        btnListRefresh.setOnClickListener {
            DataCache.cacheDataForCategory(
                "Tools",
                homeViewModel,
                rvListListItem,
                listFragment,
                fireStore
            )

            Toast.makeText(
                listFragment.requireContext(),
                "List items are updated.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}