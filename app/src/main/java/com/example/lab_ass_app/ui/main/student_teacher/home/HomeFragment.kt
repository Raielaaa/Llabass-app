package com.example.lab_ass_app.ui.main.student_teacher.home

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentHomeBinding
import com.example.lab_ass_app.utils.Helper
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeAdapter
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeModel
import com.example.lab_ass_app.ui.main.student_teacher.home.see_all.SeeAllDialog
import com.google.android.material.navigation.NavigationView

class HomeFragment : Fragment() {
    //  Binding and ViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel

    //  SharedPref
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //  Binding and ViewModel
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this@HomeFragment)[HomeViewModel::class.java]

        //  Init sharedPref
        sharedPreferences = requireActivity().getSharedPreferences("UserType_Pref", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        initClickableViews()
        initUserType()

        return binding.root
    }

    private fun initUserType() {
        binding.tvUserType.text = sharedPreferences.getString("user_type", "USER")
    }

    private fun initClickableViews() {
        initNavigationDrawer()
        initBottomNavigationDrawer()
        initFAB()
        initSeeAllButton()
        initListItemExpand()
        initListItemRV()
        initCVQR()
    }

    private fun initCVQR() {
        binding.cvTakeQR.setOnClickListener {
            viewModel.takeQR(requireActivity())
        }
    }

    private fun initListItemRV() {
        val listItemsAdapter = HomeAdapter {
            Helper.displayCustomDialog(
                this@HomeFragment,
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

        binding.rvListItems.adapter = listItemsAdapter
    }

    private fun initListItemExpand() {
        binding.ivListItemExpand.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_listFragment)
        }
    }

    private fun initSeeAllButton() {
        val seeAllAdapter = HomeAdapter {
            Helper.displayCustomDialog(
                this@HomeFragment,
                R.layout.selected_item_dialog
            )
        }
        seeAllAdapter.setItem(
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

        SeeAllDialog(seeAllAdapter).show(parentFragmentManager, "SeeAllDialog")

        binding.btnHomeSeeAll.setOnClickListener {
            SeeAllDialog(seeAllAdapter).show(parentFragmentManager, "SeeAllDialog")
        }
    }

    private fun initFAB() {
        binding.fabExperiments.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_experimentsFragment)
        }
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
            ivTakeQR.setOnClickListener {
                Helper.takeQR(requireActivity())
            }
        }
    }

    private fun initNavigationDrawer() {
        val drawerLayout: DrawerLayout = Helper.drawerLayoutInstance
        val navDrawerLayout: NavigationView = Helper.navDrawerInstance
        navDrawerLayout.setCheckedItem(R.id.nav_home)

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