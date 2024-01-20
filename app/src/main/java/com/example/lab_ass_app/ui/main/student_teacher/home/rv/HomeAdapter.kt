package com.example.lab_ass_app.ui.main.student_teacher.home.rv

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.RvHomeBinding
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.`object`.Helper
import com.example.lab_ass_app.utils.models.ItemFullInfoModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class HomeAdapter(
    private val activity: Activity,
    private val fireStore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val context: Context,
    private val clickedListener: () -> Unit
) : RecyclerView.Adapter<HomeAdapter.HomeAdapterViewModel>() {
    private val collections: ArrayList<HomeModelDisplay> = ArrayList()
    @SuppressLint("NotifyDataSetChanged")
    fun setItem(list: ArrayList<HomeModelDisplay>) {
        collections.clear()
        collections.addAll(list)
        this.notifyDataSetChanged()
    }

    fun getItem() : ArrayList<HomeModelDisplay> {
        return collections
    }

    inner class HomeAdapterViewModel(private val binding: RvHomeBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(items: HomeModelDisplay, clickedListener: () -> Unit) {
            binding.apply {
                tvHomeName.text = items.itemName
                tvHomeCode.text = "${items.availableCount} / ${items.unavailableCount}"

                val count = items.availableCount
                tvHomeBorrowCount.text = if (count == 0) "AVAILABLE" else "UNAVAILABLE"
                if (count <= 0) {
                    tvHomeBorrowCount.text = "OUT OF STOCK"
                    tvHomeBorrowCount.setTextColor(ContextCompat.getColor(context, R.color.Theme_light_red))
                } else {
                    tvHomeBorrowCount.text = "AVAILABLE"
                    tvHomeBorrowCount.setTextColor(ContextCompat.getColor(context, R.color.Theme_green))
                }

                cvHomeStatus.setCardBackgroundColor(
                    ContextCompat.getColor(context, if (count > 0) R.color.Theme_green else R.color.Theme_light_red)
                )

                val gsReference = storage.getReferenceFromUrl("gs://labass-app.appspot.com/${items.imageLink}.jpg")
                Glide.with(context)
                    .load(gsReference)
                    .into(ivHomeImage)
            }
            binding.root.setOnClickListener {
                clickedListener()

                displayDialogForSelectedItem(
                    items,
                    items.itemName.split("-")[1].replace(" ", ""),
                    if (items.availableCount == 0) "Unavailable" else "Available",
                    items.itemName,
                    items.availableCount.toString(),
                    items.unavailableCount.toString()
                )
            }
        }

        private fun displayDialogForSelectedItem(
            items: HomeModelDisplay,
            sizeLocal: String,
            statusLocal: String,
            nameLocal: String,
            availableCountLocal: String,
            unavailableCountLocal: String
        ) {
            fireStore.collection("labass-app-item-description")
                .document(items.imageLink.split("/")[1])
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val imageLink = documentSnapshot.get("modelImageLink").toString()
                    val itemBorrowCount = documentSnapshot.get("modelBorrowCount").toString()
                    val itemCode = documentSnapshot.get("modelCode").toString()
                    val itemCategory = documentSnapshot.get("modelCategory").toString()
                    val itemDescription = documentSnapshot.get("modelDescription").toString()
                    val itemSize = documentSnapshot.get("modelSize").toString()

                    val itemsToBeShown = ItemFullInfoModel(
                        imageLink,
                        "${nameLocal.split("-")[0]}- $sizeLocal",
                        itemSize,
                        itemCategory,
                        statusLocal,
                        itemDescription,
                        "$availableCountLocal / $unavailableCountLocal",
                        itemCode
                    )

                    //  Dismisses the loading dialog first before showing the selected item dialog
                    Helper.dismissDialog()

                    Log.d(Constants.TAG, "displayDialogForSelectedItem: $itemsToBeShown")
                    Helper.displayCustomDialog(
                        activity,
                        R.layout.selected_item_dialog,
                        itemsToBeShown,
                        storage
                    )
                }.addOnFailureListener { exception ->
                    endTaskNotify(exception, activity)
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapterViewModel {
        val binding: RvHomeBinding = RvHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeAdapterViewModel(binding)
    }

    override fun getItemCount(): Int {
        return collections.size
    }

    override fun onBindViewHolder(holder: HomeAdapterViewModel, position: Int) {
        holder.bind(collections[position], clickedListener)
    }

    // Function to handle the end of tasks and notify the user about errors
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
}