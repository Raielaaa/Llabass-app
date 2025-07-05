package com.example.lab_ass_app.ui.main.admin.borrow_list

import androidx.fragment.app.viewModels
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
import com.example.lab_ass_app.databinding.FragmentSelectedBorrowInfoBinding
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.`object`.Helper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class SelectedBorrowInfoFragment : Fragment() {
    @Inject
    @Named("FirebaseAuth.Instance")
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var fireStore: FirebaseFirestore

    private lateinit var binding: FragmentSelectedBorrowInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectedBorrowInfoBinding.inflate(inflater, container, false)

        initComponents()

        return binding.root
    }

    private fun initComponents() {
        getItemInfo()
        initBottomNavDrawer()
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getItemInfo() {
        Helper.displayCustomDialog(
            requireActivity(),
            R.layout.custom_dialog_loading
        )

        fireStore.collection("labass-app-borrow-log")
            .whereEqualTo("modelItemName", arguments?.getString("item_name"))
            .whereEqualTo("modelItemCode", arguments?.getString("item_code"))
            .whereEqualTo("modelBorrowDateTime", arguments?.getString("borrow_date"))
            .whereEqualTo("modelBorrowDeadlineDateTime", arguments?.getString("borrow_deadline"))
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]

                    val matchedItemName = document.getString("modelItemName")
                    val matchedItemCode = document.getString("modelItemCode")
                    val matchedItemCategory = document.getString("modelItemCategory")
                    val matchedItemSize = document.getString("modelItemSize")

                    val matchedItemDate = document.getString("modelBorrowDateTime")
                    val matchedItemDeadline = document.getString("modelBorrowDeadlineDateTime")

                    binding.apply {
                        tvItemNumber.text = matchedItemCode
                        tvName.text = matchedItemName
                        tvCategory.text = matchedItemCategory
                        tvSize.text = matchedItemSize

                        tvStatus.text = getBorrowStatusFromDeadline(matchedItemDeadline.toString())
                        tvStudentNumber.text = document.getString("modelLRN")
                        tvBorrowDate.text = document.getString("modelBorrowDateTime")
                        tvBorrowDeadline.text = document.getString("modelBorrowDeadlineDateTime")
                        tvBorrowRole.text = document.getString("modelUserType")
                        tvEmail.text = document.getString("modelEmail")
                    }

                    fireStore.collection("labass-app-item-description")
                        .document(matchedItemCode.toString())
                        .get()
                        .addOnSuccessListener {
                            val gsReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://labass-app.appspot.com/${it.getString("modelImageLink")}.jpg")
                            Glide.with(requireContext())
                                .load(gsReference)
                                .into(binding.imageView14)

                            binding.tvDescription.text = it.getString("modelDescription")

                            Helper.dismissDialog()
                        }
                } else {
                    Toast.makeText(requireContext(), "No exact match found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getBorrowStatusFromDeadline(borrowDeadline: String): String {
        // Clean up irregular spaces and casing in the input
        fun cleanDateString(input: String): String {
            return input
                .replace(Regex("[\\u00A0\\u2007\\u202F]"), " ")
                .replace(Regex("\\s+:\\s+"), ":")
                .replace(Regex("\\s+"), " ")
                .replace("pm", "PM", true)
                .replace("am", "AM", true)
                .trim()
        }

        val cleanDeadline = cleanDateString(borrowDeadline)
        val formatter = DateTimeFormatter.ofPattern("M/d/yyyy, hh:mm a", Locale.ENGLISH)

        Log.d("test_log", "Clean deadline date: $cleanDeadline")

        return try {
            val deadlineDateTime = LocalDateTime.parse(cleanDeadline, formatter)
            Log.d("test_log", "Parsed deadlineDateTime = $deadlineDateTime")

            val now = LocalDateTime.now()
            Log.d("test_log", "Current time = $now")

            if (deadlineDateTime.isBefore(now)) "PAST-DUE" else "ONGOING"
        } catch (e: Exception) {
            Log.e(
                "DateParseError",
                "Failed to parse deadline: '$cleanDeadline'\nError: ${e.message}"
            )
            "Invalid date format"
        }
    }

    private fun initBottomNavDrawer() {
        binding.apply {
            btmDrawerHome.setOnClickListener {
                try {
                    findNavController().navigate(R.id.action_selectedBorrowInfoFragment_to_homeAdminFragment)
                } catch(e: Exception) {
                    Log.w("test_error", e.message.toString());
                }
            }
            btmDrawerPrivacy.setOnClickListener {
                try {
                    findNavController().navigate(R.id.action_selectedBorrowInfoFragment_to_adminPrivacyFragment)
                } catch(e: Exception) {
                    Log.w("test_error", e.message.toString());
                }
            }
            btmDrawerUser.setOnClickListener {
                try {
                    findNavController().navigate(R.id.action_selectedBorrowInfoFragment_to_adminProfileFragment)
                } catch(e: Exception) {
                    Log.w("test_error", e.message.toString());
                }
            }
            btmDrawerList.setOnClickListener {
                try {
                    findNavController().navigate(R.id.action_selectedBorrowInfoFragment_to_borrowListFragment)
                } catch(e: Exception) {
                    Log.w("test_error", e.message.toString());
                }
            }
        }
    }
}