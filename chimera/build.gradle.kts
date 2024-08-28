plugins {
  java
  id("io.freefair.lombok") version "8.6"
}

repositories {
  mavenCentral()
}

dependencies {
  compileOnly(project(":api"))
  testImplementation(project(":api"))
}