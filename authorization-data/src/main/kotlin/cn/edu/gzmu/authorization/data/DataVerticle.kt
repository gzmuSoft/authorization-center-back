package cn.edu.gzmu.authorization.data

import cn.edu.gzmu.authorization.common.API_NAME
import cn.edu.gzmu.authorization.common.BaseVerticle
import cn.edu.gzmu.authorization.data.impl.DataRestVerticle
import cn.edu.gzmu.authorization.data.impl.DataServiceImpl
import cn.edu.gzmu.authorization.data.impl.SERVICE_ADDRESS
import cn.edu.gzmu.authorization.data.impl.SERVICE_NAME
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
 * @date 2019/8/22 下午1:16
 */
class DataVerticle : BaseVerticle() {
  lateinit var dataService: DataService
  override suspend fun start() {
    super.start()
    dataService = DataServiceImpl(vertx)
    launch {
      ServiceBinder(vertx).setAddress(SERVICE_ADDRESS).register(DataService::class.java, dataService)
      vertx.deployVerticleAwait(DataRestVerticle(dataService), DeploymentOptions().setConfig(json {
        obj(
          API_NAME to "data"
        )
      }))
      publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, DataService::class.java)
    }
  }
}

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(DataVerticle::class.java.name)
}
