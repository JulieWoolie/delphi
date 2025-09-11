plugins {
  `java-library`
}

version = "1.2.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  implementation(project(":api"))
  implementation(project(":chimera"))
  compileOnly("org.ccil.cowan.tagsoup:tagsoup:1.2.1")
  testImplementation("org.ccil.cowan.tagsoup:tagsoup:1.2.1")
}