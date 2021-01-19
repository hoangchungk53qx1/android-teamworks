package com.graduation.teamwork.ui.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.graduation.teamwork.extensions.isOnline
import io.reactivex.disposables.CompositeDisposable

/**
 * com.graduation.teamwork.ui.base
 * Created on 11/10/20
 */

// https://stackoverflow.com/questions/58731415/android-view-binding-how-implement-binding-in-basic-activity-fragment

abstract class BaseFragment<T : ViewBinding, A : Any> : Fragment() {
    //TODO: data
    val compositeDisposable: CompositeDisposable = CompositeDisposable()
    protected open var binding: T? = null

    protected open var handler: A? = null //It's base activity

    protected abstract fun setBinding(inflater: LayoutInflater, container: ViewGroup?): T

    private val TAG = "__BaseFragmentActivity"
    override fun onAttach(context: Context) {
        super.onAttach(context)
        @Suppress("UNCHECKED_CAST")
        this.handler = this.activity as? A
        Log.d(TAG, "onAttach: OK")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = this.setBinding(inflater, container)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    fun isNetworkConnected(): Boolean {
        val context = binding!!.root.context

        return isOnline(context)
    }
//
//    protected fun getUser(): DtUser? {
//        return Prefs.getString(Constant.PREFS.USER.value, null).fromDtUser()
//    }
//
//    protected fun saveUser(user: DtUser) {
//        Prefs.putString(Constant.PREFS.USER.value, user.toJson())
//    }
//
//    protected fun clearUser() {
//        Prefs.putString(Constant.PREFS.USER.value, null)
//    }
}