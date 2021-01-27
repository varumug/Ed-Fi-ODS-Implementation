package OdsApi_Packages.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.FileContentReplacer
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.freeDiskSpace
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.nuGetFeedCredentials
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.replaceContent
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dotnetBuild
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dotnetRestore
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.nuGetPack
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.nuGetPublish
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.VcsTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object OdsApi_Packages_EdFiExtensionsTemplate : Template({
    name = "EdFi-Extensions-Template"

    artifactRules = """%nuget.pack.output%\%PackageId%.%version.core%*.nupkg"""
    buildNumberPattern = "%version%"

    params {
        param("script.initdev.parameters", "-NoDeploy -UsePlugins")
        param("script.create.template.parameters", """-samplePath "./Ed-Fi-Standard/Descriptors/"  -noExtensions""")
        param("git.branch.default", "main")
        param("nuget.pack.files", """Ed-Fi-Extensions\Extensions\EdFi.Ods.Extensions.Sample\EdFi.Ods.Extensions.Sample.nuspec""")
        param("nuget.package.name", "EdFi.Ods.Extensions.Sample")
        param("nuget.pack.properties", """
            id=%PackageId%
            title=%PackageId%
            description=%nuget.package.description%
        """.trimIndent())
        param("PackageId", "EdFi%odsapi.package.suffix%.Ods.Extensions.Sample")
        param("nuget.package.description", "EdFi.Ods.Extensions.Sample")
        param("projectFolder", """Ed-Fi-Extensions\Extensions\%extension.project%""")
    }

    vcs {
        root(_Self.vcsRoots.EdFiOds, "+:. => Ed-Fi-ODS")
        root(_Self.vcsRoots.EdFiOdsImplementation, "+:. => Ed-Fi-ODS-Implementation")
        root(_Self.vcsRoots.EdFiExtensions, "+:Extensions/%extension.project% => Ed-Fi-Extensions/Extensions/%extension.project%", "+:LICENSE.txt => LICENSE.txt")

        cleanCheckout = true
    }

    steps {
        dotnetRestore {
            name = "Restore NuGet packages"
            id = "RUNNER_447"
            projects = "%projectFolder%"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        powerShell {
            name = "Run CodeGen"
            id = "RUNNER_458"
            formatStderrAsError = true
            scriptMode = script {
                content = """
                    ${'$'}ErrorActionPreference = 'Stop'

                    . "%teamcity.build.checkoutDir%\%script.initdev%"
                    Invoke-CodeGen -Engine SQLServer -IncludePlugins
                """.trimIndent()
            }
        }
        dotnetBuild {
            name = "Build Extension Project"
            id = "RUNNER_398"
            projects = "%projectFolder%"
            configuration = "%msbuild.buildConfiguration%"
            versionSuffix = "%version%"
            args = "--no-restore"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        nuGetPack {
            name = "Pack Prerelease version"
            id = "RUNNER_197"
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            paths = "%nuget.pack.files%"
            version = "%version%"
            outputDir = "%nuget.pack.output%"
            cleanOutputDir = true
            publishPackages = true
            properties = """
                %nuget.pack.properties.default%
                %nuget.pack.properties%
            """.trimIndent()
            args = "%nuget.pack.parameters%"
        }
        nuGetPack {
            name = "Pack Release version"
            id = "RUNNER_404"
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            paths = "%nuget.pack.files%"
            version = "%version.core%"
            outputDir = "%nuget.pack.output%"
            cleanOutputDir = false
            publishPackages = true
            properties = """
                %nuget.pack.properties.default%
                %nuget.pack.properties%
            """.trimIndent()
            args = "%nuget.pack.parameters%"
        }
        nuGetPublish {
            name = "Publish Prerelease Version"
            id = "RUNNER_199"
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            packages = """%nuget.pack.output%\%PackageId%.%version%.nupkg"""
            serverUrl = "%azureArtifacts.feed.nuget%"
            apiKey = "%azureArtifacts.edFiBuildAgent.accessToken%"
        }
    }

    triggers {
        vcs {
            id = "vcsTrigger"
            quietPeriodMode = VcsTrigger.QuietPeriodMode.USE_CUSTOM
            quietPeriod = 120
            branchFilter = "+:<default>"
        }
    }

    features {
        replaceContent {
            id = "BUILD_EXT_58"
            fileRules = "Ed-Fi-Extensions/Extensions/%extension.project%/*.nuspec"
            pattern = """(?<=<id>)(.*?)(EdFi)(?=\b.*</id>)"""
            replacement = "%odsapi.package.suitenumber%"
            encoding = FileContentReplacer.FileEncoding.UTF_8
            customEncodingName = "UTF-8"
        }
        freeDiskSpace {
            id = "jetbrains.agent.free.space"
            requiredSpace = "%build.feature.freeDiskSpace%"
            failBuild = true
        }
        commitStatusPublisher {
            id = "BUILD_EXT_44"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "%EdFiBuildAgent-GitHubKeyPasshrase%"
                }
            }
        }
        nuGetFeedCredentials {
            id = "BUILD_EXT_75"
            feedUrl = "%azureArtifacts.feed.nuget%"
            username = "%azureArtifacts.edFiBuildAgent.userName%"
            password = "%azureArtifacts.edFiBuildAgent.accessToken%"
        }
    }
})
