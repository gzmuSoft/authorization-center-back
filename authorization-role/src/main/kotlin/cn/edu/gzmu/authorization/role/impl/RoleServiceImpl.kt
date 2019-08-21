package cn.edu.gzmu.authorization.role.impl

import cn.edu.gzmu.authorization.common.service.JdbcRepository
import cn.edu.gzmu.authorization.role.Role
import cn.edu.gzmu.authorization.role.RoleService
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import java.time.LocalDateTime

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/21 下午2:18
 */
class RoleServiceImpl(vertx: Vertx) : JdbcRepository(vertx), RoleService {

  override fun enable(id: Long, enable: Boolean, resultHandlerUpdate: Handler<AsyncResult<Int>>): RoleService {
    val params = JsonArray(listOf(enable, id))
    val promise = Promise.promise<Int>()
    updateOne(ENABLE_ROLE, params, promise)
    promise.future().setHandler(resultHandlerUpdate)
    return this
  }

  override fun updateRole(role: Role, resultHandler: Handler<AsyncResult<Int>>): RoleService {
    val promise = Promise.promise<Int>()
    val params = JsonArray(
      listOf(
        role.name, role.spell, role.des, role.iconCls, role.parentId, role.sort,
        role.modifyUser, LocalDateTime.now(), role.remark, role.isEnable, role.id
      )
    )
    updateOne(UPDATE_ROLE, params, promise)
    promise.future().setHandler(resultHandler)
    return this
  }

  override fun createRole(role: Role, resultHandler: Handler<AsyncResult<Int>>): RoleService {
    val promise = Promise.promise<Int>()
    val params = JsonArray(
      listOf(
        role.name, role.spell, role.des, role.iconCls, role.parentId, role.sort, role.createUser,
        role.createTime, role.modifyUser, role.modifyTime, role.remark, role.isEnable
      )
    )
    updateOne(INSERT_ROLE, params, promise)
    promise.future().setHandler(resultHandler)
    return this
  }

  override fun retrieve(resultHandler: Handler<AsyncResult<JsonArray>>): RoleService {
    val promise = Promise.promise<JsonArray>()
    retrieve(RETRIEVE, JsonArray(), promise)
    promise.future().setHandler(resultHandler)
    return this
  }
}
