package cn.edu.gzmu.authorization.model.helper

import cn.edu.gzmu.authorization.model.constant.*
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.json.JsonObject

/**
 * Web Response
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/15 下午9:49
 */
class WebResponse(
  private val type: WebResponseType = WebResponseType.JSON,
  private val values: JsonObject = JsonObject(),
  statusCode: HttpResponseStatus = HttpResponseStatus.OK
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


/**
 * 封装错误信息
 *
 * @param response 需要封装的响应
 */
private fun errorBody(
  response: JsonObject,
  httpStatus: HttpResponseStatus = HttpResponseStatus.INTERNAL_SERVER_ERROR
): JsonObject {
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
fun ok(response: JsonObject = JsonObject()): WebResponse =
  WebResponse(response)

/**
 * created 201
 *
 * @param response 响应
 * @return 结果
 */
fun created(response: JsonObject = JsonObject()): WebResponse =
  WebResponse(values = response, statusCode = HttpResponseStatus.CREATED)

/**
 * noContent 204
 *
 * @param response 响应
 * @return 结果
 */
fun noContent(response: JsonObject = JsonObject()): WebResponse =
  WebResponse(values = response, statusCode = HttpResponseStatus.NO_CONTENT)

/**
 * badRequest 400
 *
 * @param response 响应
 * @return 结果
 */
fun badRequest(response: JsonObject = JsonObject()): WebResponse =
  WebResponse(
    WebResponseType.ERROR, errorBody(response, HttpResponseStatus.BAD_REQUEST),
    HttpResponseStatus.BAD_REQUEST
  )

/**
 * unauthorized 401
 *
 * @param response 响应
 * @return 结果
 */
fun unauthorized(response: JsonObject = JsonObject()): WebResponse =
  WebResponse(
    WebResponseType.ERROR, errorBody(response, HttpResponseStatus.UNAUTHORIZED),
    HttpResponseStatus.UNAUTHORIZED
  )

/**
 * forbidden 403
 *
 * @param response 响应
 * @return 结果
 */
fun forbidden(response: JsonObject = JsonObject()): WebResponse =
  WebResponse(WebResponseType.ERROR, errorBody(response, HttpResponseStatus.FORBIDDEN), HttpResponseStatus.FORBIDDEN)

/**
 * notFound 404
 *
 * @param response 响应
 * @return 结果
 */
fun notFound(response: JsonObject = JsonObject()): WebResponse =
  WebResponse(WebResponseType.ERROR, errorBody(response, HttpResponseStatus.NOT_FOUND), HttpResponseStatus.NOT_FOUND)

/**
 * methodNotAllowed 404
 *
 * @param response 响应
 * @return 结果
 */
fun methodNotAllowed(response: JsonObject = JsonObject()): WebResponse =
  WebResponse(
    WebResponseType.ERROR, errorBody(response, HttpResponseStatus.METHOD_NOT_ALLOWED),
    HttpResponseStatus.METHOD_NOT_ALLOWED
  )

/**
 * internalServerError 500
 *
 * @param response 响应
 * @return 结果
 */
fun internalServerError(response: JsonObject = JsonObject()): WebResponse =
  WebResponse(WebResponseType.ERROR, errorBody(response), HttpResponseStatus.INTERNAL_SERVER_ERROR)
