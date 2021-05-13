package com.graduation.teamwork.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.graduation.teamwork.R
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.databinding.FragHomeBinding
import com.graduation.teamwork.extensions.*
import com.graduation.teamwork.extensions.observeOnUiThread
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.models.data.TaskListItem
import com.graduation.teamwork.ui.base.BaseFragment
import com.graduation.teamwork.ui.main.MainActivity
import com.graduation.teamwork.ui.room.details.RoomDetailActivity
import com.graduation.teamwork.ui.task.details.DetailTaskActivity
import com.graduation.teamwork.utils.PrefsManager
import com.graduation.teamwork.utils.eventbus.RxBus
import com.graduation.teamwork.utils.eventbus.RxEvent
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import kotlinx.android.synthetic.main.frag_home.*
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension


/**
 * com.graduation.teamwork.ui.home
 * Created on 11/15/20
 */

@KoinApiExtension
class HomeFragment : BaseFragment<FragHomeBinding, MainActivity>() {
    private val TAG = "__HomeFragment"

    /**
     * INJECT
     */
    private val viewModel: HomeViewModel by inject()
    private val prefs: PrefsManager by inject()

    /**
     * DATA
     */
    private var dataMapSource = mutableListOf<Map<TaskListItem.Header, List<TaskListItem.DtTask>>>()
    private var dataListSource = mutableListOf<TaskListItem>()
    private var currentUser: DtUser? = prefs.getUser()
    private var isShow = true

    private val adapter = HomeAdapter(
        dataListSource
    ) { item ->
        when (item) {
            is TaskListItem.Header -> {
                Intent(requireContext(), RoomDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.ID_ROOM.value, item.asRoom()._id ?: "")
                    startActivity(this)
                    activity?.overridePendingTransition(R.anim.from_right_in, R.anim.nothing)
                }
            }
            else -> {
                val header = dataMapSource.firstOrNull {
                    it.filterValues { it1 -> it1.contains((item as TaskListItem.DtTask)) }
                        .isNotEmpty()
                }

                header?.let {
                    val users = header.keys.first().users
                        ?.filter { it.user != null }
                        ?.map { it.user!! }

                    Intent(requireContext(), DetailTaskActivity::class.java).apply {
                        putExtra(Constant.IntentKey.DETAIL_USER.value, users?.toJson())
                        putExtra(
                            Constant.IntentKey.DETAIL_TASK.value,
                            (item as TaskListItem.DtTask).toJson()
                        )
                        startActivity(this)
                        activity?.overridePendingTransition(R.anim.from_right_in, R.anim.nothing)
                    }
                }
            }
        }
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragHomeBinding =
        FragHomeBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupData()
        setupViews()
        setupListener()
    }

    override fun onResume() {
        super.onResume()
        binding?.tvEmpty?.setVisiable(dataListSource.isEmpty() && this.adapter.getCurrentList().isEmpty())
        if (isOnline(requireContext())) {
            if (dataMapSource.isNullOrEmpty() || prefs.isChange()) {
                handler?.showProgress()
                viewModel.getTaskWithRoom(currentUser!!._id!!)
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.getTaskWithRoom(currentUser!!._id!!)
                    isShow = true
                }, 1500)
                prefs.setChange(false)
            }
        } else {
            binding?.root?.let {
                requireContext().showSnackbar(
                    it,
                    getString(R.string.notify_connect_wifi),
                    Snackbar.LENGTH_SHORT
                )
            }
        }
    }

    private fun setupData() {
        if (currentUser == null) {
            currentUser = prefs.getUser()
        }
        viewModel.getTaskWithRoom(currentUser!!._id!!)
        isShow = true
    }

    private fun setupViews() {
        handler?.run {
            showFloatActionButton(isShow = true)
            showToolbar(isShow = true)
        }

        binding?.run {
            with(recyclerHome) {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = this@HomeFragment.adapter
            }
        }
    }

    private fun setupListener() {
        viewModel.resources.observe(viewLifecycleOwner) {
            if (!it.data.isNullOrEmpty()) {
                dataListSource = emptyList<TaskListItem>().toMutableList()

                // convert map to list
                dataMapSource.apply {
                    clear()
                    addAll(it.data as MutableList<Map<TaskListItem.Header, List<TaskListItem.DtTask>>>)

                    map { item ->
                        item.flatMap { (header, data) ->
                            mutableListOf<TaskListItem>()
                                .apply {
                                    add(header)
                                    addAll(data)
                                }
                        }
                    }.onEach { dataListSource.addAll(it) }
                }

                Log.d(TAG, "setupListener: ${dataListSource.isEmpty()} - ${this.adapter.getCurrentList().isEmpty()}")
                binding?.tvEmpty?.setVisiable(dataListSource.isEmpty() && this.adapter.getCurrentList().isEmpty())
                if (dataListSource.isNotEmpty()) {
                    this.adapter.submitList(dataListSource)
//                    binding?.recyclerHome?.adapter = this.adapter
                    refresh()
                }
            }
            if (isShow) {
                handler?.hideProgress()
                isShow = false
            }
        }

        /**
         * Listen rxBus
         */
        RxBus.listenBehavior(RxEvent.EvenSearchData::class.java)
            .observeOnUiThread()
            .autoDisposable(viewLifecycleOwner.scope())
            .subscribe {
                this.adapter.filter.filter(it.text)
            }

        RxBus.listenPublisher(RxEvent.RoomUpdate::class.java)
            .observeOnUiThread()
            .autoDisposable(viewLifecycleOwner.scope())
            .subscribe {
                viewModel.getTaskWithRoom(currentUser!!._id!!)
                isShow = true
            }
    }

    private fun refresh() {
        // notifyDataSetChanged must run in main ui thread, if run in not ui thread, it will not update until manually scroll recyclerview
        handler?.runOnUiThread { adapter.notifyDataSetChanged() }
    }

}
