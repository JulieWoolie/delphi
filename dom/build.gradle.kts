plugins {
  `java-library`
}

version = "1.0.2-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  implementation(project(":api"))
  implementation(project(":chimera"))
  implementation("org.ccil.cowan.tagsoup:tagsoup:1.2.1")
}