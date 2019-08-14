package cn.edu.gzmu.authorization.verticle.sysuser

import cn.edu.gzmu.authorization.annotation.Route
import cn.edu.gzmu.authorization.model.constant.USER
import cn.edu.gzmu.authorization.verticle.web.ControllerVerticle
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.eventbus.requestAwait
import kotlinx.coroutines.launch

/**
 * Sys User Verticle
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/14 下午1:12
 */
@Route(USER)
class SysUserVerticle : ControllerVerticle() {

  override suspend fun handleGet(request: WebRequest): WebResponse {
    val path = request.getPath().substringAfterLast(USER)
    return if (path == ALL) {
      ok(
        vertx.eventBus().requestAwait<JsonObject>
          (SysUserMysqlVerticle::class.java.name, JsonObject()).body()
      )
    } else super.handleGet(request)
  }

}
