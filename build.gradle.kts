plugins {
  java
}

repositories {
  mavenCentral()
}

subprojects {
  apply(plugin = "java")

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
  }

  tasks {
    test {
      useJUnitPlatform()
      systemProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")
    }
  }

  java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
  }
}