package cn.edu.gzmu.authorization

import io.vertx.core.Vertx
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/18 上午11:21
 */
@ExtendWith(VertxExtension::class)
class JdbcVerticleTest {
  @Test
  fun jdbcTest(vertx: Vertx, testContext: VertxTestContext) {
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
    client.getConnection { ar ->
      if (ar.failed()) {
        testContext.failNow(ar.cause())
      }
      val connection = ar.result()
      connection.query("SELECT * FROM sys_user") {
        if (it.succeeded()) {
          val result = it.result()
          result.rows.forEach(System.out::println)
          testContext.completeNow()
        } else {
          testContext.failNow(it.cause())
        }
      }
    }
  }
}
