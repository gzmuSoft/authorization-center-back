package cn.edu.gzmu.authorization.common

import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpHeaderValues
import io.netty.handler.codec.http.HttpResponseStatus
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

private fun response(context: RoutingContext, response: JsonObject = JsonObject(), httpStatus: HttpResponseStatus) =
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
  context.response()
    .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
    .setStatusCode(httpStatus.code())
    .end(
      if (!response.isEmpty) {
        response.toString()
      } else if (ex != null) {
        ex.printStackTrace()
        ex.message ?: httpStatus.reasonPhrase() ?: response.toString()
      } else
        response.toString()
    )
}

/**
 * ok 200
 */
fun ok(context: RoutingContext, response: JsonObject = JsonObject()) =
  response(context, response, HttpResponseStatus.OK)

/**
 * created 201
 */
fun created(context: RoutingContext, response: JsonObject = JsonObject()) =
  response(context, response, HttpResponseStatus.CREATED)

/**
 * no content 204
 */
fun noContent(context: RoutingContext, response: JsonObject = JsonObject()) =
  response(context, response, HttpResponseStatus.NO_CONTENT)

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


