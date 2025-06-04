dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        mavenCentral()
    }
}

rootProject.name = "tacticai-spring-msa-mono"

include("common-module")
include("core-service")
include("game-service")
include("gate-way-service")