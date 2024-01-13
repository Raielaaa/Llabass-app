package com.example.lab_ass_app.ui.main.admin.home

import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.lab_ass_app.R
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HomeAdminViewModel @Inject constructor(
    @Named("FirebaseFireStore.Instance")
    private val firebaseFireStore: FirebaseFirestore
) : ViewModel() {
    fun initUserTopStatus(homeAdminFragment: HomeAdminFragment, cvHomeStatus: CardView) {
        firebaseFireStore.collection("labass-app-borrow-log")
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    cvHomeStatus.setCardBackgroundColor(ContextCompat.getColor(homeAdminFragment.requireContext(), R.color.Theme_green))
                } else {
                    cvHomeStatus.setCardBackgroundColor(ContextCompat.getColor(homeAdminFragment.requireContext(), R.color.Theme_light))
                }
            }
    }
}
