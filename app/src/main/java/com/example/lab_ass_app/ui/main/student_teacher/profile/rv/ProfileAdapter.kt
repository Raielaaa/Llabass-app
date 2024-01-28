package com.example.lab_ass_app.ui.main.student_teacher.profile.rv

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.RvProfileBinding
import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.BorrowModel
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.`object`.Helper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileAdapter(
    private val context: Context,
    private val storage: FirebaseStorage,
    private val fireStore: FirebaseFirestore
) : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {
    inner class ProfileViewHolder(private val binding: RvProfileBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(itemInfo: BorrowModel) {
            binding.apply {
                tvProfileItemName.text = itemInfo.modelItemName
                tvProfileBorrowDate.text = itemInfo.modelBorrowDateTime
                tvProfileBorrowDeadline.text = itemInfo.modelBorrowDeadlineDateTime

                fireStore.collection("labass-app-item-description")
                    .document(itemInfo.modelItemCode)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        val imageLink = documentSnapshot.get("modelImageLink")

                        val gsReference = storage.getReferenceFromUrl("gs://labass-app.appspot.com/$imageLink.jpg")
                        Glide.with(context)
                            .load(gsReference)
                            .into(ivProfileImage)
                    }.addOnFailureListener { exception ->
                        Log.e(Constants.TAG, "ProfileAdapter: ", exception)
                        Toast.makeText(
                            context,
                            "An error occurred: ${exception.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                val timeDifferenceInMinutes = Helper.getBorrowTimeDifference(itemInfo.modelBorrowDateTime, itemInfo.modelBorrowDeadlineDateTime)
                tvProfileBorrowStatus.text = timeDifferenceInMinutes

                if (timeDifferenceInMinutes.toInt() >= 0) {
                    tvProfileBorrowStatus.text = "ON-BORROW"
                    tvProfileBorrowStatus.setTextColor(ContextCompat.getColor(context, R.color.Theme_green))
                    cvProfileStatus.setCardBackgroundColor(ContextCompat.getColor(context, R.color.Theme_green))
                } else {
                    tvProfileBorrowStatus.text = "PAST-DUE"
                    tvProfileBorrowStatus.setTextColor(ContextCompat.getColor(context, R.color.Theme_light_red))
                    cvProfileStatus.setCardBackgroundColor(ContextCompat.getColor(context, R.color.Theme_light_red))
                }
            }
        }
    }

    private val collection: ArrayList<BorrowModel> = ArrayList()
    @SuppressLint("NotifyDataSetChanged")
    fun setList(newList: ArrayList<BorrowModel>) {
        collection.apply {
            clear()
            addAll(newList)
        }
        notifyDataSetChanged()
    }

    fun getListSize() = collection.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val binding = RvProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProfileViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return collection.size
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(collection[position])
    }
}