package com.example.lab_ass_app.ui.main.student_teacher.profile

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentProfileBinding
import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.BorrowModel
import com.example.lab_ass_app.ui.main.student_teacher.profile.rv.ProfileAdapter
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.`object`.DataCache
import com.example.lab_ass_app.utils.`object`.Helper
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @Named("FirebaseFireStore.Instance")
    private val fireStore: FirebaseFirestore
) : ViewModel() {
    fun displayRealTimeProfileInfoToRV(
        hostFragment: Fragment,
        rvProfileList: RecyclerView,
        binding: FragmentProfileBinding
    ) {
        Helper.displayCustomDialog(
            hostFragment.requireActivity(),
            R.layout.custom_dialog_loading
        )
        DataCache.rvBorrowInfoProfile.clear()
        fireStore.collection("labass-app-borrow-log")
            .whereGreaterThanOrEqualTo(FieldPath.documentId(), DataCache.filter)
            .whereLessThan(FieldPath.documentId(), DataCache.filter + '\uF7FF')
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val modelBorrowDateTime = document.getString("modelBorrowDateTime")!!
                    val modelBorrowDeadlineDateTime = document.getString("modelBorrowDeadlineDateTime")!!
                    val modelEmail = document.getString("modelEmail")!!
                    val modelItemCategory = document.getString("modelItemCategory")!!
                    val modelItemCode = document.getString("modelItemCode")!!
                    val modelItemName = document.getString("modelItemName")!!
                    val modelItemSize = document.getString("modelItemSize")!!
                    val modelLRN = document.getString("modelLRN")!!
                    val modelUserID = document.getString("modelUserID")!!
                    val modelUserType = document.getString("modelUserType")!!

                    DataCache.rvBorrowInfoProfile.add(
                        BorrowModel(
                            modelEmail,
                            modelLRN,
                            modelUserType,
                            modelUserID,
                            modelItemCode,
                            modelItemName,
                            modelItemCategory,
                            modelItemSize,
                            modelBorrowDateTime,
                            modelBorrowDeadlineDateTime
                        )
                    )
                }
                initHomeUserStatusUI(binding, hostFragment)
                val adapter = ProfileAdapter(
                    hostFragment.requireContext(),
                    FirebaseStorage.getInstance(),
                    fireStore
                )

                adapter.setList(DataCache.rvBorrowInfoProfile)
                rvProfileList.adapter = adapter

                binding.apply {
                    if (adapter.getListSize() == 0) {
                        rvProfileList.visibility = View.INVISIBLE
                        ivNoDataFound.visibility = View.VISIBLE
                    } else {
                        rvProfileList.visibility = View.VISIBLE
                        ivNoDataFound.visibility = View.INVISIBLE
                    }
                }
                Helper.dismissDialog()
            }.addOnFailureListener { exception ->
                endTaskException(exception, hostFragment)
            }
    }

    private fun endTaskException(exception: Exception, hostFragment: Fragment) {
        Helper.dismissDialog()
        Toast.makeText(
            hostFragment.requireContext(),
            "Error: ${exception.localizedMessage}",
            Toast.LENGTH_LONG
        ).show()
        Log.e(Constants.TAG, "endTaskException-profileViewModel: ${exception.message}", )
    }

    @SuppressLint("SetTextI18n")
    private fun initHomeUserStatusUI(binding: FragmentProfileBinding, hostFragment: Fragment) {
        val borrowCount = DataCache.borrowedItemsInfo.size

        binding.apply {
            when (borrowCount) {
                3 -> {
                    tvHomePending.text = "$borrowCount/3"
                    tvHomeStatus.apply {
                        text = "Limit reached!"
                        setTextColor(ContextCompat.getColor(hostFragment.requireContext(), R.color.Theme_light_red))
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