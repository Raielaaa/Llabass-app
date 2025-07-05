package com.example.lab_ass_app.ui.main.admin.borrow_list

import android.app.Activity
import android.content.Context
import android.health.connect.datatypes.units.Length
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.RvHomeBinding
import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.BorrowModel
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeModelDisplay
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.models.ItemFullInfoModel
import com.example.lab_ass_app.utils.`object`.Helper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BorrowModelAdapter(
    private val borrowList: List<BorrowModel>,
    private val hostFragment: Fragment,
    private val clickedListener: () -> Unit
) : RecyclerView.Adapter<BorrowModelAdapter.BorrowViewHolder>() {

    inner class BorrowViewHolder(val binding: RvHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        val storage = FirebaseStorage.getInstance()
        val fireStore = FirebaseFirestore.getInstance().collection("labass-app-item-description")

        fun bind(items: BorrowModel, clickedListener: () -> Unit) {
            binding.apply {
                tvHomeName.text = items.modelItemName
                tvHomeTitle.text = "Borrower: "
                tvHomeCode.text = items.modelEmail
                tvHomeSubtitle.text = "Borrow date: "
                tvHomeBorrowCount.text = items.modelBorrowDateTime

                cvHomeStatus.setCardBackgroundColor(
                    ContextCompat.getColor(hostFragment.requireContext(), if (isDateTimePast(items.modelBorrowDeadlineDateTime)) R.color.Theme_light_red else R.color.Theme_green)
                )

                fireStore.whereEqualTo("modelName", items.modelItemName)
                    .whereEqualTo("modelCategory", items.modelItemCategory)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            val imageLink = documents.documents.first().getString("modelImageLink")

                            val gsReference = storage.getReferenceFromUrl("gs://labass-app.appspot.com/$imageLink.jpg")
                            Glide.with(hostFragment.requireContext())
                                .load(gsReference)
                                .into(ivHomeImage)
                        }
                    }
            }
            binding.root.setOnClickListener {
                val bundle = bundleOf(
                    "item_name" to items.modelItemName,
                    "item_code" to items.modelItemCode,
                    "borrow_date" to items.modelBorrowDateTime,
                    "borrow_deadline" to items.modelBorrowDeadlineDateTime,
                    "borrower_id" to items.modelUserID
                )

                hostFragment.findNavController().navigate(R.id.action_borrowListFragment_to_selectedBorrowInfoFragment, bundle)
            }
//            binding.root.setOnClickListener {
//                clickedListener()
//
//                displayDialogForSelectedItem(
//                    items,
//                    items.itemName.split("-")[1].replace(" ", ""),
//                    if (items.availableCount == 0) "Unavailable" else "Available",
//                    items.itemName,
//                    items.availableCount.toString(),
//                    items.unavailableCount.toString()
//                )
//            }

//        private fun displayDialogForSelectedItem(
//            items: HomeModelDisplay,
//            sizeLocal: String,
//            statusLocal: String,
//            nameLocal: String,
//            availableCountLocal: String,
//            unavailableCountLocal: String
//        ) {
//            fireStore.collection("labass-app-item-description")
//                .document(items.imageLink.split("/")[1])
//                .get()
//                .addOnSuccessListener { documentSnapshot ->
//                    val imageLink = documentSnapshot.get("modelImageLink").toString()
//                    val itemBorrowCount = documentSnapshot.get("modelBorrowCount").toString()
//                    val itemCode = documentSnapshot.get("modelCode").toString()
//                    val itemCategory = documentSnapshot.get("modelCategory").toString()
//                    val itemDescription = documentSnapshot.get("modelDescription").toString()
//                    val itemSize = documentSnapshot.get("modelSize").toString()
//
//                    val itemsToBeShown = ItemFullInfoModel(
//                        imageLink,
//                        "${nameLocal.split("-")[0]}- $sizeLocal",
//                        itemSize,
//                        itemCategory,
//                        statusLocal,
//                        itemDescription,
//                        "$availableCountLocal / $unavailableCountLocal",
//                        itemCode
//                    )
//
//                    //  Dismisses the loading dialog first before showing the selected item dialog
//                    Helper.dismissDialog()
//
//                    Log.d(Constants.TAG, "displayDialogForSelectedItem: $itemsToBeShown")
//                    Helper.displayCustomDialog(
//                        activity,
//                        R.layout.selected_item_dialog,
//                        itemsToBeShown,
//                        storage
//                    )
//                }.addOnFailureListener { exception ->
//                    endTaskNotify(exception, activity)
//                }
        }
    }

    fun isDateTimePast(dateString: String): Boolean {
        val format = SimpleDateFormat("M/d/yyyy, hh : mm a", Locale.getDefault())
        format.isLenient = false

        return try {
            val inputDate = format.parse(dateString)
            val currentDate = Calendar.getInstance().time

            inputDate.before(currentDate)
        } catch (e: Exception) {
            false // or log the error
        }
    }

    private fun endTaskNotify(exception: Exception, activity: Activity) {
        // Display an error message, log the exception, and dismiss the loading dialog
        displayToastMessage("Error: ${exception.localizedMessage}", activity)
        Log.e(Constants.TAG, "isCredentialsWithUserTypeExist: ${exception.message}")
        Log.e(Constants.TAG, "isCredentialsWithUserTypeExist: ${exception}")
        Helper.dismissDialog()
    }

    // Display toast message
    private fun displayToastMessage(message: String, activity: Activity) {
        Toast.makeText(
            activity,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorrowViewHolder {
        val binding = RvHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BorrowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BorrowViewHolder, position: Int) {
        val item = borrowList[position]

        holder.bind(borrowList[position], clickedListener)
    }

    override fun getItemCount(): Int = borrowList.size
}
