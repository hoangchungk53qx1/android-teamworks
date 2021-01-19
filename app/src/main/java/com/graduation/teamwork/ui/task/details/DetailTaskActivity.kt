package com.graduation.teamwork.ui.task.details

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Data
import com.graduation.teamwork.R
import com.graduation.teamwork.adapters.SubtaskAdapter
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.databinding.ActDetailTaskBinding
import com.graduation.teamwork.extensions.*
import com.graduation.teamwork.models.DtSubtask
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.models.data.TaskListItem
import com.graduation.teamwork.ui.base.BaseActivity
import com.graduation.teamwork.utils.PrefsManager
import com.graduation.teamwork.utils.eventbus.RxBus
import com.graduation.teamwork.utils.eventbus.RxEvent
import com.graduation.teamwork.utils.notify.NotifyHelper
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.act_detail_task.*
import kotlinx.android.synthetic.main.act_detail_task.view.*
import kotlinx.android.synthetic.main.act_task.view.*
import kotlinx.android.synthetic.main.act_task.view.tvTitle
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*


class DetailTaskActivity : BaseActivity<ActDetailTaskBinding>(), TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener {
    override fun setBinding(inflater: LayoutInflater): ActDetailTaskBinding =
        ActDetailTaskBinding.inflate(inflater)

    /**
     * INJECT
     */
    private val viewModel: DetailViewModel by inject()
    private val prefs: PrefsManager by inject()

    private var currentUser: DtUser? = prefs.getUser()
    private lateinit var task: TaskListItem.DtTask
    private lateinit var users: List<DtUser>

    private var mAdapter = SubtaskAdapter(mutableListOf())

    private var timeHour = ""
    private var timeDay = ""
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var timePickerDialog: TimePickerDialog

    private val mNotifyHelper: NotifyHelper = NotifyHelper.getInstance(this)!!

    override fun onViewReady(savedInstanceState: Bundle?) {

        receiverData()
        setupViews()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()

        updateViews()
    }

    private fun receiverData() {
        if (intent.getStringExtra(Constant.INTENT.DETAIL_TASK.value) != null &&
            intent.getStringExtra(Constant.INTENT.DETAIL_USER.value) != null
        ) {
            task = intent.getStringExtra(Constant.INTENT.DETAIL_TASK.value)!!
                .fromObject<TaskListItem.DtTask>()!!
            users = intent.getStringExtra(Constant.INTENT.DETAIL_USER.value)!!.fromJson()!!
        }

    }

