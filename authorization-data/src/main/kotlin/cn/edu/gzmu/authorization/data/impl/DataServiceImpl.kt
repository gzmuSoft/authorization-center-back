package cn.edu.gzmu.authorization.data.impl

import cn.edu.gzmu.authorization.common.service.JdbcRepository
import cn.edu.gzmu.authorization.data.DataService
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/22 下午1:17
 */
class DataServiceImpl(vertx: Vertx) : JdbcRepository(vertx), DataService {

  override fun retrieve(parentId: Long, resultHandler: Handler<AsyncResult<JsonArray>>): DataService {
    val promise = Promise.promise<JsonArray>()
    retrieve(RETRIEVE, JsonArray(listOf(parentId)), promise)
    promise.future().setHandler(resultHandler)
    return this
  }

}
