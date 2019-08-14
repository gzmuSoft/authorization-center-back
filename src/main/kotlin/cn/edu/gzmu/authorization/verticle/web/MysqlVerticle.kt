package cn.edu.gzmu.authorization.verticle.web

import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.mysqlclient.mySQLConnectOptionsOf
import io.vertx.kotlin.mysqlclient.queryAwait
import io.vertx.kotlin.sqlclient.getConnectionAwait
import io.vertx.kotlin.sqlclient.poolOptionsOf
import kotlinx.coroutines.launch

/**
 * Jdbc Verticle
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/14 下午1:14
 */
abstract class MysqlVerticle : CoroutineVerticle() {

  protected lateinit var client: JDBCClient

  override suspend fun start() {
    client = JDBCClient.createShared(vertx, json {
      obj(
        "jdbcUrl" to "jdbc:mysql://127.0.0.1:3306/gzmu-auth?useUnicode=true&characterEncoding=UTF-8&useOldAliasMetadataBehavior=true&autoReconnect=true&serverTimezone=UTC",
        "driverClassName" to "com.mysql.cj.jdbc.Driver",
        "maximumPoolSize" to 30,
        "username" to "root",
        "password" to "123456",
        "provider_class" to HikariCPDataSourceProvider::class.java.name
      )
    })

    client.getConnection { res ->
      if (res.succeeded()) {
        start(this::class.java.asSubclass(this::class.java).name)
        println("${this::class.java.asSubclass(this::class.java).name} is deployed")
      } else {
        println("error")
      }
    }
  }

  // -----------------------------------------------------------------------------------------------------------------
  private fun start(address: String) {
    vertx.eventBus().consumer<JsonObject>(address) {
      val reqJson = it.body()
      launch {
        it.reply(handle(reqJson))
      }
    }
  }

  abstract suspend fun handle(body: JsonObject): JsonObject
}
