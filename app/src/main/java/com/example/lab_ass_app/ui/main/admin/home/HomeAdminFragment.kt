package com.example.lab_ass_app.ui.main.admin.home

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentHomeAdminBinding
import com.example.lab_ass_app.ui.main.student_teacher.home.HomeViewModel
import com.example.lab_ass_app.utils.`object`.DataCache
import com.example.lab_ass_app.utils.`object`.Helper
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class HomeAdminFragment : Fragment() {
    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var fireStore: FirebaseFirestore

    private lateinit var binding: FragmentHomeAdminBinding
    private lateinit var mViewModel: HomeAdminViewModel
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        mViewModel = ViewModelProvider(this)[HomeAdminViewModel::class.java]
        homeViewModel = ViewModelProvider(this@HomeAdminFragment)[HomeViewModel::class.java]

        initComponents()

        return binding.root
    }

    private fun initComponents() {
        initNavigation()
        initColorTransitionForCategory()
        initTopBorrows()
        initBottomRvList()
        initReport()
    }

    private fun initReport() {
        binding.fabReports.setOnClickListener {
            findNavController().navigate(R.id.action_homeAdminFragment_to_adminReportFragment)
        }
    }

    private fun initNavigation() {
        initBottomNavigationDrawer()
        initNavigationDrawer()
    }

    private fun initTopBorrows() {
        homeViewModel.initTopBorrowDisplay(binding, null, requireContext(), this@HomeAdminFragment, binding.btnHomeSeeAll)
        initTopBorrowsSelected()
    }

    private fun initTopBorrowsSelected() {
        homeViewModel.initTopBorrowDisplaySelected(binding, null, requireActivity())
    }

    private fun initBottomRvList() {
        //  Initial display
        DataCache.cacheDataForCategory(
            "Tools",
            homeViewModel,
            binding.rvListItems,
            this@HomeAdminFragment,
            fireStore
        )
    }

    private fun initBottomNavigationDrawer() {
        binding.apply {
            btmDrawerPrivacy.setOnClickListener {
                findNavController().navigate(R.id.action_homeAdminFragment_to_adminPrivacyFragment)
            }
            btmDrawerList.setOnClickListener {
                findNavController().navigate(R.id.action_homeAdminFragment_to_adminListFragment)
            }
            btmDrawerUser.setOnClickListener {
                findNavController().navigate(R.id.action_homeAdminFragment_to_adminProfileFragment)
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

        Helper.navDrawerInstance.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> navigate(R.id.homeAdminFragment, R.id.nav_home)
                R.id.nav_list -> navigate(R.id.adminListFragment, R.id.nav_list)
                R.id.nav_user -> navigate(R.id.adminProfileFragment, R.id.nav_user)
                R.id.nav_privacy -> navigate(R.id.adminPrivacyFragment, R.id.nav_privacy)
            }
            true
        }
    }

    // Navigate to the selected fragment with custom animations
    private fun navigate(fragment: Int, drawerItem: Int) {
        binding.apply {
            Helper.drawerLayoutInstance.closeDrawer(GravityCompat.START)
            Helper.navDrawerInstance.setCheckedItem(drawerItem)

            CoroutineScope(Dispatchers.IO).launch {
                delay(500)

                withContext(Dispatchers.Main) {
                    findNavController().navigate(
                        fragment,
                        null,
                        getCustomNavOptions(R.anim.fade_in, R.anim.fade_out)
                    )
                }
            }
        }
    }

    // Define custom navigation options with animations
    private fun getCustomNavOptions(enterAnim: Int, exitAnim: Int): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(enterAnim)
            .setExitAnim(exitAnim)
            .build()
    }

    private fun initColorTransitionForCategory() {
        binding.apply {
            cvCategoryTools.setOnClickListener {
                //  Display on-click RV Tools
                DataCache.cacheDataForCategory(
                    "Tools",
                    homeViewModel,
                    binding.rvListItems,
                    this@HomeAdminFragment,
                    fireStore
                )

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
            cvCategoryChem.setOnClickListener {
                //  Display on-click RV Chemicals
                DataCache.cacheDataForCategory(
                    "Chemicals",
                    homeViewModel,
                    binding.rvListItems,
                    this@HomeAdminFragment,
                    fireStore
                )

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
