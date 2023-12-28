package com.example.lab_ass_app.ui.main.student_teacher.experiments.homeList

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentExperimentsBinding
import com.example.lab_ass_app.utils.Helper
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeAdapter
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeModel

class ExperimentsFragment : Fragment() {
    private lateinit var viewModel: ExperimentsViewModel
    private lateinit var binding: FragmentExperimentsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExperimentsBinding.inflate(inflater, container, false)

        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_experimentsFragment_to_homeFragment)
        }

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
//        binding.rvExperimentsList.adapter = listItemsAdapter

        return binding.root
    }
}