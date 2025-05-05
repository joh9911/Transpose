package com.example.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *>
) {
    with(pluginManager) {
        apply("org.jetbrains.kotlin.plugin.compose")
    }

    commonExtension.run {
        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = libs.findVersion("composeCompiler").get().toString()
        }

        dependencies {
            val bom = libs.findLibrary("androidx.compose.bom").get()
            add("implementation", project.libs.findLibrary("androidx.activity.compose").get())
            add("implementation", platform(bom))
            add("androidTestImplementation", platform(bom))
            add("testImplementation", project.libs.findLibrary("junit").get())
            add("debugImplementation", libs.findLibrary("androidx.ui.tooling.preview").get())
            add("implementation", "androidx.compose.runtime:runtime-tracing:1.7.8")

        }
    }
}
