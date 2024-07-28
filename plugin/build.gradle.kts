plugins {
  java
  id("io.freefair.lombok") version "8.6"
}

repositories {
  mavenCentral()
  maven("https://libraries.minecraft.net/")
}

dependencies {
  implementation(project(":dom"))
  implementation(project(":api"))

  compileOnly("net.forthecrown:grenadier:2.5.2")
  testImplementation("net.forthecrown:grenadier:2.5.2")
}