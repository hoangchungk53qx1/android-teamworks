package com.graduation.teamwork.ui.slider

import SlideExplode
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import androidx.transition.*
import com.graduation.teamwork.R
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.databinding.FragSliderRoomBinding
import com.graduation.teamwork.extensions.showToast
import com.graduation.teamwork.extensions.toJson
import com.graduation.teamwork.extensions.toObject
import com.graduation.teamwork.models.DtRoom
import com.graduation.teamwork.ui.base.BaseFragment
import com.graduation.teamwork.ui.room.details.RoomDetailActivity
import com.graduation.teamwork.ui.room.details.member.MemberDetailFragment
import org.koin.core.component.KoinApiExtension
import setCommonInterpolator

private val transitionInterpolator = FastOutSlowInInterpolator()
private const val TRANSITION_DURATION = 300L
private const val TAP_POSITION = "tap_position"

@KoinApiExtension
class SliderRoomFragment : BaseFragment<FragSliderRoomBinding, RoomDetailActivity>() {
    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragSliderRoomBinding = FragSliderRoomBinding.inflate(inflater, container, false)

    private lateinit var mRoom: DtRoom

    private var tapPosition = NO_POSITION
    private val viewRect = Rect()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.run {
            mRoom = this.getString(Constant.IntentKey.ROOM.value)!!.toObject<DtRoom>()!!
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tapPosition = savedInstanceState?.getInt(TAP_POSITION, NO_POSITION) ?: NO_POSITION
        postponeEnterTransition()

        setupViews()
        setupListener()
    }

    private fun setupViews() {
        startPostponedEnterTransition()

        binding?.run {
            root.doOnPreDraw {
                if (exitTransition == null) {
                    exitTransition = SlideExplode().apply {
                        duration = TRANSITION_DURATION
                        interpolator = transitionInterpolator
                    }
                }
            }
        }
    }

    private fun setupListener() {
        binding?.run {
            icSliderActivity.setOnClickListener {
                context?.showToast("Activity")
            }
            icSliderArchived.setOnClickListener {
                context?.showToast("Archived")
            }
            icSliderCopy.setOnClickListener {
                context?.showToast("Copy")
            }
            icSliderGhim.setOnClickListener {
                context?.showToast("Pin")
            }
            icSliderInfo.setOnClickListener {
                context?.showToast("Info")
            }
            icSliderMark.setOnClickListener {
                it.isEnabled = !it.isEnabled
                context?.showToast("Mark")
            }
            icSliderPowerUps.setOnClickListener {
                context?.showToast("Power-ups")
            }
            icSliderShare.setOnClickListener {
                context?.showToast("Share")
            }
            icSliderTagsSaved.setOnClickListener {
                context?.showToast("Tags")
            }
            icSliderMember.setOnClickListener {
                actionClickedMember(SLIDE_ROOM_POSITION.MEMBER.value)
            }

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(TAP_POSITION, tapPosition)
    }

    private fun actionClickedMember(position: Int) {
        tapPosition = position
        binding?.icSliderMember?.getGlobalVisibleRect(viewRect)

        (this.exitTransition as Transition).epicenterCallback =
            object : Transition.EpicenterCallback() {
                override fun onGetEpicenter(transition: Transition) = viewRect
            }

        val sharedElementTransition = TransitionSet()
            .addTransition(ChangeBounds())
            .addTransition(ChangeTransform())
            .addTransition(ChangeImageTransform()).apply {
                duration = TRANSITION_DURATION
                setCommonInterpolator(transitionInterpolator)
            }

        val fragment = MemberDetailFragment.newInstance(mRoom).apply {
            sharedElementEnterTransition = sharedElementTransition
            sharedElementReturnTransition = sharedElementTransition
        }

        ViewCompat.setTransitionName(
            binding!!.icSliderMember,
            getString(R.string.send_slide_member)
        )
        handler!!.supportFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(handler!!.getSlideFragment(), fragment)
            .addToBackStack(null)
//            .addSharedElement(binding!!.icSliderMember, getString(R.string.send_slide_member))
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance(room: DtRoom): SliderRoomFragment = SliderRoomFragment().apply {
            arguments = Bundle().apply { putString(Constant.IntentKey.ROOM.value, room.toJson()) }
        }
    }

}

enum class SLIDE_ROOM_POSITION(val value: Int) {
    INFO(0),
    MEMBER(1),
    ACTIVITY(2),
    TAG_SAVED(3),
    SETTING_ROOM(4),
    MARK_ROOM(5),
    PIN(6),
    SHARE(7)

}