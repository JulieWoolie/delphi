plugins {
  java
}

version = "0.1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  implementation(project(":api"))
  implementation(project(":dom"))
  implementation(project(":chimera"))

//  compileOnly("org.graalvm.polyglot:polyglot:24.1.2")
//  compileOnly("org.graalvm.polyglot:js-community:24.1.2")

  compileOnly("org.graalvm.js:js-language:24.1.2")
  testImplementation("org.graalvm.js:js-language:24.1.2")

  annotationProcessor("org.graalvm.truffle:truffle-dsl-processor:24.1.2")
  testAnnotationProcessor("org.graalvm.truffle:truffle-dsl-processor:24.1.2")
}