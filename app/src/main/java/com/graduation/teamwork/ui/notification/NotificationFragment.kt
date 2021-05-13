package com.graduation.teamwork.ui.notification

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduation.teamwork.R
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.databinding.FragNotificationBinding
import com.graduation.teamwork.extensions.observeOnUiThread
import com.graduation.teamwork.extensions.toJson
import com.graduation.teamwork.extensions.toObject
import com.graduation.teamwork.models.DtRoom
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.models.firebase.InfoNoti
import com.graduation.teamwork.models.firebase.NotiDetailRoom
import com.graduation.teamwork.models.firebase.NotiDetailTask
import com.graduation.teamwork.models.firebase.NotiDetailUser
import com.graduation.teamwork.ui.base.BaseFragment
import com.graduation.teamwork.ui.main.MainActivity
import com.graduation.teamwork.ui.room.details.RoomDetailActivity
import com.graduation.teamwork.ui.task.details.DetailTaskActivity
import com.graduation.teamwork.utils.FirebaseManager
import com.graduation.teamwork.utils.PrefsManager
import com.graduation.teamwork.utils.eventbus.RxBus
import com.graduation.teamwork.utils.eventbus.RxEvent
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension

/**
 * com.graduation.teamwork.ui.notification
 * Created on 11/15/20
 */

@KoinApiExtension
class NotificationFragment : BaseFragment<FragNotificationBinding, MainActivity>() {
    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragNotificationBinding = FragNotificationBinding.inflate(inflater, container, false)

    /**
     * INJECT
     */
    private val prefs: PrefsManager by inject()
    private val firebase: FirebaseManager by inject()

    private var currentUser: DtUser? = prefs.getUser()
    private var allTasks = prefs.getTasks()
    private var allRooms = prefs.getRooms()
    private var allUsers = prefs.getUsers()
    private var notiList = mutableListOf<InfoNoti>()

    private val mAdapter = NotifyAdapter(notiList) { item ->
        when (item.category) {
            Constant.NotifyType.ROOM -> {
                val data = item.data.toObject<DtRoom>()
                Intent(activity, RoomDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.ROOM.value, data)
                    putExtra(Constant.IntentKey.ID_ROOM.value, data?._id!!)
                    startActivity(this)
                    activity?.overridePendingTransition(R.anim.from_right_in, R.anim.nothing)
                }
            }
            Constant.NotifyType.TASK -> {
                Intent(requireContext(), DetailTaskActivity::class.java).apply {
                    putExtra(Constant.IntentKey.DETAIL_USER.value, allUsers?.toJson())
                    putExtra(Constant.IntentKey.DETAIL_TASK.value, item.data)
                    startActivity(this)
                    activity?.overridePendingTransition(R.anim.from_right_in, R.anim.nothing)
                }
            }
            else -> {
                handler?.gotoProfile()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (currentUser == null) {
            currentUser = prefs.getUser()
        }

        firebase.readAll(currentUser!!._id!!, allRooms)
        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        handler?.run {
            showFloatActionButton(isShow = false)
            showToolbar(isShow = true)
        }

        binding?.run {
            with(recyclerNoti) {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = mAdapter
            }
        }
    }

    private fun setupListeners() {
        RxBus.listenPublisher(RxEvent.EventNotiUpdated::class.java)
            .observeOnUiThread()
            .autoDisposable(lifecycle.scope())
            .subscribe {
                notiList.clear()

                it.data.keys.forEach { key ->
                    val items = it.data[key]
                    when (key) {
                        "rooms" -> {
                            items?.forEach { item ->
                                item.toObject<NotiDetailRoom>()
                                    ?.asInfoNoti(currentUser!!._id!!, allUsers, allRooms)
                                    ?.let { notiList.add(it) }
                            }
                        }
                        "tasks" -> {
                            items?.forEach { item ->
                                item.toObject<NotiDetailTask>()
                                    ?.asInfoNoti(currentUser!!._id!!, allUsers, allTasks)
                                    ?.let { notiList.add(it) }
                            }
                        }
                        "users" -> {
                            items?.forEach { item ->
                                item.toObject<NotiDetailUser>()?.asInfoNoti()
                                    ?.let { notiList.add(it) }
                            }
                        }
                    }
                }
                mAdapter.submitList(notiList.sortedWith(compareByDescending { it.timestampL }))
            }
    }

}