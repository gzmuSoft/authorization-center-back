package cn.edu.gzmu.authorization.user

import cn.edu.gzmu.authorization.common.RestVerticle
import cn.edu.gzmu.authorization.common.internalServerError
import cn.edu.gzmu.authorization.common.ok
import cn.edu.gzmu.authorization.user.impl.API_RETRIEVE_ONE
import cn.edu.gzmu.authorization.user.impl.SERVICE_NAME
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import kotlinx.coroutines.launch


/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/17 下午5:36
 */
class UserRestVerticle(private val userService: UserService) : RestVerticle() {

  override suspend fun start() {
    super.start()
    val router: Router = Router.router(vertx)
    router.route().handler(BodyHandler.create())
    router.get(API_RETRIEVE_ONE).handler(this::retrieveOne)

    val host = config.getString("product.http.address", "0.0.0.0")
    val port = config.getInteger("product.http.port", 9001)

    launch {
      createHttpServer(router, host, port)
      publishHttpEndpoint(SERVICE_NAME, host, port)
    }
  }

  private fun retrieveOne(context: RoutingContext) {
    val id = context.request().getParam("id").toLong()
    userService.retrievePage(1, 1, Handler {
      if (it.succeeded()) ok(context, JsonObject().put("content", it.result()))
      else internalServerError(context = context, ex = it.cause())
    })
//    userService.retrieveUser(id, Handler() {
//      if (it.failed()) {
//        it.cause().printStackTrace()
//        internalServerError(context = context, ex = it.cause())
//      }
//      ok(context, it.result())
//    })
  }


}
