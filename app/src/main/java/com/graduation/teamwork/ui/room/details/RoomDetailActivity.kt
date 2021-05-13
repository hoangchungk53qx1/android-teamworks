package com.graduation.teamwork.ui.room.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.observe
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.github.florent37.hollyviewpager.HollyViewPagerConfigurator
import com.graduation.teamwork.R
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.databinding.ActRoomDetailBinding
import com.graduation.teamwork.extensions.gone
import com.graduation.teamwork.extensions.showToast
import com.graduation.teamwork.extensions.visiable
import com.graduation.teamwork.models.DtRoom
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.ui.base.BaseActivity
import com.graduation.teamwork.ui.room.scroll.ScrollViewFragment
import com.graduation.teamwork.ui.slider.SliderRoomFragment
import com.graduation.teamwork.utils.DialogManager
import com.graduation.teamwork.utils.PrefsManager
import com.graduation.teamwork.utils.eventbus.RxBus
import com.graduation.teamwork.utils.eventbus.RxEvent
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.act_room_detail.view.*
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension

/**
 * com.graduation.teamwork.ui.task
 * Created on 11/19/20
 */


@KoinApiExtension
class RoomDetailActivity : BaseActivity<ActRoomDetailBinding>() {
    override fun setBinding(inflater: LayoutInflater): ActRoomDetailBinding =
        ActRoomDetailBinding.inflate(inflater)

    private val TAG = "__RoomDetailActivity"

    /**
     * INJECT
     */
    private val viewModel: RoomDetailViewModel by inject()
    private val prefs: PrefsManager by inject()
    private val dialogs: DialogManager by inject()

    private var currentUser: DtUser? = prefs.getUser()
    private var frags = mutableListOf<ScrollViewFragment>()
    var users = mutableListOf<DtUser>()
    private var isRotate = false
    private var isOpenDrawerDetail = false
    private var isOpenDrawer = false
    private var roomId: String = ""
    private lateinit var room: DtRoom

    override fun onViewReady(savedInstanceState: Bundle?) {
        receiverData()
        setViews()
        setListener()
    }

    private fun receiverData() {
        if (intent.getStringExtra(Constant.IntentKey.ID_ROOM.value) != null) {
            roomId = intent.getStringExtra(Constant.IntentKey.ID_ROOM.value) ?: ""
        }
        viewModel.getRoom(roomId)
        viewModel.getAllUser(currentUser!!._id!!)
        showProgress()
    }

