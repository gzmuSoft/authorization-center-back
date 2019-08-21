package cn.edu.gzmu.authorization.role

import cn.edu.gzmu.authorization.common.API_NAME
import cn.edu.gzmu.authorization.common.BaseVerticle
import cn.edu.gzmu.authorization.role.impl.RoleRestVerticle
import cn.edu.gzmu.authorization.role.impl.RoleServiceImpl
import cn.edu.gzmu.authorization.role.impl.SERVICE_ADDRESS
import cn.edu.gzmu.authorization.role.impl.SERVICE_NAME
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.kotlin.core.deployVerticleAwait
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.serviceproxy.ServiceBinder
import kotlinx.coroutines.launch

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/21 下午2:15
 */
class RoleVerticle : BaseVerticle() {
  lateinit var roleService: RoleService

  override suspend fun start() {
    super.start()
    roleService = RoleServiceImpl(vertx)
    launch {
      ServiceBinder(vertx).setAddress(SERVICE_ADDRESS).register(RoleService::class.java, roleService)
      vertx.deployVerticleAwait(RoleRestVerticle(roleService), DeploymentOptions().setConfig(json {
        obj(
          API_NAME to "role"
        )
      }))
      publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, RoleService::class.java)
    }
  }
}

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(RoleVerticle::class.java.name)
}
