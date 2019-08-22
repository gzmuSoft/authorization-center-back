package cn.edu.gzmu.authorization.res

import cn.edu.gzmu.authorization.common.API_NAME
import cn.edu.gzmu.authorization.common.BaseVerticle
import cn.edu.gzmu.authorization.res.impl.ResRestVerticle
import cn.edu.gzmu.authorization.res.impl.ResServiceImpl
import cn.edu.gzmu.authorization.res.impl.SERVICE_ADDRESS
import cn.edu.gzmu.authorization.res.impl.SERVICE_NAME
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
 * @date 2019/8/21 下午7:27
 */
class ResVerticle: BaseVerticle() {
  lateinit var resService: ResService

  override suspend fun start() {
    super.start()
    resService = ResServiceImpl(vertx)
    launch {
      ServiceBinder(vertx).setAddress(SERVICE_ADDRESS).register(ResService::class.java, resService)
      vertx.deployVerticleAwait(ResRestVerticle(resService), DeploymentOptions().setConfig(json {
        obj(
          API_NAME to "res"
        )
      }))
      publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, ResService::class.java)
    }
  }
}

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(ResVerticle::class.java.name)
}
