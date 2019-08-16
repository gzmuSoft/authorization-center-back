package cn.edu.gzmu.authorization

import cn.edu.gzmu.authorization.annotation.Route
import cn.edu.gzmu.authorization.model.constant.DASHBOARD
import cn.edu.gzmu.authorization.verticle.sysuser.SysUserMysqlVerticle
import cn.edu.gzmu.authorization.verticle.sysuser.sysUserRoute
import cn.edu.gzmu.authorization.verticle.web.DispatchVerticle
import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME
import io.vertx.core.logging.SLF4JLogDelegateFactory
import io.vertx.ext.web.Router

/**
 * Main
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/13 下午4:17
 */

@Route(DASHBOARD)
class MainVerticle : DispatchVerticle() {
  override suspend fun routerHandler(router: Router) {
    sysUserRoute(router)
  }

}

fun main() {
  System.setProperty(LOGGER_DELEGATE_FACTORY_CLASS_NAME, SLF4JLogDelegateFactory::class.java.name)
  val vertx = Vertx.vertx()
  vertx.deployVerticle(MainVerticle::class.java.name)
  vertx.deployVerticle(SysUserMysqlVerticle::class.java.name)
}
