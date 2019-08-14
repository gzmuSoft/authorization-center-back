package cn.edu.gzmu.authorization.verticle.dashboard

import cn.edu.gzmu.authorization.annotation.Route
import cn.edu.gzmu.authorization.model.constant.DASHBOARD
import cn.edu.gzmu.authorization.model.constant.ERROR_MESSAGE
import cn.edu.gzmu.authorization.model.constant.PATH
import cn.edu.gzmu.authorization.verticle.web.ControllerVerticle
import io.vertx.core.json.JsonObject

/**
 * Dashboard Verticle
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/13 下午5:18
 */
@Route(DASHBOARD)
class DashboardVerticle : ControllerVerticle() {
  override suspend fun handleGet(request: WebRequest): WebResponse =
    try {
      ok(JsonObject().put(PATH, request.getPath()))
    } catch (throwable: Throwable) {
      throwable.printStackTrace()
      internalServerError(JsonObject().put(ERROR_MESSAGE, throwable.message))
    }
}
