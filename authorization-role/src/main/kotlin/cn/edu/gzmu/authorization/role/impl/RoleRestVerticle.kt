package cn.edu.gzmu.authorization.role.impl

import cn.edu.gzmu.authorization.common.RestVerticle
import cn.edu.gzmu.authorization.common.exception.BadRequestException
import cn.edu.gzmu.authorization.role.Role
import cn.edu.gzmu.authorization.role.RoleConverter
import cn.edu.gzmu.authorization.role.RoleService
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import kotlinx.coroutines.launch
import java.util.*

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/21 下午2:17
 */
@Suppress("RedundantSuspendModifier")
class RoleRestVerticle(private val roleService: RoleService) : RestVerticle() {
  override suspend fun start() {
    super.start()
    val router: Router = Router.router(vertx)
    router.route().handler(BodyHandler.create())
    router.get(API_ROLE_IS_ENABLE).coroutineHandler(this::enable)
    router.get(API_ROLE).coroutineHandler(this::retrieve)
    router.put(API_ROLE).coroutineHandler(this::update)
    router.post(API_ROLE).coroutineHandler(this::create)

    val host = config.getString("product.http.address", "0.0.0.0")
    val port = config.getInteger("product.http.port", 9002)

    launch {
      createHttpServer(router, host, port)
      publishHttpEndpoint(SERVICE_NAME, host, port)
    }
  }

  private suspend fun enable(context: RoutingContext) {
    val request = context.request()
    val id = request.getParam("id")
    val enable = request.getParam("enable")
    if (id == null || enable == null || id.isNullOrBlank() || enable.isNullOrBlank()) context.fail(BadRequestException("缺少必要的参数信息"))
    roleService.enable(id!!.toLong(), enable!!.toBoolean(), resultHandlerUpdate(context))
  }

  private suspend fun retrieve(context: RoutingContext) {
    roleService.retrieve(resultHandlerNonEmpty(context))
  }

  private suspend fun update(context: RoutingContext) {
    val body = context.bodyAsJson
    val role = Role()
    RoleConverter.fromJson(body, role)
    if (Objects.isNull(role.id)) context.fail(BadRequestException())
    roleService.updateRole(role, resultHandlerUpdate(context))
  }

  private suspend fun create(context: RoutingContext) {
    val body = context.bodyAsJson
    val role = Role()
    RoleConverter.fromJson(body, role)
    roleService.createRole(role, resultHandlerCreate(context))
  }
}
