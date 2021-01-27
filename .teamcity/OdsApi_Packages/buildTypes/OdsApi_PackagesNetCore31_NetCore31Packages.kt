package OdsApi_Packages.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.nuGetFeedCredentials
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.DotnetTestStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dotnetBuild
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dotnetPack
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dotnetRestore
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dotnetTest
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.nuGetPublish
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.VcsTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object OdsApi_PackagesNetCore31_NetCore31Packages : Template({
    name = "Net Core 3.1 Packages"

    artifactRules = """%nuget.pack.output%\*.nupkg"""
    buildNumberPattern = "%version%"

    params {
        param("pathToSolutionFile", "")
        param("pathToTestFile", "")
        param("version.major", "1")
        param("dotnet.pack.parameters", "-p:NoWarn=NU5123 -p:PackageId=%system.teamcity.buildConfName%%odsapi.package.suffix%")
        param("msbuild.exe", "")
    }

    vcs {
        cleanCheckout = true
    }

    steps {
        dotnetRestore {
            name = "Restore NuGet packages"
            id = "RUNNER_191"
            projects = "%pathToSolutionFile%"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        dotnetBuild {
            name = "Build Solution"
            id = "RUNNER_189"
            projects = "%pathToSolutionFile%"
            configuration = "%msbuild.buildConfiguration%"
            versionSuffix = "%version%"
            args = "--no-restore"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        dotnetTest {
            name = "Run Tests"
            id = "RUNNER_319"
            projects = "%pathToSolutionFile%"
            configuration = "%msbuild.buildConfiguration%"
            skipBuild = true
            args = "--no-build --filter TestCategory!=LocalOnly"
            logging = DotnetTestStep.Verbosity.Normal
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        dotnetPack {
            name = "Pack Prerelease version"
            id = "RUNNER_193"
            projects = "%pathToSolutionFile%"
            configuration = "%msbuild.buildConfiguration%"
            outputDir = "%nuget.pack.output%"
            skipBuild = true
            args = "-p:PackageVersion=%version% %dotnet.pack.parameters%"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        dotnetPack {
            name = "Pack Release version"
            id = "RUNNER_194"
            projects = "%pathToSolutionFile%"
            configuration = "%msbuild.buildConfiguration%"
            outputDir = "%nuget.pack.output%"
            skipBuild = true
            args = "-p:PackageVersion=%version.core% %dotnet.pack.parameters%"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        nuGetPublish {
            name = "Publish Prerelease Version"
            id = "RUNNER_195"
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            packages = """%nuget.pack.output%\*.%version%.nupkg"""
            serverUrl = "%azureArtifacts.feed.nuget%"
            apiKey = "%azureArtifacts.edFiBuildAgent.accessToken%"
        }
    }

    triggers {
        vcs {
            id = "vcsTrigger"
            quietPeriodMode = VcsTrigger.QuietPeriodMode.USE_DEFAULT
            triggerRules = "+:**"
            branchFilter = """
                +:*
                -:*-v2
            """.trimIndent()
        }
    }

    features {
        commitStatusPublisher {
            id = "BUILD_EXT_43"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "%github.accessToken%"
                }
            }
        }
        nuGetFeedCredentials {
            id = "BUILD_EXT_74"
            feedUrl = "%azureArtifacts.feed.nuget%"
            username = "%azureArtifacts.edFiBuildAgent.userName%"
            password = "%azureArtifacts.edFiBuildAgent.accessToken%"
        }
    }
})
