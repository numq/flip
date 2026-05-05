plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    iosX64()

    iosArm64()

    iosSimulatorArm64()

    sourceSets {
        val iosMain by creating {
            dependsOn(commonMain.get())

            dependencies {
                implementation(projects.entrypoint)
            }
        }

        val iosX64Main by getting { dependsOn(iosMain) }

        val iosArm64Main by getting { dependsOn(iosMain) }

        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }
    }

    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach {
        it.binaries.framework {
            baseName = "flip"
            isStatic = true
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}