package com.graduation.teamwork.extensions

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.graduation.teamwork.R


fun isOnline(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    } else {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}

val Context.versionCode: Int
    get() = packageManager.getPackageInfo(packageName, 0).versionCode

fun Context.showToast(@StringRes res: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, res, duration).show()
}

fun Context.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun Context.showSnackbar(
	view: View,
	@StringRes message: Int,
	duration: Int = Snackbar.LENGTH_SHORT
) {
    Snackbar.make(view, message, duration).show()
}

fun Context.showSnackbar(
	view: View,
	message: String,
	duration: Int = Snackbar.LENGTH_SHORT
) {
    Snackbar.make(view, message, duration)
        .show()
}

fun Context.showSnackbar(
	view: View,
	message: String,
	duration: Int = Snackbar.LENGTH_SHORT,
	messageAction: String,
	actionOk: () -> Unit
) {
    Snackbar.make(view, message, duration)
        .setAction(messageAction) { _ ->
            actionOk()
        }
        .show()
}

fun Context.showSnackbar(
	view: View,
	@StringRes message: Int,
	duration: Int = Snackbar.LENGTH_SHORT,
	@StringRes messageAction: Int,
	actionOk: () -> Unit
) {
    Snackbar.make(view, message, duration)
        .setAction(messageAction) { _ ->
            actionOk()
        }
        .show()
}

/**
 * INTENT
 */

fun Context.shareApp(message: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, R.string.app_name)
        putExtra(Intent.EXTRA_TEXT, message)
    }

    startActivity(Intent.createChooser(shareIntent, "Choose One"))
}