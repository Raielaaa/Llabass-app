package com.example.lab_ass_app.ui.main.admin.util

import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.BorrowModel
import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.report.ReportModel
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeModelLive

object DataCache {
    var reportList: ArrayList<ReportModel> = ArrayList()
    var profileList: ArrayList<BorrowModel> = ArrayList()
    var profileBorrowCount: Int = 0

    var toolsList: ArrayList<HomeModelLive> = ArrayList()
    var chemicalsList: ArrayList<HomeModelLive> = ArrayList()
}