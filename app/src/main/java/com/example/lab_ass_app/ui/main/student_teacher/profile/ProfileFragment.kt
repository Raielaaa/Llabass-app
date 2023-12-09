package com.example.lab_ass_app.ui.main.student_teacher.profile

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
import com.example.lab_ass_app.databinding.FragmentProfileBinding
import com.example.lab_ass_app.ui.Helper

class ProfileFragment : Fragment() {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        initBottomNavDrawer()
        initNavigationDrawer()

        return binding.root
    }

    private fun initNavigationDrawer() {
        val drawerLayout: DrawerLayout = Helper.navDrawerInstance

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
                findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
            }
            btmDrawerPrivacy.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_privacyFragment)
            }
            btmDrawerList.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_listFragment)
            }
        }
    }
}