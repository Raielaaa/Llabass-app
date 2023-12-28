package com.example.lab_ass_app.ui.main.student_teacher.home.rv

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.RvHomeBinding
import com.google.firebase.storage.FirebaseStorage

class HomeAdapter(
    private val storage: FirebaseStorage,
    private val context: Context,
    private val clickedListener: () -> Unit
) : RecyclerView.Adapter<HomeAdapter.HomeAdapterViewModel>() {
    private val collections: ArrayList<HomeModelLive> = ArrayList()
    @SuppressLint("NotifyDataSetChanged")
    fun setItem(list: ArrayList<HomeModelLive>) {
        collections.clear()
        collections.addAll(list)
        this.notifyDataSetChanged()
    }

    fun getItem() : ArrayList<HomeModelLive> {
        return collections
    }

    inner class HomeAdapterViewModel(private val binding: RvHomeBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(items: HomeModelLive, clickedListener: () -> Unit) {
            binding.apply {
                tvHomeName.text = items.itemNameModel
                tvHomeCode.text = items.itemCodeModel
                tvHomeBorrowCount.text = "${items.itemBorrowCountModel} Borrows"
                cvHomeStatus.setCardBackgroundColor(
                    ContextCompat.getColor(context, if (items.itemStatusModel == "Available") R.color.Theme_green else R.color.Theme_light)
                )

                val gsReference = storage.getReferenceFromUrl("gs://labass-app.appspot.com/${items.imageLink}.jpg")
                Glide.with(context)
                    .load(gsReference)
                    .into(ivHomeImage)
            }
            binding.root.setOnClickListener {
                clickedListener()
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
}