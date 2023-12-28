package com.example.lab_ass_app.ui.main.student_teacher.list

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentListBinding
import com.example.lab_ass_app.utils.Helper
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
        initColorTransitionForCategory()

        return binding.root
    }

    private fun initListRV() {
//        val listItemsAdapter = HomeAdapter {
//            Helper.displayCustomDialog(
//                requireActivity(),
//                R.layout.selected_item_dialog
//            )
//        }
//        listItemsAdapter.setItem(
//            ArrayList(
//                listOf(
//                    HomeModel(
//                        Uri.parse("android.resources://${requireActivity().packageName}/drawable/image_placeholder"),
//                        "PETRI DISH",
//                        "384732",
//                        "14 Borrows",
//                        "AVAILABLE"
//                    ),
//                    HomeModel(
//                        Uri.parse("android.resources://${requireActivity().packageName}/drawable/image_placeholder"),
//                        "PETRI DISH",
//                        "384732",
//                        "14 Borrows",
//                        "AVAILABLE"
//                    ),
//                    HomeModel(
//                        Uri.parse("android.resources://${requireActivity().packageName}/drawable/image_placeholder"),
//                        "PETRI DISH",
//                        "384732",
//                        "14 Borrows",
//                        "AVAILABLE"
//                    ),
//                    HomeModel(
//                        Uri.parse("android.resources://${requireActivity().packageName}/drawable/image_placeholder"),
//                        "PETRI DISH",
//                        "384732",
//                        "14 Borrows",
//                        "AVAILABLE"
//                    ),
//                    HomeModel(
//                        Uri.parse("android.resources://${requireActivity().packageName}/drawable/image_placeholder"),
//                        "PETRI DISH",
//                        "384732",
//                        "14 Borrows",
//                        "AVAILABLE"
//                    ),
//                    HomeModel(
//                        Uri.parse("android.resources://${requireActivity().packageName}/drawable/image_placeholder"),
//                        "PETRI DISH",
//                        "384732",
//                        "14 Borrows",
//                        "AVAILABLE"
//                    ),
//                    HomeModel(
//                        Uri.parse("android.resources://${requireActivity().packageName}/drawable/image_placeholder"),
//                        "PETRI DISH",
//                        "384732",
//                        "14 Borrows",
//                        "AVAILABLE"
//                    ),
//                )
//            )
//        )
//
//        binding.rvListListItem.adapter = listItemsAdapter
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

    private fun initColorTransitionForCategory() {
        binding.apply {
            cvCategoryTools.setOnClickListener {
                clCategoryTools.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.Theme_color_main))
                tvCategoryToolsTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                tvCategoryToolsCategory.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                ivCategoryToolsArrow.setImageURI(Uri.parse("android.resource://${requireActivity().packageName}/drawable/right_arrow_white"))
                ivCategoryToolsPic.setImageURI(Uri.parse("android.resource://${requireActivity().packageName}/drawable/top_tools"))

                clCategoryChem.background = ContextCompat.getDrawable(requireContext(), R.drawable.home_top_bg)
                clCategoryChem.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                tvCategoryChemTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.Theme_color_main))
                tvCategoryChemCategory.setTextColor(ContextCompat.getColor(requireContext(), R.color.Theme_color_main))
                ivCategoryChemArrow.setImageURI(Uri.parse("android.resource://${requireActivity().packageName}/drawable/right_arrow"))
                ivCategoryChemPic.setImageURI(Uri.parse("android.resource://${requireActivity().packageName}/drawable/top_chem"))
            }
            cvCategoryChe.setOnClickListener {
                clCategoryChem.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.Theme_color_main))
                tvCategoryChemTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                tvCategoryChemCategory.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                ivCategoryChemArrow.setImageURI(Uri.parse("android.resource://${requireActivity().packageName}/drawable/right_arrow_white"))
                ivCategoryChemPic.setImageURI(Uri.parse("android.resource://${requireActivity().packageName}/drawable/top_chem_white"))

                clCategoryTools.background = ContextCompat.getDrawable(requireContext(), R.drawable.home_top_bg)
                clCategoryTools.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                tvCategoryToolsTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.Theme_color_main))
                tvCategoryToolsCategory.setTextColor(ContextCompat.getColor(requireContext(), R.color.Theme_color_main))
                ivCategoryToolsArrow.setImageURI(Uri.parse("android.resource://${requireActivity().packageName}/drawable/right_arrow"))
                ivCategoryToolsPic.setImageURI(Uri.parse("android.resource://${requireActivity().packageName}/drawable/top_tools_themecolor"))
            }
        }
    }
}