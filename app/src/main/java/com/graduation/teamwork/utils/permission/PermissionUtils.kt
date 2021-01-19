package com.graduation.teamwork.utils.permission

import android.annotation.SuppressLint
import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telecom.TelecomManager
import android.widget.Toast

object PermissionUtils {
//    fun checkPermission(
//        activity: Activity,
//        permissionManager: PermissionManager,
//        requestCode: Int, onSuccess: (() -> Unit)? = null
//    ) {
//        if (!permissionManager.isDefaultSms()) {
//            showDefaultSmsDialog(activity, requestCode)
//        } else if (!permissionManager.isDefaultCall()) {
//            showDefaultDialerDialog(activity, onSuccess, requestCode)
//        } else {
//            onSuccess?.invoke()
//        }
//    }

    /**
     * This won't work unless we use startActivityForResult
     */
    private fun showDefaultSmsDialog(activity: Activity, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = activity.getSystemService(RoleManager::class.java) as RoleManager
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)
            activity.startActivityForResult(intent, requestCode)
        } else {
            val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, activity.packageName)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    @SuppressLint("WrongConstant")
    private fun showDefaultDialerDialog(
        activity: Activity,
        onSuccess: (() -> Unit)? = null,
        requestCode: Int
    ) {
        val packageName = activity.packageName
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (activity.getSystemService(TelecomManager::class.java)?.defaultDialerPackage != packageName) {
                val roleManager = activity.getSystemService(Context.ROLE_SERVICE) as RoleManager
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
                activity.startActivityForResult(intent, requestCode)
            } else {
                onSuccess?.invoke()
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val telecomManager = activity.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            val isAlreadyDefaultDialer = packageName == telecomManager.defaultDialerPackage
            if (isAlreadyDefaultDialer)
                return
            val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
            activity.startActivityForResult(intent, requestCode)
        } else {
            Toast.makeText(activity, "", Toast.LENGTH_LONG).show()
        }
    }
}
