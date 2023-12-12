package com.example.lab_ass_app.ui.main.student_teacher.home.rv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lab_ass_app.databinding.RvHomeBinding

class HomeAdapter() : RecyclerView.Adapter<HomeAdapter.HomeAdapterViewModel>() {
    private val collections: ArrayList<HomeModel> = ArrayList()
    @SuppressLint("NotifyDataSetChanged")
    fun setItem(list: ArrayList<HomeModel>) {
        collections.clear()
        collections.addAll(list)
        this.notifyDataSetChanged()
    }

    fun getItem() : ArrayList<HomeModel> {
        return collections
    }

    inner class HomeAdapterViewModel(private val binding: RvHomeBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(items: HomeModel) {
            binding.apply {

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
        holder.bind(collections[position])
    }
}