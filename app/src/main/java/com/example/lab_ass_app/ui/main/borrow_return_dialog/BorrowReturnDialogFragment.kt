package com.example.lab_ass_app.ui.main.borrow_return_dialog

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentBorrowReturnDialogBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BorrowReturnDialogFragment(
    private val bitmap: Bitmap?
) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBorrowReturnDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBorrowReturnDialogBinding.inflate(inflater, container, false)

        binding.ivImage.setImageBitmap(bitmap)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            val bottomSheet = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
    }
}