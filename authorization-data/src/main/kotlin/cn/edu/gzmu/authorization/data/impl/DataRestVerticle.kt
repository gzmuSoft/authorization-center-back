package cn.edu.gzmu.authorization.data.impl

import cn.edu.gzmu.authorization.common.RestVerticle
import cn.edu.gzmu.authorization.data.DataService
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import kotlinx.coroutines.launch

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/22 下午1:17
 */
@Suppress("RedundantSuspendModifier")
class DataRestVerticle(private val dataService: DataService) : RestVerticle() {
  override suspend fun start() {
    super.start()
    val router: Router = Router.router(vertx)
    router.route().handler(BodyHandler.create())
    router.get(API_DATA).coroutineHandler(this::retrieve)
    router.route().failureHandler(this::exceptionHandle)
    val host = config.getString("product.http.address", "0.0.0.0")
    val port = config.getInteger("product.http.port", 9004)

    launch {
      createHttpServer(router, host, port)
      publishHttpEndpoint(SERVICE_NAME, host, port)
    }
  }

  private suspend fun retrieve(context: RoutingContext) {
    val parentId = context.request().getParam("parentId")
    val type = context.request().getParam("type")
    dataService.retrieve(parentId, type, resultHandlerNonEmpty(context))
  }
}
