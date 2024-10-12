plugins {
  java
  id("io.freefair.lombok") version "8.6"
}

version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  compileOnly(project(":api"))
  testImplementation(project(":api"))
}