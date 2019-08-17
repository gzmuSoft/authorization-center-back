import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.3.30"
  kotlin("kapt") version "1.3.30"
  id("io.vertx.vertx-plugin") version "0.8.0"
}

group = "cn.edu.gzmu"
version = "1.0.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8
val vertxVersion = "3.8.0"

allprojects {
  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "org.jetbrains.kotlin.kapt")
  apply(plugin = "io.vertx.vertx-plugin")

  repositories {
    maven("http://maven.aliyun.com/nexus/content/groups/public/")
    mavenCentral()
    jcenter()
  }
  project.project.extra["vertxVersion"] = "3.8.0"

  tasks.withType<KotlinCompile> {
    kotlinOptions {
      freeCompilerArgs = listOf("-Xjsr305=strict")
      jvmTarget = "1.8"
    }
  }

  tasks.test {
    useJUnitPlatform()
  }

  dependencies {
    implementation("io.vertx:vertx-web")
    implementation("io.vertx:vertx-web-client")
    implementation("io.vertx:vertx-lang-kotlin")
    implementation("io.vertx:vertx-lang-kotlin-coroutines")
    implementation("io.vertx:vertx-service-discovery")
    implementation("io.vertx:vertx-service-proxy")
    implementation("io.vertx:vertx-circuit-breaker")
    implementation("io.vertx:vertx-config")
    implementation("io.vertx:vertx-mysql-client")
    implementation("io.github.jponge:vertx-boot:1.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0-RC2")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.vertx:vertx-codegen")
    implementation("com.google.guava:guava:28.0-jre")
    implementation("org.slf4j:slf4j-api:2.0.0-alpha0")
    implementation("ch.qos.logback:logback-core:1.3.0-alpha4")
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha4")
    kapt("io.vertx:vertx-service-proxy:$vertxVersion:processor")
    kapt("io.vertx:vertx-codegen:$vertxVersion:processor")
    testImplementation("io.vertx:vertx-junit5")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
  }

  kapt{
    arguments {
      arg("codegen.output", project.file("src/main/generated").path)
      arg("kapt.kotlin.generated", project.file("src/main/generated").path)
    }
    javacOptions {
      option("-AoutputDirectory", project.file("src/main/generated").path)
      option("-Acodegen.output", project.file("src/main/generated").path)
    }
  }

}

vertx {
  mainVerticle = "io.github.jponge.vertx.boot.BootVerticle"
}
