package com.example.lab_ass_app.ui.main.student_teacher.home.see_all

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.RvSeeAllBinding
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeModelLive
import com.google.firebase.storage.FirebaseStorage

class SeeAllAdapter (
    private val context: Context,
    private val storage: FirebaseStorage
) : RecyclerView.Adapter<SeeAllAdapter.SeeAllViewHolder>() {
    private val collections: ArrayList<HomeModelLive> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun setList(collection: ArrayList<HomeModelLive>) {
        collections.apply {
            clear()
            addAll(collection)
        }
        notifyDataSetChanged()
    }

    inner class SeeAllViewHolder(private val binding: RvSeeAllBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(items: HomeModelLive) {
            binding.apply {
                tvHomeName.text = items.itemNameModel
                tvHomeCode.text = items.itemCodeModel
                tvHomeBorrowCount.text = items.itemBorrowCountModel

                if (items.itemStatusModel == "Available") {
                    cvHomeStatus.setCardBackgroundColor(ContextCompat.getColor(context, R.color.Theme_green))
                } else {
                    cvHomeStatus.setCardBackgroundColor(ContextCompat.getColor(context, R.color.Theme_light_red))
                }

                val gsReference = storage.getReferenceFromUrl("gs://labass-app.appspot.com/${items.imageLink}.jpg")
                Glide.with(context)
                    .load(gsReference)
                    .into(ivHomeImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeeAllViewHolder {
        val binding: RvSeeAllBinding = RvSeeAllBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SeeAllViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return collections.size
    }

    override fun onBindViewHolder(holder: SeeAllViewHolder, position: Int) {
        holder.bind(collections[position])
    }
}