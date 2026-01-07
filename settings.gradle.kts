pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        // Nếu cần thêm repository khác
        maven { url = uri("https://jitpack.io") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven {
            name = "TarsosDSP repository"
            url = uri("https://mvn.0110.be/releases")
        }
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven {
            url = uri("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")
        }
        maven {
            url = uri("https://artifact.bytedance.com/repository/pangle/")
        }
        maven{
            url =  uri("https://maven.pkg.github.com/vinhvox/PandaSdk_2026")
            credentials{
                username = "vinhvox"
                password = "asdfqwe"
            }
        }
    }
}

rootProject.name = "Base Compose 2026"
include(":app")
 