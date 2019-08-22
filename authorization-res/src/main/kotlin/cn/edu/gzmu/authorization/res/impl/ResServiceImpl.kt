package cn.edu.gzmu.authorization.res.impl

import cn.edu.gzmu.authorization.common.service.JdbcRepository
import cn.edu.gzmu.authorization.res.ResService
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/21 下午7:30
 */
class ResServiceImpl(vertx: Vertx) : JdbcRepository(vertx), ResService {

  override fun enable(id: Long, enable: Boolean, resultHandlerUpdate: Handler<AsyncResult<Int>>): ResService {
    val params = JsonArray(listOf(enable, id))
    val promise = Promise.promise<Int>()
    updateOne(ENABLE_RES, params, promise)
    promise.future().setHandler(resultHandlerUpdate)
    return this
  }

  override fun retrieve(resultHandlerNonEmpty: Handler<AsyncResult<JsonArray>>): ResService {
    val promise = Promise.promise<JsonArray>()
    retrieve(RETRIEVE, JsonArray(), promise)
    promise.future().setHandler(resultHandlerNonEmpty)
    return this
  }

}
