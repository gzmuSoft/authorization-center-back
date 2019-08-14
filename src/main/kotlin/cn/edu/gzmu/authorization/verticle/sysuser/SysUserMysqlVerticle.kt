package cn.edu.gzmu.authorization.verticle.sysuser

import cn.edu.gzmu.authorization.model.constant.*
import cn.edu.gzmu.authorization.model.helper.Page
import cn.edu.gzmu.authorization.util.getCondition
import cn.edu.gzmu.authorization.verticle.web.MysqlVerticle
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.jsonArrayOf
import io.vertx.kotlin.ext.sql.getConnectionAwait
import io.vertx.kotlin.ext.sql.queryAwait
import io.vertx.kotlin.ext.sql.queryWithParamsAwait
import io.vertx.kotlin.sqlclient.getConnectionAwait
import io.vertx.kotlin.sqlclient.queryAwait
import kotlinx.coroutines.launch
import java.util.stream.Collectors

/**
 * SysUser Mysql Verticle
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/14 下午1:48
 */
class SysUserMysqlVerticle : MysqlVerticle() {
  override suspend fun handle(body: JsonObject): JsonObject {
    val params = JsonArray()
    val page = body.getString(PAGE, PAGE_START).toInt()
    val size = body.getString(SIZE, PAGE_SIZE).toInt()
    val condition = body.getCondition(NAME)
    params.add(condition).add(condition).add(condition)
      .add((page - 1) * size).add(size)
    val connection = client.getConnectionAwait()
    val resultSet = connection.queryWithParamsAwait(QUERY_PAGE, params)
    params.remove(4)
    params.remove(3)
    val count = connection.queryWithParamsAwait(QUERY_COUNT, params).results[0].getInteger(0)
    val result = resultSet.rows.toCollection(ArrayList())
    return Page(count, page, size, result.size, JsonArray(result)).toJson()
  }
}