    private fun setViews() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDarkTask)

        binding?.run {
            setSupportActionBar(toolbar)
            showBackButton(true)
        }
    }

    private fun setListener() {
        binding?.run {
            with(this.fabLayout) {
                fabAdd.setOnFloatingActionsMenuUpdateListener(object :
                    FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {
                    override fun onMenuCollapsed() {
                        floatView.gone()
                    }

                    override fun onMenuExpanded() {
                        floatView.visiable()
                    }

                })

                floatView.setOnClickListener {
                    fabAdd.toggle()
                }

                fabAddStage.setOnClickListener {
                    fabAdd.toggle()
                    dialogs.inputDialog(this@RoomDetailActivity,
                        getString(R.string.add_stage),
                        getString(
                            R.string.name_stage
                        ),
                        {
                            if (it.isNotBlank()) {
                                showProgress()

                                viewModel.addStageInRoom(room._id!!, it, currentUser!!._id!!)
                            } else {
                                Toasty.error(this@RoomDetailActivity, R.string.notify_empty_stage).show()
                            }
                        },
                        {})
                }

                fabAddMember.setOnClickListener {
                    fabAdd.toggle()
                    dialogs.inputDialog(
                        this@RoomDetailActivity,
                        R.string.add_member,
                        R.string.add_member_mail,
                        { mail ->
                            if (mail.isNotBlank()) {
                                val user = this@RoomDetailActivity.users.firstOrNull {
                                    it.mail?.equals(mail) ?: false
                                }

                                if (user != null) {
                                    viewModel.addUserInRoom(
                                        room._id!!,
                                        currentUser!!._id!!,
                                        user._id!!
                                    )
                                    Toasty.success(this@RoomDetailActivity, "Thêm thành viên thành công").show()
                                } else {
                                    Toasty.error(this@RoomDetailActivity, "Thêm thành viên thất bại").show()
                                }
                            } else {
                                Toasty.error(this@RoomDetailActivity, R.string.notify_empty_stage).show()
                            }
                        })
                }
            }

            with(viewModel) {
                resourcesRoom.observeForever {
                    if (it.data != null) {
                        room = it.data

                        reloadFragsManager()
                        updateViews()

                        RxBus.publishToPublishSubject(RxEvent.RoomUpdate(roomId))
                        RxBus.publishToPublishSubject(RxEvent.MemberAdded)
                    }
                    hideProgress()
                }

                resourcesUser.observe(this@RoomDetailActivity) {
                    if (it.data != null) {
                        users.clear()
                        users.addAll(it.data)
                    }
                    hideProgress()
                }
            }

//            RxBus.listenPublisher(RxEvent.RoomUpdate::class.java)
//                .observeOnUiThread()
//                .autoDisposable(this@RoomDetailActivity.scope())
//                .subscribe {
//                    Log.d(TAG, "setListener: roomId = ${it.id}")
//                    viewModel.getRoom(it.id)
//                    showProgress()
//                }
        }
    }

    private fun reloadFragsManager() {
        frags.clear()
        val users = room.users?.mapNotNull { it.user }

        room.stages?.let {
            for (stage in it) {
                if (stage != null) {
                    val scrollFound = frags.find { it.getStageId() == stage._id ?: "" }

                    if (scrollFound != null) {
                        scrollFound.updateStage(stage)
                    } else {
                        frags.add(ScrollViewFragment.newInstance(stage, users!!, roomId))
                    }

                }
            }
        }
    }

    private fun updateViews() {
        binding?.run {
            toolbar.rlToolbarTask.tvTitle.text = room.name

//            hollyViewPager.setBackgroundResource(R.color.accentGreen)
            //Holley Viewpager
            if (hollyViewPager.configurator == null) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(fragRoomSlider.id, SliderRoomFragment.newInstance(room))
                    .commit()
            }
            hollyViewPager.configurator =
                HollyViewPagerConfigurator { page ->
                    if (room.stages.isNullOrEmpty()) {
                        10.0f
                    } else {
                        room.stages?.get(page)?.tasks?.size?.plus(2)!!.toFloat()
                    }
                }

            updateAdapterScroll()
        }
    }

    private fun updateAdapterScroll() {
//        Log.d(TAG, "updateAdapterScroll: OK - ${room.stages?.get(0)?.tasks?.first()?.labels}")
        binding?.hollyViewPager?.setAdapter(object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getCount(): Int = frags.size

            override fun getItem(position: Int): Fragment = frags[position]

            override fun getPageTitle(position: Int) = room.stages?.get(position)?.name
        })
    }

    fun getSlideFragment() = binding!!.fragRoomSlider.id

    private fun closeDrawerTask() {
        binding?.drawerTask?.closeDrawers()
        isOpenDrawerDetail = false
    }

    fun openDrawerTask(v: View) {
        binding?.drawerTask?.openDrawer(GravityCompat.END)
        isOpenDrawerDetail = true
    }

    override fun onBackPressed() {
        if (isRotate) {
            binding?.floatView?.performClick()
        } else if (isOpenDrawerDetail) {
            if (supportFragmentManager.backStackEntryCount >= 1) {
                supportFragmentManager.popBackStack()
            } else {
                closeDrawerTask()
            }
        } else {
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            super.onBackPressed()
            true
        }
        R.id.moreHor -> {
            binding?.drawerTask?.openDrawer(GravityCompat.END)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.nothing, R.anim.from_right_out)
    }
}