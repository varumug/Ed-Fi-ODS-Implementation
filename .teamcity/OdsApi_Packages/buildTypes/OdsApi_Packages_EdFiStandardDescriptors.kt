package OdsApi_Packages.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.nuGetFeedCredentials
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.nuGetPack
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.nuGetPublish
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.schedule

object OdsApi_Packages_EdFiStandardDescriptors : BuildType({
    name = "EdFi.Standard.Descriptors"

    artifactRules = """%nuget.pack.output%\%PackageId%.%version.core%*.nupkg"""
    buildNumberPattern = "%version%"

    params {
        param("pathToSolutionFile", """%teamcity.build.checkoutDir%\%MigrationUtilityRoot%\Migration.sln""")
        param("git.branch.default", "main")
        param("nuget.pack.files", "%env.TEMP%/%nuget.package.name%/EdFi.Standard.Descriptors.nuspec")
        param("script.build.edfi.standard.descriptors", """%MigrationUtilityRoot%\EdFi.Ods.Utilities.Migration\EdFi.Standard.Descriptors\build-edfi-standard-descriptors.psm1""")
        param("nuget.package.description", "EdFi.Standard.Descriptors")
        param("MigrationUtilityRoot", "Ed-Fi-MigrationUtility")
        param("nuget.package.name", "EdFi.Standard.Descriptors")
        param("script.build.edfi.standard.descriptors.parameters", "")
        param("nuget.pack.properties", """
            id=%PackageId%
            title=%PackageId%
            description=%nuget.package.description%
        """.trimIndent())
        param("PackageId", "EdFi%odsapi.package.suffix%.Standard.Descriptors")
        param("version.patch", "0")
        param("version.major", "1")
        param("version.minor", "0")
    }

    vcs {
        root(OdsApi_Packages.vcsRoots.OdsApi_Packages_EdFiAllianceEdFiStandard, "=> Ed-Fi-Standard")
        root(_Self.vcsRoots.EdFiOds, "+:. => Ed-Fi-ODS")
        root(_Self.vcsRoots.EdFiOdsImplementation, "+:. => Ed-Fi-ODS-Implementation")
        root(OdsApi_Packages.vcsRoots.OdsApi_Packages_EdFiMigrationUtility, "=> %MigrationUtilityRoot%")
    }

    steps {
        powerShell {
            name = "New-EdFiStandardDescriptorsPackage"
            formatStderrAsError = true
            scriptMode = script {
                content = """
                    Import-Module -Force -Scope Global "%teamcity.build.checkoutDir%\%script.build.edfi.standard.descriptors%"
                    New-EdFiStandardDescriptorsPackage %script.build.edfi.standard.descriptors.parameters%
                """.trimIndent()
            }
        }
        nuGetPack {
            name = "Pack Prerelease version"
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
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            packages = """%nuget.pack.output%\*.%version%.nupkg"""
            serverUrl = "%azureArtifacts.feed.nuget%"
            apiKey = "%azureArtifacts.edFiBuildAgent.accessToken%"
        }
    }

    triggers {
        schedule {
            schedulingPolicy = daily {
                hour = 5
            }
            branchFilter = "+:<default>"
            triggerBuild = always()
            enforceCleanCheckout = true
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
        nuGetFeedCredentials {
            feedUrl = "%azureArtifacts.feed.nuget%"
            username = "%azureArtifacts.edFiBuildAgent.userName%"
            password = "%azureArtifacts.edFiBuildAgent.accessToken%"
        }
    }
})
