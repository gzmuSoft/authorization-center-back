package cn.edu.gzmu.authorization.res.impl

import cn.edu.gzmu.authorization.common.RestVerticle
import cn.edu.gzmu.authorization.common.exception.BadRequestException
import cn.edu.gzmu.authorization.res.ResService
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import kotlinx.coroutines.launch

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/21 下午7:29
 */
@Suppress("RedundantSuspendModifier")
class ResRestVerticle(private val resService: ResService) : RestVerticle() {
  override suspend fun start() {
    super.start()
    val router: Router = Router.router(vertx)
    router.route().handler(BodyHandler.create())
    router.get(API_RES_IS_ENABLE).coroutineHandler(this::enable)
    router.get(API_RES).coroutineHandler(this::retrieve)

    val host = config.getString("product.http.address", "0.0.0.0")
    val port = config.getInteger("product.http.port", 9003)

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
    resService.enable(id!!.toLong(), enable!!.toBoolean(), resultHandlerUpdate(context))
  }

  private suspend fun retrieve(context: RoutingContext) {
    resService.retrieve(resultHandlerNonEmpty(context))
  }
}
