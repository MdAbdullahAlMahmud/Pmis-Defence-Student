package com.mkrlabs.pmisstudent.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.mkrlabs.pmisstudent.BuildConfig
import com.mkrlabs.pmisstudent.R
import com.mkrlabs.pmisstudent.databinding.ActivityHomeBinding
import com.mkrlabs.pmisstudent.fragment.HomeFragment
import com.mkrlabs.pmisstudent.util.CommonFunction
import com.mkrlabs.pmisstudent.util.Constant
import com.mkrlabs.pmisstudent.util.SharedPref
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    lateinit var activityHomeBinding: ActivityHomeBinding
    lateinit var navController: NavController

    private lateinit var appBarConfiguration: AppBarConfiguration

    lateinit var  firebase : FirebaseFirestore
    lateinit var  mAuth: FirebaseAuth
    lateinit var  sharedPref: SharedPref


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityHomeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(activityHomeBinding.root)
        sharedPref = SharedPref(this)
        firebase = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        navController = findNavController(R.id.newsNavHostFragment)

       /* appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.home_fragment,
                R.id.profile_fragment
            ),
            activityHomeBinding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)*/

        activityHomeBinding.navView.setupWithNavController(navController)

        if (Build.VERSION.SDK_INT >= 33) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            hasNotificationPermissionGranted = true
        }

        setUpNavigationItemClickListener()




    }

    private fun setUpNavigationItemClickListener(){

        activityHomeBinding.navigationDrawerItem.navRateTv.setOnClickListener{
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }
        }

        activityHomeBinding.navigationDrawerItem.navLogoutTV.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes") { dialog, which ->
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(R.id.action_homeFragment_to_loginFragment)

                } // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }



        activityHomeBinding.navigationDrawerItem.navDeveloperTV.setOnClickListener {
            closeNavigationDrawer()
            navController.navigate(R.id.action_homeFragment_to_informationFragment)
        }

        activityHomeBinding.navigationDrawerItem.navVersion.setOnClickListener {

            val versionName = BuildConfig.VERSION_NAME
            val  appName = resources.getString(R.string.app_name)
            val appNameWithVersion = " $appName $versionName"
            CommonFunction.infoToast(this@HomeActivity,appNameWithVersion)
        }



    }

    public fun openNavigationDrawer(){
        activityHomeBinding.drawerLayout.open()
    }

    fun closeNavigationDrawer(){
        activityHomeBinding.drawerLayout.closeDrawers()
    }




    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasNotificationPermissionGranted = isGranted
            if (!isGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                            showNotificationPermissionRationale()
                        } else {
                            showSettingDialog()
                        }
                    }
                }
            } else {
                Toast.makeText(applicationContext, "notification permission granted", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private fun showSettingDialog() {
        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle("Notification Permission")
            .setMessage("Notification permission is required, Please allow notification permission from setting")
            .setPositiveButton("Ok") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showNotificationPermissionRationale() {

        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle("Alert")
            .setMessage("Notification permission is required, to show notification")
            .setPositiveButton("Ok") { _, _ ->
                if (Build.VERSION.SDK_INT >= 33) {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    var hasNotificationPermissionGranted = false
}