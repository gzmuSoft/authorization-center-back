package cn.edu.gzmu.authorization

import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.mysqlclient.mySQLConnectOptionsOf
import io.vertx.kotlin.sqlclient.poolOptionsOf
import io.vertx.mysqlclient.MySQLPool
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/17 上午9:16
 */
@ExtendWith(VertxExtension::class)
class MysqlVerticleTest {
  @Test
  fun verticle_deployed(vertx: Vertx, testContext: VertxTestContext) {
    val pool = MySQLPool.pool(
      vertx, mySQLConnectOptionsOf(
        port = 3306,
        host = "127.0.0.1",
        database = "gzmu-auth",
        user = "gzmu",
        password = "123456"
      ),
      poolOptionsOf(maxSize = 5)
    )
    pool.getConnection {
      if (it.succeeded()) {
        println("success")
        val connection = it.result()
        connection.query("SELECT * FROM sys_user") { res ->
          if (res.succeeded()) {
            println(res.result().size())
//            res.result().forEach(System.out::println)
            testContext.completeNow()
          } else {
            println("error")
            testContext.failNow(it.cause())
          }
        }
      } else {
        println("error")
        testContext.failNow(it.cause())
      }
    }
  }

}
