package com.graduation.teamwork.ui.base

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import androidx.annotation.IdRes
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewbinding.ViewBinding
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.graduation.teamwork.R
import com.graduation.teamwork.extensions.gone
import com.graduation.teamwork.extensions.visiable
import com.graduation.teamwork.ui.login.LoginActivity
import com.mukesh.permissions.EasyPermissions
import com.mukesh.permissions.OnPermissionListener
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.progress_dialog.view.*


/**
 * com.graduation.teamwork.ui.base
 * Created on 11/10/20
 */

interface IOnBackPressed {
    fun onBackPressed(): Boolean
}

abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {
    //TODO: data
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar)!! }
    val progress by lazy { findViewById<ConstraintLayout>(R.id.cslProgress)!! }
    lateinit var badge: BadgeDrawable
    protected open var binding: T? = null

    protected abstract fun setBinding(inflater: LayoutInflater): T
    protected abstract fun onViewReady(savedInstanceState: Bundle?)

    private val TAG = "__BaseActivity"

    protected val listPermissions =
        mutableListOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)

    protected val easyPermissions = EasyPermissions.Builder()
        .with(this) //Activity
        .listener(
            object : OnPermissionListener {
                override fun onAllPermissionsGranted(@NonNull permissions: List<String>) {
                    // Triggered if all permissions were given

                    permissionGrantedAll(permissions)
                }

                override fun onPermissionsGranted(@NonNull permissions: List<String>) {
                    // Lists all the permissions that were granted
//                    Log.d(TAG, "onPermissionsGranted: ${permissions.size}")
//                    permissions.forEach { Log.d(TAG, "onPermissionsGranted: $it") }
                    permissionGranted(permissions)
                }

                override fun onPermissionsDenied(@NonNull permissions: List<String>) {
                    // Lists all the permissions that were denied
                    permissionDenied(permissions)
//                    Log.d(TAG, "onPermissionsDenied: ${permissions.size}")
//                    permissions.forEach { Log.d(TAG, "onPermissionsDenied: $it") }
                }
            })
        .build()

    open fun permissionGrantedAll(permissions: List<String>) {

    }

    open fun permissionGranted(permissions: List<String>) {

    }

    open fun permissionDenied(permissions: List<String>) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = this.setBinding(layoutInflater)
        setContentView(binding?.root)

        onViewReady(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun setTitle(titleId: Int) {
        title = getString(titleId)
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        toolbar.title = title
    }

    fun setBadge(
        bottomView: BottomNavigationView,
        @IdRes menuId: Int,
        number: Int,
        visiable: Boolean = true
    ) {
        badge = bottomView.getOrCreateBadge(menuId)
            .apply {
                isVisible = visiable

//                setNumber(number)
            }

    }

    fun clearBadge() {
        if (::badge.isInitialized) {
            badge.run {
                isVisible = false
                clearNumber()
            }
        }
    }

    fun showProgress(message: String? = null) {
        if (message != null) {
            progress.tvProgress.text = message
        }
        progress.visiable()
    }

    fun hideProgres() {
        progress.tvProgress.text = getString(R.string.loading_message)
        progress.gone()
    }

//    protected fun getUser(): DtUser? =
//        if (Prefs.getString(Constant.PREFS.USER.value, null) != null) {
//            Prefs.getString(Constant.PREFS.USER.value, null).fromDtUser()
//        } else null
//
//
//    protected fun saveUser(user: DtUser) {
//        Prefs.putString(Constant.PREFS.USER.value, user.toJson())
//    }

    protected open fun showBackButton(show: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(show)
    }

    protected fun showAlert(
        title: String = "",
        message: String,
        strOk: String = "OK",
        strCancel: String = "CANCEL",
        onCancel: () -> Unit,
        onOk: () -> Unit
    ) {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(strOk) { dialog, _ ->
                onOk()
                dialog.dismiss()
            }
            .setNegativeButton(strCancel) { dialog, _ ->
                onCancel()
                dialog.dismiss()
            }
            .show()
    }

    protected fun showAlert(
        title: String = "SUCCESS",
        message: String,
        strOk: String = "OK",
        onOk: () -> Unit
    ) {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(strOk) { _, _ ->
                onOk()
            }
            .setCancelable(false)
            .show()
    }

}