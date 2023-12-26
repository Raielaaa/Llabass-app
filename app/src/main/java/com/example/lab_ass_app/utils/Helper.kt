package com.example.lab_ass_app.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.content.SharedPreferences.Editor
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.MainActivity
import com.example.lab_ass_app.R
import com.example.lab_ass_app.ui.account.login.LoginFragment
import com.example.lab_ass_app.ui.account.login.LoginViewModel
import com.example.lab_ass_app.ui.account.login.google_facebook_bottom_dialog.InputLRNFragment
import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.BorrowReturnDialogFragment
import com.facebook.login.LoginManager
import com.google.android.material.navigation.NavigationView
import java.lang.ref.WeakReference

object Helper {
    //  Encapsulation principle for Activity reference
    var mainActivityInstance: WeakReference<Activity>? = null

    fun setActivityReference(activity: Activity) {
        mainActivityInstance = WeakReference(activity)
    }

    fun getActivityReference(): Activity? {
        return mainActivityInstance?.get()
    }

    lateinit var drawerLayoutInstance: DrawerLayout
    lateinit var navDrawerInstance: NavigationView

    private var dialog: Dialog? = null
    val TAG: String = "MyTag"

    var hostFragmentInstanceForFacebookLogin: Fragment? = null
    var navControllerFromMain: NavController? = null

    @SuppressLint("ObsoleteSdkInt")
    fun displayCustomDialog(
        activity: Activity,
        layoutDialog: Int,
        minWidthPercentage: Float = 0.75f
    ) {
        try {
            if (!activity.isFinishing) {
                dialog = Dialog(activity)

                dialog?.apply {
                    setContentView(layoutDialog)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window!!.setBackgroundDrawable(ResourcesCompat.getDrawable(
                            activity.resources,
                            R.drawable.custom_dialog_bg,
                            null))
                    }
                    window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    setCancelable(false)
                    window!!.attributes.windowAnimations = R.style.animation

                    // Calculate the minWidth in pixels based on the percentage of the screen width
                    val screenWidth = getScreenWidth(activity)
                    val minWidth = (screenWidth * minWidthPercentage).toInt()

                    dialog?.apply {
                        setCancelable(true)
                        findViewById<ConstraintLayout>(R.id.clMain)?.minWidth = minWidth
                        findViewById<ConstraintLayout>(R.id.clMain)?.setOnClickListener {
                            dismiss()
                        }
                        findViewById<ConstraintLayout>(R.id.clMainSelectedItem)?.minWidth = minWidth
                        findViewById<ImageView>(R.id.ivExitDialog)?.setOnClickListener {
                            dismiss()
                        }

                        try {
                            findViewById<CardView>(R.id.cvQR).setOnClickListener {
                                takeQR(getActivityReference()!!)
                            }
                        } catch (ignored: Exception) { }
                    }
                    show()
                }
            }
        } catch (err: Exception) {
            Log.e(TAG, "displayCustomDialog: ${err.message}")
            displayToastMessage(
                activity,
                "Error: ${err.localizedMessage}"
            )
        }

