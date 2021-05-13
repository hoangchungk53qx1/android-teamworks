package com.graduation.teamwork.extensions

import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.graduation.teamwork.ui.base.FragmentViewBindingDelegate

fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
	FragmentViewBindingDelegate(this, viewBindingFactory)