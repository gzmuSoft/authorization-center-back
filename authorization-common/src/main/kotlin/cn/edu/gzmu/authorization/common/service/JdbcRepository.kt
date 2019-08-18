package cn.edu.gzmu.authorization.common.service

import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider
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

  protected fun retrieveArray(sql: String, params: JsonArray, promise: Promise<JsonArray>) {
    client.getConnection { ar ->
      if (ar.succeeded()) {
        val sqlConnection = ar.result()
        sqlConnection.queryWithParams(sql, params) {
          if (it.succeeded()) {
            val result = it.result()
            promise.complete(JsonArray(result.rows))
          } else {
            promise.fail(it.cause())
          }
        }
      } else {
        promise.fail(ar.cause())
      }
    }
  }
}
