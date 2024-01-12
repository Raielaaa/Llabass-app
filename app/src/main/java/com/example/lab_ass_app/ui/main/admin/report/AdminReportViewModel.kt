package com.example.lab_ass_app.ui.main.admin.report;

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView
import com.example.lab_ass_app.R
import com.example.lab_ass_app.ui.main.admin.report.rv.AdminReportAdapter
import com.example.lab_ass_app.ui.main.admin.util.DataCache
import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.report.ReportModel
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.`object`.Helper
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AdminReportViewModel @Inject constructor(
    @Named("FirebaseFireStore.Instance")
    private val firebaseFireStore: FirebaseFirestore
) : ViewModel() {
    fun initRV(rvReport: RecyclerView, hostFragment: Fragment) {
        Helper.displayCustomDialog(
            hostFragment.requireActivity(),
            R.layout.custom_dialog_loading
        )

        firebaseFireStore.collection("labass-app-report-log")
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val listToBeDisplayed: ArrayList<ReportModel> = ArrayList()

                for (document in documentSnapshot.documents) {
                    listToBeDisplayed.add(
                        ReportModel(
                            document.get("modelSenderEmail").toString(),
                            document.get("modelSenderID").toString(),
                            document.get("modelSenderLRN").toString(),
                            document.get("modelSenderUserType").toString(),
                            document.get("modelItemName").toString(),
                            document.get("modelItemCategory").toString(),
                            document.get("modelItemCode").toString(),
                            document.get("modelItemSize").toString(),
                            document.get("modelReportTitle").toString(),
                            document.get("modelReportContent").toString(),
                            document.get("modelCurrentDateTime").toString()
                        )
                    )
                }
                val adapter = AdminReportAdapter(firebaseFireStore, hostFragment.requireActivity())
                adapter.setList(listToBeDisplayed)
                rvReport.adapter = adapter

                Helper.dismissDialog()
            }
    }
}