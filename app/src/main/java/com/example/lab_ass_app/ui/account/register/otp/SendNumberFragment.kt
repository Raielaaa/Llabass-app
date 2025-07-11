package com.example.lab_ass_app.ui.account.register.otp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lab_ass_app.utils.`object`.Helper
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentSendNumberBinding
import com.example.lab_ass_app.utils.`object`.DataCache
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class SendNumberFragment : Fragment() {
    private lateinit var binding: FragmentSendNumberBinding
    private val viewModel: SendNumberViewModel by viewModels()

    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSendNumberBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            val mobileNumber = binding.editTextNumber.text.toString().trim()
            if (mobileNumber.isNotEmpty()) {
                if (isValidMobileNumber(mobileNumber)) {
                    Helper.displayCustomDialog(requireActivity(), R.layout.custom_dialog_loading)
                    sendOtp(mobileNumber)
                } else {
                    displayToastMessage("Invalid mobile number format. Please enter a valid number (e.g., 09xxxxxxxxx, 970xxxxxxx, or +639xxxxxxxxx).")
                }
            } else {
                displayToastMessage("Error: Mobile number cannot be empty.")
            }
        }

        return binding.root
    }

    private fun isValidMobileNumber(mobileNumber: String): Boolean {
        val regex = Regex("^(09\\d{9}|\\+639\\d{9}|9\\d{9})$")
        return regex.matches(mobileNumber)
    }

    private fun sendOtp(rawPhoneNumber: String) {
        val phoneNumber = formatToE164(rawPhoneNumber)
        if (phoneNumber == null) {
            displayToastMessage("Error: Invalid phone number format.")
            return
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity()) // Required to auto-retrieve the OTP
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d("SendNumberFragment", "Verification completed instantly.")
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Helper.dismissDialog()
                    Log.e("SendNumberFragment", "📛 Verification failed: ${e.message}")
                    Log.e("SendNumberFragment", "📛 Cause: ${e.cause}")
                    Log.e("SendNumberFragment", "📛 Class: ${e::class.java.name}")
                    Log.e("SendNumberFragment", Log.getStackTraceString(e))

                    displayToastMessage("Verification failed: ${e.localizedMessage}")
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    Helper.dismissDialog()
                    Log.d("SendNumberFragment", "OTP sent successfully.")
                    displayToastMessage("OTP sent successfully.")
                    this@SendNumberFragment.verificationId = verificationId
                    resendToken = token

                    // Cache the verificationId for later use
                    DataCache.verificationId = verificationId
                    DataCache.resendToken = token
                    DataCache.inputtedMobileNumber = phoneNumber

                    navigateToVerifyOtpScreen()
                }

                override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                    Helper.dismissDialog()
                    Log.w("SendNumberFragment", "OTP auto-retrieval timed out.")
                    displayToastMessage("The One-Time Password (OTP) has expired. Please request a new OTP.")
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun formatToE164(mobileNumber: String): String? {
        return when {
            mobileNumber.startsWith("+") -> mobileNumber // Already E.164
            mobileNumber.startsWith("09") && mobileNumber.length == 11 -> {
                // Convert 09xxxxxxxxx to +639xxxxxxxxx
                "+63" + mobileNumber.substring(1)
            }
            mobileNumber.startsWith("9") && mobileNumber.length == 10 -> {
                // Assuming 970 is a short local number, convert to +63970xxxxxxx
                "+63" + mobileNumber
            }
            else -> null
        }
    }

    private fun navigateToVerifyOtpScreen() {
        findNavController().navigate(R.id.action_sendNumberFragment_to_verifyOTPFragment)
    }

    private fun displayToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}
