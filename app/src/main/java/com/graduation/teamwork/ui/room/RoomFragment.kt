package com.graduation.teamwork.ui.room

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.graduation.teamwork.R
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.databinding.FragRoomBinding
import com.graduation.teamwork.extensions.*
import com.graduation.teamwork.models.DtRoom
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.ui.base.BaseFragment
import com.graduation.teamwork.ui.main.MainActivity
import com.graduation.teamwork.ui.room.details.RoomDetailActivity
import com.graduation.teamwork.utils.DialogManager
import com.graduation.teamwork.utils.PrefsManager
import com.graduation.teamwork.utils.eventbus.RxBus
import com.graduation.teamwork.utils.eventbus.RxEvent
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension

/**
 * com.graduation.teamwork.ui.room
 * Created on 11/15/20
 */
@KoinApiExtension
class RoomFragment : BaseFragment<FragRoomBinding, MainActivity>() {
    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragRoomBinding = FragRoomBinding.inflate(inflater, container, false)

    private val TAG = "__RoomFragment"

    /**
     * INJECT
     */
    private val viewModel: RoomViewModel by inject()
    private val prefs: PrefsManager by inject()
    private val dialogs: DialogManager by inject()

    private val roomAdapter = RoomAdapter(emptyList()) { item, position ->
        Intent(activity, RoomDetailActivity::class.java).apply {
            putExtra(Constant.IntentKey.ID_ROOM.value, item._id!!)
            startActivity(this)
            activity?.overridePendingTransition(R.anim.from_right_in, R.anim.nothing)
        }
    }

    private var currentUser: DtUser? = prefs.getUser()
    private var groupIdCurrent: String = ""
    private var rooms = mutableListOf<DtRoom>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupData()
        setupViews()
        setupListener()
    }

    override fun onResume() {
        super.onResume()
        if (isOnline(requireContext())) {
            if (rooms.isEmpty()) {
                handler?.showProgress()
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

    override fun onDestroyView() {
        super.onDestroyView()
        prefs.saveLoadOnlyGroup(null)
    }

    private fun setupData() {
        handler?.showProgress()

        if (prefs.getLoadOnlyGroup() == null) {
            viewModel.getRoomByUser(currentUser!!._id!!)
        } else {
            viewModel.getAllRoomInGroup(prefs.getLoadOnlyGroup()!!)
        }
    }

    private fun setupListener() {
        viewModel.resources.observe(viewLifecycleOwner) {
            if (it.data != null) {
                rooms.clear()
                rooms.addAll(it.data)

                roomAdapter.submitList(rooms)
                binding?.run {
                    recyclerRoom.visiable()
                    tvEmpty.gone()
                }
            } else {
                binding?.run {
                    recyclerRoom.gone()
                    tvEmpty.visiable()
                }
            }

            it.data?.let { it1 ->
                Log.d(TAG, "setupListener: $it1")
            }
            handler?.hideProgress()
        }

        RxBus.listenPublisher(RxEvent.GroupChanged::class.java)
            .observeOnUiThread()
            .autoDisposable(viewLifecycleOwner.scope())
            .subscribe {
                groupIdCurrent = it.id
                handler?.showProgress()
                viewModel.getAllRoomInGroup(it.id)
            }

        RxBus.listenPublisher(RxEvent.RoomUpdate::class.java)
            .observeOnUiThread()
            .autoDisposable(viewLifecycleOwner.scope())
            .subscribe {
                handler?.showProgress()
                viewModel.getRoomByUser(currentUser!!._id!!)
            }

        RxBus.listenBehavior(RxEvent.EvenSearchData::class.java)
            .observeOnUiThread()
            .autoDisposable(viewLifecycleOwner.scope())
            .subscribe {
                roomAdapter.filter.filter(it.text)
            }
    }

    fun setupViews() {
        // from base
        handler?.run {
            showFloatActionButton(isShow = true)
            showToolbar(isShow = true)
        }

        binding?.recyclerRoom?.run {
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
            adapter = ScaleInAnimationAdapter(roomAdapter);
        }

    }
}