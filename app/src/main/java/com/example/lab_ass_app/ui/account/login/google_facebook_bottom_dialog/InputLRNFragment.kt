package com.example.lab_ass_app.ui.account.login.google_facebook_bottom_dialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.Toast
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentInputLRNBinding
import com.example.lab_ass_app.ui.account.login.LoginFragment
import com.example.lab_ass_app.ui.account.login.LoginViewModel
import com.example.lab_ass_app.utils.Helper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class InputLRNFragment(
    private val spUser: Spinner,
    private val loginFragment: LoginFragment,
    private val loginViewModel: LoginViewModel
) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentInputLRNBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentInputLRNBinding.inflate(inflater, container, false)

        binding.apply {
            cvProceed.setOnClickListener {
                if (etLRN.text.toString().isNotEmpty()) {
                    dismiss()
                    Helper.setUserTypeAndLRNForGoogleSignIn(
                        etLRN.text.toString(),
                        spUser.selectedItem.toString()
                    )
                    loginViewModel.loginViaGoogle(loginFragment, spUser.selectedItem.toString())
                } else {
                    Toast.makeText(requireParentFragment().requireActivity(), "Error: LRN is required to proceed", Toast.LENGTH_LONG).show()
                }
            }
        }

        return binding.root
    }
}