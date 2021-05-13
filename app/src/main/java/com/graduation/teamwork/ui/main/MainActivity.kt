package com.graduation.teamwork.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.graduation.teamwork.R
import com.graduation.teamwork.databinding.ActMainBinding
import com.graduation.teamwork.extensions.*
import com.graduation.teamwork.models.DtGroup
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.ui.add.AddRoomActivity
import com.graduation.teamwork.ui.base.BaseActivity
import com.graduation.teamwork.ui.base.Success
import com.graduation.teamwork.ui.home.HomeFragment
import com.graduation.teamwork.ui.notification.NotificationFragment
import com.graduation.teamwork.ui.profile.ProfileFragment
import com.graduation.teamwork.ui.room.RoomFragment
import com.graduation.teamwork.utils.DialogManager
import com.graduation.teamwork.utils.FirebaseManager
import com.graduation.teamwork.utils.PrefsManager
import com.graduation.teamwork.utils.eventbus.RxBus
import com.graduation.teamwork.utils.eventbus.RxEvent
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.act_main.*
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension


enum class FragmentIndex(val value: Int) {
    HOME(0),
    ROOM(1),
    NOTIFICATION(2),
    PROFILE(3)
}

@KoinApiExtension
class MainActivity : BaseActivity<ActMainBinding>() {

    /**
     * INJECT
     */
    private val viewModel: MainViewModel by inject()
    private val prefs: PrefsManager by inject()
    private val dialogs: DialogManager by inject()
    private val firebase: FirebaseManager by inject()

    private val fragmentsManager = mutableListOf<Fragment>(
        HomeFragment(),
        RoomFragment(),
        NotificationFragment(),
        ProfileFragment()
    )

    private lateinit var groups: List<DtGroup>
    private var currentUser: DtUser? = prefs.getUser()
    private var allTasks = prefs.getTasks()
    private var allRooms = prefs.getRooms()
    private var allUsers = prefs.getUsers()
    private var isRotate = false

    override fun setBinding(inflater: LayoutInflater): ActMainBinding =
        ActMainBinding.inflate(inflater)

    override fun onViewReady(savedInstanceState: Bundle?) {
        getData()
        setupViews()
        setupListener()

        navBottomSelected(R.id.navBottomHome)
    }

    private fun getData() {
        if (currentUser == null) {
            currentUser = prefs.getUser()
        }

        viewModel.getAllGroup(currentUser!!._id!!)
        viewModel.getAllMember()
        viewModel.getAllTask()
        viewModel.getAllRoomByUser(currentUser!!._id!!)
    }

    private fun setupViews() {
        Toasty.success(this, "Chào mừng ${currentUser?.fullname} đăng nhập thành công").show()
        binding?.let {
            showFloatActionButton(isShow = true)
            setBadge(bottomView = navBottom, menuId = R.id.navBottomNotifi, number = 99)
        }
    }

    fun navBottomSelected(@IdRes id: Int) {
        binding?.navBottom?.selectedItemId = id
    }

    private fun setupListener() {
        binding?.run {
            navDrawer.setNavigationItemSelectedListener { item: MenuItem ->
                item.isChecked = true
                binding?.drawerLayout?.closeDrawers()
                true
            }

            //navigation bottom
            navBottom.setOnNavigationItemSelectedListener { item: MenuItem ->
                if (isRotate) {
                    floatView.performClick()
                    false
                } else {
                    return@setOnNavigationItemSelectedListener when (item.itemId) {
                        R.id.navBottomHome -> {
                            setTitle(getString(R.string._home))
                            replaceFragment(FragmentIndex.HOME.value)
                            true
                        }
                        R.id.navBottomRoom -> {
                            setTitle(getString(R.string.room))
                            replaceFragment(FragmentIndex.ROOM.value)
                            true
                        }
                        R.id.navBottomNotifi -> {
                            setTitle(getString(R.string.notifications))
                            this@MainActivity.clearBadge()
                            replaceFragment(FragmentIndex.NOTIFICATION.value)
                            true
                        }
                        R.id.navBottomProfiles -> {
                            setTitle(getString(R.string.profile))
                            replaceFragment(FragmentIndex.PROFILE.value)
                            true
                        }
                        else -> false
                    }
                }

            }

            with(fabLayout) {
                fabAdd.setOnFloatingActionsMenuUpdateListener(object :
                    FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {
                    override fun onMenuCollapsed() {
                        floatView.gone()
                    }

                    override fun onMenuExpanded() {
                        Handler(Looper.getMainLooper()).postDelayed({
                            floatView.visiable()
                        }, 100)
                    }
                })

                floatView.setOnClickListener { fabAdd.toggle() }

                fabAddRoom.run {
                    setOnClickListener {
                        fabAdd.toggle()
                        if (this@MainActivity.groups.isNullOrEmpty()) {

                            showToast("Vui lòng thêm Nhóm mới trước khi tạo Phòng")
                        } else {
                            Intent(this@MainActivity, AddRoomActivity::class.java)
                                .apply {
                                    startActivity(this)
                                    overridePendingTransition(R.anim.from_in_bottom, R.anim.nothing)
                                }
                        }
                    }
                    setOnLongClickListener {
                        showToast("Thêm mới phòng")
                        true
                    }
                }

                fabAddGroup.run {
                    setOnLongClickListener {
                        showToast("Thêm mới nhóm")
                        true
                    }
                    setOnClickListener {
                        fabAdd.toggle()
                        dialogs.inputDialog(this@MainActivity, "Thêm nhóm", "tên nhóm", {
                            if (it.isNotBlank()) {
                                viewModel.addGroup(currentUser!!._id!!, it)
                            } else {
                                Toasty.error(this@MainActivity, R.string.notify_error_name_empty).show()
                            }
                        }, {})
                    }
                }
            }

            toolbar.run {
                toolbar.setOnClickListener {
                    binding?.drawerLayout?.openDrawer(GravityCompat.START)
                }

                btnSearch.setOnClickListener {
                    toolbarSearch.edtSearch.showKeyboard()
                    circleReveal(R.id.toolbarSearch, 1, containsOverflow = true, isShow = true)
                }
            }

            toolbarSearch.run {
                toolbarSearch.setOnClickListener {
                    edtSearch.hideKeyboard()
                    circleReveal(R.id.toolbarSearch, 1, containsOverflow = true, isShow = false)
                }

                btnClose.setOnClickListener {
                    edtSearch.setText("")
                }

                edtSearch.doOnTextChanged { text, _, _, _ ->
                    RxBus.publishToBehaviorSubject(RxEvent.EvenSearchData(text.toString()))
                }
            }
        }

        viewModel.events.observe(this) {
            when (it) {
                is Success -> {
                    RxBus.publishToPublishSubject(RxEvent.GroupAdded)
                    this.viewModel.getAllGroup(currentUser!!._id!!)
                    Toasty.success(this, "Thêm nhóm mới thành công").show()
                }
                is Error -> {
                    Toasty.error(this, "Thêm nhóm mới lỗi, vui lòng thử lại").show()
                }
            }
        }

        viewModel.resources.observe(this) {
            if (it.data != null) {
                this.groups = it.data
                prefs.saveTotalGroup(it.data.size)
            }
        }

        viewModel.resourcesRoom.observe(this) {
            if (it.data != null) {
                prefs.saveRooms(it.data)
                prefs.saveTotalRoom(it.data.size)
                allRooms = it.data

                if (!allRooms.isNullOrEmpty()) {
                    firebase.readAll(currentUser!!._id!!, allRooms)
                }
            }
        }

        viewModel.resourcesTask.observe(this) {
            if (it.data != null) {
                prefs.saveTasks(it.data)

                allTasks = it.data
            }
        }

        viewModel.resourcesUser.observe(this) {
            if (it.data != null) {
                prefs.saveUsers(it.data)
            }
        }
    }

