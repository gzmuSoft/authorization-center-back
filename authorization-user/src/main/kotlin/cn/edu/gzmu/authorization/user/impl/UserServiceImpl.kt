package cn.edu.gzmu.authorization.user.impl

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

  override fun retrievePage(page: Int, size: Int, resultHandler: Handler<AsyncResult<JsonArray>>): UserService {
    val params = JsonArray().add(1)
    val promise = Promise.promise<JsonArray>()
    retrieveArray(RETRIEVE_USER, params, promise)
    promise.future().setHandler(resultHandler)
    return this
  }

  override fun retrieveUser(userId: Long, resultHandler: Handler<AsyncResult<JsonObject>>): UserService {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun createUser(user: User, resultHandler: Handler<AsyncResult<Void>>): UserService {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}
