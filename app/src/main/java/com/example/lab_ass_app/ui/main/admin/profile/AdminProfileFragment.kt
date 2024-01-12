package com.example.lab_ass_app.ui.main.admin.profile

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
import com.example.lab_ass_app.databinding.FragmentAdminProfileBinding
import com.example.lab_ass_app.utils.`object`.Helper
import com.google.android.material.navigation.NavigationView

class AdminProfileFragment : Fragment() {
    private lateinit var binding: FragmentAdminProfileBinding
    private lateinit var mViewModel: AdminProfileViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminProfileBinding.inflate(inflater, container, false)
        mViewModel = ViewModelProvider(this)[AdminProfileViewModel::class.java]

        initComponents()

        return binding.root
    }

    private fun initComponents() {
        initNavigation()
    }

    private fun initNavigation() {
        initNavigationDrawer()
        initBottomNavDrawer()
    }

    private fun initNavigationDrawer() {
        val drawerLayout: DrawerLayout = Helper.drawerLayoutInstance
        val navDrawerLayout: NavigationView = Helper.navDrawerInstance
        navDrawerLayout.setCheckedItem(R.id.nav_user)

        binding.ivNavDrawer.setOnClickListener {
            // Toggle the drawer (open if closed, close if open)
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun initBottomNavDrawer() {
        binding.apply {
            btmDrawerHome.setOnClickListener {
                findNavController().navigate(R.id.action_adminProfileFragment_to_homeAdminFragment)
            }
            btmDrawerPrivacy.setOnClickListener {
                findNavController().navigate(R.id.action_adminProfileFragment_to_adminPrivacyFragment)
            }
            btmDrawerList.setOnClickListener {
                findNavController().navigate(R.id.action_adminProfileFragment_to_adminListFragment)
            }
        }
    }
}