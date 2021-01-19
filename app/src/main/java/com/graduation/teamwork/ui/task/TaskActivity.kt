package com.graduation.teamwork.ui.task

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.github.florent37.hollyviewpager.HollyViewPagerConfigurator
import com.graduation.teamwork.R
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.databinding.ActTaskBinding
import com.graduation.teamwork.models.DtRoom
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.models.Room
import com.graduation.teamwork.ui.base.BaseActivity
import com.graduation.teamwork.ui.task.scroll.ScrollViewFragment
import com.graduation.teamwork.ui.task.scroll.ScrollViewFragment.Companion.newInstance
import com.graduation.teamwork.utils.DialogManager
import com.graduation.teamwork.utils.PrefsManager
import com.graduation.teamwork.extensions.showToast
import com.graduation.teamwork.ui.base.IOnBackPressed
import com.graduation.teamwork.ui.home.HomeFragment
import com.graduation.teamwork.ui.notification.NotificationFragment
import com.graduation.teamwork.ui.profile.ProfileFragment
import com.graduation.teamwork.ui.room.RoomFragment
import com.graduation.teamwork.ui.slider.SliderRoomFragment
import com.graduation.teamwork.ui.task.details.member.MemberDetailFragment
import com.graduation.teamwork.utils.animation.ViewAnimation
import com.graduation.teamwork.utils.eventbus.RxBus
import com.graduation.teamwork.utils.eventbus.RxEvent
import kotlinx.android.synthetic.main.act_task.*
import kotlinx.android.synthetic.main.act_task.view.*
import kotlinx.android.synthetic.main.frag_holley_scroll.*
import org.koin.android.ext.android.inject

/**
 * com.graduation.teamwork.ui.task
 * Created on 11/19/20
 */


class TaskActivity : BaseActivity<ActTaskBinding>() {
    override fun setBinding(inflater: LayoutInflater): ActTaskBinding =
        ActTaskBinding.inflate(inflater)

    private val TAG = "__TaskActivity"

    /**
     * INJECT
     */
    private val viewModel: TaskViewModel by inject()
    private val prefs: PrefsManager by inject()
    private val dialogs: DialogManager by inject()

    private lateinit var room: DtRoom
    private var currentUser: DtUser? = prefs.getUser()
    var users = mutableListOf<DtUser>()

    private var frags = mutableListOf<ScrollViewFragment>()
    private var isRotate = false


//    private val fragmentsManager = mutableListOf<Fragment>(
//        SliderRoomFragment.newInstance(),
//        MemberDetailFragment.newInstance()
//    )

    override fun onViewReady(savedInstanceState: Bundle?) {
        receiverData()
        setViews()
        setListener()
    }

    private fun receiverData() {
        if (currentUser == null) {
            currentUser = prefs.getUser()
        }
        if (intent.getParcelableExtra<Room>(Constant.INTENT.ROOM.value) != null) {
            room = intent.getParcelableExtra(Constant.INTENT.ROOM.value)!!

            reloadFragsManager()
        }

        viewModel.getAllUser(currentUser!!._id!!)
    }

    private fun reloadFragsManager() {
        frags.clear()

        Log.d(TAG, "reloadFragsManager: ${room.users?.size} - ${room.users} ")

        val users = room.users?.filter { it.user != null }?.map {
            it.user!!
        }
        Log.d(TAG, "reloadFragsManager: ${users?.size} - ${users}")
        room.stages?.let {
            for (stage in it) {
                if (stage != null) {
                    val frag = newInstance(stage, users!!)

                    frags.add(frag)
                }
            }
        }
    }

