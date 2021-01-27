package OdsApi_Cloud.buildTypes

import OdsApi.buildTypes.OdsApi_OdsApiInitDevUnitTestPackage
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.PowerShellStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.nuGetPack
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.VcsTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.finishBuildTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object OdsApi_Cloud_CloudOdsApiBuildPackage : BuildType({
    name = "Cloud ODS/API: Build, Package"

    artifactRules = "%nuget.pack.output%/*.nupkg"
    buildNumberPattern = "%version%"
    publishArtifacts = PublishMode.SUCCESSFUL

    params {
        param("nuget.pack.files", "NugetPackages/EdFi.CloudODS.nuspec")
        param("version.minor", "1")
    }

    vcs {
        root(_Self.vcsRoots.EdFiOds, ". => Ed-Fi-ODS")
        root(_Self.vcsRoots.EdFiOdsImplementation, ". => Ed-Fi-ODS-Implementation")

        cleanCheckout = true
        showDependenciesChanges = true
    }

    steps {
        powerShell {
            name = "Remove-EdFiDatabases"
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
            name = "Create Database Backups"
            platform = PowerShellStep.Platform.x64
            edition = PowerShellStep.Edition.Desktop
            formatStderrAsError = true
            scriptMode = script {
                content = ". Ed-Fi-ODS-Implementation/logistics/scripts/activities/build/CloudOds/PrepareDatabasesForExport.ps1 -artifactPath %nuget.pack.output%"
            }
        }
        nuGetPack {
            name = "Create NuGet Packages"
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            paths = "%nuget.pack.files%"
            version = "%version%"
            outputDir = "%nuget.pack.output%"
            cleanOutputDir = false
            properties = "%nuget.pack.properties.default%"
            args = "%nuget.pack.parameters%"
        }
    }

    triggers {
        vcs {
            quietPeriodMode = VcsTrigger.QuietPeriodMode.USE_DEFAULT
            triggerRules = """
                +:**.psm1
                +:**.ps1
            """.trimIndent()
        }
        finishBuildTrigger {
            buildType = "${OdsApi_OdsApiInitDevUnitTestPackage.id}"
            successfulOnly = true
        }
        finishBuildTrigger {
            buildType = "EdFi_OdsTools_AdminAppForSuite3_BuildBranch"
            successfulOnly = true
        }
    }

    features {
        commitStatusPublisher {
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "%EdFiBuildAgent-GitHubKeyPasshrase%"
                }
            }
        }
    }

    dependencies {
        dependency(AbsoluteId("EdFi_OdsTools_AdminAppForSuite3_BuildBranch")) {
            snapshot {
                onDependencyFailure = FailureAction.CANCEL
                onDependencyCancel = FailureAction.CANCEL
            }

            artifacts {
                cleanDestination = true
                artifactRules = """
                    +:EdFi%odsapi.package.suffix%.ODS.AdminApp.Web.*.nupkg!** => %nuget.pack.output%/EdFi.ODS.AdminApp.Web
                    -:EdFi%odsapi.package.suffix%.ODS.AdminApp.Web.*-*.nupkg
                    +:EdFi%odsapi.package.suffix%.ODS.AdminApp.Web.*.nupkg!/Artifacts => Ed-Fi-ODS-AdminApp/Application/EdFi.Ods.AdminApp.Web/Artifacts/
                """.trimIndent()
            }
        }
        dependency(OdsApi.buildTypes.OdsApi_OdsApiInitDevUnitTestPackage) {
            snapshot {
                onDependencyFailure = FailureAction.CANCEL
                onDependencyCancel = FailureAction.CANCEL
            }

            artifacts {
                cleanDestination = true
                artifactRules = """
                    +:EdFi%odsapi.package.suffix%.Ods.SwaggerUI.*.nupkg!** => %nuget.pack.output%/%odsapi.package.swaggerUI%
                    +:EdFi%odsapi.package.suffix%.Ods.WebApi.*.nupkg!** => %nuget.pack.output%/%odsapi.package.webApi%
                    -:EdFi%odsapi.package.suffix%.Ods.WebApi.PreRelease.*.nupkg
                """.trimIndent()
            }
        }
    }
})
