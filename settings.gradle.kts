pluginManagement {
    repositories {
        google()
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

rootProject.name = "CineCompare"

include(":app")

// Core modules
include(":core:core-ui")
include(":core:core-network")
include(":core:core-database")
include(":core:core-common")

// Aggregator modules
include(":aggregator:aggregator-base")
include(":aggregator:aggregator-bookmyshow")
include(":aggregator:aggregator-district")
include(":aggregator:aggregator-ticketnew")

// Feature modules
include(":feature:feature-home")
include(":feature:feature-movie-detail")
include(":feature:feature-compare")
include(":feature:feature-search")
include(":feature:feature-profile")
