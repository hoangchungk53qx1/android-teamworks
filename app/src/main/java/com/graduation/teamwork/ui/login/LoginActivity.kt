package com.graduation.teamwork.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.graduation.teamwork.R
import com.graduation.teamwork.databinding.ActLoginBinding
import com.graduation.teamwork.extensions.setVisiable
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.ui.base.BaseActivity
import com.graduation.teamwork.ui.main.MainActivity
import com.graduation.teamwork.utils.PrefsManager
import com.graduation.teamwork.extensions.showSnackbar
import com.graduation.teamwork.extensions.showToast
import kotlinx.android.synthetic.main.act_login.*
import kotlinx.android.synthetic.main.act_login.view.*
import org.koin.android.ext.android.inject


class LoginActivity : BaseActivity<ActLoginBinding>() {

    private val viewModel: LoginViewModel by inject()
    private val prefs: PrefsManager by inject()

    private val TAG = "__LoginActivity"

    // DATA
    private lateinit var userDefault: DtUser
    private var isLogin = true

    override fun setBinding(inflater: LayoutInflater): ActLoginBinding =
        ActLoginBinding.inflate(inflater)

    override fun onViewReady(savedInstanceState: Bundle?) {
        setViews()
        setupListener()
    }

    private fun setViews() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.blackColor)

        //auto login
        if (prefs.getUser() != null) {
            userDefault = prefs.getUser()!!

            binding?.edtMail?.setText(userDefault.mail!!)
            binding?.edtPassword?.setText(userDefault.password!!)

            viewModel.loginWithMail(
                userDefault.mail!!,
                userDefault.password!!
            )

            showProgress(getString(R.string.loading_message_login))

            turnOffClicked(isEnable = false)
        }

    }

    private fun turnOffClicked(isEnable: Boolean) {
        binding?.run {
            btnLogin.isEnabled = isEnable
            edtMail.isEnabled = isEnable
            edtPassword.isEnabled = isEnable
            edtUsername.isEnabled = isEnable
        }
    }

    private fun setLoginOrRegister(isLogin: Boolean) {
        binding?.run {
            rlUsername.setVisiable(!isLogin)
            viewLine.setVisiable(!isLogin)
            if (isLogin) {
                tvSuggess.text = getString(R.string.suggest_register)
                btnLogin.text = getString(R.string.login)
                btnSignUp.text = getString(R.string.sign_up)
            } else {
                tvSuggess.text = getString(R.string.suggest_login)
                btnLogin.text = getString(R.string.register)
                btnSignUp.text = getString(R.string.sign_in)
            }
        }

        this.isLogin = isLogin

    }

    private fun setupListener() {
        // view
        binding?.btnLogin?.setOnClickListener {

            if (this.isLogin) {
                val password = binding?.edtPassword?.text.toString()
                val email = binding?.edtMail?.text.toString()

                if (password.isNullOrBlank() || email.isNullOrBlank()) {
                    showSnackbar(it, "Vui lòng nhập đầy đủ thông tin")
                    return@setOnClickListener
                }

                viewModel.loginWithMail(
                    binding?.edtMail?.text.toString(),
                    binding?.edtPassword?.text.toString()
                )
                showProgress("Login...")
                turnOffClicked(isEnable = false)
            } else {
                val username = binding?.edtUsername?.text.toString()
                val password = binding?.edtPassword?.text.toString()
                val email = binding?.edtMail?.text.toString()

                if (username.isNullOrBlank() || password.isNullOrBlank() || email.isNullOrBlank()) {
                    showSnackbar(it, "Vui lòng nhập đầy đủ thông tin")
                    return@setOnClickListener
                }

                viewModel.addUser(
                    username, password, email
                )
                showProgress("Register...")
                turnOffClicked(isEnable = false)
            }
        }

        binding?.run {
            btnSignUp.setOnClickListener {
                setLoginOrRegister(!isLogin)
            }
        }

        // view model
        viewModel.resources.observe(this, {
            hideProgres()
            turnOffClicked(isEnable = true)
            if (it.data?.firstOrNull() != null) {
                // save data

                prefs.saveUser(it.data.first())

                Intent(this, MainActivity::class.java).also { it ->
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }

            } else {
                binding?.let { it1 ->
                    showSnackbar(
                        it1.root,
                        getString(R.string.notifi_error_wifi),
                        Snackbar.LENGTH_LONG
                    )
                }
            }
        })
    }
}
