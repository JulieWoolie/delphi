plugins {
  java
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(project(":api"))
  implementation(project(":dom"))
  implementation(project(":chimera"))
  implementation(project(":nlayout"))
}