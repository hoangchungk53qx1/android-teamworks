package com.graduation.teamwork.utils

import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

open class DayAxisValueFormatter(private val applist:List<String>) : ValueFormatter() {
    private var appList: List<String> = applist

    private var chart: BarLineChartBase<*>? = null

    override fun getFormattedValue(value: Float, axis: AxisBase?): String? {

        //Insert code here to return value from your custom array or based on some processing
        return appList[value.toInt()]
    }
}