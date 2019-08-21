package cn.edu.gzmu.authorization.user.impl

import cn.edu.gzmu.authorization.common.ORDER_BY_SORT
import cn.edu.gzmu.authorization.common.service.JdbcRepository
import cn.edu.gzmu.authorization.user.User
import cn.edu.gzmu.authorization.user.UserService
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/17 下午4:21
 */
class UserServiceImpl(vertx: Vertx) : JdbcRepository(vertx), UserService {

  override fun statusChange(id: Long, status: Boolean, resultHandler: Handler<AsyncResult<Int>>): UserService {
    val statusValue = if (status) 1 else 0
    val promise = Promise.promise<Int>()
    updateOne(STATUS_CHANGE, JsonArray(listOf(statusValue, id)), promise)
    promise.future().setHandler(resultHandler)
    return this
  }

  override fun existOne(
    name: String,
    email: String,
    phone: String,
    resultHandler: Handler<AsyncResult<JsonObject>>
  ): UserService {
    var option = ""
    val params = JsonArray()
    if (name.isNotEmpty()) {
      option += "AND name = ?"
      params.add(name)
    }
    if (email.isNotEmpty()) {
      option += "AND email = ?"
      params.add(email)
    }
    if (phone.isNotEmpty()) {
      option += "AND phone = ?"
      params.add(phone)
    }
    val promise = Promise.promise<JsonObject>()
    retrieveOne(EXIST_USER + option + ORDER_BY_SORT, params, promise)
    promise.future().setHandler(resultHandler)
    return this
  }

  override fun retrievePage(
    name: String,
    email: String,
    phone: String,
    page: Int,
    size: Int,
    resultHandler: Handler<AsyncResult<JsonArray>>
  ): UserService {
    val params = JsonArray(listOf(name, email, phone, (page - 1) * size, size))
    val promise = Promise.promise<JsonArray>()
    retrieve(RETRIEVE_PAGE, params, promise, listOf("pwd"))
    promise.future().setHandler(resultHandler)
    return this
  }

  override fun retrieveUser(userId: Long, resultHandler: Handler<AsyncResult<JsonObject>>): UserService {
    val params = JsonArray().add(userId)
    val promise = Promise.promise<JsonObject>()
    retrieveOne(RETRIEVE_USER, params, promise, listOf("pwd"))
    promise.future().setHandler(resultHandler)
    return this
  }

  override fun createUser(user: User, resultHandler: Handler<AsyncResult<Int>>): UserService {
    val params = JsonArray(
      listOf(
        user.name, user.spell, user.pwd, user.status, user.icon, user.email, user.phone,
        user.onlineStatus, user.sort, user.createUser, user.modifyUser, user.remark, true
      )
    )
    val promise = Promise.promise<Int>()
    updateOne(INSERT_USER, params, promise)
    promise.future().setHandler(resultHandler)
    return this
  }

  override fun updateUser(user: User, resultHandler: Handler<AsyncResult<Int>>): UserService {
    val params = JsonArray(
      listOf(
        user.name, user.spell, user.pwd, user.status, user.icon, user.email, user.phone,
        user.onlineStatus, user.sort, user.modifyUser, user.remark, user.isEnable,
        user.id
      )
    )
    val promise = Promise.promise<Int>()
    updateOne(UPDATE_USER, params, promise)
    promise.future().setHandler(resultHandler)
    return this
  }
}
