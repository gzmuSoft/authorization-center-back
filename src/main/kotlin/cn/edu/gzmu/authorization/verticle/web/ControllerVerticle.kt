package cn.edu.gzmu.authorization.verticle.web

import cn.edu.gzmu.authorization.model.constant.*
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpMethod.*
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.launch
import io.netty.handler.codec.http.HttpResponseStatus.*
import io.vertx.kotlin.core.Vertx

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

  /**
   * 封装错误信息
   *
   * @param response 需要封装的响应
   */
  private fun errorBody(response: JsonObject, httpStatus: HttpResponseStatus = INTERNAL_SERVER_ERROR): JsonObject {
    if (!response.containsKey(ERROR_CODE)) response.put(ERROR_CODE, httpStatus.code())
    if (!response.containsKey(ERROR_MESSAGE)) response.put(ERROR_MESSAGE, httpStatus.reasonPhrase())
    return response
  }

  /**
   * ok 200
   *
   * @param response 响应
   * @return 结果
   */
  protected fun ok(response: JsonObject = JsonObject()): WebResponse =
    WebResponse(response)

  /**
   * created 201
   *
   * @param response 响应
   * @return 结果
   */
  protected fun created(response: JsonObject = JsonObject()): WebResponse =
    WebResponse(values = response, statusCode = CREATED)

  /**
   * noContent 204
   *
   * @param response 响应
   * @return 结果
   */
  protected fun noContent(response: JsonObject = JsonObject()): WebResponse =
    WebResponse(values = response, statusCode = NO_CONTENT)

  /**
   * badRequest 400
   *
   * @param response 响应
   * @return 结果
   */
  protected fun badRequest(response: JsonObject = JsonObject()): WebResponse =
    WebResponse(WebResponseType.ERROR, errorBody(response, BAD_REQUEST), BAD_REQUEST)

  /**
   * unauthorized 401
   *
   * @param response 响应
   * @return 结果
   */
  protected fun unauthorized(response: JsonObject = JsonObject()): WebResponse =
    WebResponse(WebResponseType.ERROR, errorBody(response, UNAUTHORIZED), UNAUTHORIZED)

  /**
   * forbidden 403
   *
   * @param response 响应
   * @return 结果
   */
  protected fun forbidden(response: JsonObject = JsonObject()): WebResponse =
    WebResponse(WebResponseType.ERROR, errorBody(response, FORBIDDEN), FORBIDDEN)

  /**
   * notFound 404
   *
   * @param response 响应
   * @return 结果
   */
  protected fun notFound(response: JsonObject = JsonObject()): WebResponse =
    WebResponse(WebResponseType.ERROR, errorBody(response, NOT_FOUND), NOT_FOUND)

  /**
   * methodNotAllowed 404
   *
   * @param response 响应
   * @return 结果
   */
  protected fun methodNotAllowed(response: JsonObject = JsonObject()): WebResponse =
    WebResponse(WebResponseType.ERROR, errorBody(response, METHOD_NOT_ALLOWED), METHOD_NOT_ALLOWED)

  /**
   * internalServerError 500
   *
   * @param response 响应
   * @return 结果
   */
  protected fun internalServerError(response: JsonObject = JsonObject()): WebResponse =
    WebResponse(WebResponseType.ERROR, errorBody(response), INTERNAL_SERVER_ERROR)

  // -----------------------------------------------------------------------------------------------------------------

  /**
   * 相应类型
   */
  enum class WebResponseType {
    /**
     * 无响应体
     */
    EMPTY,
    /**
     * json
     */
    JSON,
    /**
     * error
     */
    ERROR
  }

  inner class WebResponse(
    private val type: WebResponseType = WebResponseType.JSON,
    private val values: JsonObject = JsonObject(),
    statusCode: HttpResponseStatus = OK
  ) {
    constructor(json: JsonObject = JsonObject()) : this(WebResponseType.JSON, json)
    constructor() : this(WebResponseType.EMPTY)

    init {
      values.put(STATUS_CODE, statusCode.code())
    }

    fun toJson(): JsonObject =
      when (type) {
        WebResponseType.JSON -> JsonObject().put(RESPONSE_JSON, values)
        WebResponseType.ERROR -> JsonObject().put(RESPONSE_ERROR, values)
        else -> JsonObject().put(RESPONSE_EMPTY, true)
      }
  }

  inner class WebRequest(private val request: JsonObject) {
    fun getPath(): String = request.getString(PATH)
    fun getHttpMethod(): String = request.getString(HTTP_METHOD)
    fun getCookies(): JsonObject = request.getJsonObject(COOKIES)
    fun getHeaders(): JsonObject = request.getJsonObject(HEADERS)
    fun getQueryParams(): JsonObject = request.getJsonObject(QUERY_PARAM)
    fun getFormAttributes(): JsonObject = request.getJsonObject(FORM_ATTRIBUTES)
    fun getUploadFiles(): JsonObject = request.getJsonObject(UPLOAD_FILES)
    fun getUploadFileNames(): JsonObject = request.getJsonObject(UPLOAD_FILE_NAMES)
    fun body(): JsonObject = request.getJsonObject(BODY)
  }

}
