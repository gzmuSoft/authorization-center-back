dependencies {
  implementation(project(":authorization-common"))
  implementation("io.vertx:vertx-auth-oauth2")
}

vertx {
  mainVerticle = "io.github.jponge.vertx.boot.BootVerticle"
}
