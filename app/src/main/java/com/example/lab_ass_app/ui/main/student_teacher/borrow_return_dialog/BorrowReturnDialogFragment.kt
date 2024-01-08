package com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog

import android.graphics.Bitmap
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.text.color
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.ViewModelProvider
import com.example.lab_ass_app.MainActivity
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentBorrowReturnDialogBinding
import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.date_time.DateTimeSelectedListener
import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.date_time.SetDateDialogFragment
import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.date_time.SetTimeDialogFragment
import com.example.lab_ass_app.ui.main.student_teacher.home.HomeViewModel
import com.example.lab_ass_app.ui.main.student_teacher.list.ListViewModel
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.Helper
import com.example.lab_ass_app.utils.models.ItemInfoModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class BorrowReturnDialogFragment(
    private val bitmap: Bitmap?,
    private val mainActivity: MainActivity,
    private val itemInfoModel: ItemInfoModel,
    private val currentUserLRN: String,
    private val currentUserEmail: String,
    private val currentUserType: String,
    private val currentUserID: String
) : BottomSheetDialogFragment(), DateTimeSelectedListener {
    //  Firebase fireStore
    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var firebaseFireStore: FirebaseFirestore

    private lateinit var binding: FragmentBorrowReturnDialogBinding
    private lateinit var borrowReturnDialogViewModel: BorrowReturnDialogViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var listViewModel: ListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBorrowReturnDialogBinding.inflate(inflater, container, false)
        borrowReturnDialogViewModel = ViewModelProvider(this@BorrowReturnDialogFragment)[BorrowReturnDialogViewModel::class.java]
        homeViewModel = ViewModelProvider(this@BorrowReturnDialogFragment)[HomeViewModel::class.java]
        listViewModel = ViewModelProvider(this@BorrowReturnDialogFragment)[ListViewModel::class.java]

        initViews()

        return binding.root
    }

    private fun initViews() {
        initDisplayComponents()
        initSpinner()
        initReportTextView()
        initReportTv()
        binding.ivImage.setImageBitmap(bitmap)
        initDateTimeChooser()
        initProceedCancelButton()
    }

    private fun initProceedCancelButton() {
        binding.apply {
            btnCancel.setOnClickListener {
                this@BorrowReturnDialogFragment.dismiss()
            }
            btnBRProceed.setOnClickListener {
                if (spUser2.selectedItem == "BORROW") {
                    if (tvDate.text != "Set Date" && tvTime.text != "Set Time") {
                        Helper.displayCustomDialog(
                            requireActivity(),
                            R.layout.custom_dialog_loading
                        )

                        borrowReturnDialogViewModel.checkBorrowAvailability(
                            listViewModel,
                            homeViewModel,
                            requireActivity(),
                            "$currentUserLRN-$currentUserEmail",
                            this@BorrowReturnDialogFragment,
                            currentUserEmail,
                            currentUserLRN,
                            currentUserType,
                            currentUserID,
                            itemInfoModel.modelCode,
                            itemInfoModel.modelName,
                            itemInfoModel.modelCategory,
                            itemInfoModel.modelSize,
                            getCurrentDateTime(),
                            "${tvDate.text}, ${tvTime.text}"
                        )
                    } else {
                        displayToastMessage("Error: Borrow Date and Borrow Time must not be empty")
                    }
                } else if (spUser2.selectedItem == "RETURN") {
                    Helper.displayCustomDialog(
                        requireActivity(),
                        listViewModel,
                        this@BorrowReturnDialogFragment,
                        R.layout.fragment_return_item,
                        firebaseFireStore,
                        currentUserLRN,
                        currentUserEmail,
                        itemInfoModel,
                        homeViewModel
                    )
                }
            }
        }
    }

    private fun getCurrentDateTime(): String {
        //  Get current date and time
        val currentDateTime = LocalDateTime.now()

        //  Date-time format
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy, hh:mm a")

        return currentDateTime.format(formatter)
    }

    private fun initDisplayComponents() {
        binding.apply {
            tvItemName.text = itemInfoModel.modelName
            tvName.text = currentUserEmail
            tvCategory.text = itemInfoModel.modelCategory
            tvDescription.text = itemInfoModel.modelDescription
            tvLRN.text = "$currentUserLRN - $currentUserType"

            if (itemInfoModel.modelStatus.contains("Unavailable")) {
                tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.Theme_red))
            } else if (itemInfoModel.modelStatus == "Available") {
                tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.Theme_green))
            }
            tvStatus.text = itemInfoModel.modelStatus.split(":")[0]
        }
    }

    private fun initDateTimeChooser() {
        binding.apply {
            cvSetDate.setOnClickListener {
                SetDateDialogFragment(this@BorrowReturnDialogFragment).show(parentFragmentManager, "SetDate_Dialog")
            }
            cvSetTime.setOnClickListener {
                SetTimeDialogFragment(this@BorrowReturnDialogFragment).show(parentFragmentManager, "SetTime_Dialog")
            }
        }
    }

    private fun initReportTv() {
        binding.tvReport.setOnClickListener {
            findNavController().navigate(R.id.reportFragment, bundleOf("ScannedItemInfo" to itemInfoModel.modelCode))
            this.dismiss()
        }
    }

    private fun initReportTextView() {
        binding.tvReport.text = SpannableStringBuilder()
            .append("Encountered a problem? ")
            .color(ContextCompat.getColor(requireContext(), R.color.Theme_color_main)) { append("REPORT NOW!") }
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            val bottomSheet = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
    }

    private fun initSpinner() {
        binding.apply {
            val spinner: Spinner = spUser2
            val spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.borrow_return_option,
                android.R.layout.simple_spinner_dropdown_item
            )

            spinner.adapter = spinnerAdapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedItem = parent?.getItemAtPosition(position).toString()

                    if (selectedItem == "RETURN") {
                        cvSetDate.isEnabled = false
                        setViewAndChildrenEnabled(cvSetDate, false)

                        cvSetTime.isEnabled = false
                        setViewAndChildrenEnabled(cvSetTime, false)
                    } else {
                        cvSetDate.isEnabled = true
                        setViewAndChildrenEnabled(cvSetDate, true)

                        cvSetTime.isEnabled = true
                        setViewAndChildrenEnabled(cvSetTime, true)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //  Nothing
                }
            }
        }
    }

    private fun setViewAndChildrenEnabled(view: View, enabled: Boolean) {
        view.isEnabled = enabled
        if (view is ViewGroup) {
            for (index in 0 until view.childCount) {
                val child = view.getChildAt(index)
                setViewAndChildrenEnabled(child, enabled)
            }
        }
    }

    override fun onDateSelected(selectedDate: String) {
        binding.tvDate.text = selectedDate
    }

    override fun onTimeSelected(selectedTime: String) {
        binding.tvTime.text = selectedTime
    }

    // Function to handle the end of tasks and notify the user about errors
    private fun endTaskNotify(exception: Exception) {
        // Display an error message, log the exception, and dismiss the loading dialog
        displayToastMessage("Error: ${exception.localizedMessage}")
        Log.e(Constants.TAG, "BorrowReturnDialogFragment: ${exception.message}")
        Helper.dismissDialog()
        this.dismiss()
    }

    // Display toast message
    private fun displayToastMessage(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}