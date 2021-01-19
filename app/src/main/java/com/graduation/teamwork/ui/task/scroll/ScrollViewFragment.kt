package com.graduation.teamwork.ui.task.scroll

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.florent37.hollyviewpager.HollyViewPagerBus
import com.graduation.teamwork.R
import com.graduation.teamwork.adapters.StageAdapter
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.databinding.FragHolleyScrollBinding
import com.graduation.teamwork.extensions.fromJson
import com.graduation.teamwork.extensions.fromObject
import com.graduation.teamwork.models.DtStage
import com.graduation.teamwork.ui.base.BaseFragment
import com.graduation.teamwork.ui.task.TaskActivity
import com.graduation.teamwork.ui.task.TaskViewModel
import com.graduation.teamwork.ui.task.details.DetailTaskActivity
import com.graduation.teamwork.utils.DialogManager
import com.graduation.teamwork.extensions.showToast
import com.graduation.teamwork.extensions.toJson
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.utils.eventbus.RxBus
import com.graduation.teamwork.utils.eventbus.RxEvent
import kotlinx.android.synthetic.main.act_task.*
import kotlinx.android.synthetic.main.act_task.view.*
import org.koin.android.ext.android.inject

/**
 * com.graduation.teamwork.ui.task.scroll
 * Created on 11/21/20
 */

class ScrollViewFragment : BaseFragment<FragHolleyScrollBinding, TaskActivity>() {
    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragHolleyScrollBinding =
        FragHolleyScrollBinding.inflate(inflater, container, false)

    private val TAG = "__ScrollViewFragment"

    /**
     * INJECT
     */
    private val viewModel: TaskViewModel by inject()
    private val dialogs: DialogManager by inject()

    private val adapter = StageAdapter(mutableListOf()) { item, position ->
//        handler?.showToast("clicked in ${item.name}")

        Intent(requireContext(), DetailTaskActivity::class.java).also {
            it.putExtra(Constant.INTENT.DETAIL_TASK.value, item.toJson())
            it.putExtra(Constant.INTENT.DETAIL_USER.value, mUsers.toJson())


            startActivity(it)
        }
    }
    lateinit var mStage: DtStage
    lateinit var mUsers: List<DtUser>
//    var tasks = mutableListOf<TaskListItem.DtTask>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        arguments?.run {
            mStage = this.getString(Constant.INTENT.STAGE.value)!!.fromObject<DtStage>()!!
            mUsers = this.getString(Constant.INTENT.USERS.value)!!.fromJson()!!
            Log.d(TAG, "onCreateView: ${mStage.tasks?.size}")
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

            recyclerStage.run {
                layoutManager = LinearLayoutManager(context)
                adapter = this@ScrollViewFragment.adapter
                setHasFixedSize(true)
            }

            HollyViewPagerBus.registerScrollView(activity, holleyScroll)

            if (::mStage.isInitialized) {
                tvStage.text = mStage.name
            }
//            HollyViewPagerBus.registerRecyclerView(context, recyclerStage)
        }

        if (::mStage.isInitialized) {
            mStage.tasks?.let { adapter.submitList(it) }
        }
    }

    private fun setListener() {
        binding?.run {
            frlAddTask.setOnClickListener {
                dialogs.inputDialog(requireContext(), getString(R.string.add_task), getString(R.string.dialog_add_task), {
                    if (it.isNotBlank()) {
                        handler?.showProgress(getString(R.string.loading_message))
                        mStage._id?.let { it1 -> viewModel.addTaskInStage(it1, it) }
                    } else {
                        requireContext().showToast(getString(R.string.notify_error_name_empty))
                    }
                }, {

                })
            }
        }

        viewModel.resources.observe(viewLifecycleOwner, {
            handler?.hideProgres()
            if (it.data != null) {
                mStage = it.data.first()
                Log.d(TAG, "updateViews: TIME - ALL ${mStage.tasks}")
                mStage.tasks?.let { it1 -> adapter.submitList(it1) }

                RxBus.publishToPublishSubject(RxEvent.UpdateRoom)
            }
        })
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
        fun newInstance(stage: DtStage, users: List<DtUser>): ScrollViewFragment {

            val bundle = Bundle().apply {
                putString(Constant.INTENT.STAGE.value, stage.toJson())
                putString(Constant.INTENT.USERS.value, users.toJson())
            }

            return ScrollViewFragment().apply {
                arguments = bundle
            }
        }
    }
}