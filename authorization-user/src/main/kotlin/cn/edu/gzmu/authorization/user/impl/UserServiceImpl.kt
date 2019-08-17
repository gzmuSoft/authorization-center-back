package cn.edu.gzmu.authorization.user.impl

import cn.edu.gzmu.authorization.common.service.MysqlRepository
import cn.edu.gzmu.authorization.user.User
import cn.edu.gzmu.authorization.user.UserService
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.sqlclient.Tuple

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/17 下午4:21
 */
class UserServiceImpl(vertx: Vertx) : MysqlRepository(vertx), UserService {

  override fun retrieveUser(userId: Long, resultHandler: Handler<AsyncResult<JsonObject>>): UserService {
    val promise = Promise.promise<JsonObject>()
    retrieveOne(RETRIEVE_USER, Tuple.of(userId), promise)
    promise.future().setHandler(resultHandler)
    return this
  }



  override fun createUser(user: User, resultHandler: Handler<AsyncResult<Void>>): UserService {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}
