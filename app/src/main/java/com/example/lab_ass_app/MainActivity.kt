package com.example.lab_ass_app

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.lab_ass_app.databinding.ActivityMainBinding
import com.example.lab_ass_app.utils.Helper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // View Binding and NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    // Bitmap for QR code
    private var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize navigation host fragment and navigation drawer
        initNavHostFragment()
        initNavDrawer()
    }

    // Initialize navigation drawer
    private fun initNavDrawer() {
        binding.apply {
            // Set up instances for DrawerLayout and NavigationView in Helper class
            Helper.drawerLayoutInstance = drawerLayout
            Helper.navDrawerInstance = navDrawer

            // Set navigation item click listener
            navDrawer.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_home -> navigate(R.id.homeFragment, R.id.nav_home)
                    R.id.nav_list -> navigate(R.id.listFragment, R.id.nav_list)
                    R.id.nav_user -> navigate(R.id.profileFragment, R.id.nav_user)
                    R.id.nav_privacy -> navigate(R.id.privacyFragment, R.id.nav_privacy)
                }
                true
            }
        }
    }

    // Navigate to the selected fragment with custom animations
    private fun navigate(fragment: Int, drawerItem: Int) {
        binding.apply {
            drawerLayout.closeDrawer(GravityCompat.START)
            navDrawer.setCheckedItem(drawerItem)

            CoroutineScope(Dispatchers.IO).launch {
                delay(500)

                withContext(Dispatchers.Main) {
                    navController.navigate(
                        fragment,
                        null,
                        getCustomNavOptions(R.anim.fade_in, R.anim.fade_out)
                    )
                }
            }
        }
    }

    // Define custom navigation options with animations
    private fun getCustomNavOptions(enterAnim: Int, exitAnim: Int): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(enterAnim)
            .setExitAnim(exitAnim)
            .build()
    }

    // Initialize navigation host fragment
    private fun initNavHostFragment() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        navController = navHostFragment.navController
    }

    // Handle result from QR code scanner
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Helper.CAMERA_PERMISSION_CODE) {
            val extras: Bundle? = data?.extras

            try {
                // Retrieve image bitmap from extras
                imageBitmap = extras?.get("data") as Bitmap

                // Display QR code dialog
                Helper.displayBorrowReturnDialog(
                    supportFragmentManager,
                    imageBitmap,
                    this@MainActivity
                )
            } catch (err: Exception) {
                // Handle errors
                displayToastMessage("An error occurred: ${err.localizedMessage}")
                return
            }
        }
    }

    // Display toast message
    private fun displayToastMessage(message: String) {
        Toast.makeText(
            this@MainActivity,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}