    private fun setViews() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDarkTask)

        binding?.run {
            setSupportActionBar(toolbar)
            showBackButton(true)
            toolbar.rlToolbarTask.tvTitle.text = room.name

            //Holley Viewpager
//            hollyViewPager.viewPager.pageMargin = resources.getDimensionPixelOffset(R.dimen.viewpager_margin)
            hollyViewPager.configurator =
                HollyViewPagerConfigurator { page ->

                    if (room.stages.isNullOrEmpty()) {
                        10.0f
                    } else {
                        room.stages!![page]?.tasks?.size?.plus(2)!!.toFloat()
                    }

                }

            updateAdapterScroll()

            supportFragmentManager
                .beginTransaction()
                .replace(fragRoomSlider.id, SliderRoomFragment.newInstance(room))
                .commit()
        }
    }

    fun getSlideFragment() = binding!!.fragRoomSlider.id

    private fun updateAdapterScroll() {
        binding?.hollyViewPager?.setAdapter(object : FragmentPagerAdapter(
            supportFragmentManager
        ) {
            override fun getCount(): Int = frags.size

            override fun getItem(position: Int): Fragment =
                frags[position]

            override fun getPageTitle(position: Int): CharSequence? =
                room.stages?.get(position)?.name
        })
    }

    private fun setListener() {
        binding?.run {

            with(this.fabAdd) {
                ViewAnimation.init(fabAddStage)
                ViewAnimation.init(fabAddMember)

                this.fabAddInRoom.setOnClickListener {
                    isRotate = ViewAnimation.rotateFab(it, !isRotate)

                    ViewAnimation.showAnimation(fabAddStage, isRotate)
                    ViewAnimation.showAnimation(fabAddMember, isRotate)
                }

                fabAddStage.setOnClickListener {
                    dialogs.inputDialog(this@TaskActivity, getString(R.string.add_stage), getString(
                        R.string.name_stage
                    ), {
                        if (it.isNotBlank()) {
                            showProgress(getString(R.string.loading_message))

                            Log.d(
                                TAG,
                                "setListener: roomid= ${room._id!!} && userid = ${currentUser!!._id!!} "
                            )

                            viewModel.addStageInRoom(room._id!!, it, currentUser!!._id!!)
                        } else {
                            showToast(getString(R.string.notify_empty_stage))
                        }
                    }, {

                    })
                }

                fabAddMember.setOnClickListener {
                    dialogs.inputDialog(this@TaskActivity,
                        R.string.add_member,
                        R.string.add_member_mail,
                        { mail ->
                            if (mail.isNotBlank()) {
                                val user = this@TaskActivity.users.firstOrNull {
                                    it.mail?.equals(mail) ?: false
                                }

                                if (user != null) {
                                    viewModel.addUserInRoom(
                                        room._id!!,
                                        currentUser!!._id!!,
                                        user._id!!
                                    )

                                    showToast("Thêm thành viên thành công")
                                } else {
                                    showToast("Thêm thành viên thất bại")
                                }
                            } else {
                                showToast(getString(R.string.notify_empty_stage))
                            }
                        },
                        {

                        })
                }
            }

        }

        viewModel.resourcesRoom.observe(this, {
            hideProgres()
            if (it.data != null) {
                room = it.data.first()

                Log.d(TAG, "setListener: ${room}")
                reloadFragsManager()
                updateAdapterScroll()

                RxBus.publishToPublishSubject(RxEvent.UpdateRoom)
                RxBus.publishToPublishSubject(RxEvent.MemberAdded)
            }
        })

        viewModel.resourcesUser.observe(this, {
            if (it.data != null) {
                users.clear()
                users.addAll(it.data)
            }
        })
    }

    fun closeDrawerTask() {
        binding?.drawerTask?.closeDrawers()
    }

    fun openDrawerTask(v: View) {
        binding?.drawerTask?.openDrawer(GravityCompat.END)
    }

    override fun onBackPressed() {
        if (binding?.drawerTask?.isDrawerOpen(GravityCompat.END) == true) {
            if (supportFragmentManager.backStackEntryCount >= 1) {
                supportFragmentManager.popBackStack()
            } else {
                closeDrawerTask()
            }
        } else {
//            super.onBackPressed()
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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
    }

}