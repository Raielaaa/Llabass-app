package com.example.lab_ass_app.ui.main.admin.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentAdminProfileBinding
import com.example.lab_ass_app.ui.main.admin.util.DataCache
import com.example.lab_ass_app.utils.`object`.Helper
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminProfileFragment : Fragment() {
    private lateinit var binding: FragmentAdminProfileBinding
    private lateinit var adminProfileViewModel: AdminProfileViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminProfileBinding.inflate(inflater, container, false)
        adminProfileViewModel = ViewModelProvider(this)[AdminProfileViewModel::class.java]

        initComponents()

        return binding.root
    }

    private fun initComponents() {
        initNavigation()
        initRV()
        initRefreshButton()
        initSearchButton()
        initBorrowCount()
    }

    private fun initBorrowCount() {
        binding.tvProfileBorrowCount.text = DataCache.profileBorrowCount.toString()
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
                    val inputtedText: String = p0.toString().lowercase()

                    adminProfileViewModel.initSearchFunction(inputtedText, this@AdminProfileFragment, binding.rvProfileList)
                }

                override fun afterTextChanged(p0: Editable?) {
//                    TODO("Not yet implemented")
                }
            })
        }
    }

    private fun initRefreshButton() {
        binding.cvProfileRefresh.setOnClickListener {
            DataCache.profileList.clear()
            DataCache.profileBorrowCount = 0
            initRV()
        }
    }

    private fun initRV() {
        adminProfileViewModel.initRV(binding.rvProfileList, this@AdminProfileFragment, binding.tvProfileBorrowCount)
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