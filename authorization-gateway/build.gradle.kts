dependencies {
  implementation(project(":authorization-common"))
}

vertx {
  mainVerticle = "io.github.jponge.vertx.boot.BootVerticle"
}
