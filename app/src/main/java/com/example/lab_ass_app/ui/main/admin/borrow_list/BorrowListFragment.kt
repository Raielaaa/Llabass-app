package com.example.lab_ass_app.ui.main.admin.borrow_list

import android.annotation.SuppressLint
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentBorrowListBinding
import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.BorrowModel
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeModelDisplay
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.`object`.DataCache
import com.example.lab_ass_app.utils.`object`.Helper
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
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

    private var listToBeDisplayedToRV: ArrayList<BorrowModel> = ArrayList()

    private var isToolsSelected: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBorrowListBinding.inflate(inflater, container, false)

        initBottomNavDrawer()
        initNavigationDrawer()
        initListRV()
        initColorTransitionForCategory()
        initSearchButton()
        initRefreshButtonAndTV()

        return binding.root
    }

    private fun initRefreshButtonAndTV() {
        binding.btnListRefresh.setOnClickListener {
            //  Init date for TextView
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(Calendar.getInstance().time)
            val dateToBeDisplayed = "Updated as of $formattedDate"
            binding.tvCurrentDate.text = dateToBeDisplayed

            borrowToolsList.clear()
            borrowChemicalList.clear()
            initListRV()
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

                    if (isToolsSelected) {
                        filterItems(borrowToolsList, inputtedText, true)
                    } else {
                        filterItems(borrowChemicalList, inputtedText, false)
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
//                    TODO("Not yet implemented")
                }
            })
        }
    }

    private fun filterItems(
        listToBeFiltered: ArrayList<BorrowModel>,
        inputtedText: String,
        isToolsSelected: Boolean
    ) {
        listToBeDisplayedToRV.clear()
        for (item in listToBeFiltered) {
            if (item.modelItemCode.lowercase().contains(inputtedText.lowercase()) ||
                item.modelItemName.lowercase().contains(inputtedText.lowercase()) ||
                item.modelItemSize.lowercase().contains(inputtedText.lowercase()) ||
                item.modelBorrowDateTime.lowercase().contains(inputtedText.lowercase()) ||
                item.modelBorrowDeadlineDateTime.lowercase().contains(inputtedText.lowercase()) ||
                item.modelUserType.lowercase().contains(inputtedText.lowercase()) ||
                item.modelEmail.lowercase().contains(inputtedText.lowercase()) ||
                item.modelLRN.lowercase().contains(inputtedText.lowercase())
            ) {
                listToBeDisplayedToRV.add(item)
            }
        }

        displayRV(listToBeDisplayedToRV, "Tools")

        if (inputtedText.isEmpty()) {
            if (isToolsSelected) displayRV(borrowToolsList, "Tools")
            else displayRV(borrowChemicalList, "Chemicals")
        }
    }

    private fun initColorTransitionForCategory() {
        binding.apply {
            cvCategoryTools.setOnClickListener {
                //  Display on-click RV Tools
                etListSearch.setText("")
                displayRV(borrowToolsList, "Tools")
                isToolsSelected = true

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
                displayRV(borrowChemicalList, "Chemicals")
                isToolsSelected = false

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

                displayRV(borrowToolsList, "Tools")

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

    private fun displayRV(borrowModelList: ArrayList<BorrowModel>, itemType: String) {
        val adapter = BorrowModelAdapter(borrowModelList, requireContext()) {
            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show();
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