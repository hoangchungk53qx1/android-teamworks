package com.graduation.teamwork.ui.slider

import SlideExplode
import android.animation.ObjectAnimator
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
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
import com.graduation.teamwork.extensions.fromJson
import com.graduation.teamwork.extensions.fromObject
import com.graduation.teamwork.extensions.showToast
import com.graduation.teamwork.extensions.toJson
import com.graduation.teamwork.models.DtRoom
import com.graduation.teamwork.models.DtStage
import com.graduation.teamwork.ui.base.BaseFragment
import com.graduation.teamwork.ui.task.TaskActivity
import com.graduation.teamwork.ui.task.details.member.MemberDetailFragment
import setCommonInterpolator

private val transitionInterpolator = FastOutSlowInInterpolator()
private const val TRANSITION_DURATION = 300L
private const val TAP_POSITION = "tap_position"

class SliderRoomFragment : BaseFragment<FragSliderRoomBinding, TaskActivity>() {
    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragSliderRoomBinding = FragSliderRoomBinding.inflate(inflater, container, false)

    private var tapPosition = NO_POSITION
    val viewRect = Rect()

    lateinit var mRoom: DtRoom

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.run {
            mRoom = this.getString(Constant.INTENT.ROOM.value)!!.fromObject<DtRoom>()!!
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

    fun setupViews() {
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(TAP_POSITION, tapPosition)
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

//            handler?.closeDrawerTask()
        }
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SliderRoomFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(room: DtRoom): SliderRoomFragment {

            val bundle = Bundle().apply {
                putString(Constant.INTENT.ROOM.value, room.toJson())
            }

            return SliderRoomFragment().apply {
                arguments = bundle
            }
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