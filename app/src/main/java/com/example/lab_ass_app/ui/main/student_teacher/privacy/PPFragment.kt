package com.example.lab_ass_app.ui.main.student_teacher.privacy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R

class PPFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_p_p, container, false)

        val ivBack: ImageView = view.findViewById(R.id.ivBack)
        ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_PPFragment_to_privacyFragment)
        }

        return view
    }
}