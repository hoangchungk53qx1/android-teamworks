package com.graduation.teamwork.utils

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.graduation.teamwork.extensions.fromObject
import com.graduation.teamwork.models.DtRoom
import com.graduation.teamwork.models.firebase.NotiDetailRoom
import com.graduation.teamwork.models.firebase.NotiDetailTask
import com.graduation.teamwork.models.firebase.NotiDetailUser
import com.graduation.teamwork.utils.eventbus.RxBus
import com.graduation.teamwork.utils.eventbus.RxEvent
import org.json.JSONObject
import kotlin.collections.HashMap


class FirebaseManager(
    val database: DatabaseReference = Firebase.database.reference
) {

    private val TAG = "__FirebaseManager"
    fun readUser() {
        val userDatabase = database.child("/users")
        userDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue<HashMap<String, Any>>()

                val listData = value?.keys

                listData?.forEach {
                    Log.d(TAG, "onDataChange: KEY = ${it}")

                    val listKeyDetail = value[it] as? HashMap<*, *>
                    listKeyDetail?.keys?.forEach { key ->
                        Log.d(TAG, "onDataChange: KEY_DETAIl = ${key}")

                        Log.d(TAG, "onDataChange: DATA_DETAIl = ${listKeyDetail[key]}")
                    }

                    Log.d(TAG, "-------------------------")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }

        })
    }

    fun readRoom() {
        val roomDatabase = database.child("/rooms")
        roomDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue<HashMap<String, Any>>()

                val listData = value?.keys

                listData?.forEach {
                    Log.d(TAG, "onDataChange: KEY = ${it}")


                    Log.d(TAG, "onDataChange: DATA = ${value[it]}")
                    Log.d(TAG, "-------------------------")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }

        })
    }

    fun readTask() {
        val taskDatabase = database.child("/tasks")
        taskDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue<HashMap<String, Any>>()

                val listData = value?.keys

                listData?.forEach {
                    Log.d(TAG, "onDataChange: KEY = ${it}")

                    Log.d(TAG, "onDataChange: DATA = ${value[it]}")

                    Log.d(TAG, "-------------------------")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }

        })
    }

    fun readAll(currentIdUser: String, rooms: List<DtRoom>?) {
        Log.d(TAG, "readAll: ${rooms?.size} - ${currentIdUser}")
//
//        rooms?.map { it.users }
//            ?.forEach {
//                it?.forEach { user ->
//                    Log.d(TAG, "readAll: name = ${user.user?.fullname} - id = ${user.user?._id}")
//                }
//            }
//
        val data = hashMapOf<String, List<String>>()
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue<HashMap<String, Any>>()

                if (value != null) {
                    data.clear()
                }

                value?.keys?.forEach {
                    Log.d(TAG, "onDataChange: KEY = ${it}")

                    val dataMap = value[it] as HashMap<*, *>
                    val category = it
                    dataMap.keys.forEach { item ->
                        val dataItem = dataMap[item] as HashMap<*, *>
                        val dataString = JSONObject(dataItem).toString()

                        val key = category
                        var values = data[key]?.toMutableList()

                        if (values.isNullOrEmpty()) {
                            values = mutableListOf()
                        }

                        when (category) {
                            "rooms" -> {
                                val idRoom =
                                    dataString.fromObject<NotiDetailRoom>()?.idCategory

                                val shouldAdd = !rooms?.filter { room -> room._id == idRoom }
                                    ?.map { it.users }
                                    ?.filter { userInRooms ->

                                        userInRooms?.find { userInRoom -> userInRoom.user?._id == currentIdUser } != null

                                    }.isNullOrEmpty()

                                if (shouldAdd) {
                                    values.add(dataString)
                                }
                            }
                            "tasks" -> {
                                val idTask =
                                    dataString.fromObject<NotiDetailTask>()?.idCategory

                                val shouldAdd = !rooms?.map {
                                    it.stages
                                }?.filter { stages ->
                                    !(stages?.filter { stage ->

                                        stage?.tasks?.find { task -> task._id == idTask } != null
                                    }.isNullOrEmpty())

                                }.isNullOrEmpty()

                                if (shouldAdd) {
                                    values.add(dataString)
                                }
                            }
                            else -> {
                                val idUser =
                                    dataString.fromObject<NotiDetailUser>()?.idUserPerformer

                                if (idUser == currentIdUser) {
                                    values.add(dataString)
                                }
                            }
                        }

                        data[key] = values

                    }
                }

                if (data.isNotEmpty()) {
                    RxBus.publishToPublishSubject(RxEvent.EventNotiUpdated(data))
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }

        })
    }

}