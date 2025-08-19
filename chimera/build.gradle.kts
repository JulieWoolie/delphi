plugins {
  java
}

version = "1.1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  compileOnly(project(":api"))
  testImplementation(project(":api"))
}