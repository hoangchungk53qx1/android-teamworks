package com.graduation.teamwork.ui.notification

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduation.teamwork.adapters.NotiAdapter
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.databinding.FragNotificationBinding
import com.graduation.teamwork.extensions.fromObject
import com.graduation.teamwork.extensions.observeOnUiThread
import com.graduation.teamwork.extensions.toJson
import com.graduation.teamwork.models.DtRoom
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.models.firebase.InfoNoti
import com.graduation.teamwork.models.firebase.NotiDetailRoom
import com.graduation.teamwork.models.firebase.NotiDetailTask
import com.graduation.teamwork.models.firebase.NotiDetailUser
import com.graduation.teamwork.ui.base.BaseFragment
import com.graduation.teamwork.ui.main.MainActivity
import com.graduation.teamwork.ui.task.TaskActivity
import com.graduation.teamwork.ui.task.details.DetailTaskActivity
import com.graduation.teamwork.utils.FirebaseManager
import com.graduation.teamwork.utils.PrefsManager
import com.graduation.teamwork.utils.eventbus.RxBus
import com.graduation.teamwork.utils.eventbus.RxEvent
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import org.koin.android.ext.android.inject

/**
 * com.graduation.teamwork.ui.notification
 * Created on 11/15/20
 */

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
    private val TAG = "__NotificationFragment"

    private val mAdapter = NotiAdapter(notiList) { item ->
        Log.d(TAG, "Category:  ${item.category}")
        when (item.category) {
            Constant.TYPE_NOTI.ROOM -> {
                val data = item.data.fromObject<DtRoom>()

                val intent = Intent(activity, TaskActivity::class.java).apply {
                    putExtra(Constant.INTENT.ROOM.value, data)
                }

                startActivity(intent)
            }
            Constant.TYPE_NOTI.TASK -> {
//                val data = item.data.fromObject<DtT>()

                Intent(requireContext(), DetailTaskActivity::class.java).also {
                    it.putExtra(Constant.INTENT.DETAIL_USER.value, allUsers.toJson())
                    it.putExtra(Constant.INTENT.DETAIL_TASK.value, item.data)

                    startActivity(it)
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
                                item.fromObject<NotiDetailRoom>()
                                    ?.asInfoNoti(currentUser!!._id!!, allUsers, allRooms)
                                    ?.let { it1 ->
                                        notiList.add(
                                            it1
                                        )
                                    }
                            }
                        }
                        "tasks" -> {
                            items?.forEach { item ->
                                item.fromObject<NotiDetailTask>()
                                    ?.asInfoNoti(currentUser!!._id!!, allUsers, allTasks)
                                    ?.let { it1 ->
                                        notiList.add(
                                            it1
                                        )
                                    }
                            }
                        }
                        "users" -> {
                            items?.forEach { item ->
                                item.fromObject<NotiDetailUser>()?.asInfoNoti()?.let { it1 ->
                                    notiList.add(
                                        it1
                                    )
                                }
                            }
                        }
                    }
                }
                mAdapter.submitList(notiList.sortedWith(compareByDescending { it.timestampL }))
            }
    }

}