        try {
            dialog?.findViewById<TextView>(R.id.tvDialogOk)?.setOnClickListener {
                dialog?.dismiss()
            }
        } catch (err: Exception) {
            Log.e(TAG, "displayCustomDialog: ${err.message}")
            displayToastMessage(
                activity,
                "Error: ${err.localizedMessage}"
            )
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    fun displayCustomDialog(
        hostFragment: Fragment,
        layoutDialog: Int,
        spinner: Spinner,
        loginFragment: LoginFragment,
        loginViewModel: LoginViewModel,
        loginProcess: String,
        minWidthPercentage: Float = 0.75f
    ) {
        try {
            dialog = Dialog(hostFragment.requireActivity())

            dialog?.apply {
                setContentView(layoutDialog)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window!!.setBackgroundDrawable(ResourcesCompat.getDrawable(
                        hostFragment.requireActivity().resources,
                        R.drawable.custom_dialog_bg,
                        null))
                }
                window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setCancelable(false)
                window!!.attributes.windowAnimations = R.style.animation

                // Calculate the minWidth in pixels based on the percentage of the screen width
                val screenWidth = getScreenWidth(hostFragment.requireActivity())
                val minWidth = (screenWidth * minWidthPercentage).toInt()

                dialog?.apply {
                    findViewById<TextView>(R.id.tvDialogOk)?.setOnClickListener {
                        dismiss()
                        InputLRNFragment(spinner, loginFragment, loginViewModel, loginProcess).show(hostFragment.parentFragmentManager, "LRN_Bottom_Dialog")
                    }
                    findViewById<ConstraintLayout>(R.id.clMain)?.minWidth = minWidth
                    findViewById<ConstraintLayout>(R.id.clMainSelectedItem)?.minWidth = minWidth
                    findViewById<ImageView>(R.id.ivExitDialog)?.setOnClickListener {
                        dismiss()
                    }
                }
                show()
            }
        } catch (err: Exception) {
            Log.e(TAG, "displayCustomDialog: ${err.message}")
            displayToastMessage(
                hostFragment.requireContext(),
                "Error: ${err.localizedMessage}"
            )
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    fun displayCustomDialog(
        activity: Activity,
        layoutDialog: Int,
        applicationContext: Context,
        minWidthPercentage: Float = 0.75f
    ) {
        try {
            if (!activity.isFinishing) {
                dialog = Dialog(activity)

                dialog?.apply {
                    setContentView(layoutDialog)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window!!.setBackgroundDrawable(ResourcesCompat.getDrawable(
                            activity.resources,
                            R.drawable.custom_dialog_bg,
                            null))
                    }
                    window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    setCancelable(false)
                    window!!.attributes.windowAnimations = R.style.animation

                    // Calculate the minWidth in pixels based on the percentage of the screen width
                    val screenWidth = getScreenWidth(activity)
                    val minWidth = (screenWidth * minWidthPercentage).toInt()

                    dialog?.apply {
                        setCancelable(false)
                        findViewById<ConstraintLayout>(R.id.clMain)?.minWidth = minWidth
                        findViewById<ConstraintLayout>(R.id.clMain)?.setOnClickListener {
                            LoginManager.getInstance().logOut()

                            //  Restarting the app
                            val ctx: Context = applicationContext
                            val pm: PackageManager = ctx.packageManager
                            val intent: Intent? = pm.getLaunchIntentForPackage(ctx.packageName)
                            val mainIntent = Intent.makeRestartActivityTask(intent?.component)
                            ctx.startActivity(mainIntent)
                            Runtime.getRuntime().exit(0)

                            dismiss()
                        }
                        findViewById<ConstraintLayout>(R.id.clMainSelectedItem)?.minWidth = minWidth
                    }
                    show()
                }
            }
        } catch (err: Exception) {
            Log.e(TAG, "displayCustomDialog: ${err.message}")
            displayToastMessage(
                activity,
                "Error: ${err.localizedMessage}"
            )
        }
    }

    fun displayBorrowReturnDialog(
        fragmentManager: FragmentManager,
        bitmap: Bitmap?,
        mainActivity: MainActivity,
        itemInfoModel: ItemInfoModel,
        currentUserLRN: String,
        currentUserEmail: String
    ) {
        BorrowReturnDialogFragment(bitmap, mainActivity, itemInfoModel, currentUserLRN, currentUserEmail).show(fragmentManager, "BorrowReturn_Dialog")
    }

    private fun getScreenWidth(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        val windowManager = activity.getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    private fun displayToastMessage(
        context: Context,
        message: String?
    ) {
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun dismissDialog() {
        dialog?.dismiss()
    }

    fun takeQR(activity: Activity) {
        //  Method for starting the camera
        cameraPermission(activity)
    }

    //  Requesting for permission
    private fun cameraPermission(activity: Activity) {
        val cameraPermission: String = android.Manifest.permission.CAMERA
        val permissionGranted: Int = PackageManager.PERMISSION_GRANTED

        if (ContextCompat.checkSelfPermission(activity, cameraPermission) != permissionGranted) {
            ActivityCompat.requestPermissions(activity, arrayOf(cameraPermission), 111)
        } else {
            takeImage(activity)
        }
    }

    //  Opening Camera if permission is granted
    private fun takeImage(activity: Activity) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            activity.startActivityForResult(intent, 1)
        } catch (err: Exception) {
            displayToastMessage(activity, err.localizedMessage)
            Log.e(TAG, "takeImage: ${err.message}", )
        }
    }

    var lrn: String = ""
    var userType: String = ""

    fun setUserTypeAndLRNForGoogleSignIn(
        lrn: String,
        userType: String
    ) {
        this.lrn = lrn
        this.userType = userType
    }
}