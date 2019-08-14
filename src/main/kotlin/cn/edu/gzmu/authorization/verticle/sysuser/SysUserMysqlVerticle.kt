package cn.edu.gzmu.authorization.verticle.sysuser

import cn.edu.gzmu.authorization.verticle.web.MysqlVerticle
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.jsonArrayOf
import io.vertx.kotlin.sqlclient.getConnectionAwait
import io.vertx.kotlin.sqlclient.queryAwait
import kotlinx.coroutines.launch

/**
 * SysUser Mysql Verticle
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/14 下午1:48
 */
class SysUserMysqlVerticle : MysqlVerticle() {
  override suspend fun handle(body: JsonObject): JsonObject {
    TODO("jdbc get method wait to select....")
  }
}
