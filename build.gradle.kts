plugins {
  `maven-publish`
  kotlin("jvm") version "1.3.72"
}

group = "com.github.wumo"
version = "1.0.7"

repositories {
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))
  implementation(kotlin("reflect"))
  implementation("com.sun.mail:javax.mail:1.6.2")
  
  testImplementation("junit:junit:4.13")
  testImplementation(kotlin("test-junit"))
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "9"
  }
  compileTestKotlin {
    kotlinOptions.jvmTarget = "9"
  }
}

val kotlinSourcesJar by tasks

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["kotlin"])
      artifact(kotlinSourcesJar)
    }
  }
}