package cn.edu.gzmu.authorization.user

import cn.edu.gzmu.authorization.common.*
import cn.edu.gzmu.authorization.common.exception.BadRequestException
import cn.edu.gzmu.authorization.common.exception.ResourceNotFoundException
import cn.edu.gzmu.authorization.user.impl.*
import io.vertx.core.Handler
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import kotlinx.coroutines.launch
import java.util.*
import java.util.stream.Collectors


/**
 * 用户 rest
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/17 下午5:36
 */
@Suppress("RedundantSuspendModifier")
class UserRestVerticle(private val userService: UserService) : RestVerticle() {

  override suspend fun start() {
    super.start()
    val router: Router = Router.router(vertx)
    router.route().handler(BodyHandler.create())
    router.get(API_EXIST).coroutineHandler(this::existOne)
    router.get(API_STATUS).coroutineHandler(this::status)
    router.get(API_USER_ONE).coroutineHandler(this::retrieveOne)
    router.get(API_USER).coroutineHandler(this::retrievePage)
    router.post(API_USER).coroutineHandler(this::addOne)
    router.put(API_USER).coroutineHandler(this::updateOne)
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
  private suspend fun retrieveOne(context: RoutingContext) {
    val id = context.request().getParam("id")?.toLong()
    if (id == null) context.fail(BadRequestException("缺少 id 属性"))
    userService.retrieveUser(id!!, resultHandlerNonEmpty(context))
  }

  /**
   * 获取多个，分页
   *
   * @param context 路由
   */
  private suspend fun retrievePage(context: RoutingContext) {
    val request = context.request()
    userService.retrievePage(
      likeParam(request.getParam("name")),
      likeParam(request.getParam("email")),
      likeParam(request.getParam("phone")),
      request.getParam("page")?.toInt() ?: 1,
      request.getParam("size")?.toInt() ?: 10,
      Handler { res ->
        if (res.succeeded()) {
          val list = res.result().list.stream().map { it as JsonObject }.collect(Collectors.toList())
          val result = list.stream().map {
            val roles = list.filter { ele -> it.getLong("id") == ele.getLong("id") }
              .map { ele ->
                json {
                  obj(
                    "id" to ele.getLong("role_id"),
                    "name" to ele.getString("role_name")
                  )
                }
              }.toCollection(mutableListOf())
            it.put("roles", JsonArray(roles))
          }.collect(Collectors.toList())
          ok(context, JsonArray(result.distinctBy { it.getLong("id") }))
        } else context.fail(res.cause())
      }
    )
  }

  /**
   * 添加一个
   *
   * @param context 路由
   */
  private suspend fun addOne(context: RoutingContext) {
    val body = context.bodyAsJson
    val user = User()
    UserConverter.fromJson(body, user)
    userService.createUser(user, resultHandlerCreate(context))
  }

  /**
   * 更新一个
   *
   * @param context 路由
   */
  private suspend fun updateOne(context: RoutingContext) {
    val body = context.bodyAsJson
    val user = User()
    UserConverter.fromJson(body, user)
    if (Objects.isNull(user.id)) context.fail(BadRequestException())
    else userService.updateUser(user, resultHandlerUpdate(context))
  }

  /**
   * 是否存在一个
   *
   * @param context 路由
   */
  private suspend fun existOne(context: RoutingContext) {
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

  /**
   * 修改一个用户的状态
   *
   * @param context 路由
   */
  private suspend fun status(context: RoutingContext) {
    val request = context.request()
    val id = request.getParam("id")
    val status = request.getParam("status")
    if (id.isNullOrBlank() || status.isNullOrBlank()) context.fail(BadRequestException("缺少必要的参数信息"))
    userService.statusChange(id!!.toLong(), status!!.toBoolean(), resultHandlerUpdate(context))
  }
}
