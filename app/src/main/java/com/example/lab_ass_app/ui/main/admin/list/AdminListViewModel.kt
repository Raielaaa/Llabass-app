package com.example.lab_ass_app.ui.main.admin.list;

import androidx.lifecycle.ViewModel;
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeModelLive
import com.example.lab_ass_app.utils.`object`.DataCache

class AdminListViewModel : ViewModel() {
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
            if (item.itemNameModel.lowercase().contains(inputtedText.lowercase()) ||
                item.itemCodeModel.lowercase().contains(inputtedText.lowercase())
                ) {
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
}