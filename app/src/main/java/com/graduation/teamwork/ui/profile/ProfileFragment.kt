package com.graduation.teamwork.ui.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.observe
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.crazylegend.imagepicker.pickers.SingleImagePicker
import com.graduation.teamwork.R
import com.graduation.teamwork.databinding.FragProfileBinding
import com.graduation.teamwork.extensions.gone
import com.graduation.teamwork.extensions.visiable
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.ui.base.BaseFragment
import com.graduation.teamwork.ui.login.LoginActivity
import com.graduation.teamwork.ui.main.MainActivity
import com.graduation.teamwork.utils.PrefsManager
import com.graduation.teamwork.utils.eventbus.RxBus
import com.graduation.teamwork.utils.eventbus.RxEvent
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.act_login.*
import kotlinx.android.synthetic.main.frag_profile.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.android.ext.android.inject
import java.io.File

class ProfileFragment : BaseFragment<FragProfileBinding, MainActivity>() {
    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragProfileBinding = FragProfileBinding.inflate(inflater, container, false)

    private val viewModel: ProfileViewModel by inject()
    private val prefs: PrefsManager by inject()

    // data
    companion object {
        const val PICK_PHOTO = 122
    }

    private var currentUser: DtUser? = prefs.getUser()

    private val TAG = "__ProfileFragment"

    //intent
    private val registerPhotoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent? = it.data

                if (data != null) {
                    val uri = data.data
                    upload(uri)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        getProfile()
        setUpListener()
    }

