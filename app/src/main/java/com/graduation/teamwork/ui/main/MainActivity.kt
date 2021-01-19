package com.graduation.teamwork.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.IdRes
import androidx.core.view.GravityCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.graduation.teamwork.R
import com.graduation.teamwork.databinding.ActMainBinding
import com.graduation.teamwork.extensions.observeOnUiThread
import com.graduation.teamwork.ui.add.AddRoomActivity
import com.graduation.teamwork.ui.base.BaseActivity
import com.graduation.teamwork.ui.home.HomeFragment
import com.graduation.teamwork.ui.notification.NotificationFragment
import com.graduation.teamwork.ui.profile.ProfileFragment
import com.graduation.teamwork.ui.room.RoomFragment
import com.graduation.teamwork.utils.animation.ViewAnimation
import com.graduation.teamwork.extensions.setVisiable
import com.graduation.teamwork.extensions.showToast
import com.graduation.teamwork.models.DtGroup
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.ui.base.Success
import com.graduation.teamwork.utils.DialogManager
import com.graduation.teamwork.utils.FirebaseManager
import com.graduation.teamwork.utils.PrefsManager
import com.graduation.teamwork.utils.eventbus.RxBus
import com.graduation.teamwork.utils.eventbus.RxEvent
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import kotlinx.android.synthetic.main.act_main.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import org.koin.android.ext.android.inject

enum class FragmentIndex(val value: Int) {
    HOME(0),
    ROOM(1),
    NOTIFICATION(2),
    PROFILE(3)

}

class MainActivity : BaseActivity<ActMainBinding>() {
    private val TAG = "__MainActivity"

    /**
     * INJECT
     */
    private val viewModel: MainViewModel by inject()
    private val prefs: PrefsManager by inject()
    private val dialogs: DialogManager by inject()
    private val firebase: FirebaseManager by inject()

    //TODO: data
    private var currentUser: DtUser? = prefs.getUser()
    private var allTasks = prefs.getTasks()
    private var allRooms = prefs.getRooms()
    private var allUsers = prefs.getUsers()
    private lateinit var groups: List<DtGroup>

    private var isRotate = false

    //TODO: data
    private val fragmentsManager = mutableListOf<Fragment>(
        HomeFragment(),
        RoomFragment(),
        NotificationFragment(),
        ProfileFragment()
    )

    override fun setBinding(inflater: LayoutInflater): ActMainBinding =
        ActMainBinding.inflate(inflater)

    override fun onViewReady(savedInstanceState: Bundle?) {
        getData()
        setupViews()
        setupListener()

        navBottomSelected(R.id.navBottomHome)
    }

