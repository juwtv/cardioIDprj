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
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            // cardioid: graph github
            url = uri("https://jitpack.io")
        }
    }
}

rootProject.name = "TestesComunicacao"
include(":app")
include(":segundatc")
include(":shared")
include(":cardioid_ble-release")