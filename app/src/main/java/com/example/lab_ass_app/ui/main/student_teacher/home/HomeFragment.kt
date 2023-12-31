package com.example.lab_ass_app.ui.main.student_teacher.home

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentHomeBinding
import com.example.lab_ass_app.utils.Helper
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeAdapter
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeModel
import com.example.lab_ass_app.ui.main.student_teacher.home.see_all.SeeAllDialog
import com.example.lab_ass_app.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class HomeFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    @Inject
    @Named("FirebaseStorage.Instance")
    lateinit var firebaseStorage: StorageReference

    //  Binding and ViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel

    //  SharedPref
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //  Binding and ViewModel
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(this@HomeFragment)[HomeViewModel::class.java]

        //  Init sharedPref
        sharedPreferences = requireActivity().getSharedPreferences("UserType_Pref", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this@HomeFragment)

        //  Dismiss dialog for assurance
        Helper.dismissDialog()

        //  Database retrieving functions
        initDataRetrievalFromFirebase()

        initClickableViews()
        initUserType()

        return binding.root
    }

    private fun initDataRetrievalFromFirebase() {
        initTopBorrows()
        initBottomRvList()
    }

    private fun initBottomRvList() {
        homeViewModel.initListItemRV(binding.rvListItems, this@HomeFragment)
    }

    private fun initTopBorrows() {
        homeViewModel.initTopBorrowDisplay(binding, requireContext(), this@HomeFragment)
        initTopBorrowsSelected()
    }

    private fun initTopBorrowsSelected() {
        homeViewModel.initTopBorrowDisplaySelected(binding, requireActivity())
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
        initCVQR()
        initObjectValues()
        initColorTransitionForCategory()
    }

    private fun initObjectValues() {
        Helper.setActivityReference(requireActivity())
    }

    private fun initCVQR() {
        binding.cvTakeQR.setOnClickListener {
            homeViewModel.takeQR(requireActivity())
        }
    }

    private fun initListItemExpand() {
        binding.ivListItemExpand.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_listFragment)
        }
    }

    private fun initSeeAllButton() {
//        val seeAllAdapter = HomeAdapter {
//            Helper.displayCustomDialog(
//                requireActivity(),
//                R.layout.selected_item_dialog
//            )
//        }
//
//        binding.btnHomeSeeAll.setOnClickListener {
//            SeeAllDialog(seeAllAdapter).show(parentFragmentManager, "SeeAllDialog")
//        }
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

    override fun onSharedPreferenceChanged(sharedPred: SharedPreferences?, key: String?) {
        Log.d(Constants.TAG, "onSharedPreferenceChanged: testttt")
        if (key == "fbNoAccount_key") {
            findNavController().navigate(R.id.accountOpeningFragment)
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
            cvCategoryChem.setOnClickListener {
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