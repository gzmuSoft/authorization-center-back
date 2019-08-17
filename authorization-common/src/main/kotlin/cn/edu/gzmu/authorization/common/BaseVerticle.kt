package cn.edu.gzmu.authorization.common

import io.vertx.circuitbreaker.CircuitBreaker
import io.vertx.circuitbreaker.CircuitBreakerOptions
import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.core.impl.ConcurrentHashSet
import io.vertx.core.json.JsonObject
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.ServiceDiscoveryOptions
import kotlinx.coroutines.launch
import io.vertx.kotlin.servicediscovery.publishAwait
import io.vertx.servicediscovery.types.EventBusService
import io.vertx.servicediscovery.types.HttpEndpoint
import io.vertx.servicediscovery.types.JDBCDataSource
import io.vertx.servicediscovery.types.MessageSource


/**
 * Base Verticle
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/16 下午6:43
 */
abstract class BaseVerticle : CoroutineVerticle() {
  private val log = LoggerFactory.getLogger(BaseVerticle::class.java)

  protected lateinit var discovery: ServiceDiscovery
  protected lateinit var circuitBreaker: CircuitBreaker
  protected var registeredRecords: MutableSet<Record> = ConcurrentHashSet()

  override suspend fun start() {
    discovery = ServiceDiscovery.create(vertx, ServiceDiscoveryOptions().setBackendConfiguration(config))
    launch {
      val cbOptions = config.getJsonObject(CIRCUIT_BREAKER) ?: JsonObject()
      circuitBreaker = CircuitBreaker.create(
        cbOptions.getString(CIRCUIT_NAME, CIRCUIT_BREAKER), vertx,
        CircuitBreakerOptions()
          .setFallbackOnFailure(true)
          .setMaxFailures(cbOptions.getInteger(CIRCUIT_MAX_FAILURES, 5))
          .setTimeout(cbOptions.getLong(CIRCUIT_TIMEOUT, 10000L))
          .setResetTimeout(cbOptions.getLong(CIRCUIT_RESET_TIMEOUT, 30000L))
      )
    }
  }


  protected suspend fun publishHttpEndpoint(name: String, host: String, port: Int) {
    publish(
      HttpEndpoint.createRecord(
        name, host, port, "/",
        JsonObject().put("api.name", config.getString("api.name", ""))
      )
    )
  }

  protected suspend fun publishMessageSource(name: String, address: String) {
    publish(MessageSource.createRecord(name, address))
  }

  protected suspend fun publishJDBCDataSource(name: String, location: JsonObject) {
    publish(JDBCDataSource.createRecord(name, location, JsonObject()))
  }

  protected suspend fun publishEventBusService(name: String, address: String, serviceClass: Class<*>) {
    publish(EventBusService.createRecord(name, address, serviceClass))
  }

  private suspend fun publish(record: Record) {
    discovery.publish(record) {
      if (it.succeeded()) {
        registeredRecords.add(record)
        log.info("Service <${record.name}> published succeed!")
      } else{
        log.error("Service <${record.name}> published failed!")
      }
    }
  }

  override suspend fun stop() {
    val result = registeredRecords.map {
      Future.future<Any> { promise ->
        discovery.unpublish(it.registration, promise::complete)
      }
    }
    CompositeFuture.all(result).setHandler {
      if (it.succeeded()) log.info("Success stop authorization-common")
      else log.error("Failed stop authorization-common")
    }
  }
}
