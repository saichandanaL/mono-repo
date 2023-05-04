import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetClean
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetPublish
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetRestore

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

    buildType(MonoRepoApis)
}

object MonoRepoApis : BuildType({
    name = "Mono-repo-apis"

    artifactRules = "+:./build => %env%_%branch%_%apiname%_%version%_%build.number%.zip"

    params {
        text("apiname", "ASPNETCoreWebAPI", display = ParameterDisplay.PROMPT, allowEmpty = true)
        text("env", "dev", display = ParameterDisplay.PROMPT, allowEmpty = true)
        text("branch", "main", display = ParameterDisplay.PROMPT, allowEmpty = true)
        text("version", "v1", display = ParameterDisplay.PROMPT, allowEmpty = true)
        text("solution_file", "ASPNETCoreWebAPI/SampleWebApiAspNetCore.sln", display = ParameterDisplay.PROMPT, allowEmpty = false)
        text("project_file_endswith_.csproj", "ASPNETCoreWebAPI/SampleWebApiAspNetCore/SampleWebApiAspNetCore.csproj", display = ParameterDisplay.PROMPT, allowEmpty = false)
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        dotnetClean {
            name = "clean all files"
            projects = "%project_file_endswith_.csproj%"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        dotnetRestore {
            name = "Restore dependencies"
            projects = "%solution_file%"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        dotnetPublish {
            name = "Publish"
            projects = "%project_file_endswith_.csproj%"
            outputDir = "./build"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
    }
})
