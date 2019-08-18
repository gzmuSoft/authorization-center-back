package cn.edu.gzmu.authorization.common.service

import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.kotlin.sqlclient.poolOptionsOf
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.Tuple
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.mysqlclient.mySQLConnectOptionsOf
import io.vertx.sqlclient.Row


/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/17 下午4:26
 */
@Deprecated(message = "由于 vertx-mysql-client 模块不能够使用而废弃",
  replaceWith = ReplaceWith("JdbcRepository"))
open class MysqlRepository(vertx: Vertx) {
  var mysqlPool: MySQLPool = MySQLPool.pool(
    vertx, mySQLConnectOptionsOf(
      port = 3306,
      host = "127.0.0.1",
      database = "gzmu-auth",
      user = "gzmu",
      password = "123456"
    ), poolOptionsOf(maxSize = 5)
  )

//  protected fun executeNoResult(sql: String, params: Tuple): Int {
//    val connection = mysqlPool.getConnectionAwait()
//    val rowSet = connection.preparedQueryAwait(sql, params)
//    connection.close()
//    return rowSet.first().getInteger(0)
//  }

  protected fun retrieveOne(sql: String, param: Tuple, promise: Promise<JsonObject>) {
    mysqlPool.getConnection { rs ->
      if (rs.failed()) {
        rs.cause().printStackTrace()
        promise.fail(rs.cause())
      }
      val connection = rs.result()
      connection.preparedQuery(sql, param) { ar ->
        if (ar.failed()) {
          ar.cause().printStackTrace()
          promise.fail(ar.cause())
        }
        val rowSet = ar.result()
        val columnsNames = rowSet.columnsNames()
        val result = JsonObject()
        if (rowSet.size() != 0) {
          val first: Row = rowSet.first()
          columnsNames.forEach {
            result.put(it, first.getValue(it))
          }
        }
        promise.complete(result)
      }
    }

//    val result = JsonArray()
//    rowSet.forEach {
//      val row = JsonObject()
//      columnsNames.forEach { column ->
//        row.put(column, it.getValue(column))
//      }
//      result.add(row)
//    }
  }
}