    fun showToolbar(isShow: Boolean) {
        binding?.toolbar?.toolbar?.setVisiable(isShow)
        binding?.toolbar?.cslToolbar?.setVisiable(isShow)
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
        binding?.let { it ->
            //FloatingActionButton
            showFloatActionButton(isShow = true)
            with(it.fabList) {
//                ViewAnimation.init(fabAddTask)
                ViewAnimation.init(fabAddRoom)
                ViewAnimation.init(fabAddGroup)
            }

            setBadge(bottomView = navBottom, menuId = R.id.navBottomNotifi, 99)
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
                return@setOnNavigationItemSelectedListener when (item.itemId) {
                    R.id.navBottomHome -> {
                        title = getString(R.string.home)
                        replaceFragment(FragmentIndex.HOME.value)
                        true
                    }
                    R.id.navBottomRoom -> {
                        title = getString(R.string.room)
//                        setBadge(bottomView = navBottom, menuId = R.id.navBottomNotifi, 99)
                        replaceFragment(FragmentIndex.ROOM.value)
                        true
                    }
                    R.id.navBottomNotifi -> {
                        title = getString(R.string.notifications)
                        this@MainActivity.clearBadge()
                        replaceFragment(FragmentIndex.NOTIFICATION.value)
                        true
                    }
                    R.id.navBottomProfiles -> {
                        title = getString(R.string.profile)
                        replaceFragment(FragmentIndex.PROFILE.value)
                        true
                    }
                    else -> false
                }
            }

            with(fabList) {
                fabAdd.setOnClickListener { view ->
                    isRotate = ViewAnimation.rotateFab(view, !isRotate)

//                    ViewAnimation.showAnimation(fabAddTask, isRotate)
                    ViewAnimation.showAnimation(fabAddRoom, isRotate)
                    ViewAnimation.showAnimation(fabAddGroup, isRotate)
                }

                fabAddRoom.setOnClickListener {
                    Log.d(TAG, "setupListener: AddRoom")

                    if (this@MainActivity.groups.isNullOrEmpty()) {
                        showToast("Vui lòng thêm Nhóm mới trước khi tạo Phòng")
                    } else {
                        Intent(this@MainActivity, AddRoomActivity::class.java).also {
                            startActivity(it)
                        }
                        fabAdd.performClick()
                    }
                }

                fabAddRoom.setOnLongClickListener {
                    showToast("Thêm mới phòng")
                    true
                }

                fabAddGroup.setOnLongClickListener {
                    showToast("Thêm mới nhóm")
                    true
                }

                fabAddGroup.setOnClickListener {
                    dialogs.inputDialog(this@MainActivity, "Thêm nhóm", "tên nhóm", {
                        if (it.isNotBlank()) {
                            viewModel.addGroup(currentUser!!._id!!, it)
                        } else {
                            showToast(getString(R.string.notify_error_name_empty))
                        }
                    }, {})
                    Log.d(TAG, "setupListener: AddGroup")
                    fabAdd.performClick()
                }
            }

            toolbar.edtSearch.doOnTextChanged { text, _, _, _ ->
                RxBus.publishToBehaviorSubject(RxEvent.EvenSearchData(text.toString()))

            }
        }

        viewModel.events.observe(this, {
            when (it) {
                is Success -> {
                    RxBus.publishToPublishSubject(RxEvent.GroupAdded)

                    this.viewModel.getAllGroup(currentUser!!._id!!)
                    showToast("Thêm nhóm mới thành công")
                }
                is Error -> {
                    showToast("Thêm nhóm mới lỗi, vui lòng thử lại")
                }
            }
        })
        viewModel.resources.observe(this, {
            if (it.data != null) {
                this.groups = it.data
                prefs.saveTotalGroup(it.data.size)
            }
        })

        viewModel.resourcesRoom.observe(this, {
            if (it.data != null) {
                prefs.saveRooms(it.data)
                prefs.saveTotalRoom(it.data.size)
                allRooms = it.data



                if (!allRooms.isNullOrEmpty()) {
                    firebase.readAll(currentUser!!._id!!, allRooms)
                }
            }
        })

        viewModel.resourcesTask.observe(this, {
            if (it.data != null) {
                prefs.saveTasks(it.data)

                allTasks = it.data
            }
        })

        viewModel.resourcesUser.observe(this, {
            if (it.data != null) {
                prefs.saveUsers(it.data)
            }
        })
    }

    fun gotoProfile() {
        navBottomSelected(R.id.navBottomProfiles)
    }

    fun gotoRoom(id: String? = null) {
        navBottomSelected(R.id.navBottomRoom)
        prefs.saveLoadOnlyGroup(id)
    }

    private fun replaceFragment(index: Int) {
        binding?.frmMain?.id?.let {

            supportFragmentManager.beginTransaction()
                .replace(it, fragmentsManager[index])
                .commit()
        }
    }

    fun showFloatActionButton(isShow: Boolean) {
        binding?.run {
            fabList.cslFab.setVisiable(isShow)
        }
    }

    fun closeDrawer() {
        binding?.drawerLayout?.closeDrawers()
    }

    fun openDrawer(v: View) {
        binding?.drawerLayout?.openDrawer(GravityCompat.START)
    }

    fun openSettings(v: View) {
        showToast(getString(R.string.open_settings))
    }

    fun actionSearch(v: View) {
        val textSearch = binding?.toolbar?.edtSearch?.text
        RxBus.publishToBehaviorSubject(RxEvent.EvenSearchData(textSearch.toString()))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                binding?.drawerLayout?.openDrawer(GravityCompat.START)
                true
            }

            else -> super.onOptionsItemSelected(item)

        }
    }


}
