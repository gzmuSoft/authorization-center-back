package cn.edu.gzmu.authorization

import cn.edu.gzmu.authorization.annotation.Route
import cn.edu.gzmu.authorization.dashboard.DashboardVerticle
import cn.edu.gzmu.authorization.model.constant.DASHBOARD
import cn.edu.gzmu.authorization.web.ControllerVerticle
import cn.edu.gzmu.authorization.web.DispatchVerticle
import io.vertx.core.Vertx
import kotlin.reflect.KClass

/**
 * Main
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/13 下午4:17
 */

@Route(DASHBOARD)
class MainVerticle : DispatchVerticle() {

  override suspend fun getVerticleAddressByPath(path: String): String =
    if (path.startsWith(routePath(DashboardVerticle::class)))
      DashboardVerticle::class.java.name
    else ""

}

fun routePath(verticle: KClass<out ControllerVerticle>): String =
  (verticle.annotations.find { it is Route } as? Route)!!.value


fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(DashboardVerticle::class.java.name)
  vertx.deployVerticle(MainVerticle::class.java.name)
}