    @SuppressLint("ResourceAsColor")
//    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpListener() {
        // view
        binding?.run {
            rlLogout.setOnClickListener {
                if (currentUser != null) {
                    viewModel.logout(currentUser!!._id!!)
                    prefs.clearUser()
                    Intent(requireContext(), LoginActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                }
            }
            tvEditUser.setOnClickListener {
                tvConfirmUser.visiable()
                turnOffClicked(isEnable = true)
                tvEditUser.setTextColor(R.color.blackColor)
                tvEditPassword.setTextColor(R.color.blue)
                rlLogout.setTextColor(R.color.blue)
            }
            tvConfirmUser.setOnClickListener {
                if (currentUser != null) {
                    handler?.showProgress()
                    viewModel.updateInfoUser(
                        currentUser!!._id!!,
                        binding?.edtMail?.text.toString(),
                        binding?.edtPhoneNumber?.text.toString(),
                        binding?.edtCity?.text.toString()
                    )
                }
            }
            tvEditPassword.setOnClickListener {
                tvComfirmPassword.visiable()
                rlPasword.visiable()
                rlPersonalInfo.gone()
                tvEditUser.setTextColor(R.color.blue)
                tvEditPassword.setTextColor(R.color.blackColor)
                rlLogout.setTextColor(R.color.blue)
            }
            tvComfirmPassword.setOnClickListener {
                if (currentUser != null) {
                    handler?.showProgress()
                    val current = edtCurrentPassword.text.toString()
                    val newPasword = edtNewPassword.text.toString()
                    val confirmPassword = edtConfirmPassword.text.toString()
                    if (current.isBlank() || newPasword.isBlank() || confirmPassword.isBlank()) {
                        handler?.hideProgres()
                        Toasty.warning(
                            context?.applicationContext!!,
                            "This field is required",
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                        return@setOnClickListener
                    } else if (!newPasword.equals(confirmPassword)) {
                        handler?.hideProgres()
                        Toasty.warning(
                            context?.applicationContext!!,
                            "passwords do not match",
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                        return@setOnClickListener
                    } else {
                        viewModel.updatePassword(
                            currentUser!!._id!!,
                            binding?.edtCurrentPassword?.text.toString(),
                            binding?.edtConfirmPassword?.text.toString()
                        )
                    }
                } else {
                    viewModel.logout(currentUser!!._id!!)
                }
            }
            imgProfile.setOnClickListener {

                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@setOnClickListener
                }
                SingleImagePicker.showPicker(requireContext()) {
                    upload(it.contentUri)
                }

            }
        }

        // view model
        viewModel.resources.observe(viewLifecycleOwner, {
            handler?.hideProgres()
            if (!it.data.isNullOrEmpty() && it.data.isNotEmpty()) {

                this.currentUser = it.data[0]

                this.updateProfile(this.currentUser!!)

            }
        })
        viewModel.resourceUpdate.observe(viewLifecycleOwner, {
            handler?.hideProgres()
            if (!it.data.isNullOrEmpty() && it.data.isNotEmpty()) {
                turnOffClicked(isEnable = false)
                binding?.tvConfirmUser?.gone()

                this.currentUser = it.data[0]
                binding?.tvUsername?.text = currentUser!!.fullname
                binding?.idUser?.text = currentUser!!._id
                binding?.edtMail?.setText(currentUser!!.mail)
                binding?.edtCity?.setText(currentUser!!.city)
                binding?.edtPhoneNumber?.setText(currentUser!!.numberphone)
            }
        })
        viewModel.resourceUpdatePassword.observe(viewLifecycleOwner, { it ->
            handler?.hideProgres()
            if (!it.data?.data.isNullOrEmpty()) {
                Toasty.success(
                    context?.applicationContext!!,
                    "Change password success",
                    Toast.LENGTH_SHORT,
                    true
                ).show()

                it.data?.data?.first()?.let { it1 ->
                    prefs.saveUser(it1)
                }
            } else {
                Toasty.error(
                    context?.applicationContext!!,
                    "Change password error!",
                    Toast.LENGTH_SHORT,
                    true
                ).show()
            }
        })
        viewModel.resourceUpdateAvatar.observe(viewLifecycleOwner, {
            handler?.hideProgres()

            if (!it.data.isNullOrEmpty()) {
                Toasty.success(
                    context?.applicationContext!!,
                    "Up load Success",
                    Toast.LENGTH_SHORT,
                    true
                ).show()

                this.currentUser = it.data[0]

                this.updateProfile(this.currentUser!!)
                this.prefs.saveUser(this.currentUser!!)
                RxBus.publishToPublishSubject(RxEvent.UpdateAvatar)
            } else {
                Toasty.error(
                    context?.applicationContext!!,
                    "Change avatar error!",
                    Toast.LENGTH_SHORT,
                    true
                ).show()
            }
        })
    }

    private fun updateProfile(user: DtUser) {
        handler?.hideProgres()

        Glide.with(this)
            .load(user.image?.url)
            .placeholder(R.drawable.bg_demo_1)
            .into(img_profile)

        binding?.tvUsername?.text = user.fullname
        binding?.idUser?.text = user._id
        binding?.edtMail?.setText(user.mail)
        binding?.edtCity?.setText(user.city)
        binding?.edtPhoneNumber?.setText(user.numberphone)
    }

    private fun upload(uri: Uri?) {
        if (uri == null) {
            return
        }

        handler?.showProgress()
        val file = File(getRealPathFromURI(uri) ?: "")

        val photoContent: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val part = MultipartBody.Part.createFormData(
            "image", file.name, photoContent
        )

        viewModel.updateAvatar(currentUser!!._id!!, part)
    }

    private fun turnOffClicked(isEnable: Boolean) {
        binding?.run {
            binding?.edtMail?.isEnabled = isEnable
            binding?.edtPhoneNumber?.isEnabled = isEnable
            binding?.edtCity?.isEnabled = isEnable
        }
    }

    private fun setupViews() {
        handler?.run {
            showFloatActionButton(isShow = false)
            showToolbar(isShow = true)
            turnOffClicked(isEnable = false)
        }

        binding?.run {
            tvTotalGroup.text = "${prefs.getTotalGroup()}"
            tvTotalRoom.text = "${prefs.getTotalRoom()}"
        }
    }

    private fun getProfile() {
        handler?.run {
            showFloatActionButton(isShow = false)
            showToolbar(isShow = false)
        }
        if (currentUser == null) {
            currentUser = prefs.getUser()
        }

        if (currentUser != null) {
            this.updateProfile(this.currentUser!!)
        }
    }

    fun getRealPathFromURI(contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = contentUri?.let {
            CursorLoader(
                handler?.applicationContext!!,
                it,
                proj,
                null,
                null,
                null
            )
        }

        val cursor: Cursor? = loader?.loadInBackground()

        val columnIndex: Int? = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val result: String? = columnIndex?.let {
            cursor.getString(it)
        }
        cursor?.close()

        return result
    }


    override fun onResume() {
        super.onResume()
        if (currentUser == null) {
            handler?.showProgress()
        }
    }
}