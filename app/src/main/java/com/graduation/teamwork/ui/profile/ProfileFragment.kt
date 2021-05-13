package com.graduation.teamwork.ui.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.observe
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.crazylegend.imagepicker.pickers.SingleImagePicker
import com.graduation.teamwork.R
import com.graduation.teamwork.databinding.FragProfileBinding
import com.graduation.teamwork.extensions.hideKeyboard
import com.graduation.teamwork.extensions.setVisiable
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.ui.base.BaseFragment
import com.graduation.teamwork.ui.login.LoginActivity
import com.graduation.teamwork.ui.main.MainActivity
import com.graduation.teamwork.utils.PrefsManager
import com.graduation.teamwork.utils.eventbus.RxBus
import com.graduation.teamwork.utils.eventbus.RxEvent
import es.dmoral.toasty.Toasty
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension
import java.io.File

@KoinApiExtension
class ProfileFragment : BaseFragment<FragProfileBinding, MainActivity>() {
    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragProfileBinding = FragProfileBinding.inflate(inflater, container, false)

    /**
     * INJECT
     */
    private val viewModel: ProfileViewModel by inject()
    private val prefs: PrefsManager by inject()

    private var currentUser: DtUser? = prefs.getUser()
    private var typeEdit = ProfileEditType.NONE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupListener()
        getProfile()
    }

    private fun setupViews() {
        handler?.run {
            showFloatActionButton(isShow = false)
            showToolbar(isShow = true)
        }

        binding?.run {
            tvTotalGroup.text = "${prefs.getTotalGroup()}"
            tvTotalRoom.text = "${prefs.getTotalRoom()}"
        }
    }

    private fun setupDefaultData() {
        binding?.run {
            Glide.with(this@ProfileFragment)
                .load(R.drawable.ic_call_24dp)
                .into(imgPhoneNumber)
            Glide.with(this@ProfileFragment)
                .load(R.drawable.ic_location_24dp)
                .into(imgCity)
            Glide.with(this@ProfileFragment)
                .load(R.drawable.ic_email_24dp)
                .into(imgMail)
            Glide.with(this@ProfileFragment)
                .load(currentUser?.image?.url ?: R.drawable.default_avatar)
                .into(imgProfile)

            edtMail.hint = ""
            edtCity.hint = ""
            edtPhoneNumber.hint = ""
            edtMail.setText(currentUser?.mail ?: "")
            edtCity.setText(currentUser?.city ?: "")
            edtPhoneNumber.setText(currentUser?.numberphone ?: "")

            tvUserId.text = currentUser?._id ?: ""
            tvUsername.text = currentUser?.fullname ?: ""
            tvTitleProfile.text = getString(R.string.infomation)

            edtMail.isEnabled = false
            edtCity.isEnabled = false
            edtPhoneNumber.isEnabled = false
            tvConfirmUser.setVisiable(false)

            tvEditUser.setTextColor(Color.BLACK)
            tvEditPassword.setTextColor(Color.BLACK)
        }
    }

    private fun setupListener() {
        binding?.run {
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

            tvEditPassword.setOnClickListener {
                tvEditUser.setTextColor(Color.BLACK)
                tvEditPassword.setTextColor(Color.BLUE)
                isEditProfile(true, ProfileEditType.PASSWORD)
            }

            tvEditUser.setOnClickListener {
                tvEditUser.setTextColor(Color.BLUE)
                tvEditPassword.setTextColor(Color.BLACK)
                isEditProfile(true, ProfileEditType.PROFILE)
            }

            tvConfirmUser.setOnClickListener {
                val mailOrigin = currentUser?.mail ?: ""
                val cityOrigin = currentUser?.city ?: ""
                val numberPhoneOrigin = currentUser?.numberphone ?: ""

                val mailOrNewPasswordInput = edtMail.text.toString()
                val cityOrConfirmPasswordInput = edtCity.text.toString()
                val numberPhoneOldPasswordInput = edtPhoneNumber.text.toString()

                if (
                    (mailOrNewPasswordInput.isBlank() && cityOrConfirmPasswordInput.isBlank() && numberPhoneOldPasswordInput.isBlank())
                    or
                    (mailOrNewPasswordInput == mailOrigin && cityOrConfirmPasswordInput == cityOrigin && numberPhoneOldPasswordInput == numberPhoneOrigin)
                ) {
                    setupDefaultData()
                } else if (typeEdit == ProfileEditType.PASSWORD &&
                    (mailOrNewPasswordInput.isBlank() || cityOrConfirmPasswordInput.isBlank() || numberPhoneOldPasswordInput.isBlank())
                ) {
                    Toasty.warning(
                        context?.applicationContext!!,
                        "This field is required",
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                } else {
                    if (typeEdit == ProfileEditType.PROFILE) {
                        viewModel.updateInfoUser(
                            id = currentUser?._id!!,
                            mail = mailOrNewPasswordInput,
                            phone = numberPhoneOldPasswordInput,
                            city = cityOrConfirmPasswordInput
                        )
                    } else if (typeEdit == ProfileEditType.PASSWORD) {
                        viewModel.updatePassword(
                            id = currentUser?._id!!,
                            oldPassword = numberPhoneOldPasswordInput,
                            newPassword = mailOrNewPasswordInput
                        )
                    }

                    edtCity.hideKeyboard()
                    edtMail.hideKeyboard()
                    edtPhoneNumber.hideKeyboard()
                    handler?.showProgress()

                }
            }
            rlLogout.setOnClickListener {
                viewModel.logout(currentUser!!._id!!)
                prefs.clearUser()
                Toasty.normal(requireContext(), "Tạm biệt ${currentUser?.fullname}").show()
                Intent(requireContext(), LoginActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            }

            with(viewModel) {
                resources.observe(viewLifecycleOwner) {
                    handler?.hideProgress()
                    if (!it.data.isNullOrEmpty() && it.data.isNotEmpty()) {

                        this@ProfileFragment.currentUser = it.data[0]
                        setupDefaultData()
                    }
                }

                resourceUpdate.observe(viewLifecycleOwner) {
                    handler?.hideProgress()

                    if (!it.data.isNullOrEmpty()) {
                        this@ProfileFragment.currentUser = it.data.first()
                        prefs.saveUser(it.data.first())
                    }

                    setupDefaultData()
                }

                resourceUpdatePassword.observe(viewLifecycleOwner) {
                    handler?.hideProgress()
                    if (!it.data?.data.isNullOrEmpty()) {
                        Toasty.success(
                            context?.applicationContext!!,
                            "Change password success",
                            Toast.LENGTH_SHORT,
                            true
                        ).show()

                        it.data?.data?.first()?.let {
                            prefs.saveUser(it)
                            this@ProfileFragment.currentUser = it
                        }
                    } else {
                        Toasty.error(
                            context?.applicationContext!!,
                            "Change password error!",
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                    setupDefaultData()
                }

                resourceUpdate.observe(viewLifecycleOwner) {
                    handler?.hideProgress()

                    if (!it.data.isNullOrEmpty()) {
                        Toasty.success(
                            context?.applicationContext!!,
                            "Update profile success",
                            Toast.LENGTH_SHORT,
                            true
                        ).show()

                        this@ProfileFragment.run {
                            currentUser = it.data[0]
                            setupDefaultData()
                            prefs.saveUser(currentUser!!)
                        }
                        RxBus.publishToPublishSubject(RxEvent.UpdateAvatar)
                    } else {
                        Toasty.error(
                            context?.applicationContext!!,
                            "Update profile error!",
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                }

                resourceUpdateAvatar.observe(viewLifecycleOwner) {
                    handler?.hideProgress()

                    if (!it.data.isNullOrEmpty()) {
                        Toasty.success(
                            context?.applicationContext!!,
                            "Up load Success",
                            Toast.LENGTH_SHORT,
                            true
                        ).show()

                        this@ProfileFragment.run {
                            currentUser = it.data[0]
                            prefs.saveUser(currentUser!!)
                        }

                        RxBus.publishToPublishSubject(RxEvent.UpdateAvatar)
                    } else {
                        Toasty.error(
                            context?.applicationContext!!,
                            "Change avatar error!",
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                    setupDefaultData()
                }
            }

        }
    }

    @KoinApiExtension
    private fun upload(uri: Uri?) {
        if (uri == null) {
            return
        }

        val file = File(getRealPathFromURI(uri) ?: "")

        val photoContent: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData(
            "image", file.name, photoContent
        )

        viewModel.updateAvatar(currentUser!!._id!!, part)
        handler?.showProgress()
    }

    private fun isEditProfile(
        isEdit: Boolean = false,
        type: ProfileEditType = ProfileEditType.NONE
    ) {
        typeEdit = type

        binding?.run {
            edtMail.isEnabled = isEdit
            edtCity.isEnabled = isEdit
            edtPhoneNumber.isEnabled = isEdit
            tvConfirmUser.setVisiable(isEdit)

            when (type) {
                ProfileEditType.PROFILE -> {
                    Glide.with(this@ProfileFragment)
                        .load(R.drawable.ic_call_24dp)
                        .into(imgPhoneNumber)

                    Glide.with(this@ProfileFragment)
                        .load(R.drawable.ic_location_24dp)
                        .into(imgCity)

                    Glide.with(this@ProfileFragment)
                        .load(R.drawable.ic_email_24dp)
                        .into(imgMail)

                    edtPhoneNumber.hint = "Nhập số điện thoại"
                    edtMail.hint = "Nhập email"
                    edtCity.hint = "Nhập thành phố"

                    edtPhoneNumber.setText(currentUser?.numberphone ?: "")
                    edtMail.setText(currentUser?.mail ?: "")
                    edtCity.setText(currentUser?.city ?: "")

                    tvTitleProfile.text = getString(R.string.infomation)
                }
                ProfileEditType.PASSWORD -> {
                    Glide.with(this@ProfileFragment)
                        .load(R.drawable.ic_key)
                        .into(imgPhoneNumber)

                    Glide.with(this@ProfileFragment)
                        .load(R.drawable.ic_key)
                        .into(imgCity)

                    Glide.with(this@ProfileFragment)
                        .load(R.drawable.ic_key)
                        .into(imgMail)

                    edtPhoneNumber.hint = "Mật khẩu cũ"
                    edtMail.hint = "Mật khẩu mới"
                    edtCity.hint = "Nhập lại mật khẩu"

                    edtPhoneNumber.setText("")
                    edtMail.setText("")
                    edtCity.setText("")

                    tvTitleProfile.text = "Password"
                }
                // NONE
                else -> {
                    Glide.with(this@ProfileFragment)
                        .load(R.drawable.ic_call_24dp)
                        .into(imgPhoneNumber)

                    Glide.with(this@ProfileFragment)
                        .load(R.drawable.ic_location_24dp)
                        .into(imgCity)

                    Glide.with(this@ProfileFragment)
                        .load(R.drawable.ic_email_24dp)
                        .into(imgMail)

                    tvTitleProfile.text = getString(R.string.infomation)
                }
            }
        }
    }

    private fun getProfile() {
        handler?.run {
            showFloatActionButton(isShow = false)
            showToolbar(isShow = false)
        }
        if (currentUser == null) {
            currentUser = prefs.getUser()
        } else {
            setupDefaultData()
        }
    }

    private fun getRealPathFromURI(contentUri: Uri?): String? {
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
        setupDefaultData()
    }
}