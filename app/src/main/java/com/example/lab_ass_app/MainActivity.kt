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
import com.example.lab_ass_app.ui.Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavHostFragment()
        initNavDrawer()
    }

    private fun initNavDrawer() {
        binding.apply {
            Helper.drawerLayoutInstance = drawerLayout
            Helper.navDrawerInstance = navDrawer

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

    private fun navigate(fragment: Int, drawerItem: Int) {
        binding.apply {
            drawerLayout.closeDrawer(GravityCompat.START)
            navDrawer.setCheckedItem(drawerItem)
            CoroutineScope(Dispatchers.IO).launch {
                delay(500)
                withContext(Dispatchers.Main) {
                    navController.navigate(fragment, null, getCustomNavOptions(R.anim.fade_in, R.anim.fade_out))
                }
            }
        }
    }

    private fun getCustomNavOptions(enterAnim: Int, exitAnim: Int): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(enterAnim)
            .setExitAnim(exitAnim)
            .build()
    }

    // For QR code
    private var imageBitmap: Bitmap? = null

    private fun initNavHostFragment() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        navController = navHostFragment.navController
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Helper.CAMERA_PERMISSION_CODE) {
            val extras: Bundle? = data?.extras

            try {
                imageBitmap = extras?.get("data") as Bitmap

                Helper.displayBorrowReturnDialog(
                    supportFragmentManager,
                    imageBitmap,
                    this@MainActivity
                )
            } catch (err: Exception) {
                displayToastMessage("An error occurred: ${err.localizedMessage}")

                return
            }
        }
    }

    private fun displayToastMessage(message: String) {
        Toast.makeText(
            this@MainActivity,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}