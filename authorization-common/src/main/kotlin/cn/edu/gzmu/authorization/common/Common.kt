package cn.edu.gzmu.authorization.common

import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpHeaderValues
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/16 下午6:55
 */
// ----------------------------- circuit-breaker
const val CIRCUIT_BREAKER = "circuit-breaker"
const val CIRCUIT_NAME = "name"
const val CIRCUIT_MAX_FAILURES = "maxFailures"
const val CIRCUIT_TIMEOUT = "timeout"
const val CIRCUIT_RESET_TIMEOUT = "resetTimeout"

const val IS_ENABLE_TRUE = " is_enable = 1"
const val ORDER_BY_SORT = " order by sort,id"
const val API_NAME = "api.name"

private fun response(context: RoutingContext, response: JsonObject = JsonObject(), httpStatus: HttpResponseStatus) =
  context.response()
    .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
    .setStatusCode(httpStatus.code())
    .end(response.toBuffer())

private fun response(context: RoutingContext, response: JsonArray = JsonArray(), httpStatus: HttpResponseStatus) =
  context.response()
    .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
    .setStatusCode(httpStatus.code())
    .end(response.toBuffer())

private fun response(
  context: RoutingContext,
  response: JsonObject = JsonObject(),
  httpStatus: HttpResponseStatus,
  ex: Throwable?
) {
  response.put("error_code", httpStatus.code())
  context.response()
    .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
    .setStatusCode(httpStatus.code())
    .end(
      when {
        response.containsKey("error_message") -> response.toString()
        ex != null -> response.put(
          "error_message",
          try {
            JsonObject(ex.message).toString()
          } catch (e: Exception) {
            ex.message ?: httpStatus.reasonPhrase()
          }
        ).toString()
        else -> response
          .put("error_message", httpStatus.reasonPhrase())
          .toString()
      }
    )
}

/**
 * ok 200
 */
fun ok(context: RoutingContext, response: JsonObject = JsonObject()) =
  response(context, response, HttpResponseStatus.OK)

/**
 * ok 200
 */
fun ok(context: RoutingContext, response: JsonArray = JsonArray()) =
  response(context, response, HttpResponseStatus.OK)

/**
 * created 201
 */
fun created(context: RoutingContext) =
  context.response()
    .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
    .setStatusCode(HttpResponseStatus.CREATED.code())
    .end()

/**
 * no content 204
 */
fun noContent(context: RoutingContext) =
  context.response()
    .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
    .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
    .end()

/**
 * temporarily moved 302
 */
fun temporarilyMoved(context: RoutingContext, response: JsonObject = JsonObject()) =
  response(context, response, HttpResponseStatus.FOUND)

/**
 * bad request 400
 */
fun badRequest(context: RoutingContext, response: JsonObject = JsonObject(), ex: Throwable? = null) =
  response(context, response, HttpResponseStatus.BAD_REQUEST, ex)

/**
 * unauthorized 401
 */
fun unauthorized(context: RoutingContext, response: JsonObject = JsonObject(), ex: Throwable? = null) =
  response(context, response, HttpResponseStatus.UNAUTHORIZED, ex)

/**
 * forbidden 403
 */
fun forbidden(context: RoutingContext, response: JsonObject = JsonObject(), ex: Throwable? = null) =
  response(context, response, HttpResponseStatus.FORBIDDEN, ex)

/**
 * not found 404
 */
fun notFound(context: RoutingContext, response: JsonObject = JsonObject(), ex: Throwable? = null) =
  response(context, response, HttpResponseStatus.NOT_FOUND, ex)

/**
 * method not allowed 405
 */
fun methodNotAllowed(context: RoutingContext, response: JsonObject = JsonObject(), ex: Throwable? = null) =
  response(context, response, HttpResponseStatus.METHOD_NOT_ALLOWED, ex)

/**
 * internal server error 500
 */
fun internalServerError(context: RoutingContext, response: JsonObject = JsonObject(), ex: Throwable? = null) =
  response(context, response, HttpResponseStatus.INTERNAL_SERVER_ERROR, ex)

/**
 * bad gateway 502
 */
fun badGateway(context: RoutingContext, response: JsonObject = JsonObject(), ex: Throwable? = null) =
  response(context, response, HttpResponseStatus.BAD_GATEWAY, ex)

/**
 * service unavailable 503
 */
fun serviceUnavailable(context: RoutingContext, response: JsonObject = JsonObject(), ex: Throwable? = null) =
  response(context, response, HttpResponseStatus.SERVICE_UNAVAILABLE, ex)


