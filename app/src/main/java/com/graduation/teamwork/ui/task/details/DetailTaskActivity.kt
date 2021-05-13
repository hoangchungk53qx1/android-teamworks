package com.graduation.teamwork.ui.task.details

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.crazylegend.imagepicker.pickers.SingleImagePicker
import com.graduation.teamwork.R
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.databinding.ActDetailTaskBinding
import com.graduation.teamwork.extensions.*
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.models.TypeAttachment
import com.graduation.teamwork.models.data.TaskListItem
import com.graduation.teamwork.ui.base.BaseActivity
import com.graduation.teamwork.ui.task.details.adapter.DetailAttachmentAdapter
import com.graduation.teamwork.ui.task.details.adapter.DetailLabelAdapter
import com.graduation.teamwork.ui.task.details.adapter.DetailMemberAdapter
import com.graduation.teamwork.ui.task.details.adapter.DetailTaskListAdapter
import com.graduation.teamwork.utils.DialogManager
import com.graduation.teamwork.utils.PrefsManager
import com.graduation.teamwork.utils.eventbus.RxBus
import com.graduation.teamwork.utils.eventbus.RxEvent
import com.graduation.teamwork.utils.notify.NotifyHelper
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.act_detail_task.view.*
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
    private val dialogs: DialogManager by inject()
    private val prefs: PrefsManager by inject()

    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var timePickerDialog: TimePickerDialog
    private lateinit var task: TaskListItem.DtTask
    private lateinit var users: List<DtUser>

    private val TAG = "__DetailTaskActivity"

    private val mNotifyHelper: NotifyHelper = NotifyHelper.getInstance(this)!!
    private var adapterLabel = DetailLabelAdapter(Constant.LabelColor.allColors(), mutableListOf())
    private var adapterTask = DetailTaskListAdapter(mutableListOf()) { item ->
        viewModel.setCompletedSubTask(item._id!!, item.name ?: "", item.isCompleted)
        RxBus.publishToPublishSubject(RxEvent.RoomUpdate(""))
        prefs.setChange(true)
    }
    private var adapterLink = DetailAttachmentAdapter(mutableListOf()) {}
    private var adapterImage = DetailAttachmentAdapter(mutableListOf()) {}
    private var adapterMember = DetailMemberAdapter(mutableListOf(), mutableListOf()) { item, _ ->
        if (item.isSelected) {
            viewModel.addUserInTask(task._id!!, item._id!!, currentUser?._id!!)
        } else {
            viewModel.deleteUserInTask(task._id!!, item._id!!, currentUser?._id!!)
        }
        RxBus.publishToPublishSubject(RxEvent.RoomUpdate(""))
        prefs.setChange(true)
    }

    private var currentUser: DtUser? = prefs.getUser()
    private var currentSheetOpen = Constant.SheetType.NONE
    private var timeHour = ""
    private var timeDay = ""

    override fun onViewReady(savedInstanceState: Bundle?) {
        receiverData()
        setupViews()
        setupListeners()
    }

    private fun receiverData() {
        if (intent.getStringExtra(Constant.IntentKey.DETAIL_TASK.value) != null &&
            intent.getStringExtra(Constant.IntentKey.DETAIL_USER.value) != null
        ) {
            task = intent.getStringExtra(Constant.IntentKey.DETAIL_TASK.value)!!
                .toObject<TaskListItem.DtTask>()!!
            users = intent.getStringExtra(Constant.IntentKey.DETAIL_USER.value)!!.toListObject()!!
        }
    }

    private fun setupToolbar() {
        binding?.run {
            showBackButton(isShow = true)
            setSupportActionBar(toolbar)
//            toolbar.title = task.name

        }
    }

    private fun setupDefaultValue() {
        binding?.run {
            toolbar.tvTitle.text = task.name
            edtDescription.setText(task.description)

            if (task.deadline == 0L) {
                detailInfo.tvDeadline.text = "Ngày hết hạn"
            } else {
                detailInfo.tvDeadline.text = task.deadline.toDate().formatDateTime()
            }

            if (::users.isInitialized) {

                adapterMember.submitList(users)
                adapterMember.submitSelected(users.intersectUser(task.users!!))
            }
            if (::task.isInitialized) {
                task.let {
                    it.subtasks?.let { adapterTask.submitList(it) }
                    it.attachments?.let {
                        adapterLink.submitList(it.filter { it.type == TypeAttachment.LINK.value })
                        adapterImage.submitList(it.filter { it.type == TypeAttachment.IMAGE.value })
                    }

                    adapterLabel.submitSelected(task.labels ?: mutableListOf())
                }

                /* check show recyclerview */
                with(detailAttachment) {
                    recyclerLink.setVisiable(
                        !task.attachments.isNullOrEmpty() &&
                                !task.attachments?.filter { it.type == TypeAttachment.LINK.value }
                                    .isNullOrEmpty()
                    )
                    tvAttachmentLinkEmpty.setVisiable(
                        task.attachments.isNullOrEmpty() ||
                                task.attachments?.filter { it.type == TypeAttachment.LINK.value }
                                    .isNullOrEmpty()
                    )

                    recyclerImage.setVisiable(
                        !task.attachments.isNullOrEmpty() &&
                                !task.attachments?.filter { it.type == TypeAttachment.IMAGE.value }
                                    .isNullOrEmpty()
                    )
                    tvAttachmentImageEmpty.setVisiable(
                        task.attachments.isNullOrEmpty() &&
                                task.attachments?.filter { it.type == TypeAttachment.IMAGE.value }
                                    .isNullOrEmpty()
                    )
                }

                with(detailTask) {
                    recyclerTask.setVisiable(!task.subtasks.isNullOrEmpty())
                    tvTaskEmpty.setVisiable(task.subtasks.isNullOrEmpty())
                }
            }
        }
    }

    private fun setupViews() {
        binding?.run {
            setupToolbar()
            setupDefaultValue()

            /* setup recyclerview */
            with(detailTask.recyclerTask) {
                layoutManager = LinearLayoutManager(this@DetailTaskActivity)
                setHasFixedSize(true)
                adapter = adapterTask
            }

            with(detailAttachment) {
                recyclerImage.run {
                    layoutManager = GridLayoutManager(this@DetailTaskActivity, 3)
                    setHasFixedSize(true)
                    adapter = adapterImage
                }

                recyclerLink.run {
                    layoutManager = LinearLayoutManager(this@DetailTaskActivity)
                    setHasFixedSize(true)
                    adapter = adapterLink
                }
            }

            with(bottomSheetLayout.recyclerSheet) {
                layoutManager = LinearLayoutManager(this@DetailTaskActivity)
                setHasFixedSize(true)
                adapter = adapterMember
            }
        }
    }

    private fun setupListeners() {
        binding?.run {

            with(detailInfo) {
                lnDeadline.setOnClickListener {
                    if (bottomSheetLayout.bottomSheet.isVisible) {
                        setShowButtomSheet(isShow = false)
                    } else {
                        showDatetimePicker()
                    }
                }

                lnLabel.setOnClickListener {
                    bottomSheetLayout.run {
                        tvTitle.text = "Nhãn thẻ"
                        recyclerSheet.adapter = adapterLabel
                        btnAdd.gone()
                    }
                    currentSheetOpen = Constant.SheetType.LABEL
                    toggleButtomSheet()
                }

                lnMember.setOnClickListener {
                    bottomSheetLayout.run {
                        tvTitle.text = "Thành viên của thẻ"
                        recyclerSheet.adapter = adapterMember
                        btnAdd.gone()
                    }
                    currentSheetOpen = Constant.SheetType.MEMBER

                    toggleButtomSheet()
                }

                detailTask.btnAdd.setOnClickListener {
                    dialogs.inputDialog(
                        context = this@DetailTaskActivity,
                        title = "Thêm nhiệm vụ",
                        handlerOk = {
                            viewModel.addSubTask(task._id!!, it, currentUser?._id!!)
                            showProgress("Thêm nhiệm vụ")
                        })
                }

                detailAttachment.btnAdd.setOnClickListener {
                    dialogs.singleChoice(
                        this@DetailTaskActivity,
                        "Lựa chọn",
                        listOf("Camera", "Photos", "Link")
                    ) {
                        Log.d(TAG, "setupListeners: selected $it")

                        when (it) {
                            "Photos" -> {
                                if (ActivityCompat.checkSelfPermission(
                                        this@DetailTaskActivity,
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    SingleImagePicker.showPicker(this@DetailTaskActivity) {
                                        viewModel.uploadImage(
                                            this@DetailTaskActivity,
                                            it.contentUri,
                                            task._id!!,
                                            currentUser?._id!!
                                        )
                                        showProgress("Thêm ảnh")
                                    }
                                }
                            }
                            "Camera" -> {

                            }
                            else -> {
                                dialogs.inputDialog(
                                    this@DetailTaskActivity,
                                    "Thêm link",
                                    "https://www.youtube.com/",
                                    {
                                        if (!it.isBlank()) {
                                            viewModel.uploadLink(task._id!!, it, currentUser?._id!!)

                                            showProgress("Thêm link")
                                        }
                                    })
                            }
                        }
                    }
                }

                bottomSheetLayout.run {
                    btnClose.setOnClickListener {
                        if (bottomSheetLayout.bottomSheet.isVisible) {
                            setShowButtomSheet(isShow = false)
                        }

                        if (adapterLabel.hasSelectedColor() && currentSheetOpen == Constant.SheetType.LABEL) {
                            viewModel.setLabels(
                                task._id!!,
                                adapterLabel.getSelectedColor()
                            )
                            adapterLabel.resetSelected()
                            showProgress("Thêm nhãn")
                        } else if (adapterMember.hasSelectedMember() && currentSheetOpen == Constant.SheetType.MEMBER) {
                            viewModel.queryMemberInTask(task._id!!)
                            adapterMember.resetSelected()
                            showProgress("Thêm thành viên")
                        }
                    }

                    btnAdd.setOnClickListener {
                        dialogs.inputDialog(
                            context = this@DetailTaskActivity,
                            title = "Thêm nhiệm vụ",
                            handlerOk = {
                                viewModel.addSubTask(task._id!!, it, currentUser?._id!!)
                                showProgress("Thêm nhiệm vụ")
                            })
                    }
                }
            }

            with(viewModel) {
                resources.observe(this@DetailTaskActivity) {
                    if (it.data != null) {
                        this@DetailTaskActivity.task = it.data
                        setupDefaultValue()
                        RxBus.publishToPublishSubject(RxEvent.RoomUpdate(""))
                        prefs.setChange(true)
                    }
                    hideProgress()
                }

                resourcesUpdate.observe(this@DetailTaskActivity) {
                    if (it.data != null) {
                        this@DetailTaskActivity.task = it.data.first()
                        setupDefaultValue()
                        RxBus.publishToPublishSubject(RxEvent.RoomUpdate(""))
                        prefs.setChange(true)
                    }
                    hideProgress()
                }
            }
        }
    }

    private fun showDatetimePicker() {
        val now = Calendar.getInstance()
        if (task.deadline != 0L) {
            now.timeInMillis = task.deadline
        }

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

    private fun setShowButtomSheet(isShow: Boolean = false) {
        binding?.run {
            if (isShow) {
                bottomSheetLayout.bottomSheet.slideUp()
            } else {
                bottomSheetLayout.bottomSheet.slideDown()
            }
        }
    }

    private fun toggleButtomSheet() {
        binding?.run {
            if (bottomSheetLayout.bottomSheet.isVisible) {
                bottomSheetLayout.bottomSheet.slideDown()
                currentSheetOpen = Constant.SheetType.NONE
            } else {
                bottomSheetLayout.bottomSheet.slideUp()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (binding?.bottomSheetLayout?.bottomSheet?.isVisible == true) {
            setShowButtomSheet(isShow = false)
        } else {
            super.onBackPressed()
        }
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        timeHour = "$hourOfDay:$minute"
        val date =
            SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            ).parse("$timeDay $timeHour")

        if (date!!.time > now()) {
            val delay = date.time - now()
            setScheduler(delay)

            viewModel.changeDeadline(task._id!!, date.time, currentUser!!._id!!)
            Toasty.success(this, "Lên lịch thành công").show()
        } else {
            Toasty.error(this, "Vui lòng chọn thời gian lớn hơn hiện tại").show()
        }
    }

    private fun setScheduler(timeDelay: Long) {
        mNotifyHelper.schedule(timeDelay)
    }

    override fun onDateSet(
        view: DatePickerDialog?,
        year: Int,
        monthOfYear: Int,
        dayOfMonth: Int
    ) {
        timeDay = "$dayOfMonth/${monthOfYear + 1}/$year"

        val now = Calendar.getInstance()
        if (task.deadline != null) {
            now.timeInMillis = task.deadline!!
        }

        if (!::timePickerDialog.isInitialized) {
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
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.nothing, R.anim.from_right_out)
    }
}
