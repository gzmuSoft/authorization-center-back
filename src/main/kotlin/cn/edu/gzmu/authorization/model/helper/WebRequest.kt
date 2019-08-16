package cn.edu.gzmu.authorization.model.helper

import cn.edu.gzmu.authorization.model.constant.*
import io.vertx.core.json.JsonObject

/**
 * Web Request
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/15 下午9:49
 */
class WebRequest(private val request: JsonObject) {
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
