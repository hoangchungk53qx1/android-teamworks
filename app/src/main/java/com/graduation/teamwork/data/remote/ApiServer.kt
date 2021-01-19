package com.graduation.teamwork.data.remote

import com.graduation.teamwork.models.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * com.graduation.teamwork.data.remote
 * Created on 11/16/20
 */
interface ApiServer {

    /**
     * USER
     */
    @GET("user/")
    fun queryAllUser(): Single<User>

    @GET("user/query/{id}")
    fun queryUserById(@Path(value = "id") id: String): Single<User>

    @GET("user/query/")
    fun queryUserByName(@Query("username") username: String): Single<User>

    @FormUrlEncoded
    @POST("user/add")
    fun addUser(
        @Field("fullname") username: String,
        @Field("password") password: String,
        @Field("mail") mail: String
    ): Single<User>

    @FormUrlEncoded
    @POST("user/update/{id}")
    fun updateUser(
        @Path(value = "id") id: String,
        @Field(value = "numberphone") numberphone: String,
        @Field(value = "city") city: String,
        @Field(value = "mail") mail: String
    ): Single<User>

    @FormUrlEncoded
    @POST("user/update/password/{id}")
    fun updatePasswored(
        @Path(value = "id") id: String,
        @Field(value = "oldPassword") oldPassword: String,
        @Field(value = "newPassword") newPassword: String
    ): Single<User>

    @Multipart
    @POST("/user/update/avatar/{id}")
    fun updateAvatar(@Path(value = "id") id: String, @Part part: MultipartBody.Part): Single<User>

    /**
     * AUTHEN
     */
    @FormUrlEncoded
    @POST("authen/login")
    fun loginWithUsername(
        @Field("fullname", encoded = true) username: String,
        @Field("password", encoded = true) password: String
    ): Single<User>

    @POST("authen/login")
    @FormUrlEncoded
    fun loginWithMail(
        @Field("mail") mail: String, @Field("password") password: String
    ): Single<User>

    @POST("authen/logout/{id}")
    fun logout(@Path("id") id: String): Completable

    /**
     * GROUP
     */
    @GET("group")
    fun queryAllGroup(): Single<Group>

    @GET("group/query/{id}")
    fun queryGroup(@Path("id") id: String): Single<Group>

    @GET("group/query/user/{idUser}")
    fun queryGroupByUser(@Path("idUser") idUser: String): Single<Group>

    @GET("group/query/{idGroup}")
    fun queryRoomInGroup(@Path("idGroup") idGroup: String): Single<Room>

    @FormUrlEncoded
    @POST("group/create/{idUser}")
    fun addGroup(
        @Path("idUser") idUser: String,
        @Field("name") name: String
    ): Single<Group>

    @FormUrlEncoded
    @POST("group/add-room/{idGroup}")
    fun addRoomInGroup(
        @Path("idGroup") idGroup: String,
        @Field("name") nameRoom: String,
        @Field("idUser") idUser: String
    ): Single<Group>


    /**
     * ROOM
     */
    @GET("room")
    fun queryAllRoom(): Single<Room>

    @GET("room/{id}")
    fun queryRoom(@Path("id") id: String): Single<Room>

    @GET("room/query/{idUser}")
    fun queryRoomByUser(@Path("idUser") idUser: String): Single<Room>

    @GET("room/{idRoom}/stage")
    fun queryStageInRoom(@Path("idRoom") idRoom: String): Single<Room>

    @GET("room/{idRoom}/user")
    fun queryUserInRoom(@Path("idRoom") idRoom: String): Single<BaseModel>

    @FormUrlEncoded
    @POST("room/create")
    fun addRoom(
        @Field("name") name: String,
        @Field("idUser") idUser: String
    ): Single<Room>

    @FormUrlEncoded
    @POST("room/{idRoom}/stage/create")
    fun addStageInRoom(
        @Path("idRoom") idRoom: String,
        @Field("name") nameStage: String,
        @Field("idUser") idUser: String
    ): Single<Room>

    @FormUrlEncoded
    @POST("room/{idRoom}/deleteStage/{idStage}")
    fun deleteStageInRoom(
        @Path("idRoom") idRoom: String,
        @Path("idStage") idStage: String
    ): Single<Room>

    @FormUrlEncoded
    @POST("room/{idRoom}/add-user")
    fun addUserInRoom(
        @Path("idRoom") idRoom: String,
        @Field("idUserAction") idUserAction: String,
        @Field("idUserAdded") idUserAdded: String
    ): Single<Room>

    @FormUrlEncoded
    @POST("room/{idRoom}/delete/{idUser}")
    fun deleteUserInRoom(
        @Path("idRoom") idRoom: String,
        @Path("idUser") idUser: String,
        @Field("idUserDelete") idUserWillDeleted: String
    ): Single<Room>

    @FormUrlEncoded
    @POST("room/{idRoom}/set-level/{idUser}")
    fun setLevel(
        @Path("idRoom") idRoom: String,
        @Path("idUser") idUser: String,
        @Field("idUserWillSet") idUserWillSet: String,
        @Field("level") level: Int
    ): Single<Room>

    /**
     * STAGE
     */
    @GET("stage")
    fun queryAllStage(): Single<Stage>

    @FormUrlEncoded
    @POST("stage/add/task")
    fun addTaskInStage(
        @Field("idStage") id: String,
        @Field("name") name: String
    ): Single<Stage>

    /**
     * TASK
     */

    @GET("task")
    fun queryAllTask(): Single<Task>

    @FormUrlEncoded
    @POST("task/add/subtask")
    fun addSubtaskInTask(
        @Field("id") idTask: String,
        @Field("name") nameSubtask: String,
        @Field("idUser") idUser: String,
    ): Single<Task>

    @Multipart
    @POST("/task/{id}/add-attachment")
    fun uploadImageTask(
        @Path("id") id: String,
        @Part part: MultipartBody.Part,
        @Field("idUser") idUser: String
    ): Single<Task>

    @FormUrlEncoded
    @POST("task/add/user")
    fun addUserInTask(
        @Field("id") idTask: String,
        @Field("idUserWillAdded") idUser: String,
        @Field("idUser") idUserAction: String
    ): Single<Task>


    @FormUrlEncoded
    @POST("task/delete/user")
    fun deleteUserInTask(
        @Field("id") idTask: String,
        @Field("idCategory") idCategory: String,
        @Field("idUserAction") idUserAction: String
    ): Single<Task>

    @FormUrlEncoded
    @POST("task/{id}/add-attachment-link")
    fun addLinkTask(
        @Path("id") idTask: String, @Field("link") link: String,
        @Field("idUser") idUser: String
    ): Single<Task>


    @GET("task/query/{id}")
    fun queryUserInTask(@Path("id") idTask: String): Single<Task>

    @FormUrlEncoded
    @POST("task/update/deadline/{id}")
    fun changeDeadline(
        @Path("id") id: String,
        @Field("deadline") deadline: Long,
        @Field("idUserAction") idUser: String
    ): Single<Task>

    /**
     * SUBTASK
     */

    @GET("subtask")
    fun queryAllSubtask(): Single<Subtask>

    /**
     * HISTORY
     */
    @GET("history")
    fun queryAllHistory(): Single<History>

    @GET("history/query/{category}")
    fun queryHistoryByUser(
        @Path("category") category: String,
        @Field("idUser") idUser: String
    ): Single<History>

    @GET("history/query/{category}")
    fun queryHistoryByRoom(
        @Path("category") category: String,
        @Field("idRoom") idRoom: String
    ): Single<History>

}

/**
 * Weather datasource - Retrofit tagged
 */