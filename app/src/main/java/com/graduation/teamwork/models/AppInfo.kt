package com.graduation.teamwork.models

import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class AppInfo(
    val appName: String? = null,
    val appPkgName: String? = null,
    val appIcon:  @RawValue Drawable? = null,
    var isSelectedForStats: Boolean? = null,

):Parcelable