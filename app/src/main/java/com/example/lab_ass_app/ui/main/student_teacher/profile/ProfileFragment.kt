package com.example.lab_ass_app.ui.main.student_teacher.profile

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentProfileBinding
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.`object`.Helper
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    @Inject
    @Named("FirebaseAuth.Instance")
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var fireStore: FirebaseFirestore

    @Inject
    @Named("FirebaseStorage.Instance")
    lateinit var firebaseStorage: StorageReference

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding

    //  Image chooser
    private lateinit var pickMediaLauncher: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        profileViewModel = ViewModelProvider(this@ProfileFragment)[ProfileViewModel::class.java]

        initBottomNavDrawer()
        initNavigationDrawer()
        initRV()
        initRefreshButton()

        //  init past-due notice
        Helper.checkPastDue(firebaseAuth, fireStore, binding.cvPastDueExist)

        initUserImageDisplay()

        return binding.root
    }

    private fun initImageResourceChooser() {
        pickMediaLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                val storage = FirebaseStorage.getInstance()
                Helper.uploadImageToFireStore(uri, binding.ivUserImage, firebaseStorage, storage, this@ProfileFragment, firebaseAuth)
            } else {
                Toast.makeText(requireContext(), "No media selected", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initUserImageDisplay() {
        if (Helper.userImageProfile != null) {
            binding.ivUserImage.setImageURI(Helper.userImageProfile)
        } else {
            firebaseStorage.child("user_image/${firebaseAuth.currentUser!!.uid}")
                .getBytes(Long.MAX_VALUE)
                .addOnSuccessListener { bytes ->
                    // Convert the byte array to a Bitmap and set it in the ImageView
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                    Helper.userImageProfile = Helper.bitmapToUri(bitmap, requireActivity())
                    binding.ivUserImage.setImageBitmap(bitmap)
                }.addOnFailureListener { exception ->
                    // Handle failures
                    exception.printStackTrace()
                }
        }

        initImageResourceChooser()
        Helper.chooseImage(binding.ivUserImage, pickMediaLauncher)
    }

    private fun initRefreshButton() {
        binding.btnProfileRefresh.setOnClickListener {
            profileViewModel.displayRealTimeProfileInfoToRV(this@ProfileFragment, binding.rvProfileList, binding)
        }
    }

    private fun initRV() {
        profileViewModel.displayRealTimeProfileInfoToRV(this@ProfileFragment, binding.rvProfileList, binding)
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
                findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
            }
            btmDrawerPrivacy.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_privacyFragment)
            }
            btmDrawerList.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_listFragment)
            }
            ivTakeQR.setOnClickListener {
                Helper.takeQR(requireActivity())
            }
        }
    }
}