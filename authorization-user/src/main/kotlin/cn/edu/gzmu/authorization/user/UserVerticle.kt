package cn.edu.gzmu.authorization.user

import cn.edu.gzmu.authorization.common.BaseVerticle
import cn.edu.gzmu.authorization.user.impl.SERVICE_ADDRESS
import cn.edu.gzmu.authorization.user.impl.SERVICE_NAME
import cn.edu.gzmu.authorization.user.impl.UserServiceImpl
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Vertx
import io.vertx.kotlin.config.ConfigStoreOptions
import io.vertx.kotlin.config.configStoreOptionsOf
import io.vertx.kotlin.core.deployVerticleAwait
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.serviceproxy.ServiceBinder

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/17 上午10:24
 */
class UserVerticle : BaseVerticle() {
  lateinit var userService: UserService

  override suspend fun start() {
    super.start()
    userService = UserServiceImpl(vertx)
    ServiceBinder(vertx).setAddress(SERVICE_ADDRESS).register(UserService::class.java, userService)
    vertx.deployVerticleAwait(UserRestVerticle(userService))
    publishEventBusService(
      SERVICE_NAME,
      SERVICE_ADDRESS, UserService::class.java)
  }

}

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(UserVerticle::class.java.name)
}
