package com.example.lab_ass_app.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
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
import com.bumptech.glide.Glide
import com.example.lab_ass_app.MainActivity
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentHomeBinding
import com.example.lab_ass_app.databinding.FragmentListBinding
import com.example.lab_ass_app.ui.account.login.LoginFragment
import com.example.lab_ass_app.ui.account.login.LoginViewModel
import com.example.lab_ass_app.ui.account.login.google_facebook_bottom_dialog.InputLRNFragment
import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.BorrowReturnDialogFragment
import com.example.lab_ass_app.ui.main.student_teacher.home.HomeViewModel
import com.example.lab_ass_app.ui.main.student_teacher.list.ListViewModel
import com.example.lab_ass_app.utils.models.ItemFullInfoModel
import com.example.lab_ass_app.utils.models.ItemInfoModel
import com.facebook.login.LoginManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.system.exitProcess

object Helper {
    //  Encapsulation principle for Activity reference
    var mainActivityInstance: WeakReference<Activity>? = null

    //  HomeBinding for UI update
    @SuppressLint("StaticFieldLeak")
    var homeBinding: FragmentHomeBinding? = null

    //  ListBinding for UI update
    var listBinding: FragmentListBinding? = null

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
    @SuppressLint("StaticFieldLeak")
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
                        setCancelable(false)
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
    fun displayCustomDialogForNoInternet(
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
                        setCancelable(false)
                        findViewById<ConstraintLayout>(R.id.clMain)?.minWidth = minWidth
                        findViewById<ConstraintLayout>(R.id.clMainSelectedItem)?.minWidth = minWidth
                        findViewById<TextView>(R.id.tvNoInternetOk)?.setOnClickListener {
                            exitProcess(0)
                        }
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

    @SuppressLint("ObsoleteSdkInt")
    fun displayCustomDialog(
        activity: Activity,
        layoutDialog: Int,
        itemFullInfoModel: ItemFullInfoModel,
        storage: FirebaseStorage,
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
                        findViewById<ConstraintLayout>(R.id.clMainSelectedItem)?.minWidth = minWidth
//                        findViewById<ConstraintLayout>(R.id.clMain)?.setOnClickListener {
//                            dismiss()
//                        }
                        findViewById<ImageView>(R.id.ivExitDialog)?.setOnClickListener {
                            dismiss()
                        }

                        //  Content
                        val gsReference = storage.getReferenceFromUrl("gs://labass-app.appspot.com/${itemFullInfoModel.itemImageLink}.jpg")
                        val selectedItemImage = findViewById<ImageView>(R.id.ivSelectedItemSize)
                        Glide.with(activity.applicationContext)
                            .load(gsReference)
                            .into(selectedItemImage)
                        findViewById<TextView>(R.id.tvSelectedItemTitle).text = itemFullInfoModel.itemName
                        findViewById<TextView>(R.id.tvSelectedItemCategory).text = itemFullInfoModel.itemCategory
                        findViewById<TextView>(R.id.tvSelectedItemContent).text = itemFullInfoModel.itemDescription
                        findViewById<TextView>(R.id.tvSelectedItemStatus).text = itemFullInfoModel.itemStatus
                        if (itemFullInfoModel.itemStatus == "Available") {
                            findViewById<TextView>(R.id.tvSelectedItemStatus).setTextColor(ContextCompat.getColor(activity, R.color.Theme_green))
                        } else {
                            findViewById<TextView>(R.id.tvSelectedItemStatus).setTextColor(ContextCompat.getColor(activity, R.color.Theme_light_red))
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
    }

    @SuppressLint("ObsoleteSdkInt")
    fun displayCustomDialog(
        activity: Activity,
        layoutDialog: Int,
        title: String,
        content: String,
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
                        findViewById<ConstraintLayout>(R.id.clMain)?.minWidth = minWidth
                        findViewById<TextView>(R.id.tvDialogOk)?.setOnClickListener {
                            dialog?.dismiss()
                        }
                        findViewById<TextView>(R.id.tvDialogTitle)?.text = title
                        findViewById<TextView>(R.id.tvDialogContent)?.text = content
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

    @SuppressLint("ObsoleteSdkInt")
    fun displayCustomDialogForSuccessBorrow(
        activity: Activity,
        layoutDialog: Int,
        minWidthPercentage: Float = 0.75f
    ) {
        dialog?.dismiss()
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
                    setCancelable(true)
                    window!!.attributes.windowAnimations = R.style.animation

                    // Calculate the minWidth in pixels based on the percentage of the screen width
                    val screenWidth = getScreenWidth(activity)
                    val minWidth = (screenWidth * minWidthPercentage).toInt()

                    dialog?.apply {
                        findViewById<ConstraintLayout>(R.id.clMain)?.minWidth = minWidth
                        findViewById<TextView>(R.id.tvDialogOk)?.setOnClickListener {
                            dialog?.dismiss()
                        }
                        findViewById<ConstraintLayout>(R.id.clMain)?.setOnClickListener {
                            dialog?.dismiss()
                        }
                        findViewById<TextView>(R.id.tvDialogTitle)?.text = "Borrow notice"
                        findViewById<TextView>(R.id.tvDialogContent)?.text = "Borrow registration for the item has been successfully completed."
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

    @SuppressLint("ObsoleteSdkInt")
    fun displayCustomDialog(
        activity: Activity,
        listViewModel: ListViewModel,
        hostFragment: BottomSheetDialogFragment,
        layoutDialog: Int,
        firebaseFireStore: FirebaseFirestore,
        currentUserLRN: String,
        currentUserEmail: String,
        itemInfoModel: ItemInfoModel,
        homeViewModel: HomeViewModel,
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
                    findViewById<ConstraintLayout>(R.id.clMain)?.minWidth = minWidth
                    findViewById<TextView>(R.id.tvReturnCancel)?.setOnClickListener {
                        dismiss()
                    }
                    findViewById<TextView>(R.id.tvReturnConfirm)?.setOnClickListener {
                        displayCustomDialog(
                            hostFragment.requireActivity(),
                            R.layout.custom_dialog_loading
                        )
                        val filter = "$currentUserLRN-$currentUserEmail"

                        firebaseFireStore.collection("labass-app-borrow-log")
                            .whereGreaterThanOrEqualTo(FieldPath.documentId(), filter)
                            .whereLessThan(FieldPath.documentId(), filter + '\uF7FF')
                            .whereEqualTo("modelItemCode", itemInfoModel.modelCode)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                if (querySnapshot.documents.isEmpty()) {
                                    dismissDialog()
                                    displayCustomDialog(
                                        hostFragment.requireActivity(),
                                        R.layout.custom_dialog_notice,
                                        "Return Unsuccessful",
                                        "The scanned item is not currently listed in your active borrowing records."
                                    )
                                } else {
                                    for (document in querySnapshot.documents) {
                                        document.reference.delete()
                                    }

                                    homeViewModel.retrieveBorrowedItemInfoFromDB(
                                        hostFragment,
                                        homeBinding!!,
                                        listViewModel,
                                        activity
                                    )
                                    changeReturnedItemStatus(firebaseFireStore, itemInfoModel, hostFragment)
                                    dismiss()
                                }
                            }.addOnFailureListener { exception ->
                                Toast.makeText(
                                    hostFragment.requireContext(),
                                    "An error occurred: ${exception.localizedMessage}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e(TAG, "displayCustomDialog: ${exception.message}")
                            }
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

    private fun changeReturnedItemStatus(
        firebaseFireStore: FirebaseFirestore,
        itemInfoModel: ItemInfoModel,
        hostFragment: BottomSheetDialogFragment
    ) {
        firebaseFireStore.collection("labass-app-item-description")
            .document(itemInfoModel.modelCode)
            .update("modelStatus", "Available")
            .addOnSuccessListener {
                dismissDialog()
//                displayCustomDialog(
//                    hostFragment.requireActivity(),
//                    R.layout.custom_dialog_notice,
//                    "Return Successful",
//                    "The return process has been successfully completed for the borrowed item"
//                )
                Toast.makeText(
                    hostFragment.requireContext(),
                    "Return Successful: Kindly check your profile status.",
                    Toast.LENGTH_LONG
                ).show()
            }.addOnFailureListener { exception ->
                displayToastMessage(hostFragment.requireContext(), "An error occurred: ${exception.localizedMessage}")
                Log.e(TAG, "changeReturnedItemStatus: ${exception.message}")
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
        currentUserEmail: String,
        currentUserType: String,
        currentUserID: String
    ) {
        BorrowReturnDialogFragment(
            bitmap,
            mainActivity,
            itemInfoModel,
            currentUserLRN,
            currentUserEmail,
            currentUserType,
            currentUserID
        ).show(fragmentManager, "BorrowReturn_Dialog")
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
        Log.d(Constants.TAG, "camera-path: 1")
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            activity.startActivityForResult(intent, 1)
            Log.d(Constants.TAG, "camera-path: 2")
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

    fun getBorrowTimeDifference(modelBorrowDateTime: String, modelBorrowDeadlineDateTime: String): String {
        val borrowDeadlineDateTime = modelBorrowDeadlineDateTime.split(",")

        val borrowDeadlineDateTimeFinal = "${borrowDeadlineDateTime[0]} ${borrowDeadlineDateTime[1].replace("\\s*:\\s*".toRegex(), ":")}"
        val currentDateTime = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(Date())

        return calculateDateTimeDifferenceInMinutes(currentDateTime, borrowDeadlineDateTimeFinal).toString()
    }

    private fun calculateDateTimeDifferenceInMinutes(dateTime1: String, dateTime2: String): Long {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())

        // Parse date-time strings to Date objects
        val parsedDateTime1 = dateFormat.parse(dateTime1)
        val parsedDateTime2 = dateFormat.parse(dateTime2)

        // Calculate time difference in milliseconds
        val timeDifference = parsedDateTime2!!.time - parsedDateTime1!!.time

        // Convert milliseconds to minutes
        return timeDifference / (60 * 1000)
    }

    private var userFilter: String = ""

    fun checkPastDue(auth: FirebaseAuth, firebaseFireStore: FirebaseFirestore, cvPastDueExist: CardView) {
        if (userFilter.isEmpty()) {
            val userID = auth.currentUser?.uid

            firebaseFireStore.collection("labass-app-user-account-initial")
                .document(userID.toString())
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val userEmail = documentSnapshot.get("userEmailModel")
                    val userLRN = documentSnapshot.get("userLRNModel")

                    val filter = "$userLRN-$userEmail"
                    userFilter = filter

                    firebaseFireStore.collection("labass-app-borrow-log")
                        .whereGreaterThanOrEqualTo(FieldPath.documentId(), userFilter)
                        .whereLessThan(FieldPath.documentId(), userFilter + '\uF7FF')
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (querySnapshot.documents.isNotEmpty()) {
                                val itemDeadlines: ArrayList<String> = ArrayList()

                                for (document in querySnapshot.documents) {
                                    itemDeadlines.add(document.get("modelBorrowDeadlineDateTime").toString())
                                }

                                for (itemDeadline in itemDeadlines) {
                                    if (getBorrowTimeDifference("", itemDeadline).toLong() <= 0) {
                                        cvPastDueExist.visibility = View.VISIBLE
                                    }
                                }
                            }
                        }
                }
        } else {
            firebaseFireStore.collection("labass-app-borrow-log")
                .whereGreaterThanOrEqualTo(FieldPath.documentId(), userFilter)
                .whereLessThan(FieldPath.documentId(), userFilter + '\uF7FF')
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.documents.isNotEmpty()) {
                        val itemDeadlines: ArrayList<String> = ArrayList()

                        for (document in querySnapshot.documents) {
                            itemDeadlines.add(document.get("modelBorrowDeadlineDateTime").toString())
                        }

                        for (itemDeadline in itemDeadlines) {
                            if (getBorrowTimeDifference("", itemDeadline).toLong() <= 0) {
                                cvPastDueExist.visibility = View.VISIBLE
                            }
                        }
                    }
                }
        }
    }
}