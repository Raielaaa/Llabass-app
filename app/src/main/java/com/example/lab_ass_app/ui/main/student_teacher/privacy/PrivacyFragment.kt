package com.example.lab_ass_app.ui.main.student_teacher.privacy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentPrivacyBinding
import com.example.lab_ass_app.utils.`object`.Helper
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class PrivacyFragment : Fragment() {
    @Inject
    @Named("FirebaseAuth.Instance")
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var fireStore: FirebaseFirestore

    private lateinit var viewModel: PrivacyViewModel
    private lateinit var binding: FragmentPrivacyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrivacyBinding.inflate(inflater, container, false)

        initNavigationDrawer()
        initTermsAndPolicyClickListener()
        initBottomNavDrawer()

        //  init past-due notice
        Helper.checkPastDue(firebaseAuth, fireStore, binding.cvPastDueExist)

        return binding.root
    }

    private fun initBottomNavDrawer() {
        binding.apply {
            btmDrawerHome.setOnClickListener {
                findNavController().navigate(R.id.action_privacyFragment_to_homeFragment)
            }
            btmDrawerList.setOnClickListener {
                findNavController().navigate(R.id.action_privacyFragment_to_listFragment)
            }
            btmDrawerUser.setOnClickListener {
                findNavController().navigate(R.id.action_privacyFragment_to_profileFragment)
            }
            ivTakeQR.setOnClickListener {
                Helper.takeQR(requireActivity())
            }
        }
    }

    private fun initTermsAndPolicyClickListener() {
        binding.apply {
            tvTOS.setOnClickListener {
                findNavController().navigate(R.id.action_privacyFragment_to_TOSFragment)
            }
            ivTOS.setOnClickListener {
                findNavController().navigate(R.id.action_privacyFragment_to_TOSFragment)
            }
            tvPP.setOnClickListener {
                findNavController().navigate(R.id.action_privacyFragment_to_PPFragment)
            }
            ivPP.setOnClickListener {
                findNavController().navigate(R.id.action_privacyFragment_to_PPFragment)
            }
        }
    }

    private fun initNavigationDrawer() {
        val drawerLayout: DrawerLayout = Helper.drawerLayoutInstance
        val navDrawerLayout: NavigationView = Helper.navDrawerInstance
        navDrawerLayout.setCheckedItem(R.id.nav_privacy)

        binding.ivPrivacyNavDrawer.setOnClickListener {
            // Toggle the drawer (open if closed, close if open)
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }
}