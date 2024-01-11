package com.example.lab_ass_app.ui.main.admin.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentAdminListBinding

class AdminListFragment : Fragment() {
    private lateinit var binding: FragmentAdminListBinding
    private lateinit var mViewModel: AdminListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminListBinding.inflate(inflater, container, false)
        mViewModel = ViewModelProvider(this)[AdminListViewModel::class.java]

        return binding.root
    }
}
