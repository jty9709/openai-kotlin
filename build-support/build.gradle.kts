plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(kotlin("gradle-plugin", libs.versions.kotlin.get()))
    compileOnly(kotlin("gradle-plugin-api", libs.versions.kotlin.get()))
}

gradlePlugin {
    plugins {
        create("build-support") {
            id = "build-support"
            implementationClass = "BuildSupport"
        }
    }
}
