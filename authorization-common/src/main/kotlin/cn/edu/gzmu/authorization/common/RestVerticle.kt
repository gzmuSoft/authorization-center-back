package cn.edu.gzmu.authorization.common

import io.vertx.ext.web.Router
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.CookieHandler
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.SessionHandler
import io.vertx.ext.web.sstore.ClusteredSessionStore
import io.vertx.ext.web.sstore.LocalSessionStore
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.launch
import java.util.HashSet
import java.util.Objects
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject


/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/16 下午8:27
 */
abstract class RestVerticle : BaseVerticle() {
  suspend fun createHttpServer(router: Router, host: String, port: Int) {
    vertx.createHttpServer()
      .requestHandler(router::accept)
      .listenAwait(port, host)
    println("${this::class.java.asSubclass(this::class.java).name} http is start on $host:$port")
  }

  protected fun enableCorsSupport(router: Router): Router {
    val allowHeaders = HashSet<String>()
    allowHeaders.add("x-requested-with")
    allowHeaders.add("Access-Control-Allow-Origin")
    allowHeaders.add("origin")
    allowHeaders.add("Content-Type")
    allowHeaders.add("accept")
    router.route().handler(
      CorsHandler.create("*")
        .allowedHeaders(allowHeaders)
        .allowedMethod(HttpMethod.GET)
        .allowedMethod(HttpMethod.POST)
        .allowedMethod(HttpMethod.PUT)
        .allowedMethod(HttpMethod.DELETE)
        .allowedMethod(HttpMethod.PATCH)
        .allowedMethod(HttpMethod.OPTIONS)
    )
    return router
  }

  protected fun enableCookie(router: Router) {
    router.route()
      .handler(CookieHandler.create())
  }

  protected fun enableLocalSession(router: Router, name: String) {
    Objects.requireNonNull(name)
    router.route()
      .handler(CookieHandler.create())
      .handler(SessionHandler.create(LocalSessionStore.create(vertx, name)))
  }

  protected fun enableClusteredSession(router: Router, name: String) {
    Objects.requireNonNull(name)
    router.route()
      .handler(CookieHandler.create())
      .handler(SessionHandler.create(ClusteredSessionStore.create(vertx, name)))
  }

  protected fun likeParam(param: String?): String = if (Objects.isNull(param)) "%" else "%$param%"


  fun Route.coroutineHandler(fn: suspend (RoutingContext) -> Unit) {
    handler { ctx ->
      launch(ctx.vertx().dispatcher()) {
        try {
          fn(ctx)
        } catch (e: Exception) {
          ctx.fail(e)
        }
      }
    }
  }

  protected fun <T> resultHandlerNonEmpty(context: RoutingContext): Handler<AsyncResult<T>> {
    return Handler { ar ->
      if (ar.succeeded()) {
        val res = ar.result()
        if (res == null || (res is JsonObject && res.isEmpty)) {
          notFound(context)
          return@Handler
        }
        if (res is JsonArray) ok(context, res)
        else ok(context, JsonObject.mapFrom(res))
      } else {
        internalServerError(context, ex = ar.cause())
        ar.cause().printStackTrace()
      }
    }
  }
}

