package cn.edu.gzmu.authorization.role

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
 * @date 2019/8/21 下午2:16
 */
@VertxGen
@ProxyGen
interface RoleService {

  @Fluent
  fun retrieve(resultHandler: Handler<AsyncResult<JsonArray>>): RoleService

  @Fluent
  fun createRole(role: Role, resultHandler: Handler<AsyncResult<Int>>): RoleService

  @Fluent
  fun updateRole(role: Role, resultHandler: Handler<AsyncResult<Int>>): RoleService

  @Fluent
  fun enable(id: Long, enable: Boolean, resultHandlerUpdate: Handler<AsyncResult<Int>>): RoleService

}
