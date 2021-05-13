package com.graduation.teamwork.utils

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import com.bumptech.glide.Glide
import com.graduation.teamwork.R

class DialogManager {

    fun singleChoice(context: Context, title: String, values: List<String>, onResult: (value: String) -> Unit) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setItems(values.toTypedArray()) { dialog, which ->
                onResult(values[which])
                dialog.dismiss()
            }

            show()
        }
    }

    fun infoDialog(context: Context, title: String, messenger: String? = null) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            if (messenger != null) {
                setMessage(messenger)
            }

            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

            show()
        }
    }

    fun inputDialog(
        context: Context,
        title: String,
        textHint: String? = null,
        handlerOk: (data: String) -> Unit,
        handlerCancel: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(context).apply {
            val input = EditText(context).apply {
                inputType = InputType.TYPE_CLASS_TEXT
            }
            if (textHint != null) {
                input.hint = textHint
            }

            setTitle(title)
            setView(input)
            setPositiveButton("OK") { dialog, _ ->
                handlerOk(input.text.toString())

                dialog.dismiss()
            }

            setNegativeButton("CANCEL") { dialog, _ ->
                if (handlerCancel != null) {
                    handlerCancel()
                }
                dialog.dismiss()
            }
            show()
        }
    }

    fun inputDialog(
        context: Context,
        @StringRes title: Int,
        @StringRes textHint: Int? = null,
        handlerOk: (data: String) -> Unit,
        handlerCancel: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(context).apply {
            val input = EditText(context).apply {
                inputType = InputType.TYPE_CLASS_TEXT
            }
            if (textHint != null) {
                input.setHint(textHint)
            }

            setTitle(title)
            setView(input)
            setPositiveButton("OK") { dialog, _ ->
                handlerOk(input.text.toString())

                dialog.dismiss()
            }

            setNegativeButton("CANCEL") { dialog, _ ->
                if (handlerCancel != null) {
                    handlerCancel()
                }
                dialog.dismiss()
            }

            show()
        }
    }

    fun addRoomDialog(
        context: Context,
        inflater: LayoutInflater,
        handlerOK: (data: String) -> Unit,
        handlerCancel: (() -> Unit)? = null
    ) {
        val alertLayout = inflater.inflate(R.layout.dialog_addroom, null)
        val btnOk = alertLayout.findViewById<Button>(R.id.btnDialogAccept)
        val btnCancel = alertLayout.findViewById<Button>(R.id.btnDialogCancel)
        val editText = alertLayout.findViewById<EditText>(R.id.edtDialogText)
        val imageView = alertLayout.findViewById<ImageView>(R.id.imgDialogIcon)
        val tvTitle = alertLayout.findViewById<TextView>(R.id.tvDialogTitle)

        tvTitle.text = "Thêm phòng mới"
        Glide
            .with(context)
            .load(R.drawable.ic_add_room)
            .centerInside()
            .into(imageView)

        val alert = AlertDialog.Builder(context)
        alert.setView(alertLayout)
        alert.show()

        btnOk.setOnClickListener {
            val text = editText.text.toString()
        }

        btnCancel.setOnClickListener {
        }

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