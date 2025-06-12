package com.example.lab_ass_app.ui.main.admin.borrow_list

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lab_ass_app.R

class BorrowListFragment : Fragment() {

    companion object {
        fun newInstance() = BorrowListFragment()
    }

    private val viewModel: BorrowListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_borrow_list, container, false)
    }
}