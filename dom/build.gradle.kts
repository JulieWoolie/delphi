plugins {
  `java-library`
  id("io.freefair.lombok") version "8.6"
}

version = "1.0.1-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  implementation(project(":api"))
  implementation(project(":chimera"))
}