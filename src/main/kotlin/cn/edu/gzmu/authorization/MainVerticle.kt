package cn.edu.gzmu.authorization

import cn.edu.gzmu.authorization.annotation.Route
import cn.edu.gzmu.authorization.verticle.dashboard.DashboardVerticle
import cn.edu.gzmu.authorization.model.constant.DASHBOARD
import cn.edu.gzmu.authorization.verticle.sysuser.SysUserMysqlVerticle
import cn.edu.gzmu.authorization.verticle.sysuser.SysUserVerticle
import cn.edu.gzmu.authorization.verticle.web.ControllerVerticle
import cn.edu.gzmu.authorization.verticle.web.DispatchVerticle
import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME
import io.vertx.core.logging.SLF4JLogDelegateFactory
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
    when {
        path.startsWith(routePath(DashboardVerticle::class)) -> DashboardVerticle::class.java.name
        path.startsWith(routePath(SysUserVerticle::class)) -> SysUserVerticle::class.java.name
        else -> ""
    }

}

fun routePath(verticle: KClass<out ControllerVerticle>): String =
  (verticle.annotations.find { it is Route } as? Route)!!.value


fun main() {
  System.setProperty(LOGGER_DELEGATE_FACTORY_CLASS_NAME, SLF4JLogDelegateFactory::class.java.name)
  val vertx = Vertx.vertx()
  vertx.deployVerticle(DashboardVerticle::class.java.name)
  vertx.deployVerticle(SysUserVerticle::class.java.name)
  vertx.deployVerticle(SysUserMysqlVerticle::class.java.name)
  vertx.deployVerticle(MainVerticle::class.java.name)
}
