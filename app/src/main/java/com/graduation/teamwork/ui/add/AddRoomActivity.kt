package com.graduation.teamwork.ui.add

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.lifecycle.observe
import com.graduation.teamwork.R
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.databinding.LayoutAddRoomBinding
import com.graduation.teamwork.extensions.showToast
import com.graduation.teamwork.extensions.toObject
import com.graduation.teamwork.models.DtGroup
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.ui.base.BaseActivity
import com.graduation.teamwork.ui.base.Error
import com.graduation.teamwork.ui.base.Success
import com.graduation.teamwork.ui.room.details.RoomDetailActivity
import com.graduation.teamwork.utils.PrefsManager
import com.graduation.teamwork.utils.eventbus.RxBus
import com.graduation.teamwork.utils.eventbus.RxEvent
import es.dmoral.toasty.Toasty
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension


@KoinApiExtension
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

    private fun getData() {
        if (currentUser == null) {
            currentUser = prefs.getUser()
        }

        viewModel.getAllGroupForUser(currentUser!!._id!!)
    }

    private fun setViews() {
        window.statusBarColor =
            ContextCompat.getColor(this, com.graduation.teamwork.R.color.colorPrimaryDark)

        binding?.run {
            // spinner
            spnRoom.onItemSelectedListener = this@AddRoomActivity
            val adapter: ArrayAdapter<*> =
                ArrayAdapter<Any?>(
                    this@AddRoomActivity,
                    android.R.layout.simple_spinner_item,
                    getListNameGroup()
                )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spnRoom.adapter = adapter
        }
    }

    private fun setListeners() {
        // views
        binding?.run {
            imgCancel.setOnClickListener { finish() }
            imgAdd.setOnClickListener {
                val name = edtName.text.toString()

                if (name.isNotBlank()) {
                    val groupId = groups[currentSelectedGroup]._id
                    viewModel.addGroupInRoom(groupId, name, currentUser!!._id!!)

                    showProgress("Đang thêm phòng")
                } else {
                    Toasty.error(this@AddRoomActivity, R.string.notify_error_name_empty).show()
                }

            }
        }

        viewModel.run {
            resources.observe(this@AddRoomActivity) {
                if (it.data != null) {
                    groups.clear()
                    groups.addAll(it.data)

                    val adapter: ArrayAdapter<*> =
                        ArrayAdapter<Any?>(
                            this@AddRoomActivity,
                            android.R.layout.simple_spinner_item,
                            getListNameGroup()
                        )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding?.spnRoom?.adapter = adapter
                }
            }

            events.observe(this@AddRoomActivity) {
                when (it) {
                    is Success -> {
                        val item = it.data?.toObject<DtGroup>()
                        if (item != null) {
                            RxBus.publishToPublishSubject(RxEvent.RoomUpdate(""))
                            prefs.setChange(true)

                            Toasty.success(this@AddRoomActivity, R.string.notifi_success_add_room).show()
                            Intent(this@AddRoomActivity, RoomDetailActivity::class.java).apply {
//                                putExtra(Constant.IntentKey.ROOM.value, item.rooms.last())
                                putExtra(Constant.IntentKey.ID_ROOM.value, item.rooms.last()._id)
                                startActivity(this)
                                finish()
                            }
                        }
                    }
                    is Error -> {
                        Toasty.error(this@AddRoomActivity, R.string.notify_error_group).show()
                    }
                }
                hideProgress()
            }
        }

    }

    private fun getListNameGroup(): List<String> {
        return groups.map {
            it.name
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentSelectedGroup = position
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.nothing, R.anim.from_out_top)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //
    }
}