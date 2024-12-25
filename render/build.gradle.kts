plugins {
  java
  id("io.freefair.lombok") version "8.6"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(project(":api"))
  implementation(project(":dom"))
  implementation(project(":chimera"))

  implementation("org.lwjgl:lwjgl-yoga:3.3.5")
  implementation("org.lwjgl:lwjgl-yoga::natives-windows")

}