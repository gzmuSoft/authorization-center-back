package cn.edu.gzmu.authorization.verticle.web

import cn.edu.gzmu.authorization.model.constant.*
import cn.edu.gzmu.authorization.model.helper.WebRequest
import cn.edu.gzmu.authorization.model.helper.WebResponse
import cn.edu.gzmu.authorization.model.helper.methodNotAllowed
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpMethod.*
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.launch

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/13 下午5:31
 */
@Deprecated(message = "不再通过这种方式分发请求，直接路由绑定")
abstract class ControllerVerticle : CoroutineVerticle() {

  override suspend fun start() {
    start(this::class.java.asSubclass(this::class.java).name)
    println("${this::class.java.asSubclass(this::class.java).name} is deployed")
  }
  // -----------------------------------------------------------------------------------------------------------------

  private fun start(address: String) {
    vertx.eventBus().consumer<JsonObject>(address) {
      val reqJson = it.body()

      // --------------------------- launch
      launch {
        val request = WebRequest(reqJson)
        when (HttpMethod(reqJson.getString(HTTP_METHOD))) {
          POST -> it.reply(handlePost(request).toJson())
          GET -> it.reply(handleGet(request).toJson())
          PUT -> it.reply(handlePut(request).toJson())
          PATCH -> it.reply(handlePatch(request).toJson())
          DELETE -> it.reply(handleDelete(request).toJson())
          else -> it.reply(methodNotAllowed())
        }
      }
    }
  }

  // -----------------------------------------------------------------------------------------------------------------

  open suspend fun handleGet(request: WebRequest): WebResponse = methodNotAllowed()
  open suspend fun handlePost(request: WebRequest): WebResponse = methodNotAllowed()
  open suspend fun handlePut(request: WebRequest): WebResponse = methodNotAllowed()
  open suspend fun handleDelete(request: WebRequest): WebResponse = methodNotAllowed()
  open suspend fun handlePatch(request: WebRequest): WebResponse = methodNotAllowed()

  // -----------------------------------------------------------------------------------------------------------------

}
