package com.example.lab_ass_app.ui.main.student_teacher.profile

import android.net.Uri
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
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeAdapter
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeModel
import com.google.android.material.navigation.NavigationView

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
        initRV()

        return binding.root
    }

    private fun initRV() {
        val listItemsAdapter = HomeAdapter {
            Helper.displayCustomDialog(
                this,
                R.layout.selected_item_dialog
            )
        }
        listItemsAdapter.setItem(
            ArrayList(
                listOf(
                    HomeModel(
                        Uri.parse("android.resources://${requireActivity().packageName}/drawable/image_placeholder"),
                        "PETRI DISH",
                        "384732",
                        "14 Borrows",
                        "AVAILABLE"
                    ),
                    HomeModel(
                        Uri.parse("android.resources://${requireActivity().packageName}/drawable/image_placeholder"),
                        "PETRI DISH",
                        "384732",
                        "14 Borrows",
                        "AVAILABLE"
                    ),
                    HomeModel(
                        Uri.parse("android.resources://${requireActivity().packageName}/drawable/image_placeholder"),
                        "PETRI DISH",
                        "384732",
                        "14 Borrows",
                        "AVAILABLE"
                    )
                )
            )
        )

        binding.rvProfileList.adapter = listItemsAdapter
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
                findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
            }
            btmDrawerPrivacy.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_privacyFragment)
            }
            btmDrawerList.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_listFragment)
            }
            ivTakeQR.setOnClickListener {
                Helper.takeQR(requireActivity())
            }
        }
    }
}