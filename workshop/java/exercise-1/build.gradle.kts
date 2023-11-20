/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    `java`
    `application`
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

application {
    mainClass.set("sfw.example.dbesdkworkshop.App")
}

dependencies {
    api("com.moandjiezana.toml:toml4j:0.7.2")
    api("software.amazon.awssdk:dynamodb:2.20.69")
    // https://mvnrepository.com/artifact/io.quarkus/quarkus-picocli
    api("io.quarkus:quarkus-picocli:3.0.3.Final")
    api("software.amazon.cryptools:AmazonCorrettoCryptoProvider:1.2.0")
    api("javax.xml.bind:jaxb-api:2.3.1")
    // BEGIN EXERCISE 1 STEP 2
    implementation(platform("software.amazon.awssdk:bom:2.19.1"))
    implementation("software.amazon.cryptography:aws-database-encryption-sdk-dynamodb:3.1.2")
    implementation("software.amazon.cryptography:aws-cryptographic-material-providers:1.0.2")
    implementation("software.amazon.awssdk:kms")
    // END EXERCISE 1 STEP 2
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.3.1")
    testImplementation("org.mockito:mockito-core:3.1.0")
}

group = "sfw.example.dbesdkworkshop"
version = "1.0-SNAPSHOT"
description = "dbesdkworkshop"
java.sourceCompatibility = JavaVersion.VERSION_1_8

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

tasks {
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(listOf("compileJava", "processResources")) // We need this for Gradle optimization to work
        archiveClassifier.set("standalone") // Naming the jar
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) } // Provided we set it up in the application plugin configuration
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
                .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
        {
            exclude("META-INF/*.SF")
            exclude("META-INF/*.DSA")
            exclude("META-INF/*.RSA")
        }
    }
    build {
        dependsOn(fatJar) // Trigger fat jar creation during build
    }
}