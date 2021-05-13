package com.graduation.teamwork.ui.slider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.graduation.teamwork.R
import com.graduation.teamwork.databinding.FragSliderBinding
import com.graduation.teamwork.extensions.observeOnUiThread
import com.graduation.teamwork.extensions.showToast
import com.graduation.teamwork.models.DtGroup
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.ui.base.BaseFragment
import com.graduation.teamwork.ui.main.MainActivity
import com.graduation.teamwork.utils.PrefsManager
import com.graduation.teamwork.utils.eventbus.RxBus
import com.graduation.teamwork.utils.eventbus.RxEvent
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import kotlinx.android.synthetic.main.frag_profile.*
import org.koin.android.ext.android.inject

/**
 * com.graduation.teamwork.ui.slider
 * Created on 11/14/20
 */

class SliderFragment : BaseFragment<FragSliderBinding, MainActivity>() {
    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragSliderBinding = FragSliderBinding.inflate(inflater, container, false)

    private val TAG = "__SliderFragment"

    /**
     * INJECT
     */
    private val viewModel: SliderViewModel by inject()
    private val prefs: PrefsManager by inject()
    private val slideAdapter = SliderAdapter(emptyList(), this::onClickedItemWith)

    private var currentUser: DtUser? = prefs.getUser()
    private var groups = mutableListOf<DtGroup>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupData()
        setupViews()
        setupListener()
    }

    private fun setupData() {
        if (currentUser == null) {
            currentUser = prefs.getUser()
        }
        viewModel.getAllGroupByUser(currentUser!!._id!!)
    }

    private fun setupViews() {
        binding?.run {
            drawerHeader.tvName.text = currentUser!!.fullname

            Glide.with(this@SliderFragment)
                .load(currentUser?.image?.url)
                .placeholder(R.drawable.bg_demo_1)
                .into(drawerHeader.avatar)

            drawerHeader.tvUserName.text = if (currentUser!!.numberphone != null) {
                currentUser!!.numberphone
            } else {
                currentUser!!.mail
            }

            //setup recyclerview
            recyclerGroup.run {
                adapter = slideAdapter
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
            }
        }
    }

    private fun setupListener() {
        // View
        binding?.run {
            lnHome.setOnClickListener {
                handler?.navBottomSelected(R.id.navBottomHome)
                handler?.closeDrawer()
            }

            lnRoom.setOnClickListener {
                handler?.navBottomSelected(R.id.navBottomRoom)
                handler?.closeDrawer()
            }

            lnSetting.setOnClickListener {
                context?.showToast("Settings")
            }

            lnHelp.setOnClickListener {
                context?.showToast("Help")
            }

            handler?.closeDrawer()
        }

        viewModel.resources.observe(viewLifecycleOwner) {
            if (it.data != null) {
                groups.clear()
                groups.addAll(it.data)

                slideAdapter.submitList(groups)
                refresh()
            }
        }

        RxBus.listenPublisher(RxEvent.GroupAdded::class.java)
            .observeOnUiThread()
            .autoDisposable(viewLifecycleOwner.scope())
            .subscribe {
                viewModel.getAllGroupByUser(currentUser!!._id!!)
            }

        RxBus.listenPublisher(RxEvent.UpdateAvatar::class.java)
            .observeOnUiThread()
            .autoDisposable(viewLifecycleOwner.scope())
            .subscribe {
                currentUser = prefs.getUser()

                binding?.run {
                    Glide.with(this@SliderFragment)
                        .load(currentUser?.image?.url)
                        .placeholder(R.drawable.bg_demo_1)
                        .into(drawerHeader.avatar)
                }
            }
    }

    private fun onClickedItemWith(item: DtGroup, position: Int) {
        handler?.run {
            gotoRoom(item._id)
            closeDrawer()
        }

        RxBus.publishToPublishSubject(RxEvent.GroupChanged(item._id))
    }

    private fun refresh() {
        // notifyDataSetChanged must run in main ui thread, if run in not ui thread, it will not update until manually scroll recyclerview
        handler?.runOnUiThread { slideAdapter.notifyDataSetChanged() }
    }


}