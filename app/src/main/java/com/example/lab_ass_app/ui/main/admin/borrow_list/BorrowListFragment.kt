package com.example.lab_ass_app.ui.main.admin.borrow_list

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentBorrowListBinding
import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.BorrowModel
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.`object`.Helper
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class BorrowListFragment : Fragment() {
    @Inject
    @Named("FirebaseAuth.Instance")
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var fireStore: FirebaseFirestore

    private val viewModel: BorrowListViewModel by viewModels()
    private lateinit var binding: FragmentBorrowListBinding

    private var borrowToolsList: ArrayList<BorrowModel> = ArrayList()
    private var borrowChemicalList: ArrayList<BorrowModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBorrowListBinding.inflate(inflater, container, false)

        initBottomNavDrawer()
        initNavigationDrawer()
        initListRV()
//        initColorTransitionForCategory()
//        initSearchButton()
//        initRefreshButtonAndTV()

        return binding.root
    }

    private fun initListRV() {
        //  Display loading dialog
        Helper.dismissDialog()
        Helper.displayCustomDialog(
            requireActivity(),
            R.layout.custom_dialog_loading
        )

        fireStore.collection("labass-app-borrow-log")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (entry in querySnapshot) {
                    val borrowStartDateTime = entry.getString("modelBorrowDateTime")
                    val borrowEndDateTime = entry.getString("modelBorrowDeadlineDateTime")
                    val email = entry.getString("modelEmail")
                    val category = entry.getString("modelItemCategory")
                    val code = entry.getString("modelItemCode")
                    val name = entry.getString("modelItemName")
                    val size = entry.getString("modelItemSize")
                    val lrn = entry.getString("modelLRN")
                    val userID = entry.getString("modelUserID")
                    val userType = entry.getString("modelUserType")

                    val compiledData = BorrowModel(
                        modelEmail = email.toString(),
                        modelLRN = lrn.toString(),
                        modelUserType = userType.toString(),
                        modelUserID = userID.toString(),
                        modelItemCode = code.toString(),
                        modelItemName = name.toString(),
                        modelItemCategory = category.toString(),
                        modelItemSize = size.toString(),
                        modelBorrowDateTime = borrowStartDateTime.toString(),
                        modelBorrowDeadlineDateTime = borrowEndDateTime.toString()
                    )

                    if (category == "Tools") {
                        borrowToolsList.add(compiledData)
                    } else {
                        borrowChemicalList.add(compiledData)
                    }
                }

                displayRV(borrowToolsList, borrowChemicalList)

                Helper.dismissDialog()
            }.addOnFailureListener { exception ->
                Helper.dismissDialog()
                Log.e(Constants.TAG, "initRefreshButtonAndTV: ${exception.message}")
                Toast.makeText(
                    context,
                    "An error occurred: ${exception.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun displayRV(borrowModelList: ArrayList<BorrowModel>, borrowChemicalList: ArrayList<BorrowModel>) {
        val adapter = BorrowModelAdapter(borrowModelList, requireContext()) {
            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT);
        }
        binding.rvListListItem.layoutManager = LinearLayoutManager(requireContext())
        binding.rvListListItem.adapter = adapter
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
        }
    }
}