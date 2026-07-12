plugins {
    java
    id("com.gradleup.shadow") version "9.1.0"
}

group = "eu.buildsouthwest"
version = "0.1-Alpha"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "alps-repo"
        url = uri("https://mvn.alps-bte.com/repository/alps-bte/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.72-stable")
    implementation("com.alpsbte.alpslib:alpslib-io:1.2.5")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.9")
    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation("space.arim.dazzleconf:dazzleconf-yaml:2.0.0-M1")
    compileOnly("org.projectlombok:lombok:1.18.40")
    annotationProcessor("org.projectlombok:lombok:1.18.40")

    testAnnotationProcessor("org.projectlombok:lombok:1.18.40")
    // Use JUnit Jupiter for testing
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

}

tasks.test {
    // Tell Gradle to use the JUnit Platform for running tests
    useJUnitPlatform()

    // Optional: Show test results in the console output
    testLogging {
        events("passed", "skipped", "failed")
    }
}

var targetJavaVersion = 25
java {
    var javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible()) {
        options.release.set(targetJavaVersion)
    }
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
    mergeServiceFiles()

    // explicit relocation (THIS is what you actually want)
    relocate("space.arim.dazzleconf", "eu.buildsouthwest.warp.shaded.dazzle")
    relocate("com.alpsbte", "eu.buildsouthwest.warp.shaded.alps")
}
