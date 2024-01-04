package com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentReportBinding
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.Helper
import com.example.lab_ass_app.utils.ItemInfoModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class ReportFragment : Fragment() {
    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var fireStore: FirebaseFirestore

    private lateinit var binding: FragmentReportBinding
    private var storage: FirebaseStorage = FirebaseStorage.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentReportBinding.inflate(inflater, container, false)

        initBackButton()
        initDisplayedInfo()

        return binding.root
    }

    private fun initDisplayedInfo() {
        val scannedItemCode = arguments?.getString("ScannedItemInfo")
        
        retrievedInfoFromFireStore(scannedItemCode)
    }

    private fun retrievedInfoFromFireStore(scannedItemCode: String?) {
        Helper.displayCustomDialog(
            requireActivity(),
            R.layout.custom_dialog_loading
        )

        fireStore.collection("labass-app-item-description")
            .document(scannedItemCode.toString())
            .get()
            .addOnSuccessListener { documentSnapshot ->
                binding.apply {
                    tvReportName.text = documentSnapshot.get("modelName").toString()
                    tvReportCode.text = documentSnapshot.get("modelCode").toString()
                    tvReportCategory.text = documentSnapshot.get("modelCategory").toString()

                    val gsReference = storage.getReferenceFromUrl("gs://labass-app.appspot.com/${documentSnapshot.get("modelImageLink")}.jpg")
                    Glide.with(requireContext())
                        .load(gsReference)
                        .into(ivImage)
                }
                Helper.dismissDialog()
            }.addOnFailureListener { exception ->
                Log.e(Constants.TAG, "retrievedInfoFromFireStore: ${exception.message}", )
                Toast.makeText(
                    requireContext(),
                    "An error occurred: ${exception.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun initBackButton() {
        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_reportFragment_to_homeFragment)
        }
    }
}