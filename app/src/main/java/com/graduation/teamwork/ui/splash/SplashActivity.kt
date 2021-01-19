package com.graduation.teamwork.ui.splash

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import androidx.annotation.NonNull
import com.graduation.teamwork.databinding.ActSplashBinding
import com.graduation.teamwork.ui.base.BaseActivity
import com.graduation.teamwork.ui.login.LoginActivity
import com.mukesh.permissions.EasyPermissions
import com.mukesh.permissions.OnPermissionListener


class SplashActivity : BaseActivity<ActSplashBinding>() {
    override fun setBinding(inflater: LayoutInflater): ActSplashBinding =
        ActSplashBinding.inflate(inflater)

    private val TAG = "__SplashActivity"

    override fun onViewReady(savedInstanceState: Bundle?) {
        if (easyPermissions.hasPermission(*listPermissions.toTypedArray())) {
            Log.d(TAG, "onViewReady: hasPermission")
            gotoMain()
        } else {
            Log.d(TAG, "onViewReady: REQUEST")
            easyPermissions.request(*listPermissions.toTypedArray())
        }
    }

    fun gotoMain() {
        Handler(Looper.getMainLooper())
            .postDelayed({
                Intent(this, LoginActivity::class.java).also {
                    it.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            }, 2000)
    }

    override fun permissionGrantedAll(permissions: List<String>) {
        gotoMain()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        easyPermissions.onRequestPermissionsResult(permissions, grantResults);

    }

}