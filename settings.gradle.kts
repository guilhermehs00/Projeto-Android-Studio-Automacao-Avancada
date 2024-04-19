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
        google()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven{
            url = uri("https://maven.pkg.github.com/guilhermehs00/Projeto-Android-Studio-Automacao-Avancada")
            credentials{
                username = "guilhermehs00"
                password = "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"
            }
        }
    }

}


rootProject.name = "Projeto Aut Avancada"
include(":app")
include(":mylibrary")
