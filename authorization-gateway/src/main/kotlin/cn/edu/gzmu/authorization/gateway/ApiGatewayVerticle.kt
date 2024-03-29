package cn.edu.gzmu.authorization.gateway

import cn.edu.gzmu.authorization.common.*
import cn.edu.gzmu.authorization.common.exception.ResourceNotFoundException
import com.google.common.base.Splitter
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import org.slf4j.LoggerFactory
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.core.http.listenAwait
import kotlinx.coroutines.launch
import io.vertx.kotlin.servicediscovery.getRecordsAwait
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.types.HttpEndpoint
import io.vertx.core.json.JsonObject
import java.util.*


/**
 * api gateway
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/16 下午6:28
 */
class ApiGatewayVerticle : RestVerticle() {
  private val log = LoggerFactory.getLogger(ApiGatewayVerticle::class.java)

  override suspend fun start() {
    super.start()
    val host = config.getString(ADDRESS, LOCALHOST)
    val port = config.getInteger(PORT, DEFAULT_PORT)
    val router = Router.router(vertx)
    val oauth2Handler = Oauth2Handler(vertx)

    enableCookie(router)
    router.route().handler(BodyHandler.create())

    router.get("/authorize").handler(oauth2Handler::authorize)
    router.get("/login/:code").handler(oauth2Handler::login)
    router.route().handler(oauth2Handler::decryptToken)
    router.route("/api/*").handler(this::dispatchRequests)
    router.route().failureHandler(this::exceptionHandle)
    val httpServerOptions = HttpServerOptions()
    vertx.createHttpServer(httpServerOptions)
      .requestHandler(router::accept)
      .listenAwait(port, host)
    log.info("Success start api gateway on $host:$port")
  }

  private fun dispatchRequests(context: RoutingContext) {
    val initialOffset = 5
    if (Objects.isNull(context.user())) {
      unauthorized(context)
      return
    }
    circuitBreaker.execute<String> {
      launch {
        val records = getAllEndpoints()
        val path = context.request().uri()
        if (path.length <= initialOffset) {
          context.fail(ResourceNotFoundException("Client $path not found!"))
          return@launch
        }
        val prefix = Splitter.on("/").omitEmptyStrings().splitToList(path)[1]

        val newPath = path.substring(initialOffset + prefix.length)
        val client = records.stream()
          .filter { it.metadata.getString(API_NAME) != null }
          .filter { it.metadata.getString(API_NAME) == prefix }
          .findAny()
        if (client.isPresent) {
          doDispatch(context, newPath, discovery.getReference(client.get()).getAs(WebClient::class.java), it)
        } else {
          context.fail(ResourceNotFoundException("Client $prefix not found!"))
        }
      }
    }.setHandler {
      if (it.failed()) context.fail(it.cause())
    }

  }

  private fun doDispatch(context: RoutingContext, path: String, client: WebClient, promise: Promise<String>) {
    client.request(context.request().method(), path).send { result ->
      if (result.succeeded()) {
        val response = result.result()
        if (response.statusCode() >= HttpResponseStatus.INTERNAL_SERVER_ERROR.code()) {
          context.fail(InterruptedException("Error: ${response.statusCode()} : ${response.bodyAsString()}"))
        } else {
          context.response().headers().addAll(response.headers())
          promise.complete()
          ok(context, response.bodyAsJsonObject())
        }
        ServiceDiscovery.releaseServiceObject(discovery, client)
      } else {
        context.fail(InterruptedException("Error: http $path failed!"))
      }
    }
  }

  private fun buildHostURI(): String {
    val port = config.getInteger(PORT, DEFAULT_PORT)
    val host = config.getString(ADDRESS, "localhost")
    return "https://$host:$port"
  }

  private suspend fun getAllEndpoints(): List<Record> {
    return discovery.getRecordsAwait { record ->
      record.type == HttpEndpoint.TYPE
    }
  }
}

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(ApiGatewayVerticle::class.java.name)
}

