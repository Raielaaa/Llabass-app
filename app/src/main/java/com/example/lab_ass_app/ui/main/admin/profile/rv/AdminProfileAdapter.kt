package com.example.lab_ass_app.ui.main.admin.profile.rv

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentAdminProfileBinding
import com.example.lab_ass_app.databinding.RvProfileAdminBinding
import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.BorrowModel
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.`object`.Helper
import com.google.firebase.storage.FirebaseStorage

class AdminProfileAdapter (
    private val context: Context,
    private val storage: FirebaseStorage,
) : RecyclerView.Adapter<AdminProfileAdapter.AdminProfileViewHolder>() {

    private val collections: ArrayList<BorrowModel> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun setList(collection: ArrayList<BorrowModel>) {
        collections.apply {
            clear()
            addAll(collection)
        }
        Log.d(Constants.TAG, "setList: $collections")
        notifyDataSetChanged()
    }

    inner class AdminProfileViewHolder(private val binding: RvProfileAdminBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: BorrowModel) {
            binding.apply {
                tvProfileBorrower.text = "${data.modelUserType} - ${data.modelEmail}"
                tvProfileBorrowerNumber.text = data.modelLRN
                tvProfileBorrowDate.text = data.modelBorrowDateTime
                tvProfileBorrowDeadline.text = data.modelBorrowDeadlineDateTime
                tvProfileItemName.text = data.modelItemName
                tvItemSize.text = data.modelItemSize

                val gsReference = storage.getReferenceFromUrl("gs://labass-app.appspot.com/${data.modelItemCategory.lowercase()}/${data.modelItemCode}.jpg")
                Glide.with(context)
                    .load(gsReference)
                    .into(ivProfileImage)

                Log.e(Constants.TAG, "bind: ${data.modelBorrowDeadlineDateTime}")
                val timeDifference = getTimeDifference(data.modelBorrowDeadlineDateTime)
                if (timeDifference < 0) {
                    tvProfileRemarks.text = "DELINQUENT"
                    tvProfileRemarks.setTextColor(ContextCompat.getColor(context, R.color.Theme_light_red))
                    cvProfileStatus.setCardBackgroundColor(ContextCompat.getColor(context, R.color.Theme_light_red))
                } else {
                    tvProfileRemarks.text = "NONE"
                    tvProfileRemarks.setTextColor(ContextCompat.getColor(context, R.color.black))
                    cvProfileStatus.setCardBackgroundColor(ContextCompat.getColor(context, R.color.Theme_green))
                }
            }
        }

        private fun getTimeDifference(modelBorrowDeadlineDateTime: String): Int {
            return Helper.getBorrowTimeDifference(null, modelBorrowDeadlineDateTime).toInt()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminProfileViewHolder {
        val binding: RvProfileAdminBinding = RvProfileAdminBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return AdminProfileViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return collections.size
    }

    override fun onBindViewHolder(holder: AdminProfileViewHolder, position: Int) {
        holder.bind(collections[position])
    }
}