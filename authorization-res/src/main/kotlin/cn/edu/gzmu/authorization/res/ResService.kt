package cn.edu.gzmu.authorization.res

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
 * @date 2019/8/21 下午7:27
 */
@VertxGen
@ProxyGen
interface ResService {
  @Fluent
  fun enable(id: Long, enable: Boolean, resultHandlerUpdate: Handler<AsyncResult<Int>>): ResService

  @Fluent
  fun retrieve(resultHandlerNonEmpty: Handler<AsyncResult<JsonArray>>): ResService
}
