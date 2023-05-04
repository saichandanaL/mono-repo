import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetBuild
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetRestore
import jetbrains.buildServer.configs.kotlin.projectFeatures.activeStorage
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2022.10"

project {

    vcsRoot(OrderApi)

    buildType(MonoRepo2)
    buildType(Build)

    features {
        feature {
            id = "PROJECT_EXT_4"
            type = "storage_settings"
            param("storage.artifactory.repository.type", "local")
            param("storage.artifactory.url", "https://teamjfrog.jfrog.io/artifactory")
            param("storage.artifactory.username", "sai")
            param("storage.type", "Artifacactory_storage")
            param("storage.artifactory.repository.key", "mono-repo-release")
            param("secure:storage.artifactory.password", "credentialsJSON:7c5b9108-0240-4971-8b8c-c08791903b6c")
        }
        activeStorage {
            id = "PROJECT_EXT_5"
            activeStorageID = "PROJECT_EXT_4"
        }
    }
}

object Build : BuildType({
    name = "Build"

    artifactRules = "+:./build=> %env%_%branch%_%apiname%_%version%_%build.number%.zip"

    params {
        text("apiname", "samplewebapi", display = ParameterDisplay.PROMPT, allowEmpty = true)
        text("env", "local", display = ParameterDisplay.PROMPT, allowEmpty = true)
        text("branch", "dev", display = ParameterDisplay.PROMPT, allowEmpty = true)
        text("version", "v1", display = ParameterDisplay.PROMPT, allowEmpty = true)
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        dotnetBuild {
            name = "Build api"
            projects = "ASPNETCoreWebAPI/SampleWebApiAspNetCore.sln"
            outputDir = "./build"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})

object MonoRepo2 : BuildType({
    name = "Mono repo 2"

    vcs {
        root(OrderApi)
    }

    steps {
        dotnetRestore {
            name = "Restore"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        dotnetBuild {
            name = "Build"
            outputDir = "./build"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
    }
})

object OrderApi : GitVcsRoot({
    name = "order-api"
    url = "https://github.com/saichandanaL/mono-repo.git"
    branch = "main"
    authMethod = password {
        userName = "saichandanaL"
        password = "credentialsJSON:0e0b4400-2b43-484c-a5e9-9adb7d358914"
    }
})
