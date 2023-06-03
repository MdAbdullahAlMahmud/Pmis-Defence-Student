package com.mkrlabs.pmisstudent.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.mkrlabs.pmisstudent.R
import com.mkrlabs.pmisstudent.databinding.ActivityHomeBinding
import com.mkrlabs.pmisstudent.fragment.HomeFragment
import com.mkrlabs.pmisstudent.util.Constant
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    lateinit var activityHomeBinding: ActivityHomeBinding
    lateinit var navController: NavController

    private lateinit var appBarConfiguration: AppBarConfiguration

    lateinit var  firebase : FirebaseFirestore
    lateinit var  mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityHomeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(activityHomeBinding.root)
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


        saveCurrentUserToken()

    }

    public fun openNavigationDrawer(){
        activityHomeBinding.drawerLayout.open()
    }


    private fun saveCurrentUserToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FirebaseToken", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            Log.d("FirebaseToken", token)
            uploadTokenToFirebase(token)

        })

        Firebase.messaging.subscribeToTopic("group_task")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Log.d("FirebaseToken", msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }


    }

    private fun  uploadTokenToFirebase(token:String){

        val map = HashMap<String,String>()

        map.put("deviceToken",token)
        mAuth.currentUser?.apply {
            firebase.collection(Constant.USER_NODE)
                .document(uid)
                .set(map, SetOptions.merge())
                .addOnSuccessListener {
                    Log.v("FirebaseToken", token)

                }.addOnFailureListener {
                    Log.v("FirebaseToken", "Failed to generate Firebase Token")
                }
        }




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