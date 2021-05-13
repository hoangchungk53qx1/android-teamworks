package com.graduation.teamwork.models.firebase

import com.graduation.teamwork.data.local.Constant

data class InfoNoti(
    val data: String,
    val content: String,
    val timestamp: String,
    val timestampL: Long,
    val category: Constant.NotifyType
)