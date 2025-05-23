plugins {
  java
}

group = "net.arcadiusmc"

repositories {
  mavenCentral()
}

subprojects {
  apply(plugin = "java")

  group = rootProject.group

  repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
  }

  dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")

    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.3")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("com.google.guava:guava:33.2.1-jre")
    testImplementation("org.slf4j:slf4j-api:2.0.13")
    testImplementation("org.slf4j:slf4j-simple:2.0.13")
    testImplementation("it.unimi.dsi:fastutil:8.5.13")
    testImplementation("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")

    if (name != "api" && name != "test-plugin") {
      val lombok = "org.projectlombok:lombok:1.18.38"
      compileOnly(lombok)
      annotationProcessor(lombok)
      testCompileOnly(lombok)
      testAnnotationProcessor(lombok)
    }
  }

  tasks {
    test {
      useJUnitPlatform()
      systemProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")
      systemProperty("joml.format", "false")
    }
    compileJava {
      options.release = 21
      options.encoding = Charsets.UTF_8.name()
    }
    javadoc {
      options.encoding = Charsets.UTF_8.name()
    }
  }

  java {

  }
}