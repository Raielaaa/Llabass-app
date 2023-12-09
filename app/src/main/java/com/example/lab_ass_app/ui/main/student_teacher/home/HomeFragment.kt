package com.example.lab_ass_app.ui.main.student_teacher.home

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
import com.example.lab_ass_app.databinding.FragmentHomeBinding
import com.example.lab_ass_app.ui.Helper

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initNavigationDrawer()
        initBottomNavigationDrawer()

        return binding.root
    }

    private fun initBottomNavigationDrawer() {
        binding.apply {
            btmDrawerPrivacy.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_privacyFragment)
            }
            btmDrawerList.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_listFragment)
            }
            btmDrawerUser.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
            }
        }
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
}