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
   * 添加一个用户
   *
   * @param user          用户数据对象
   * @param resultHandler 异步处理
   */
  @Fluent
  fun createUser(user: User, resultHandler: Handler<AsyncResult<Int>>): UserService

  /**
   * 修改一个用户
   *
   * @param user          用户数据对象
   * @param resultHandler 异步处理
   */
  @Fluent
  fun updateUser(user: User, resultHandler: Handler<AsyncResult<Int>>): UserService

  /**
   * 是否存在一个用户
   *
   * @param name          用户名称
   * @param email         用户邮箱
   * @param phone         用户电话
   * @param resultHandler 异步处理
   */
  @Fluent
  fun existOne(name: String, email: String, phone: String, resultHandler: Handler<AsyncResult<JsonObject>>): UserService

  /**
   * 查找用户 id
   *
   * @param userId        用户id
   * @param resultHandler 异步处理
   */
  @Fluent
  fun retrieveUser(userId: Long, resultHandler: Handler<AsyncResult<JsonObject>>): UserService

  /**
   * 条件分页查询
   *
   * @param name          用户名
   * @param email         邮箱
   * @param phone         电话
   * @param page          页码
   * @param size          大小
   * @param resultHandler 异步处理
   */
  @Fluent
  fun retrievePage(
    name: String,
    email: String,
    phone: String,
    page: Int,
    size: Int,
    resultHandler: Handler<AsyncResult<JsonArray>>
  ): UserService

  @Fluent
  fun statusChange(id: Long, status: Boolean, resultHandler: Handler<AsyncResult<Int>>): UserService

}
