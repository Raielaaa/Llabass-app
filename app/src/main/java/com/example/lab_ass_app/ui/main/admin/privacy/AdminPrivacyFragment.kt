package com.example.lab_ass_app.ui.main.admin.privacy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentAdminPrivacyBinding
import com.example.lab_ass_app.utils.`object`.Helper
import com.google.android.material.navigation.NavigationView

class AdminPrivacyFragment : Fragment() {
    private lateinit var binding: FragmentAdminPrivacyBinding
    private lateinit var mViewModel: AdminPrivacyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminPrivacyBinding.inflate(inflater, container, false)
        mViewModel = ViewModelProvider(this)[AdminPrivacyViewModel::class.java]

        initComponents()

        return binding.root
    }

    private fun initComponents() {
        initNavigation()
        initTermsAndPolicyClickListener()
    }

    private fun initNavigation() {
        initBottomNavDrawer()
        initNavigationDrawer()
    }

    private fun initBottomNavDrawer() {
        binding.apply {
            btmDrawerHome.setOnClickListener {
                findNavController().navigate(R.id.action_adminPrivacyFragment_to_homeAdminFragment)
            }
            btmDrawerList.setOnClickListener {
                findNavController().navigate(R.id.action_adminPrivacyFragment_to_adminListFragment)
            }
            btmDrawerUser.setOnClickListener {
                findNavController().navigate(R.id.action_adminPrivacyFragment_to_adminProfileFragment)
            }
        }
    }

    private fun initTermsAndPolicyClickListener() {
        binding.apply {
            tvTOS.setOnClickListener {
                findNavController().navigate(R.id.action_adminPrivacyFragment_to_adminTOSFragment)
            }
            ivTOS.setOnClickListener {
                findNavController().navigate(R.id.action_adminPrivacyFragment_to_adminTOSFragment)
            }
            tvPP.setOnClickListener {
                findNavController().navigate(R.id.action_adminPrivacyFragment_to_adminPPFragment)
            }
            ivPP.setOnClickListener {
                findNavController().navigate(R.id.action_adminPrivacyFragment_to_adminPPFragment)
            }
        }
    }

    private fun initNavigationDrawer() {
        val drawerLayout: DrawerLayout = Helper.drawerLayoutInstance
        val navDrawerLayout: NavigationView = Helper.navDrawerInstance
        navDrawerLayout.setCheckedItem(R.id.nav_privacy)

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
