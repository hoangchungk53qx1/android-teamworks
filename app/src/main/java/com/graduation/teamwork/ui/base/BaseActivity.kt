package com.graduation.teamwork.ui.base

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.IdRes
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kaopiz.kprogresshud.KProgressHUD
import com.mukesh.permissions.EasyPermissions
import com.mukesh.permissions.OnPermissionListener
import io.reactivex.disposables.CompositeDisposable


/**
 * com.graduation.teamwork.ui.base
 * Created on 11/10/20
 */

abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {

    protected open var binding: T? = null
    lateinit var progressHub: KProgressHUD
    lateinit var badge: BadgeDrawable

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    protected val listPermissions =
        mutableListOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
    protected val easyPermissions: EasyPermissions = EasyPermissions.Builder()
        .with(this) //Activity
        .listener(
            object : OnPermissionListener {
                override fun onAllPermissionsGranted(@NonNull permissions: List<String>) {
                    // Triggered if all permissions were given
                    permissionGrantedAll(permissions)
                }

                override fun onPermissionsGranted(@NonNull permissions: List<String>) {
                    // Lists all the permissions that were granted
                    permissionGranted(permissions)
                }

                override fun onPermissionsDenied(@NonNull permissions: List<String>) {
                    // Lists all the permissions that were denied
                    permissionDenied(permissions)

                }
            })
        .build()

    protected abstract fun setBinding(inflater: LayoutInflater): T
    protected abstract fun onViewReady(savedInstanceState: Bundle?)

    /* permission */
    open fun permissionGrantedAll(permissions: List<String>) {}

    open fun permissionGranted(permissions: List<String>) {}

    open fun permissionDenied(permissions: List<String>) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setBinding(layoutInflater)
        setContentView(binding?.root)

        progressHub = KProgressHUD.create(this)
        onViewReady(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    /**
     * BADGE
     */
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

    /**
     * PROGRESS
     */
    fun showProgress(label: String? = null) {
        if (label.isNullOrBlank()) {
            progressHub
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show()
        } else {
            progressHub
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(label)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show()
        }
    }

    fun hideProgress() {
        if (isShowProgress()) {
            progressHub.dismiss()
        }
    }

    fun isShowProgress() = progressHub.isShowing

    protected open fun showBackButton(isShow: Boolean = true) =
        supportActionBar?.setDisplayHomeAsUpEnabled(isShow)
}