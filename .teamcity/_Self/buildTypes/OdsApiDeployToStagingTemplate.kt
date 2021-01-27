package _Self.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.nuGetPublish
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell

object OdsApiDeployToStagingTemplate : Template({
    name = "ODS/API: Deploy to Staging Template"

    maxRunningBuilds = 1

    params {
        param("octopus.release.version", "%version%")
        param("octopus.release.channel", "v%version.core%")
        param("octopus.deploy.timeout", "00:45:00")
        param("octopus.deploy.arguments", "--deploymenttimeout=%octopus.deploy.timeout% --packageversion=%version%")
        param("octopus.nuget.feed", "%octopus.server%/nuget/packages")
        param("octopus.deploy.environment", "Staging")
        param("nuget.packages", "NugetPackages")
        param("octopus.release.project", "Ed-Fi-ODS")
        param("octopus.project.id", "Projects-7")
    }

    vcs {
        checkoutMode = CheckoutMode.ON_SERVER
        showDependenciesChanges = true
    }

    steps {
        powerShell {
            name = "New-OctopusChannel"
            id = "RUNNER_405"
            formatStderrAsError = true
            scriptMode = script {
                content = """
                    ${'$'}ErrorActionPreference = 'Stop'

                    Import-Module "%teamcity.build.checkoutDir%\%script.build.management%"

                    ${'$'}params = @{
                        octopusServer = "%octopus.server%"
                        octopusApiKey = "%octopus.apiKey%"
                        octopusProjectId = "%octopus.project.id%"
                        packageVersion = "%version%"
                        prereleaseSuffix = ""
                    }
                    New-OctopusChannel @params
                """.trimIndent()
            }
        }
        nuGetPublish {
            name = "Force Publishing NuGet Packages to Octopus Feed"
            id = "RUNNER_124"
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            packages = """%nuget.packages%\*.%version%.nupkg"""
            serverUrl = "%octopus.nuget.feed%"
            apiKey = "%OctopusAPIKey%"
        }
        step {
            name = "Create Octopus Release and Deploy It to Staging"
            id = "RUNNER_125"
            type = "octopus.create.release"
            param("octopus_additionalcommandlinearguments", "%octopus.deploy.arguments%")
            param("octopus_waitfordeployments", "true")
            param("octopus_channel_name", "%octopus.release.channel%")
            param("octopus_version", "3.0+")
            param("octopus_host", "%octopus.server%")
            param("octopus_project_name", "%octopus.release.project%")
            param("octopus_deployto", "%octopus.deploy.environment%")
            param("secure:octopus_apikey", "%OctopusAPIKey%")
            param("octopus_releasenumber", "%octopus.release.version%")
        }
    }

    dependencies {
        artifacts(OdsApi.buildTypes.OdsApi_OdsApiInitDevUnitTestPackage) {
            id = "ARTIFACT_DEPENDENCY_13"
            cleanDestination = true
            artifactRules = """
                *.nupkg => %nuget.packages%
                %odsapi.package.databases%.*.nupkg!** => .
            """.trimIndent()
        }
    }
})
