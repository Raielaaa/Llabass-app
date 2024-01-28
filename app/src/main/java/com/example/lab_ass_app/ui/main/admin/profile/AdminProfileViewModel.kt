package com.example.lab_ass_app.ui.main.admin.profile;

import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView
import com.example.lab_ass_app.R
import com.example.lab_ass_app.ui.main.admin.profile.rv.AdminProfileAdapter
import com.example.lab_ass_app.ui.main.admin.util.DataCache
import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.BorrowModel
import com.example.lab_ass_app.utils.`object`.Helper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AdminProfileViewModel @Inject constructor(
    @Named("FirebaseFireStore.Instance")
    private val firebaseFireStore: FirebaseFirestore
) : ViewModel() {
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    fun initRV(rvProfileList: RecyclerView, adminProfileFragment: AdminProfileFragment, tvProfileBorrowCount: TextView) {
        if (DataCache.profileList.isEmpty()) {
            Helper.displayCustomDialog(
                adminProfileFragment.requireActivity(),
                R.layout.custom_dialog_loading
            )

            firebaseFireStore.collection("labass-app-borrow-log")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val itemsToBeDisplayed: ArrayList<BorrowModel> = ArrayList()
                    var borrowCount: Int = 0

                    for (document in querySnapshot.documents) {
                        borrowCount++
                        itemsToBeDisplayed.add(
                            BorrowModel(
                                document.get("modelEmail").toString(),
                                document.get("modelLRN").toString(),
                                document.get("modelUserType").toString(),
                                document.get("modelUserID").toString(),
                                document.get("modelItemCode").toString(),
                                document.get("modelItemName").toString(),
                                document.get("modelItemCategory").toString(),
                                document.get("modelItemSize").toString(),
                                document.get("modelBorrowDateTime").toString(),
                                document.get("modelBorrowDeadlineDateTime").toString()
                            )
                        )
                    }
                    val adapter = AdminProfileAdapter(adminProfileFragment.requireContext(), storage, firebaseFireStore)
                    adapter.setList(itemsToBeDisplayed)
                    rvProfileList.adapter = adapter

                    DataCache.profileList = itemsToBeDisplayed
                    DataCache.profileBorrowCount = borrowCount
                    tvProfileBorrowCount.text = DataCache.profileBorrowCount.toString()


                    Helper.dismissDialog()
                }
        } else {
            val adapter = AdminProfileAdapter(adminProfileFragment.requireContext(), storage, firebaseFireStore)
            adapter.setList(DataCache.profileList)
            rvProfileList.adapter = adapter
        }
    }

    fun initSearchFunction(inputtedText: String, hostFragment: Fragment, rvProfileList: RecyclerView) {
        val itemToBeFiltered: ArrayList<BorrowModel> = DataCache.profileList
        val itemsToBeDisplayed: ArrayList<BorrowModel> = ArrayList()

        for (data in itemToBeFiltered) {
            if (data.modelEmail.lowercase().contains(inputtedText) ||
                data.modelLRN.lowercase().contains(inputtedText) ||
                data.modelUserType.lowercase().contains(inputtedText) ||
                data.modelItemName.lowercase().contains(inputtedText) ||
                data.modelItemSize.lowercase().contains(inputtedText)
                ) {
                itemsToBeDisplayed.add(data)
            }
        }

        val adapter = AdminProfileAdapter(hostFragment.requireContext(), storage, firebaseFireStore)
        adapter.setList(itemsToBeDisplayed)
        rvProfileList.adapter = adapter

        if (inputtedText.isEmpty()) {
            adapter.setList(DataCache.profileList)
            rvProfileList.adapter = adapter
        }
    }
}