plugins {
  `java-library`
  id("io.freefair.lombok") version "8.6"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(project(":api"))
  implementation(project(":chimera"))
}