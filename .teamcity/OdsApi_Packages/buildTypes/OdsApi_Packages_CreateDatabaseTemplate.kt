package OdsApi_Packages.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.freeDiskSpace
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.nuGetFeedCredentials
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.PowerShellStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.nuGetPack
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.nuGetPublish
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.VcsTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object OdsApi_Packages_CreateDatabaseTemplate : Template({
    name = "Create Database Template"

    artifactRules = "%nuget.pack.output%/** => ."
    buildNumberPattern = "%version%"

    params {
        param("nuget.pack.properties", """
            id=%nuget.package.name%%odsapi.package.suffix%
            title=%nuget.package.name%%odsapi.package.suffix%
            description=%nuget.package.description%
        """.trimIndent())
        param("script.create.template.parameters", """-samplePath "./Ed-Fi-Standard/"  -noExtensions""")
    }

    vcs {
        root(_Self.vcsRoots.EdFiOds, "+:. => Ed-Fi-ODS")
        root(_Self.vcsRoots.EdFiOdsImplementation, "+:. => Ed-Fi-ODS-Implementation")
        root(OdsApi_Packages.vcsRoots.OdsApi_Packages_EdFiStandard, "+:. => Ed-Fi-Standard")

        cleanCheckout = true
    }

    steps {
        powerShell {
            name = "Remove-EdFiDatabases"
            id = "RUNNER_395"
            formatStderrAsError = true
            scriptMode = script {
                content = """
                    ${'$'}ErrorActionPreference = 'Stop'

                    Import-Module "%teamcity.build.checkoutDir%\%script.build.management%"

                    Remove-EdFiDatabases -Force
                """.trimIndent()
            }
        }
        powerShell {
            name = "Initialize-DevelopmentEnvironment"
            id = "RUNNER_398"
            formatStderrAsError = true
            scriptMode = script {
                content = """
                    ${'$'}ErrorActionPreference = 'Stop'

                    . "%teamcity.build.checkoutDir%\%script.initdev%"
                    Initialize-DevelopmentEnvironment %script.initdev.parameters%
                """.trimIndent()
            }
        }
        powerShell {
            name = "Create Database Template (no extensions)"
            id = "RUNNER_196"
            platform = PowerShellStep.Platform.x64
            edition = PowerShellStep.Edition.Desktop
            formatStderrAsError = true
            scriptMode = script {
                content = """
                    Import-Module -Force -Scope Global "%teamcity.build.checkoutDir%\%script.create.template%"
                    Initialize-MinimalTemplate %script.create.template.parameters%
                """.trimIndent()
            }
            param("jetbrains_powershell_script_file", "Ed-Fi-Common/logistics/scripts/activities/build/create-populated/create-populated.ps1")
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
            id = "BUILD_EXT_76"
            feedUrl = "%azureArtifacts.feed.nuget%"
            username = "%azureArtifacts.edFiBuildAgent.userName%"
            password = "%azureArtifacts.edFiBuildAgent.accessToken%"
        }
    }

    dependencies {
        artifacts(OdsApi.buildTypes.OdsApi_OdsApiInitDevUnitTestPackage) {
            id = "ARTIFACT_DEPENDENCY_53"
            cleanDestination = true
            artifactRules = "EdFi.Ods.Api.IntegrationTestHarness.*.nupkg"
            enabled = false
        }
    }
})