    fun showToolbar(isShow: Boolean) {
        binding?.toolbar?.toolbar?.setVisiable(isShow)
    }

    fun gotoProfile() {
        navBottomSelected(R.id.navBottomProfiles)
    }

    fun gotoRoom(id: String? = null) {
        navBottomSelected(R.id.navBottomRoom)
        prefs.saveLoadOnlyGroup(id)
    }

    private fun replaceFragment(index: Int) {
        window.statusBarColor = if (index == FragmentIndex.PROFILE.value) {
            ContextCompat.getColor(this, R.color.profilePrimary)
        } else {
            ContextCompat.getColor(this, R.color.colorPrimaryDark)
        }

        binding?.run {
            vLine.setVisiable(index != FragmentIndex.PROFILE.value)
            toolbar.btnSearch.setVisiable(index != FragmentIndex.NOTIFICATION.value)

            frmMain.id.let {
                supportFragmentManager.beginTransaction()
                    .replace(it, fragmentsManager[index])
                    .commit()
            }
        }
    }

    fun showFloatActionButton(isShow: Boolean) {
        binding?.run { fabLayout.rlFab.setVisiable(isShow) }
    }

    /**
     * DRAWER HANDLER
     */
    fun closeDrawer() {
        binding?.drawerLayout?.closeDrawers()
    }

    fun openDrawer(v: View) {
        binding?.drawerLayout?.openDrawer(GravityCompat.START)
    }

    private fun circleReveal(
        viewID: Int,
        posFromRight: Int,
        containsOverflow: Boolean,
        isShow: Boolean
    ) {
        val myView = findViewById<View>(viewID)
        var width = myView.width

        val cx = width
        val cy = myView.height / 2
        val anim: Animator

        if (posFromRight > 0) {
            width -= posFromRight * resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) - resources.getDimensionPixelSize(
                R.dimen.abc_action_button_min_width_material
            ) / 2
        }
        if (containsOverflow) {
            width -= resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material)
        }

        anim = if (isShow) {
            ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0f, width.toFloat())
        } else {
            ViewAnimationUtils.createCircularReveal(myView, cx, cy, width.toFloat(), 0f)
        }.apply { duration = 220L }

        // make the view invisible when the animation is done
        anim.addListener(
            object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    if (!isShow) {
                        super.onAnimationEnd(animation)
                        myView.visibility = View.GONE
                    }
                }
            })

        // make the view visible and start the animation
        if (isShow) {
            myView.visibility = View.VISIBLE
        } else {
            binding?.toolbarSearch?.edtSearch?.setText("")
        }

        // start the animation
        anim.start()
    }

    private fun setTitle(title: String) {
        binding?.toolbar?.tvTitle?.text = title
    }

    fun openSettings(v: View) {
        showToast(getString(R.string.open_settings))
    }

    override fun onBackPressed() {
        if (isRotate) {
            binding?.fabLayout?.fabAdd?.toggle()
        } else {
            super.onBackPressed()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.nothing, R.anim.from_out_top)
    }
}
