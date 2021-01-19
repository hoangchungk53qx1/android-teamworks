package com.graduation.teamwork.ui.add

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.databinding.LayoutAddRoomBinding
import com.graduation.teamwork.extensions.fromObject
import com.graduation.teamwork.models.DtGroup
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.ui.base.BaseActivity
import com.graduation.teamwork.ui.base.Error
import com.graduation.teamwork.ui.base.Success
import com.graduation.teamwork.ui.task.TaskActivity
import com.graduation.teamwork.utils.PrefsManager
import com.graduation.teamwork.extensions.showToast
import com.graduation.teamwork.utils.eventbus.RxBus
import com.graduation.teamwork.utils.eventbus.RxEvent
import org.koin.android.ext.android.inject


class AddRoomActivity : BaseActivity<LayoutAddRoomBinding>(), AdapterView.OnItemSelectedListener {
    override fun setBinding(inflater: LayoutInflater): LayoutAddRoomBinding =
        LayoutAddRoomBinding.inflate(inflater)

    /**
     * INJECT
     */
    private val viewModel: AddViewModel by inject()
    private val prefs: PrefsManager by inject()

    // data
    private var currentUser: DtUser? = prefs.getUser()

    private var groups = mutableListOf<DtGroup>()
    private var currentSelectedGroup = 0

    override fun onViewReady(savedInstanceState: Bundle?) {
        getData()
        setViews()
        setListeners()
    }

    fun getData() {
        if (currentUser == null) {
            currentUser = prefs.getUser()
        }


        viewModel.getAllGroupForUser(currentUser!!._id!!)
    }

    fun setViews() {

        binding?.run {
            // spinner
            spnRoom.onItemSelectedListener = this@AddRoomActivity
            val adapter: ArrayAdapter<*> =
                ArrayAdapter<Any?>(
                    this@AddRoomActivity,
                    R.layout.simple_spinner_item,
                    getListNameGroup()
                )
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

            spnRoom.adapter = adapter
        }
    }

    fun setListeners() {
        // views
        binding?.run {
            imgCancel.setOnClickListener { finish() }
            imgAdd.setOnClickListener {
                val name = edtName.text.toString()

                if (name.isNotBlank()) {
                    val groupId = groups[currentSelectedGroup]._id
                    viewModel.addGroupInRoom(groupId, name, currentUser!!._id!!)

                    showToast(getString(com.graduation.teamwork.R.string.notifi_success_add_room))
                } else {
                    showToast(getString(com.graduation.teamwork.R.string.notify_error_name_empty))
                }

            }
        }

        viewModel.resources.observe(this, {
            if (it.data != null) {
                groups.clear()
                groups.addAll(it.data)

                val adapter: ArrayAdapter<*> =
                    ArrayAdapter<Any?>(
                        this@AddRoomActivity,
                        R.layout.simple_spinner_item,
                        getListNameGroup()
                    )
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                binding?.spnRoom?.adapter = adapter
            }
        })

        viewModel.events.observe(this, {
            when (it) {
                is Success -> {
                    val item = it.data?.fromObject<DtGroup>()
                    if (item != null) {
                        RxBus.publishToPublishSubject(RxEvent.UpdateRoom)

                        Intent(this, TaskActivity::class.java).also {
                            it.putExtra(Constant.INTENT.ROOM.value, item.rooms.last())

                            startActivity(it)
                            finish()
                        }



                    }
                }
                is Error -> {
                    showToast(getString(com.graduation.teamwork.R.string.notify_error_group))
                }
            }
        })
    }

    private fun getListNameGroup(): List<String> {
        return groups.map {
            it.name
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentSelectedGroup = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}