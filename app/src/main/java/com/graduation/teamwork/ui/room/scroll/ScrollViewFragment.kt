package com.graduation.teamwork.ui.room.scroll

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.florent37.hollyviewpager.HollyViewPagerBus
import com.graduation.teamwork.R
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.databinding.FragHolleyScrollBinding
import com.graduation.teamwork.extensions.showToast
import com.graduation.teamwork.extensions.toJson
import com.graduation.teamwork.extensions.toListObject
import com.graduation.teamwork.extensions.toObject
import com.graduation.teamwork.models.DtStage
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.ui.base.BaseFragment
import com.graduation.teamwork.ui.room.details.RoomDetailActivity
import com.graduation.teamwork.ui.room.details.RoomDetailViewModel
import com.graduation.teamwork.ui.task.details.DetailTaskActivity
import com.graduation.teamwork.utils.DialogManager
import com.graduation.teamwork.utils.PrefsManager
import com.graduation.teamwork.utils.eventbus.RxBus
import com.graduation.teamwork.utils.eventbus.RxEvent
import es.dmoral.toasty.Toasty
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension

/**
 * com.graduation.teamwork.ui.room.scroll
 * Created on 11/21/20
 */

@KoinApiExtension
class ScrollViewFragment : BaseFragment<FragHolleyScrollBinding, RoomDetailActivity>() {
    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragHolleyScrollBinding =
        FragHolleyScrollBinding.inflate(inflater, container, false)

    private val TAG = "__RoomDetailActivity1"

    /**
     * INJECT
     */
    private val viewModel: RoomDetailViewModel by inject()
    private val dialogs: DialogManager by inject()
    private val prefs: PrefsManager by inject()

    private lateinit var mStage: DtStage
    private lateinit var mUsers: List<DtUser>
    private lateinit var idRoom: String

    private val adapter = StageAdapter(emptyList()) { item, position ->
        Log.d(TAG, "idRoom: $idRoom")
        Intent(requireContext(), DetailTaskActivity::class.java).also {
            it.putExtra(Constant.IntentKey.DETAIL_TASK.value, item.toJson())
            it.putExtra(Constant.IntentKey.DETAIL_USER.value, mUsers.toJson())
            it.putExtra(Constant.IntentKey.ID_ROOM.value, idRoom)

            startActivity(it)
            activity?.overridePendingTransition(R.anim.from_right_in, R.anim.nothing)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.run {
            mStage = this.getString(Constant.IntentKey.STAGE.value)!!.toObject<DtStage>()!!
            mUsers = this.getString(Constant.IntentKey.USERS.value)!!.toListObject()!!
            idRoom = this.getString(Constant.IntentKey.ID_ROOM.value)!!

//            Log.d(TAG, "onCreateView: title = ${mStage.tasks?.first()?.name} - ${mStage.tasks?.first()?.labels}")
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
        setListener()
    }

    private fun setViews() {
        binding?.run {
            HollyViewPagerBus.registerScrollView(activity, holleyScroll)

            recyclerStage.run {
                layoutManager = LinearLayoutManager(context)
                adapter = this@ScrollViewFragment.adapter
                setHasFixedSize(true)
            }
        }
        updateViews()
    }

    private fun updateViews() {
        binding?.run {
            if (::mStage.isInitialized) {
                tvStage.text = mStage.name
            }
            mStage.tasks?.let { adapter.submitList(it) }
        }

    }

    fun updateStage(stage: DtStage) {
        mStage = stage
        updateViews()
    }

    fun getStageId() = if (::mStage.isInitialized) { mStage._id ?: "" } else ""

    private fun setListener() {
        binding?.run {
            frlAddTask.setOnClickListener {
                dialogs.inputDialog(
                    requireContext(),
                    getString(R.string.add_task),
                    getString(R.string.dialog_add_task),
                    {
                        if (it.isNotBlank()) {
                            handler?.showProgress()
                            mStage._id?.let { it1 -> viewModel.addTaskInStage(it1, it) }
                        } else {
                            Toasty.error(requireContext(), R.string.notify_error_name_empty).show()
                        }
                    },
                    {})
            }
        }

        viewModel.resources.observe(viewLifecycleOwner) {
            handler?.hideProgress()
            if (it.data != null) {
                mStage = it.data.first()
                mStage.tasks?.let { it1 -> adapter.submitList(it1) }

                RxBus.publishToPublishSubject(RxEvent.RoomUpdate(""))
                prefs.setChange(true)
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param title title
         * @return A new instance of fragment ScrollViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(stage: DtStage, users: List<DtUser>, id: String): ScrollViewFragment =
            ScrollViewFragment().apply {
                arguments = Bundle().apply {
                    putString(Constant.IntentKey.STAGE.value, stage.toJson())
                    putString(Constant.IntentKey.USERS.value, users.toJson())
                    putString(Constant.IntentKey.ID_ROOM.value, id)
                }
            }

    }
}