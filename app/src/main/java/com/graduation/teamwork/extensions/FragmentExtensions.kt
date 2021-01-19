package com.graduation.teamwork.extensions

import android.graphics.BitmapFactory
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.graduation.teamwork.R
import com.graduation.teamwork.ui.base.FragmentViewBindingDelegate
import kotlinx.android.synthetic.main.frag_profile.*

fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
	FragmentViewBindingDelegate(this, viewBindingFactory)