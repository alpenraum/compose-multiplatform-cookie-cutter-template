// Architecture rules for the sources of every module. Konsist parses files; it does not compile them.
plugins {
    alias(libs.plugins.kotlinJvm)
}

dependencies {
    testImplementation(libs.konsist)
    testImplementation(libs.kotlin.test)
}

tasks.test {
    useJUnitPlatform()
    // Konsist reads every module's sources; declaring them keeps up-to-date checks and the build cache honest.
    inputs
        .files(
            fileTree(rootDir) {
                include("**/src/**/*.kt")
                exclude("**/build/**")
            },
        ).withPathSensitivity(PathSensitivity.RELATIVE)
        .withPropertyName("projectSources")
}
