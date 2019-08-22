package cn.edu.gzmu.authorization.data.impl

import cn.edu.gzmu.authorization.common.ORDER_BY_SORT
import cn.edu.gzmu.authorization.common.service.JdbcRepository
import cn.edu.gzmu.authorization.data.DataService
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/22 下午1:17
 */
class DataServiceImpl(vertx: Vertx) : JdbcRepository(vertx), DataService {

  override fun retrieve(parentId: String?, type: String?, resultHandler: Handler<AsyncResult<JsonArray>>): DataService {
    val promise = Promise.promise<JsonArray>()
    val params = JsonArray()
    var options = ""
    if (parentId != null && parentId.isNotBlank()) {
      options += "AND parentId = ?"
      params.add(parentId.toLong())
    }
    if (type != null && type.isNotBlank()) {
      options += "AND type = ?"
      params.add(type.toInt())
    }
    retrieve(RETRIEVE + options + ORDER_BY_SORT, params, promise)
    promise.future().setHandler(resultHandler)
    return this
  }

}
