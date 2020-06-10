import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("java")
    id("org.jetbrains.kotlin.multiplatform") version "1.4-M2-eap-70"
    id("org.jetbrains.dokka") version "0.11.0-SNAPSHOT"
}
group = "org.jetbrains.dokka"
version = "1.0-SNAPSHOT"
repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-dev")
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    mavenCentral()
    mavenLocal()
    jcenter()
}
dependencies {
    implementation("org.javatuples:javatuples:1.2")
    implementation("log4j:log4j:1.2.17")
    implementation("org.graphstream:gs-core:1.3")
    implementation("org.graphstream:gs-ui:1.3")
    implementation("org.graphstream:gs-algo:1.3")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.4.2")
    testRuntime(
            "org.junit.jupiter:junit-jupiter-engine:5.4.2"
    )
    testRuntime(
            "org.junit.vintage:junit-vintage-engine:5.4.2"
    )
    dokkaPlugins("org.jetbrains.dokka:javadoc-plugin:0.11.0-SNAPSHOT")
}

tasks {
    val dokkaOutputDir = "dokka"
    val clean = getByName("clean", Delete::class) {
        delete(rootProject.buildDir)
        delete(dokkaOutputDir)
    }
    val dokka by getting(DokkaTask::class) {
        dependsOn(clean)
        outputDirectory = dokkaOutputDir
        outputFormat = "javadoc"
        dokkaSourceSets {
            val main by creating {
                sourceRoot {
                    path = "${rootProject.rootDir.absolutePath}/src/main/java"
                    classpath = sourceSets.getByName("main").compileClasspath.map { it.path }
                }
            }
        }
    }
}
