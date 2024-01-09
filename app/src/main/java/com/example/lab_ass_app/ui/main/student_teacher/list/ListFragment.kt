package com.example.lab_ass_app.ui.main.student_teacher.list

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentListBinding
import com.example.lab_ass_app.ui.main.student_teacher.home.HomeViewModel
import com.example.lab_ass_app.utils.`object`.Helper
import com.example.lab_ass_app.utils.`object`.DataCache
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class ListFragment : Fragment() {
    @Inject
    @Named("FirebaseAuth.Instance")
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var fireStore: FirebaseFirestore

    private lateinit var listViewModel: ListViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentListBinding
    private var isToolsSelected: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)

        initBottomNavDrawer()
        initNavigationDrawer()
        initListRV()
        initColorTransitionForCategory()
        initSearchButton()
        initRefreshButtonAndTV()

        //  init Object class binding object
        Helper.listBinding = binding

        //  init past-due notice
        Helper.checkPastDue(firebaseAuth, fireStore, binding.cvPastDueExist)

        return binding.root
    }

    private fun initRefreshButtonAndTV() {
        binding.apply {
            listViewModel.initRefreshButtonAndTV(
                requireActivity(),
                requireContext(),
                tvCurrentDate,
                btnListRefresh,
                this@ListFragment,
                homeViewModel,
                binding,
                fireStore
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initSearchButton() {
        binding.apply {
            etListSearch.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val drawableEnd = 2 // Index for the drawable end
                    val extraPadding = 60 // Drawable padding in dp
                    val drawableEndBounds = etListSearch.compoundDrawablesRelative[drawableEnd].bounds

                    // Adjusting for drawable padding
                    val drawableEndWithPadding = etListSearch.right - drawableEndBounds.width() - extraPadding
                    if (event.rawX >= drawableEndWithPadding) {
                        // Clicked on the drawable end
                        etListSearch.setText("")
                        return@setOnTouchListener true
                    }
                }
                false
            }

            etListSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                    TODO("Not yet implemented")
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val inputtedText: String = p0.toString()

                    listViewModel.initSearchFunction(
                        inputtedText,
                        isToolsSelected
                    )
                }

                override fun afterTextChanged(p0: Editable?) {
//                    TODO("Not yet implemented")
                }
            })
        }
    }

    private fun initListRV() {
        //  Display on-click RV Tools
        DataCache.cacheDataForCategory(
            "Tools",
            homeViewModel,
            binding.rvListListItem,
            this@ListFragment,
            fireStore
        )
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
                //  Display on-click RV Tools
                etListSearch.setText("")
                DataCache.cacheDataForCategory(
                    "Tools",
                    homeViewModel,
                    binding.rvListListItem,
                    this@ListFragment,
                    fireStore
                )
                isToolsSelected = true
                DataCache.isToolSelected = true

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
                //  Display on-click RV Chemicals
                etListSearch.setText("")
                DataCache.cacheDataForCategory(
                    "Chemicals",
                    homeViewModel,
                    binding.rvListListItem,
                    this@ListFragment,
                    fireStore
                )
                isToolsSelected = false
                DataCache.isToolSelected = false

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

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listViewModel = ViewModelProvider(this@ListFragment)[ListViewModel::class.java]
        homeViewModel = ViewModelProvider(this@ListFragment)[HomeViewModel::class.java]
    }
}