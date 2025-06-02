package com.example.lab_ass_app.ui.account.register.otp

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentVerifyOTBinding
import com.example.lab_ass_app.utils.`object`.DataCache
import com.example.lab_ass_app.utils.`object`.Helper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Named

@AndroidEntryPoint
class VerifyOTPFragment : Fragment() {
    @Named("FirebaseAuth.Instance")
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val viewModel: VerifyOTViewModel by viewModels()
    private lateinit var binding: FragmentVerifyOTBinding
    private var inputtedNumber = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerifyOTBinding.inflate(inflater, container, false)

        binding.apply {
            btnLogin.setOnClickListener {
                val enteredOtp = otpView.text.toString().trim()
                if (enteredOtp.isNotEmpty() && enteredOtp.length == 6) {
                    Helper.displayCustomDialog(requireActivity(), R.layout.custom_dialog_loading)
                    verifyOtpCode(enteredOtp)
                } else {
                    displayToastMessage("Please enter the 6-digit OTP code.")
                }
            }

            otpView.setOtpCompletionListener {
                inputtedNumber = it.toInt()
                Log.d("Actual Value", it)
            }

            // Find all EditTexts inside the OtpView
            otpView.post {
                val editTexts = mutableListOf<android.widget.EditText>()
                getEditTexts(otpView, editTexts)

                // Add the filter to each EditText
                val filter = android.text.InputFilter { source, _, _, _, _, _ ->
                    if (source.isNullOrEmpty()) return@InputFilter null
                    if (source.matches(Regex("\\d+"))) return@InputFilter source
                    ""
                }

                editTexts.forEach { editText ->
                    editText.filters = arrayOf(filter)
                }

                // Show the keyboard
                otpView.requestFocus()
                val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.showSoftInput(otpView, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
            }
        }

        return binding.root
    }

    private fun verifyOtpCode(otpCode: String) {
        val verificationId = DataCache.verificationId
        if (verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    Helper.dismissDialog()
                    if (task.isSuccessful) {
                        // Verification success
                        Log.d("VerifyOTPFragment", "OTP verified successfully.")
                        Helper.dismissDialog()
                        displayToastMessage("OTP verified successfully.")
                        findNavController().navigate(R.id.action_verifyOTPFragment_to_successPageFragment)
                    } else {
                        // Verification failed
                        Log.e("VerifyOTPFragment", "OTP verification failed: ${task.exception?.message}")
                        Helper.dismissDialog()
                        displayToastMessage("OTP verification failed: ${task.exception?.localizedMessage}")
                    }
                }
        } else {
            Helper.dismissDialog()
            displayToastMessage("Error: Verification ID is missing.")
            Log.e("VerifyOTPFragment", "Error: verificationId is null.")
        }
    }

    private fun displayToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun getEditTexts(view: View, editTexts: MutableList<android.widget.EditText>) {
        if (view is android.widget.EditText) {
            editTexts.add(view)
        } else if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                getEditTexts(view.getChildAt(i), editTexts)
            }
        }
    }
}