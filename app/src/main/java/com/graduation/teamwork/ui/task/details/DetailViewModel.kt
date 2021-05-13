package com.graduation.teamwork.ui.task.details

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.loader.content.CursorLoader
import com.graduation.teamwork.domain.repository.RemoteRepository
import com.graduation.teamwork.models.*
import com.graduation.teamwork.models.data.TaskListItem
import com.graduation.teamwork.ui.base.BaseViewModel
import com.graduation.teamwork.ui.base.Resource
import com.graduation.teamwork.utils.mvvm.SingleLiveEvent
import io.reactivex.rxjava3.core.Single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class DetailViewModel(
    private val remoteRepository: RemoteRepository
) : BaseViewModel() {

    private val _resources = SingleLiveEvent<Resource<TaskListItem.DtTask>>()
    val resources: SingleLiveEvent<Resource<TaskListItem.DtTask>>
        get() = _resources

    private val _resourcesSubtask = SingleLiveEvent<Resource<Subtask>>()
    val resourcesSubtask: SingleLiveEvent<Resource<Subtask>>
        get() = _resourcesSubtask

    private val _resourcesUpdate = SingleLiveEvent<Resource<List<TaskListItem.DtTask>>>()
    val resourcesUpdate: SingleLiveEvent<Resource<List<TaskListItem.DtTask>>>
        get() = _resourcesUpdate

    private val _resourcesRoom = SingleLiveEvent<Resource<List<DtRoom>>>()
    val resourcesRoom: SingleLiveEvent<Resource<List<DtRoom>>>
        get() = _resourcesRoom

    private val _resourcesUser = SingleLiveEvent<Resource<List<UserInRoom>>>()
    val resourcesUser: SingleLiveEvent<Resource<List<UserInRoom>>>
        get() = _resourcesUser

    fun addUserInTask(
        idTask: String,
        idUserWillAdded: String,
        idUserAction: String
    ) {
        launch {
            remoteRepository.addMemberInTask(idTask, idUserWillAdded, idUserAction)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe { data, error ->
                    run {
                        if (error != null) {
                            _resourcesUpdate.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@run
                        }

                        _resourcesUpdate.value = Resource.success(data.data)
                    }
                }
        }
    }

    fun addUserIntaks(
        idTask: String,
        idUserAction: String,
        users: List<DtUser>
    ) {
        val list = mutableListOf<Single<Task>>()
        users.forEach {
            list.add(remoteRepository.addMemberInTask(idTask, it._id!!, idUserAction))
        }
        Single.zip(list) {
            getTaskWith(idTask)
        }
    }

    fun getTaskWith(id: String) {
        launch {
            remoteRepository.queryTaskWithId(id)
                .observeOn(schedulerProvider.ui())
                .subscribe { data, error ->
                    run {
                        if (error != null) {
                            _resources.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@run
                        }

                        _resources.value = Resource.success(data.data?.first())
                    }
                }
        }
    }

    fun deleteUserInTask(
        idTask: String,
        idUserWillDeleted: String,
        idUserAction: String
    ) {
        launch {
            remoteRepository.deleteMemberInTask(idTask, idUserAction, idUserWillDeleted)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe { data, error ->
                    run {
                        if (error != null) {
                            _resourcesUpdate.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@run
                        }

                        _resourcesUpdate.value = Resource.success(data.data)
                    }
                }
        }
    }

    fun addUserInTasks(
        idTask: String,
        idUserAction: String,
        users: List<DtUser>
    ) {
        var count = 0
        users.onEach {
            remoteRepository.addMemberInTask(idTask, it._id!!, idUserAction)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe { data, _ ->
                    count++
                    if (count == users.size) {
                        queryMemberInTask(idTask)
                    }
                }
        }
    }

    fun changeDeadline(
        id: String,
        deadline: Long,
        idUser: String
    ) {
        launch {
            remoteRepository.changeDeadline(id, deadline, idUser)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe { data, error ->
                    run {
                        if (error != null) {
                            _resourcesUpdate.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@run
                        }

                        _resourcesUpdate.value = Resource.success(data.data)
                    }
                }
        }
    }

    fun queryMemberInTask(idTask: String) {
        launch {
            remoteRepository.queryMemberInTask(idTask)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe { data, error ->
                    run {
                        if (error != null) {
                            _resourcesUpdate.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@run
                        }

                        _resourcesUpdate.value = Resource.success(data.data)
                    }
                }
        }
    }

    fun addSubTask(
        idTask: String,
        nameSubTask: String,
        idUser: String
    ) {
        launch {
            remoteRepository.addSubtaskInTask(idTask, nameSubTask, idUser)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe { data, error ->
                    run {
                        if (error != null) {
                            _resources.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@run
                        }

                        _resources.value = Resource.success(data.data?.first())

                    }
                }
        }
    }

    fun addUserInRoom(
        idRoom: String,
        idUserAction: String,
        idUserAdded: String
    ) {
        launch {
            remoteRepository.addUserInRoom(idRoom, idUserAction, idUserAdded)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe { data, error ->
                    run {
                        if (error != null) {
                            _resourcesRoom.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@run
                        }

                        _resourcesRoom.value = Resource.success(data.data)
                    }

                }
        }
    }

    fun deleteUserInRoom(
        idRoom: String,
        idUser: String,
        idUserWillDeleted: String
    ) {
        launch {
            remoteRepository.deleteUserInRoom(idRoom, idUser, idUserWillDeleted)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe { data, error ->
                    run {
                        if (error != null) {
                            _resourcesRoom.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@run
                        }

                        _resourcesRoom.value = Resource.success(data.data)
                    }

                }
        }
    }

    fun getAllUserInRoom(idRoom: String) {
        launch {
            remoteRepository.getAllMemberInRoom(idRoom)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe { data, error ->
                    run {
                        if (error != null) {
                            _resourcesUser.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@run
                        }

                        _resourcesUser.value = Resource.success(data.data)
                    }

                }
        }
    }

    fun setLevel(
        idRoom: String,
        idUser: String,
        idUserWillSet: String,
        level: Long
    ) {
        launch {
            remoteRepository.setLevel(idRoom, idUser, idUserWillSet, level.toInt())
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe { data, error ->
                    run {
                        if (error != null) {
                            _resourcesRoom.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@run
                        }

                        _resourcesRoom.value = Resource.success(data.data)
                    }

                }
        }
    }

    fun setCompletedSubTask(id: String, name: String, isCompleted: Boolean = false) {
        launch {
            remoteRepository.setCompleted(id, name, isCompleted)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe { data, error ->
                    run {
                        if (error != null) {
                            _resourcesSubtask.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@run
                        }
                        _resourcesSubtask.value = Resource.success(data)
                    }
                }
        }
    }

    fun setLabels(id: String, labels: List<Int>) {
        launch {
            remoteRepository.updateLabel(id, labels)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe { data, error ->
                    run {
                        if (error != null) {
                            _resources.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@run
                        }
                        _resources.value = Resource.success(data = data.data?.first())
                    }
                }
        }
    }

    fun uploadImage(context: Context, uri: Uri?, idTask: String, idUser: String) {
        if (uri == null) {
            return
        }

        val file = File(getRealPathFromURI(context, uri) ?: "")
        val photoContent: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData(
            "attachment", file.name, photoContent
        )

        launch {
            remoteRepository.uploadImageTask(idTask, part, idUser,)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe { data, error ->
                    run {
                        if (error != null) {
                            _resources.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@run
                        }
                        _resources.value = Resource.success(data = data.data?.first())
                    }
                }
        }
    }

    fun uploadLink(id: String, link: String, idUser: String) {
        var newLink = link
        if (!newLink.startsWith("http")) {
            newLink = "https://${link}"
        }
        launch {
            remoteRepository.uploadLink(id, newLink, idUser)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe { data, error ->
                    run {
                        if (error != null) {
                            _resources.value =
                                Resource.error(error.localizedMessage.orEmpty(), null)
                            return@run
                        }
                        _resources.value = Resource.success(data = data.data?.first())
                    }
                }
        }
    }

    private fun getRealPathFromURI(context: Context, contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = contentUri?.let {
            CursorLoader(
                context,
                it,
                proj,
                null,
                null,
                null
            )
        }

        val cursor: Cursor? = loader?.loadInBackground()

        val columnIndex: Int? = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val result: String? = columnIndex?.let {
            cursor.getString(it)
        }
        cursor?.close()
        return result
    }

}