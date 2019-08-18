package cn.edu.gzmu.authorization.user

import io.vertx.codegen.annotations.ProxyGen
import io.vertx.codegen.annotations.VertxGen
import io.vertx.core.AsyncResult
import io.vertx.codegen.annotations.Fluent
import io.vertx.core.Handler
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject


/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/17 上午10:33
 */
@VertxGen
@ProxyGen
interface UserService {

  /**
   * Save an order into the persistence.
   *
   * @param user         user data object
   * @param resultHandler async result handler
   */
  @Fluent
  fun createUser(user: User, resultHandler: Handler<AsyncResult<Void>>): UserService

  /**
   * Retrieve the order with a certain `orderId`.
   *
   * @param userId       user id
   * @param resultHandler async result handler
   */
  @Fluent
  fun retrieveUser(userId: Long, resultHandler: Handler<AsyncResult<JsonObject>>): UserService

  /**
   * Retrieve the order with a certain `orderId`.
   *
   * @param userId       user id
   * @param resultHandler async result handler
   */
  @Fluent
  fun retrievePage(page: Int, size: Int, resultHandler: Handler<AsyncResult<JsonArray>>): UserService

}
