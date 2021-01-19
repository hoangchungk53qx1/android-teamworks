package com.graduation.teamwork.utils.eventbus

import com.graduation.teamwork.data.local.Constant

sealed class RxEvent {
    data class GroupChanged(val id: String) : RxEvent()
    data class RoomAdded(val id: String) : RxEvent()
    object TaskAdded : RxEvent()
    object StageAdded : RxEvent()

    object UpdateTask: RxEvent()
    object MemberAdded: RxEvent()
    object GroupAdded : RxEvent()
    object UpdateRoom : RxEvent()
    object UpdateAvatar : RxEvent()

    data class EventNotiUpdated(
        val data: HashMap<String, List<String>>
    ) : RxEvent()

    data class EvenSearchData(val text: String) : RxEvent()
//    object EventContactCalled : RxEvent()
//    data class EventSearchCall(val searchQuery: String) : RxEvent()
}