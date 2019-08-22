package cn.edu.gzmu.authorization.data

import io.vertx.codegen.annotations.Fluent
import io.vertx.codegen.annotations.ProxyGen
import io.vertx.codegen.annotations.VertxGen
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonArray

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/22 下午1:15
 */
@VertxGen
@ProxyGen
interface DataService {

  @Fluent
  fun retrieve(parentId: String?, type: String?, resultHandler: Handler<AsyncResult<JsonArray>>): DataService

}
