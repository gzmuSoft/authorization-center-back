dependencies {
  implementation("mysql:mysql-connector-java:8.0.17")
  implementation("com.zaxxer:HikariCP:3.3.1")
  implementation("io.vertx:vertx-auth-jwt")
  implementation("io.vertx:vertx-jdbc-client")
  implementation("io.vertx:vertx-auth-oauth2")
  implementation("io.vertx:vertx-mysql-client")
  implementation("io.vertx:vertx-service-discovery-backend-redis")
}

vertx {
  mainVerticle = "io.github.jponge.vertx.boot.BootVerticle"
}
