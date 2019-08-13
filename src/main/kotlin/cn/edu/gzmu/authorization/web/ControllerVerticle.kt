package cn.edu.gzmu.authorization.web

import cn.edu.gzmu.authorization.model.constant.*
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
abstract class ControllerVerticle : CoroutineVerticle() {
  override suspend fun start() {
    start(this::class.java.asSubclass(this::class.java).name)
    println("${this::class.java.asSubclass(this::class.java).name} is deployed")
  }

  private fun start(address: String) {
    vertx.eventBus().consumer<JsonObject>(address) {
      val reqJson = it.body()

      // --------------------------- launch
      launch {
        val request = WebRequest(reqJson)
        when (HttpMethod(reqJson.getString(HTTP_METHOD))) {
          POST -> it.reply(doPost(request).toJson())
          GET -> it.reply(doGet(request).toJson())
          PUT -> it.reply(doPut(request).toJson())
          else -> it.reply(JsonObject().put(RESPONSE_JSON,
            JsonObject().put(MESSAGE, "Http Method is not specified")))
        }
      }

    }
  }

  enum class WebResponseType {
    EMPTY_RESPONSE, JSON
  }

  inner class WebResponse(
    private val type: WebResponseType,
    val path: String = "/",
    private val values: JsonObject = JsonObject()
  ) {
    constructor(json: JsonObject = JsonObject()) : this(WebResponseType.JSON, "index.htm", json)
    constructor() : this(WebResponseType.EMPTY_RESPONSE)

    fun toJson(): JsonObject =
      when (type) {
        WebResponseType.JSON -> JsonObject().put(RESPONSE_JSON, values)
        else -> JsonObject().put(EMPTY_RESPONSE, true)
      }

  }

  inner class WebRequest(val json: JsonObject) {
    fun getPath(): String = json.getString(PATH)
    fun getHttpMethod(): String = json.getString(HTTP_METHOD)
    fun getCookies(): JsonObject = json.getJsonObject(COOKIES)
    fun getHeaders(): JsonObject = json.getJsonObject(HEADERS)
    fun getQueryParams(): JsonObject = json.getJsonObject(QUERY_PARAM)
    fun getFormAttributes(): JsonObject = json.getJsonObject(FORM_ATTRIBUTES)
    fun getUploadFiles(): JsonObject = json.getJsonObject(UPLOAD_FILES)
    fun getUploadFileNames(): JsonObject = json.getJsonObject(UPLOAD_FILE_NAMES)
    fun body(): JsonObject = json.getJsonObject(BODY)
  }

  open suspend fun doGet(request: WebRequest): WebResponse = WebResponse()
  open suspend fun doPost(request: WebRequest): WebResponse = WebResponse()
  open suspend fun doPut(request: WebRequest): WebResponse = WebResponse()

}
