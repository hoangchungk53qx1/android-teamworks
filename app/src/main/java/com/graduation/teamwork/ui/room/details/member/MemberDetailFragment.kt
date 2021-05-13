package com.graduation.teamwork.ui.room.details.member

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.graduation.teamwork.R
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.databinding.FragDetailMemberBinding
import com.graduation.teamwork.extensions.observeOnUiThread
import com.graduation.teamwork.extensions.showToast
import com.graduation.teamwork.extensions.toJson
import com.graduation.teamwork.extensions.toObject
import com.graduation.teamwork.models.DtRoom
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.models.data.MemberInRoom
import com.graduation.teamwork.ui.base.BaseFragment
import com.graduation.teamwork.ui.room.details.RoomDetailActivity
import com.graduation.teamwork.ui.task.details.DetailViewModel
import com.graduation.teamwork.utils.DialogManager
import com.graduation.teamwork.utils.PrefsManager
import com.graduation.teamwork.utils.eventbus.RxBus
import com.graduation.teamwork.utils.eventbus.RxEvent
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import es.dmoral.toasty.Toasty
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension


@KoinApiExtension
class MemberDetailFragment : BaseFragment<FragDetailMemberBinding, RoomDetailActivity>() {

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragDetailMemberBinding = FragDetailMemberBinding.inflate(inflater, container, false)

    /**
     * INJECT
     */
    private val viewModel: DetailViewModel by inject()
    private val prefs: PrefsManager by inject()
    private val dialogs: DialogManager by inject()

    private lateinit var mRoom: DtRoom
    private var currentUser: DtUser? = prefs.getUser()
    private var listMembers = mutableListOf<MemberInRoom>()

    private var mAdapter = MemberAdapter(mutableListOf()) { item ->
        when {
            item.level!! == Constant.LevelKey.CREATED.value -> {
                Toasty.error(requireContext(), "Không thể thay đổi chức vụ của người tạo phòng").show()
            }
            item.user?._id == currentUser!!._id!! -> {
                Toasty.error(requireContext(), "Bạn không thể thay đổi chức vụ của bản thân").show()
            }
            else -> {
                showDialogLevel(item)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.run {
            mRoom = this.getString(Constant.IntentKey.ROOM.value)!!.toObject<DtRoom>()!!

            if (this@MemberDetailFragment::mRoom.isInitialized) {
                listMembers = mRoom.getMemberInRooms()?.toMutableList()!!
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
        setListeners()
    }

    private fun setViews() {
        binding?.run {
            with(recyclerMember) {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = mAdapter
                mAdapter.submitList(listMembers)
            }
        }
    }

    private fun setListeners() {
        binding?.run {
            btnBack.setOnClickListener {
                handler?.onBackPressed()
            }

            lnAddMember.setOnClickListener {
                dialogs.inputDialog(requireContext(),
                    R.string.add_member,
                    R.string.add_member_mail,
                    { mail ->
                        if (mail.isNotBlank()) {
                            val foundUser = handler?.users?.firstOrNull() {
                                it.mail?.equals(mail) ?: false
                            }

                            if (foundUser != null) {
                                viewModel.addUserInRoom(
                                    mRoom._id!!,
                                    currentUser!!._id!!,
                                    foundUser._id!!
                                )
                                handler?.showProgress("Thêm thành viên")
                            } else {
                                Toasty.error(requireContext(), "Thêm thành viên thất bại").show()
                            }
                        } else {
                            Toasty.error(requireContext(), R.string.notify_empty_stage).show()
                        }
                    },
                    {})
            }
        }

        with(viewModel) {
            resourcesRoom.observe(viewLifecycleOwner) {
                if (it.data != null) {
                    listMembers.clear()
                    it.data.first().getMemberInRooms()?.let {
                        val addAll = listMembers.addAll(it)
                        mAdapter.submitList(listMembers)
                    }
                }
                handler?.hideProgress()
            }

            resourcesUser.observe(viewLifecycleOwner) {
                if (it.data != null) {
                    listMembers.clear()

                    listMembers.addAll(it.data.map { item ->
                        MemberInRoom(item._id, mRoom._id, item.level, item.user)
                    })
                    mAdapter.submitList(listMembers)
                    handler?.hideProgress()
                    Toasty.success(requireContext(), "Thực hiện thành công").show()
                }
            }
        }
        RxBus.listenPublisher(RxEvent.MemberAdded::class.java)
            .observeOnUiThread()
            .autoDisposable(viewLifecycleOwner.scope())
            .subscribe {
                viewModel.getAllUserInRoom(mRoom._id!!)
            }
    }

    private fun showDialogLevel(user: MemberInRoom) {
        val levels = arrayOf("Manager", "Member")

        var defaultLevel = 0
        if (user.level!! == Constant.LevelKey.MEMBER.value) {
            defaultLevel = 1
        }

        var selected = defaultLevel
        val levelUserInRoom = mRoom.users?.find {
            it.user!!._id!! == currentUser!!._id!!
        }?.level ?: -1

        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Chọn chức vụ cho ${user.user?.fullname}")

            setSingleChoiceItems(levels, defaultLevel) { _, which -> selected = which }

            setPositiveButton("Lưu") { dialog, which ->
                if (selected == defaultLevel) {
                    Toasty.success(requireContext(), "Thực hiện thành công").show()
                } else {

                    if (levelUserInRoom == -1L) {
                        Toasty.error(requireContext(), "Email hiện chưa sử dụng ứng dụng").show()
                    } else if (levelUserInRoom == Constant.LevelKey.MEMBER.value || levelUserInRoom > user.level) {
                        Toasty.error(requireContext(), "Bạn không đủ quyền hạn").show()
                    } else {
                        var levelSetInServer: Long = Constant.LevelKey.MEMBER.value
                        if (selected == 0) {
                            levelSetInServer = Constant.LevelKey.MANAGER.value
                        }

                        viewModel.setLevel(
                            mRoom._id!!,
                            currentUser!!._id!!,
                            user.user!!._id!!,
                            levelSetInServer
                        )

                        handler?.showProgress("Thay đổi chức vụ")
                        dialog.dismiss()
                    }
                }
            }

            setNegativeButton("Xoá khỏi phòng") { dialog, which ->
                if (levelUserInRoom == -1L) {
                    Toasty.error(requireContext(), "Email hiện chưa sử dụng ứng dụng").show()
                } else if (levelUserInRoom == Constant.LevelKey.MEMBER.value || levelUserInRoom > user.level) {
                    Toasty.error(requireContext(), "Bạn không đủ quyền hạn").show()
                } else {
                    viewModel.deleteUserInRoom(
                        mRoom._id!!,
                        currentUser!!._id!!,
                        user.user!!._id!!
                    )

                    handler?.showProgress("Xoá thành viên")
                    dialog.dismiss()
                }

            }

            create()
            show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireContext().showToast("Home")
                handler?.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment MemberDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(room: DtRoom): MemberDetailFragment = MemberDetailFragment().apply {
            arguments = Bundle().apply {
                putString(Constant.IntentKey.ROOM.value, room.toJson())
            }
        }
    }
}