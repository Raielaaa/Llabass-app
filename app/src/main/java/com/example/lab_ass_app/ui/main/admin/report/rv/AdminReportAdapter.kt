package com.example.lab_ass_app.ui.main.admin.report.rv

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.RvReportBinding
import com.example.lab_ass_app.ui.main.admin.util.DataCache
import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.report.ReportModel
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.`object`.Helper
import com.google.firebase.firestore.FirebaseFirestore

class AdminReportAdapter(
    private val firebaseFireStore: FirebaseFirestore,
    private val activity: Activity
) : RecyclerView.Adapter<AdminReportAdapter.AdminReportViewHolder>() {

    private val collections: ArrayList<ReportModel> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun setList(collection: ArrayList<ReportModel>) {
        collections.apply {
            clear()
            addAll(collection)
        }
        notifyDataSetChanged()
    }

    inner class AdminReportViewHolder(private val binding: RvReportBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ReportModel) {
            binding.apply {
                tvDateSend.text = data.modelCurrentDateTime
                tvReportTitle.text = data.modelReportTitle
                tvSenderName.text = data.modelItemName
                tvItemContent.text = data.modelReportContent
                tvItemCategory.text = data.modelItemCategory
                tvItemNumber.text = data.modelItemCode
                tvItemSize.text = data.modelItemSize
                tvSenderInfo.text = "${data.modelSenderEmail} - ${data.modelSenderLRN} - ${data.modelSenderUserType}"
            }

            binding.btnResolved.setOnClickListener {
                Helper.displayCustomDialog(
                    activity,
                    R.layout.custom_dialog_loading
                )

                firebaseFireStore.collection("labass-app-report-log")
                    .document("${data.modelSenderLRN}-${data.modelSenderUserType}-${data.modelCurrentDateTime}")
                    .delete()
                    .addOnSuccessListener {
                        Helper.dismissDialog()

                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            collections.removeAt(position)
                            notifyItemRemoved(position)
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(
                            activity,
                            "Error: ${exception.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(Constants.TAG, "bind: ${exception.message}")
                        Helper.dismissDialog()
                    }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminReportViewHolder {
        val binding = RvReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdminReportViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return collections.size
    }

    override fun onBindViewHolder(holder: AdminReportViewHolder, position: Int) {
        holder.bind(collections[position])
    }
}