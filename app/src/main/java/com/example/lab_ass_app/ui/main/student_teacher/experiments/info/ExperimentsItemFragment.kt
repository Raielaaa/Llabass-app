package com.example.lab_ass_app.ui.main.student_teacher.experiments.info

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lab_ass_app.R

class ExperimentsItemFragment : Fragment() {

    companion object {
        fun newInstance() = ExperimentsItemFragment()
    }

    private lateinit var viewModel: ExperimentsItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_experiments_item, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ExperimentsItemViewModel::class.java)
        // TODO: Use the ViewModel
    }

}