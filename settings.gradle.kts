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
    }
}

rootProject.name = "LinkAttribution-AndroidSDK"
include(":app-sample")
include(":app-shared")
include(":library:core")
include(":library:network")
include(":data:users")
include(":data:products")
//include(":feature:shared")
//include(":feature:auth")
//include(":feature:onboarding")
//include(":feature:main")
//include(":feature:settings")
include(":data:shared")
include(":data:authentication")
include(":library:link-attribution")
