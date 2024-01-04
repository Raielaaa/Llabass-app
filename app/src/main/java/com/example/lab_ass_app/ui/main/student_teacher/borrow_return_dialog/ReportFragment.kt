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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class ReportFragment : Fragment() {
    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var fireStore: FirebaseFirestore

    @Inject
    @Named("FirebaseAuth.Instance")
    lateinit var auth: FirebaseAuth

    private lateinit var binding: FragmentReportBinding
    private var storage: FirebaseStorage = FirebaseStorage.getInstance()

    //  Item data
    private lateinit var reportTitle: String
    private lateinit var reportContent: String
    private lateinit var senderEmail: String
    private lateinit var senderID: String
    private lateinit var senderLRN: String
    private lateinit var senderUserType: String
    private lateinit var itemCategory: String
    private lateinit var itemCode: String
    private lateinit var itemName: String
    private lateinit var itemSize: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentReportBinding.inflate(inflater, container, false)

        Helper.dismissDialog()
        initBackButton()
        initDisplayedInfo()
        initSubmitButton()

        return binding.root
    }

    private fun initSubmitButton() {
        binding.apply {
            btnReportSubmit.setOnClickListener {
                if (etReportTitle.text.toString().isNotEmpty() || etReportContent.text.toString().isNotEmpty()) {
                    retrieveCurrentUserInfo()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "All fields are required",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun retrieveCurrentUserInfo() {
        reportTitle = binding.etReportTitle.text.toString()
        reportContent = binding.etReportContent.text.toString()

        Helper.displayCustomDialog(
            requireActivity(),
            R.layout.custom_dialog_loading
        )
        val userID = auth.currentUser!!.uid
        fireStore.collection("labass-app-user-account-initial")
            .document(userID)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                senderEmail = documentSnapshot.get("userEmailModel").toString()
                senderID = userID
                senderLRN = documentSnapshot.get("userLRNModel").toString()
                senderUserType = documentSnapshot.get("userTypeModel").toString()

                insertReportToFireStore()
            }.addOnFailureListener { exception ->
                Log.e(Constants.TAG, "insertTitleAndDescriptionToDB: ${exception.message}", )
                Toast.makeText(
                    requireContext(),
                    "An error occurred: ${exception.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun insertReportToFireStore() {
        val itemToBeInserted = ReportModel(
            senderEmail,
            senderID,
            senderLRN,
            senderUserType,
            itemName,
            itemCategory,
            itemCode,
            itemSize,
            reportTitle,
            reportContent,
            getCurrentDateTime()
        )

        fireStore.collection("labass-app-report-log")
            .document("$senderLRN-$senderUserType-${getCurrentDateTime()}")
            .set(itemToBeInserted)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Your report has been saved successfully",
                    Toast.LENGTH_SHORT
                ).show()

                Helper.dismissDialog()
                findNavController().navigate(R.id.action_reportFragment_to_homeFragment)
            }.addOnFailureListener { exception ->
                Log.e(Constants.TAG, "insertReportToFireStore: ${exception.message}", )
                Toast.makeText(
                    requireContext(),
                    "An error occurred: ${exception.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
                Helper.dismissDialog()
            }
    }

    private fun getCurrentDateTime(): String {
        val simpleDateFormatForReport = SimpleDateFormat("MMMM dd, yyyy, hh:mm:ss a", Locale.getDefault())
        val calendar = Calendar.getInstance()

        return simpleDateFormatForReport.format(calendar.time)
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
                    itemName = documentSnapshot.get("modelName").toString()
                    itemCode = documentSnapshot.get("modelCode").toString()
                    itemCategory = documentSnapshot.get("modelCategory").toString()
                    itemSize = documentSnapshot.get("modelSize").toString()

                    tvReportName.text = itemName
                    tvReportCode.text = itemCode
                    tvReportCategory.text = itemCategory

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