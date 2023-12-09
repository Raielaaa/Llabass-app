package com.example.lab_ass_app.ui.main.student_teacher.privacy

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentPrivacyBinding
import com.example.lab_ass_app.ui.Helper

class PrivacyFragment : Fragment() {
    private lateinit var viewModel: PrivacyViewModel
    private lateinit var binding: FragmentPrivacyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrivacyBinding.inflate(inflater, container, false)

        initNavDrawer()
        initTermsAndPolicyClickListener()

        return binding.root
    }

    private fun initTermsAndPolicyClickListener() {
        binding.apply {
            tvTOS.setOnClickListener {
                findNavController().navigate(R.id.action_privacyFragment_to_TOSFragment)
            }
            ivTOS.setOnClickListener {
                findNavController().navigate(R.id.action_privacyFragment_to_TOSFragment)
            }
            tvPP.setOnClickListener {
                findNavController().navigate(R.id.action_privacyFragment_to_PPFragment)
            }
            ivPP.setOnClickListener {
                findNavController().navigate(R.id.action_privacyFragment_to_PPFragment)
            }
        }
    }

    private fun initNavDrawer() {
        val drawerLayout: DrawerLayout = Helper.navDrawerInstance

        binding.ivPrivacyNavDrawer.setOnClickListener {
            // Toggle the drawer (open if closed, close if open)
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }
}