    private fun setupViews() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDarkTask)
        binding?.run {
            setSupportActionBar(toolbar)
            showBackButton(true)
            toolbar.rlToolbarDetailTask.tvTitle.text = task.name

            with(recyclerTaskList) {
                layoutManager = LinearLayoutManager(this@DetailTaskActivity)
                setHasFixedSize(true)

                adapter = mAdapter
            }

            if (::task.isInitialized && task.deadline != null) {
                Log.d(TAG, "updateViews: TIME1 = ${task.name} - ${task.deadline!!.toDate().formatDateTime()} - ${task.deadline}")
                tvDeadline.text = task.deadline!!.toDate().formatDateTime()
            }
        }

        if (::task.isInitialized) {
            task.subtasks?.let { mAdapter.submitList(it) }
        }
    }

    private fun updateViews() {
        binding?.run{
            if (::task.isInitialized && task.deadline != null) {
                Log.d(TAG, "updateViews: TIME2 = ${task.name} - ${task.deadline!!.toDate().formatDateTime()} - ${task.deadline}")
                tvDeadline.text = task.deadline!!.toDate().formatDateTime()
            }
        }
    }

    private fun setupListeners() {
        binding?.run {
            edtAddSubTask.doOnTextChanged { text, start, before, count ->
                imgAddSubtaskAccept.setVisiable(!text.isNullOrBlank())
                imgAddSubtaskClose.setVisiable(!text.isNullOrBlank())
            }

            imgAddSubtaskClose.setOnClickListener {
                edtAddSubTask.text.clear()
            }

            imgAddSubtaskAccept.setOnClickListener {

                viewModel.addSubtask(task._id!!, edtAddSubTask.text.toString(), currentUser!!._id!!)
//                showProgress(getString(R.string.loading_message))

                val newList = this@DetailTaskActivity.task.subtasks?.toMutableList()!!
                newList.add(
                    DtSubtask(
                        _id = "dsads", name = edtAddSubTask.text.toString(), idTask = "sdasdas"
                    )
                )
                mAdapter.submitList(newList)

                RxBus.publishToPublishSubject(RxEvent.UpdateRoom)
                edtAddSubTask.text.clear()
            }

            lnDeadline.setOnClickListener {
                showDatetimePicker()
            }
        }

        viewModel.resources.observe(this, {
//            hideProgres()
            if (it.data != null) {
                this.task = it.data

                task.subtasks?.let { mAdapter.submitList(it) }
            }
        })

        viewModel.resourcesUpdate.observe(this, {
            if (it.data != null) {
                RxBus.publishToPublishSubject(RxEvent.UpdateTask)
            }
        })
    }

    fun showDatetimePicker() {
        val now = Calendar.getInstance()
        if (task.deadline != null) {
            Log.d(TAG, "updateViews: TIME3 = ${task.name} - ${task.deadline!!.toDate().formatDateTime()} - ${task.deadline}")
            now.timeInMillis = task.deadline!!
        }
        /*
            It is recommended to always create a new instance whenever you need to show a Dialog.
            The sample app is reusing them because it is useful when looking for regressions
            during testing
             */
        /*
            It is recommended to always create a new instance whenever you need to show a Dialog.
            The sample app is reusing them because it is useful when looking for regressions
            during testing
             */
        /*
            It is recommended to always create a new instance whenever you need to show a Dialog.
            The sample app is reusing them because it is useful when looking for regressions
            during testing
             */
        if (!::datePickerDialog.isInitialized) {
            datePickerDialog = DatePickerDialog.newInstance(
                this,
                now[Calendar.YEAR],
                now[Calendar.MONTH],
                now[Calendar.DAY_OF_MONTH]
            )
        } else {
            datePickerDialog.initialize(
                this,
                now[Calendar.YEAR],
                now[Calendar.MONTH],
                now[Calendar.DAY_OF_MONTH]
            )
        }

        datePickerDialog.setOnCancelListener { dialog: DialogInterface? ->
            Log.d("DatePickerDialog", "Dialog was cancelled")
        }
        datePickerDialog.show(supportFragmentManager, "Datepickerdialog")
    }


    private val TAG = "__DetailTaskActivity"
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()

                true
            }
//            R.id.moreHor -> {
//                binding?.drawerTask?.openDrawer(GravityCompat.END)
//                true
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        timeHour = "$hourOfDay:$minute"

        val fulltime = "$timeDay $timeHour"

        val date =
            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse("$fulltime")

        Log.d(TAG, "onTimeSet: ${task._id!!}")

        if (date!!.time > now()) {
            viewModel.changeDeadline(task._id!!, date.time, currentUser!!._id!!)
            binding?.tvDeadline?.text = "$timeDay $timeHour"

//            val data = Data.Builder().putInt(NOTIFICATION_ID, 0).build()
            val delay = date.time - now()

            setScheduler(delay)
            showToast("Lên lịch thành công")
        }else {
            showToast("Vui lòng chọn thời gian lớn hơn hiện tại")
        }
    }

    fun setScheduler(timeDelay: Long) {
        mNotifyHelper.schedule(timeDelay)

    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        timeDay = "$dayOfMonth/${monthOfYear + 1}/$year"

        val now = Calendar.getInstance()
        if (task.deadline != null) {
            Log.d(TAG, "updateViews: TIME4 = ${task.name} - ${task.deadline!!.toDate().formatDateTime()}")
            now.timeInMillis = task.deadline!!
        }
        /*
            It is recommended to always create a new instance whenever you need to show a Dialog.
            The sample app is reusing them because it is useful when looking for regressions
            during testing
             */if (!::timePickerDialog.isInitialized) {
            timePickerDialog = TimePickerDialog.newInstance(
                this,
                now[Calendar.HOUR_OF_DAY],
                now[Calendar.MINUTE],
                true
            )
        } else {
            timePickerDialog.initialize(
                this,
                now[Calendar.HOUR_OF_DAY],
                now[Calendar.MINUTE],
                now[Calendar.SECOND],
                true
            )
        }

        timePickerDialog.setOnCancelListener { dialogInterface: DialogInterface? ->
            Log.d("TimePicker", "Dialog was cancelled")
        }
        timePickerDialog.show(supportFragmentManager, "Timepickerdialog")
        Log.d(TAG, "onDateSet: $timeDay")
    }
}