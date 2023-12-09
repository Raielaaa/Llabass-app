package com.example.lab_ass_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    private fun initNavHostFragment() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        navController = navHostFragment.navController
    }
}