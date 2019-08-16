package cn.edu.gzmu.authorization.verticle.sysuser

import cn.edu.gzmu.authorization.model.constant.*
import cn.edu.gzmu.authorization.util.getCondition
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

/**
 * Sys User Verticle
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/14 下午1:12
 */
class SysUserController() {

  fun all(routingContext: RoutingContext) {
    val request: JsonObject = routingContext.get(REQUEST)
    val params = JsonArray()
    val page = request.getString(PAGE, PAGE_START).toInt()
    val size = request.getString(SIZE, PAGE_SIZE).toInt()
    val condition = request.getCondition(NAME)
    params.add(condition).add(condition).add(condition)
      .add((page - 1) * size).add(size)
  }

//  override suspend fun handleGet(request: WebRequest): WebResponse {
//    val path = request.getPath().substringAfterLast(USER)
//    return if (path == ALL) {
//      ok(
//        vertx.eventBus().requestAwait<JsonObject>
//          (SysUserMysqlVerticle::class.java.name, request.getQueryParams()).body()
//      )
//    } else super.handleGet(request)
//  }
}

fun sysUserRoute(router: Router): Router {
  val sysUserController = SysUserController()
  router.get(ALL).handler(sysUserController::all)
  return router
}
