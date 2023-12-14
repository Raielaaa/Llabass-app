package com.example.lab_ass_app.ui.main.student_teacher.list

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
import com.example.lab_ass_app.databinding.FragmentListBinding
import com.example.lab_ass_app.ui.Helper
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeAdapter
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeModel
import com.google.android.material.navigation.NavigationView

class ListFragment : Fragment() {
    private lateinit var viewModel: ListViewModel
    private lateinit var binding: FragmentListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)

        initBottomNavDrawer()
        initNavigationDrawer()
        initListRV()

        return binding.root
    }

    private fun initListRV() {
        val listItemsAdapter = HomeAdapter()
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
                    ),
                )
            )
        )

        binding.rvListListItem.adapter = listItemsAdapter
    }

    private fun initNavigationDrawer() {
        val drawerLayout: DrawerLayout = Helper.drawerLayoutInstance
        val navDrawerLayout: NavigationView = Helper.navDrawerInstance
        navDrawerLayout.setCheckedItem(R.id.nav_list)

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
                findNavController().navigate(R.id.action_listFragment_to_homeFragment)
            }
            btmDrawerPrivacy.setOnClickListener {
                findNavController().navigate(R.id.action_listFragment_to_privacyFragment)
            }
            btmDrawerUser.setOnClickListener {
                findNavController().navigate(R.id.action_listFragment_to_profileFragment)
            }
            ivTakeQR.setOnClickListener {
                Helper.takeQR(requireActivity())
            }
        }
    }
}