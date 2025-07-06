val allureVersion = "2.25.0"
group = "org.example"
version = "1.0-SNAPSHOT"

plugins {
    java
    id("org.openapi.generator") version "7.2.0"
    id("io.qameta.allure") version "2.11.2"
}

repositories {
    mavenCentral()
    maven { url = uri("https://dl.bintray.com/viclovsky/maven") }
}

sourceSets {
    named("main") {
        java {
            srcDir("$buildDir/generated-sources/swagger/src/main/java")
        }
    }
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.36")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(platform("io.qameta.allure:allure-bom:$allureVersion"))
    testImplementation("io.qameta.allure:allure-junit5")

    implementation("com.github.viclovsky:swagger-coverage-rest-assured:1.4.5")

    implementation("io.rest-assured:rest-assured:5.5.5")
    implementation("org.projectlombok:lombok:1.18.36")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.3")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda:2.15.3")
    implementation("org.openapitools:jackson-databind-nullable:0.2.5")
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")

    implementation(group = "org.openapitools", name = "openapi-generator-gradle-plugin", version = "7.2.0")
    implementation(group = "com.google.code.gson", name = "gson", version = "2.7")
    implementation("joda-time:joda-time:2.12.1")


    testCompileOnly("org.projectlombok:lombok:1.18.36")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.36")

}

tasks.test {
    useJUnitPlatform()

}

val openApiGenerateFront by tasks.registering(org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
    generatorName.set("java")
    remoteInputSpec.set("https://sb2frontend-altenar2-stage.biahosted.com/swagger/v1/swagger.json")
    outputDir.set("$buildDir/generated-sources/swagger")
    invokerPackage.set("")
    templateDir.set("$projectDir/src/test/resources/templates")
    apiPackage.set("com.altenar.sb2.frontend.api")
    modelPackage.set("com.altenar.sb2.frontend.model")
    importMappings.set(emptyMap())
    configOptions.set(
        mapOf(
            "dateLibrary" to "joda",
            "serializationLibrary" to "jackson",
            "interfaceOnly" to "false",
            "additionalModelTypeAnnotations" to "@lombok.Data",
            "useJakartaEe" to "true"
        )
    )
    library.set("rest-assured")
    generateApiTests.set(false)
    generateApiDocumentation.set(false)
    generateModelTests.set(false)
    generateModelDocumentation.set(false)

    globalProperties.set(mapOf("models" to ""))
}

val openApiGenerateAdmin by tasks.registering(org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
    generatorName.set("java")
    remoteInputSpec.set("https://sb2admin-altenar2-stage.biahosted.com/swagger/v1/swagger.json")
    outputDir.set("$buildDir/generated-sources/swagger")
    invokerPackage.set("")
    templateDir.set("$projectDir/src/test/resources/templates")
    apiPackage.set("com.altenar.sb2.admin.api")
    modelPackage.set("com.altenar.sb2.admin.model")
    importMappings.set(emptyMap())
    configOptions.set(
        mapOf(
            "dateLibrary" to "joda",
            "serializationLibrary" to "jackson",
            "interfaceOnly" to "false",
            "additionalModelTypeAnnotations" to "@lombok.Data",
            "useJakartaEe" to "true"
        )
    )
    library.set("rest-assured")
    generateApiTests.set(false)
    generateApiDocumentation.set(false)
    generateModelTests.set(false)
    generateModelDocumentation.set(false)

    globalProperties.set(mapOf("models" to ""))
}

tasks.register("swaggerCoverageAdmin") {
    doLast {
        exec {
            workingDir = file("$projectDir")
            commandLine("cmd", "/c", "swagger-coverage-commandline",
                "-s", "src/test/resources/swagger/swagger-admin.json",
                "-i", "swagger-coverage-output",
                "-q")
        }
    }
}

tasks.register("swaggerCoverageFront") {
    doLast {
        exec {
            workingDir = file("$projectDir")
            commandLine("cmd", "/c", "swagger-coverage-commandline",
                "-s", "src/test/resources/swagger/swagger-front.json",
                "-i", "swagger-coverage-output",
                "-q")
        }
    }
}

val cleanBuildPublish by tasks.registering(GradleBuild::class) {
    setTasks(listOf("openApiGenerateFront", "openApiGenerateAdmin"))
}

tasks.compileJava {
    dependsOn(cleanBuildPublish)
}