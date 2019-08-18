package cn.edu.gzmu.authorization.common.service

import io.vertx.core.*
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider
import io.vertx.ext.sql.ResultSet
import io.vertx.ext.sql.UpdateResult
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/18 上午11:30
 */
open class JdbcRepository(vertx: Vertx) {
  val client = JDBCClient.createShared(vertx, json {
    obj(
      "jdbcUrl" to "jdbc:mysql://127.0.0.1:3306/gzmu-auth?useUnicode=true&characterEncoding=UTF-8&useOldAliasMetadataBehavior=true&autoReconnect=true&serverTimezone=UTC",
      "driverClassName" to "com.mysql.cj.jdbc.Driver",
      "maximumPoolSize" to 30,
      "username" to "gzmu",
      "password" to "123456",
      "provider_class" to HikariCPDataSourceProvider::class.java.name
    )
  })

  /**
   * 多参数条件查询
   */
  protected fun retrieve(
    sql: String,
    params: JsonArray,
    promise: Promise<JsonArray>,
    exclude: List<String> = listOf()
  ) {
    queryWithParam(sql, params, promise, Handler {
      if (it.succeeded()) {
        val rows = it.result().rows
        exclude.forEach { e -> rows.forEach { row -> row.remove(e) } }
        promise.complete(JsonArray(rows))
      } else promise.fail(it.cause())
    })
  }

  /**
   * 多参数条件查询
   */
  protected fun retrieveOne(
    sql: String,
    params: JsonArray,
    promise: Promise<JsonObject>,
    exclude: List<String> = listOf()
  ) {
    queryWithParam(sql, params, promise, Handler {
      if (it.succeeded()) {
        val rows = it.result().rows
        val result: JsonObject
        result = if (rows.size > 0) rows[0] else JsonObject()
        exclude.forEach { e -> result.remove(e) }
        promise.complete(result)
      } else promise.fail(it.cause())
    })
  }

  protected fun updateOne(
    sql: String,
    params: JsonArray,
    promise: Promise<Int>
  ) {
    executeOne(sql, params, promise, Handler {
      if (it.succeeded()) {
        val result = it.result()
        promise.complete(result.updated)
      } else {
        it.cause().printStackTrace()
        promise.fail(it.cause())
      }
    })
  }

  private fun executeOne(
    sql: String,
    params: JsonArray,
    promise: Promise<Int>,
    handler: Handler<AsyncResult<UpdateResult>>
  ) {
    client.getConnection { ar ->
      if (ar.succeeded()) {
        val sqlConnection = ar.result()
        sqlConnection.updateWithParams(sql, params, handler)
      } else {
        ar.cause().printStackTrace()
        promise.fail(ar.cause())
      }
    }
  }


  private fun queryWithParam(
    sql: String,
    params: JsonArray,
    promise: Promise<*>,
    handler: Handler<AsyncResult<ResultSet>>
  ) {
    client.getConnection { ar ->
      if (ar.succeeded()) {
        val sqlConnection = ar.result()
        sqlConnection.queryWithParams(sql, params, handler)
      } else {
        ar.cause().printStackTrace()
        promise.fail(ar.cause())
      }
    }
  }
}
