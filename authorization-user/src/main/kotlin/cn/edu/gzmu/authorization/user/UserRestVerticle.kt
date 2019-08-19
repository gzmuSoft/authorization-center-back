package cn.edu.gzmu.authorization.user

import cn.edu.gzmu.authorization.common.*
import cn.edu.gzmu.authorization.common.exception.BadRequestException
import cn.edu.gzmu.authorization.common.exception.ResourceNotFoundException
import cn.edu.gzmu.authorization.user.impl.*
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import kotlinx.coroutines.launch
import java.util.*


/**
 * 用户 rest
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/17 下午5:36
 */
class UserRestVerticle(private val userService: UserService) : RestVerticle() {

  override suspend fun start() {
    super.start()
    val router: Router = Router.router(vertx)
    router.route().handler(BodyHandler.create())
    router.get(API_EXIST).handler(this::existOne)
    router.get(API_USER_ONE).handler(this::retrieveOne)
    router.get(API_USER).handler(this::retrievePage)
    router.post(API_USER).handler(this::addOne)
    router.put(API_USER).handler(this::updateOne)
    router.route().failureHandler(this::exceptionHandle)

    val host = config.getString("product.http.address", "0.0.0.0")
    val port = config.getInteger("product.http.port", 9001)

    launch {
      createHttpServer(router, host, port)
      publishHttpEndpoint(SERVICE_NAME, host, port)
    }
  }

  /**
   * 获取一个
   *
   * @param context 路由
   */
  private fun retrieveOne(context: RoutingContext) {
    val id = context.request().getParam("id")?.toLong()
    if (id == null) badRequest(context)
    userService.retrieveUser(id!!, resultHandlerNonEmpty(context))
  }

  /**
   * 获取多个，分页
   *
   * @param context 路由
   */
  private fun retrievePage(context: RoutingContext) {
    val request = context.request()
    userService.retrievePage(
      likeParam(request.getParam("name")),
      likeParam(request.getParam("email")),
      likeParam(request.getParam("phone")),
      request.getParam("page")?.toInt() ?: 1,
      request.getParam("size")?.toInt() ?: 10,
      resultHandlerNonEmpty(context)
    )
  }

  /**
   * 添加一个
   *
   * @param context 路由
   */
  private fun addOne(context: RoutingContext) {
    val body = context.bodyAsJson
    val user = User()
    UserConverter.fromJson(body, user)
    userService.createUser(user, Handler {
      if (it.succeeded() && it.result() > 0) created(context)
      else context.fail(it.cause())
    })
  }

  /**
   * 更新一个
   *
   * @param context 路由
   */
  private fun updateOne(context: RoutingContext) {
    val body = context.bodyAsJson
    val user = User()
    UserConverter.fromJson(body, user)
    if (Objects.isNull(user.id)) context.fail(BadRequestException())
    else userService.updateUser(user, Handler {
      if (it.succeeded() && it.result() > 0) ok(context, JsonObject())
      else context.fail(it.cause())
    })
  }

  /**
   * 是否存在一个
   *
   * @param context 路由
   */
  private fun existOne(context: RoutingContext) {
    val request = context.request()
    userService.existOne(
      request.getParam("name") ?: "",
      request.getParam("email") ?: "",
      request.getParam("phone") ?: "",
      Handler {
        if (it.succeeded()) {
          if (it.result().isEmpty) context.fail(ResourceNotFoundException())
          else ok(context, JsonObject())
        } else context.fail(it.cause())
      }
    )
  }
}
