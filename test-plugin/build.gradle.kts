plugins {
  id("java")
  kotlin("jvm") version "2.0.0"
}

group = "net.arcadiusmc"
version = "1.0.0"

repositories {
  mavenCentral()
}

dependencies {
  compileOnly(project(":api"))
  implementation(kotlin("stdlib"))
}