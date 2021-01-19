package com.graduation.teamwork.utils

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.widget.EditText
import androidx.annotation.StringRes

class DialogManager {

    fun inputDialog(context: Context, title: String, textHint: String?, handlerOk: (data: String) -> Unit, handlerCancel: () -> Unit) {
        val builder = AlertDialog.Builder(context).apply {
            setTitle(title)

            val input = EditText(context)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (textHint != null) {
                input.hint = textHint
            }
            setView(input)
            setPositiveButton("OK") { dialog, _ ->
                handlerOk(input.text.toString())

                dialog.dismiss()
            }

            setNegativeButton("CANCEL") { dialog, _ ->
                handlerCancel()
                dialog.dismiss()
            }
        }

        builder.show()
    }

    fun inputDialog(context: Context, @StringRes title: Int, @StringRes textHint: Int?, handlerOk: (data: String) -> Unit, handlerCancel: () -> Unit) {
        val builder = AlertDialog.Builder(context).apply {
            setTitle(title)

            val input = EditText(context)
            input.inputType = InputType.TYPE_CLASS_TEXT
            if (textHint != null) {
                input.setHint(textHint)
            }
            setView(input)
            setPositiveButton("OK") { dialog, _ ->
                handlerOk(input.text.toString())

                dialog.dismiss()
            }

            setNegativeButton("CANCEL") { dialog, _ ->
                handlerCancel()
                dialog.dismiss()
            }
        }

        builder.show()
    }

//    fun inputDialog(context: Context, title: String, message: String, handlerOk: (data: String) -> Unit, handlerCancel: () -> Unit) {
//        val builder = AlertDialog.Builder(context).apply {
//            setTitle(title)
//            setMessage(message)
//
//            val input = EditText(context)
//            input.inputType = InputType.TYPE_CLASS_TEXT
//
//            setView(input)
//            setPositiveButton("OK") { dialog, _ ->
//                handlerOk(input.text.toString())
//
//                dialog.dismiss()
//            }
//
//            setNegativeButton("CANCEL") { dialog, _ ->
//                handlerCancel()
//                dialog.dismiss()
//            }
//        }
//
//        builder.show()
//    }
}