package com.graduation.teamwork.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.graduation.teamwork.R
import com.graduation.teamwork.adapters.HomeAdapter
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.databinding.FragHomeBinding
import com.graduation.teamwork.extensions.*
import com.graduation.teamwork.extensions.observeOnUiThread
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.models.data.TaskListItem
import com.graduation.teamwork.ui.base.BaseFragment
import com.graduation.teamwork.ui.main.MainActivity
import com.graduation.teamwork.ui.task.TaskActivity
import com.graduation.teamwork.ui.task.details.DetailTaskActivity
import com.graduation.teamwork.utils.PrefsManager
import com.graduation.teamwork.utils.eventbus.RxBus
import com.graduation.teamwork.utils.eventbus.RxEvent
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import kotlinx.android.synthetic.main.frag_home.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.android.ext.android.inject


/**
 * com.graduation.teamwork.ui.home
 * Created on 11/15/20
 */

class HomeFragment : BaseFragment<FragHomeBinding, MainActivity>() {
    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragHomeBinding =
        FragHomeBinding.inflate(inflater, container, false)

    private val TAG = "__HomeFragment"

    /**
     * INJECT
     */
    private val viewModel: HomeViewModel by inject()
    private val prefs: PrefsManager by inject()

    // data
    private var currentUser: DtUser? = prefs.getUser()

    /**
     * DATA
     */
    private var dataMapSource = mutableListOf<Map<TaskListItem.Header, List<TaskListItem.DtTask>>>()
    private var dataListSource = mutableListOf<TaskListItem>()

    private val adapter = HomeAdapter(
        dataListSource
    ) { item ->
        when (item) {
            is TaskListItem.Header -> {
                Intent(requireContext(), TaskActivity::class.java).also {
                    it.putExtra(Constant.INTENT.ROOM.value, item.asRoom())

                    startActivity(it)
                }
//                context?.showToast("clicked at ${item.name}")
            }
            else -> {

                val header = dataMapSource.filter {
                    it.filterValues { it1 -> it1.contains((item as TaskListItem.DtTask)) }
                        .isNotEmpty()
                }.firstOrNull()

                header?.let {
                    val users = header.keys.first().users?.filter { it.user != null }?.map {
                        it.user!!
                    }

                    Intent(requireContext(), DetailTaskActivity::class.java).also {
                        it.putExtra(Constant.INTENT.DETAIL_USER.value, users.toJson())
                        Log.d(TAG, "AAAA: item = ${item}")
                        it.putExtra(Constant.INTENT.DETAIL_TASK.value, (item as TaskListItem.DtTask).toJson())

                        startActivity(it)
                    }
                }

            }


        }
    }

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
        viewModel.getTaskWithRoom(currentUser!!._id!!)
    }

    override fun onResume() {
        super.onResume()

        if (isOnline(requireContext())) {
            if (dataMapSource.isNullOrEmpty()) {
                handler?.showProgress()
                viewModel.getTaskWithRoom(currentUser!!._id!!)
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

    fun refresh() {
        // notifyDataSetChanged must run in main ui thread, if run in not ui thread, it will not update until manually scroll recyclerview
        handler?.runOnUiThread { adapter.notifyDataSetChanged() }
    }

    private fun setupListener() {
        viewModel.resources.observe(viewLifecycleOwner, { it ->
            Log.d(TAG, "setupListener: HOME-OK")
            handler?.hideProgres()

            if (!it.data.isNullOrEmpty()) {
                dataMapSource.clear()
                dataListSource = emptyList<TaskListItem>().toMutableList()

                dataMapSource =
                    it.data as MutableList<Map<TaskListItem.Header, List<TaskListItem.DtTask>>>

                dataMapSource
                    .map { item ->
                        item.flatMap { (header, data) ->
                            mutableListOf<TaskListItem>()
                                .apply {
                                    add(header)
                                    addAll(data)
                                }
                        }
                    }
                    .onEach {
                        dataListSource.addAll(it)
                    }

                if (dataListSource.isNotEmpty()) {

                    Log.d(TAG, "setupListener: $dataListSource")
                    this.adapter.submitList(dataListSource)

                    binding?.recyclerHome?.adapter = this.adapter
                    refresh()
                }
            }
        })

        RxBus.listenBehavior(RxEvent.EvenSearchData::class.java)
            .observeOnUiThread()
            .autoDisposable(viewLifecycleOwner.scope())
            .subscribe {
                this.adapter.filter.filter(it.text)
            }
        RxBus.listenPublisher(RxEvent.UpdateRoom::class.java)
            .observeOnUiThread()
            .autoDisposable(viewLifecycleOwner.scope())
            .subscribe {
                viewModel.getTaskWithRoom(currentUser!!._id!!)
            }

        RxBus.listenPublisher(RxEvent.UpdateTask::class.java)
            .observeOnUiThread()
            .autoDisposable(viewLifecycleOwner.scope())
            .subscribe {
                viewModel.getTaskWithRoom(currentUser!!._id!!)
            }
    }

    fun setupViews() {
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

}
